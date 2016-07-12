package com.github.sunnybat.portforward;

import com.github.sunnybat.portforward.ui.Interactor;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;

/**
 *
 * @author Sunnybat
 */
public class UPnPManager {

  private final List<Port> portList = new ArrayList<>();
  private final Interactor myUI;
  private boolean portsOpen;
  private GatewayDevice currentGateway;

  /**
   * Creates a new UPnPManager.
   *
   * @param ui The Interactor to update
   */
  public UPnPManager(Interactor ui) {
    myUI = ui;
  }

  /**
   * Checks whether or not the given port number is valid.
   *
   * @param port The port number to check
   * @return True if valid, false if invalid
   */
  private boolean isValidPort(int port) {
    return port > 0 && port <= 65535;
  }

  /**
   * Adds the given port to this UPnPManager. This will override any conflicting ports already added. This cannot be called while ports are open.
   *
   * @param port The port number to add
   * @param TCP Whether or not to forward TCP
   * @param UDP Whether or not to forward UDP
   * @return True if successfully added, false if not
   */
  public boolean addPort(int port, boolean TCP, boolean UDP) {
    return addPort(new Port(port, TCP, UDP));
  }

  /**
   * Adds the given Port to this UPnPManager. This will override any conflicting ports already added. This cannot be called while ports are open.
   *
   * @param toAdd The Port to add
   * @return True if successfully added, false if not
   */
  public boolean addPort(Port toAdd) {
    if (portsOpen) {
      return false;
    }
    if (!isValidPort(toAdd.getPort())) {
      return false;
    }
    if (!toAdd.shouldForwardTPC() && !toAdd.shouldForwardUDP()) {
      return false;
    }
    removePort(toAdd.getPort());
    return portList.add(toAdd);
  }

  /**
   * Removes the given port number from this UPnPManager. This removes both TCP and UDP forwarding. This cannot be called while ports are open.
   *
   * @param portNum The port number to remove
   * @return True if removed, false if not present or unable to remove
   */
  public boolean removePort(int portNum) {
    if (portsOpen) {
      return false;
    }
    if (!isValidPort(portNum)) {
      return false;
    }
    for (Port p : portList) {
      if (p.getPort() == portNum) {
        portList.remove(p);
        return true;
      }
    }
    return false;
  }

