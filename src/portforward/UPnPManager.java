package portforward;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import portforward.ui.Interactor;

/**
 *
 * @author Sunnybat
 */
public class UPnPManager {

  private final List<Port> portList = new ArrayList<>();
  private final Interactor myUI;
  private boolean portsOpen;
  private GatewayDevice activeGW;

  /**
   * Creates a new UPnPManager.
   *
   * @param ui The Interactor to update
   */
  public UPnPManager(Interactor ui) {
    myUI = ui;
  }

  public void addPort(int port, boolean TCP, boolean UDP) {
    if (portsOpen) {
      throw new IllegalStateException("Cannot add ports while ports are open!");
    }
    portList.add(new Port(port, TCP, UDP));
  }

  public boolean openPorts(String IP) {
    if (IP == null || !IP.contains(".")) {
      throw new IllegalArgumentException("Invalid IP address: " + IP);
    }
    if (portsOpen) {
      return true;
    } else {
      try {
        myUI.setPortOpening(false);
        myUI.updateStatus("Identifying router...");
        activeGW = getGateway();
        if (activeGW != null) {
          myUI.updateStatus("Using gateway:" + activeGW.getFriendlyName());
          checkCompatibility();
          if (doOpenPorts()) { // doOpenPorts will update myUI as necessary
            portsOpen = true;
            return true;
          } else {
            myUI.setPortOpening(true);
            return false;
          }
        } else {
          return false;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return true;
    }
  }

  public boolean isOpen() {
    return portsOpen;
  }

  public boolean closePorts() {
    if (!portsOpen) {
      return true;
    }
    System.out.println("Closing ports");
    try {
      for (Port portToMap : portList) {
        if (portToMap.shouldForwardTPC()) {
          if (!activeGW.deletePortMapping(portToMap.getPort(), "TCP")) {
            println("Unable to remove port " + portToMap.getPort() + " TCP");
          }
        }
        if (portToMap.shouldForwardUDP()) {
          if (!activeGW.deletePortMapping(portToMap.getPort(), "UDP")) {
            println("Unable to remove port " + portToMap.getPort() + " UDP");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    UPnPFinished("Finished closing ports.");
    return true;
  }

  private void clearPorts() {
    if (portsOpen) {
      println("ERROR: Unable to clear port list, portsOpen is true!");
      return;
    }
    portList.clear();
    println("Ports list cleared");
  }

  private void UPnPFinished(String message) {
    clearPorts();
    myUI.updateStatus(message);
    myUI.setPortOpening(true);
    portsOpen = false;
  }

  private GatewayDevice getGateway() throws Exception {
    myUI.updateStatus("Identifying router...");
    GatewayDiscover gatewayDiscover = new GatewayDiscover();
    println("Looking for Gateway Devices...");
    Map<InetAddress, GatewayDevice> gateways = gatewayDiscover.discover();
    if (gateways.isEmpty()) {
      println("No gateways found");
      println("Stopping weupnp");
      UPnPFinished("No active gateways found!");
      return null;
    }
    println(gateways.size() + " gateway(s) found\n");
    int counter = 0;
    for (GatewayDevice gw : gateways.values()) {
      counter++;
      println("Listing gateway details of device #" + counter
          + "\n\tFriendly name: " + gw.getFriendlyName()
          + "\n\tPresentation URL: " + gw.getPresentationURL()
          + "\n\tModel name: " + gw.getModelName()
          + "\n\tModel number: " + gw.getModelNumber()
          + "\n\tLocal interface address: " + gw.getLocalAddress().getHostAddress() + "\n");
    }
    // choose the first active gateway for the tests
    return gatewayDiscover.getValidGateway();
  }

  /**
   * Checks compatibility with the given GatewayDevice. Prints out information to the command-line.
   *
   * @param activeGW The GatewayDevice to check
   * @throws Exception If an Exception occurs
   */
  private void checkCompatibility() throws Exception {
    myUI.updateStatus("Checking router compatibility...");
    // testing PortMappingNumberOfEntries
    Integer portMapCount = activeGW.getPortMappingNumberOfEntries();
    println("GetPortMappingNumberOfEntries=" + (portMapCount != null ? portMapCount.toString() : "(unsupported)"));
    // testing getGenericPortMappingEntry
    PortMappingEntry portMapping0 = new PortMappingEntry();
    if (activeGW.getGenericPortMappingEntry(0, portMapping0)) {
      println("Portmapping #0 successfully retrieved (" + portMapping0.getPortMappingDescription() + ":" + portMapping0.getExternalPort() + ")");
    } else {
      println("Portmapping #0 retrival failed");
    }
  }

  /**
   * Opens the ports on the given GatewayDevice.
   *
   * @param activeGW The GatewayDevice to open ports on
   * @throws Exception If an Exception occurs while opening the ports
   */
  private boolean doOpenPorts() throws Exception {
    // enableUPnP lease duration mapping
    InetAddress localAddress = activeGW.getLocalAddress();
    println("Using local address: " + localAddress.getHostAddress());
    String externalIPAddress = activeGW.getExternalIPAddress();
    println("External address: " + externalIPAddress);
    println("Sending port mapping request for ports.");
    myUI.updateStatus("Opening ports...");
    for (Port portToMap : portList) {
      if (portToMap.shouldForwardTPC()) {
        myUI.updateStatus("Opening Port " + portToMap.getPort() + " (TCP)");
        if (!activeGW.addPortMapping(portToMap.getPort(), portToMap.getPort(), localAddress.getHostAddress(), "TCP", "PortForward UPnP TCP")) {
          println("Error mapping TCP port " + portToMap.getPort());
          UPnPFinished("Error mapping TCP port " + portToMap.getPort());
          return false;
        }
      }
      if (portToMap.shouldForwardUDP()) {
        myUI.updateStatus("Opening Port " + portToMap.getPort() + " (TCP)");
        if (!activeGW.addPortMapping(portToMap.getPort(), portToMap.getPort(), localAddress.getHostAddress(), "UDP", "PortForward UPnP UDP")) {
          println("Error mapping UDP port " + portToMap.getPort());
          UPnPFinished("Error mapping UDP port " + portToMap.getPort());
          return false;
        }
      }
    }
    println("Mapping SUCCESSFUL!");
    myUI.updateStatus("Ports are currently open!");
    myUI.setPortClosing(true);
    return true;
  }

  private void println(String msg) {
    System.out.println(msg);
  }

}