  /**
   * Opens the currently added ports. If this returns false, the currently added ports are removed from this UPnPManager.
   *
   * @param IP The IP address to forward to
   * @return True if ports opened, false if not
   */
  public boolean openPorts(String IP) {
    if (IP == null || !IP.contains(".")) {
      return false;
    } else if (portList.isEmpty()) {
      return false;
    }
    if (portsOpen) {
      return true;
    } else {
      try {
        myUI.setPortOpening(false);
        currentGateway = getGateway(); // Always refresh gateway
        if (currentGateway != null) {
          checkCompatibility();
          if (doOpenPorts(IP)) { // doOpenPorts will update myUI as necessary
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

  /**
   * Checks whether or not ports are currently open.
   *
   * @return True if ports are open, false if not
   */
  public boolean arePortsOpen() {
    return portsOpen;
  }

  /**
   * Closes the currently open ports. When finished, the currently added ports are removed from this UPnPManager.
   *
   * @return True if all open ports were closed, false otherwise
   */
  public boolean closePorts() {
    if (portList.isEmpty()) {
      return false;
    }
    if (currentGateway == null) {
      try {
        currentGateway = getGateway();
        if (currentGateway == null) {
          System.err.println("Unable to find gateway device!");
          return false;
        }
      } catch (Exception e) {
        return false;
      }
    }
    myUI.updateStatus("Closing ports");
    boolean allRemoved = true;
    try {
      for (Port portToMap : portList) {
        if (portToMap.shouldForwardTPC()) {
          if (!currentGateway.deletePortMapping(portToMap.getPort(), "TCP")) {
            System.out.println("Unable to remove port " + portToMap.getPort() + " (TCP)");
            allRemoved = false;
          } else {
            System.out.println("Removed port " + portToMap.getPort() + " (TCP)");
          }
        }
        if (portToMap.shouldForwardUDP()) {
          if (!currentGateway.deletePortMapping(portToMap.getPort(), "UDP")) {
            System.out.println("Unable to remove port " + portToMap.getPort() + " (UDP)");
            allRemoved = false;
          } else {
            System.out.println("Removed port " + portToMap.getPort() + " (UDP)");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    UPnPFinished("Finished closing ports.");
    return allRemoved;
  }

  /**
   * Clear all ports from this UPnPManager
   */
  private void clearPorts() {
    if (portsOpen) {
      System.out.println("ERROR: Unable to clear port list, portsOpen is true!");
      return;
    }
    portList.clear();
    System.out.println("Ports list cleared");
  }

  /**
   * Called when finished closing ports or an error occurs while forwarding ports. Clears all ports currently in this UPnPManager.
   *
   * @param message The message to send to the user
   */
  private void UPnPFinished(String message) {
    portsOpen = false;
    clearPorts();
    myUI.updateStatus(message);
    myUI.setPortOpening(true);
  }

  /**
   * Gets the current GatewayDevice to use
   *
   * @return The GatewayDevice to use
   * @throws Exception Because laziness, and lots of exceptions thrown by this
   */
  private GatewayDevice getGateway() throws Exception {
    myUI.updateStatus("Identifying router...");
    GatewayDiscover gatewayDiscover = new GatewayDiscover();
    System.out.println("Looking for Gateway Devices...");
    Map<InetAddress, GatewayDevice> gateways = gatewayDiscover.discover();
    if (gateways.isEmpty()) {
      System.out.println("No gateways found");
      System.out.println("Stopping weupnp");
      UPnPFinished("No active gateways found!");
      return null;
    }
    System.out.println(gateways.size() + " gateway(s) found\n");
    int counter = 0;
    for (GatewayDevice gw : gateways.values()) {
      counter++;
      System.out.println("Listing gateway details of device #" + counter
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
    Integer portMapCount = currentGateway.getPortMappingNumberOfEntries();
    System.out.println("GetPortMappingNumberOfEntries=" + (portMapCount != null ? portMapCount.toString() : "(unsupported)"));
    // testing getGenericPortMappingEntry
    PortMappingEntry portMapping0 = new PortMappingEntry();
    if (currentGateway.getGenericPortMappingEntry(0, portMapping0)) {
      System.out.println("Portmapping #0 successfully retrieved (" + portMapping0.getPortMappingDescription() + ":" + portMapping0.getExternalPort() + ")");
      myUI.updateStatus("Compatibility check successful");
    } else {
      myUI.updateStatus("Compatibility check failed");
    }
  }

  /**
   * Opens the ports on the given GatewayDevice.
   *
   * @param activeGW The GatewayDevice to open ports on
   * @throws Exception If an Exception occurs while opening the ports
   */
  private boolean doOpenPorts(String ipToForwardTo) throws Exception {
    // enableUPnP lease duration mapping
    System.out.println("Sending port mapping request for ports.");
    myUI.updateStatus("Opening ports...");
    for (Port portToMap : portList) {
      if (portToMap.shouldForwardTPC()) {
        myUI.updateStatus("Opening Port " + portToMap.getPort() + " (TCP)");
        if (!currentGateway.addPortMapping(portToMap.getPort(), portToMap.getPort(), ipToForwardTo, "TCP", "PortForward UPnP TCP")) {
          System.out.println("Error mapping TCP port " + portToMap.getPort());
          UPnPFinished("Error mapping TCP port " + portToMap.getPort());
          return false;
        }
      }
      if (portToMap.shouldForwardUDP()) {
        myUI.updateStatus("Opening Port " + portToMap.getPort() + " (UDP)");
        if (!currentGateway.addPortMapping(portToMap.getPort(), portToMap.getPort(), ipToForwardTo, "UDP", "PortForward UPnP UDP")) {
          System.out.println("Error mapping UDP port " + portToMap.getPort());
          UPnPFinished("Error mapping UDP port " + portToMap.getPort());
          return false;
        }
      }
    }
    System.out.println("Mapping SUCCESSFUL!");
    myUI.updateStatus("Ports are currently open!");
    myUI.setPortClosing(true);
    return true;
  }

}
