/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and openPorts the template in the editor.
 */
package portforward;

import java.net.InetAddress;
import java.util.*;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;

/**
 *
 * @author Sunnybat
 */
public class UPnPManager {

  private static final List<Port> portList = new ArrayList<>();
  private static volatile boolean portsOpen;
  private static volatile boolean closePorts;

  public static void addPort(int port, boolean TCP, boolean UDP) {
    portList.add(new Port(port, TCP, UDP));
  }

  public static void removePort(int port) {
    portList.add(new Port(port, true, true));
  }

  private static void clearPorts() {
    if (portsOpen) {
      println("ERROR: Unable to clear port list, portsOpen is true!");
      return;
    }
    portList.clear();
    println("Ports list cleared");
  }

  private static void println(String msg) {
    System.out.println(msg);
  }

  public static boolean openPorts() {
    if (portsOpen) {
      return false;
    } else {
      PortForward.startNewThread(new Runnable() {
        @Override
        public void run() {
          try {
            portsOpen = true;
            PortForward.setPortOptionsEnabled(false);
            PortForward.setGUIButtonEnabled(false);
            openUPnP();
          } catch (Exception e) {
          }
        }
      });
      return true;
    }
  }

  public static boolean isOpen() {
    return portsOpen;
  }

  public static boolean closePorts() {
    if (closePorts || !portsOpen) {
      return false;
    }
    System.out.println("Closing ports");
    closePorts = true;
    return true;
  }

  private static void UPnPFinished(String s) {
    closePorts = false;
    portsOpen = false;
    clearPorts();
    PortForward.UPnPFinished(s);
  }

  private static void openUPnP() throws Exception {
    println("Starting weupnp");
    PortForward.setGUIPortStatusText("Identifying router...");
    GatewayDiscover gatewayDiscover = new GatewayDiscover();
    println("Looking for Gateway Devices...");
    Map<InetAddress, GatewayDevice> gateways = gatewayDiscover.discover();
    if (gateways.isEmpty()) {
      println("No gateways found");
      println("Stopping weupnp");
      UPnPFinished("ERROR: No gateways found.");
      return;
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
    GatewayDevice activeGW = gatewayDiscover.getValidGateway();
    if (null != activeGW) {
      println("Using gateway:" + activeGW.getFriendlyName());
    } else {
      println("No active gateway device found");
      println("Stopping weupnp");
      UPnPFinished("ERROR: No active gateway device found.");
      return;
    }
    PortForward.setGUIPortStatusText("Checking router compatibility...");
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
    InetAddress localAddress = activeGW.getLocalAddress();
    println("Using local address: " + localAddress.getHostAddress());
    String externalIPAddress = activeGW.getExternalIPAddress();
    println("External address: " + externalIPAddress);
    println("Sending port mapping request for ports.");
    // enableUPnP static lease duration mapping
    PortForward.setGUIPortStatusText("Opening ports...");
    Iterator<Port> portIt = portList.iterator();
    while (portIt.hasNext()) {
      Port portToMap = portIt.next();
      if (portToMap.TCP) {
        if (!activeGW.addPortMapping(portToMap.portNumber, portToMap.portNumber, localAddress.getHostAddress(), "TCP", "PortForward UPnP TCP")) {
          println("Error mapping TCP port " + portToMap.portNumber);
          UPnPFinished("Error mapping TCP port " + portToMap.portNumber);
          return;
        }
      }
      if (portToMap.UDP) {
        if (!activeGW.addPortMapping(portToMap.portNumber, portToMap.portNumber, localAddress.getHostAddress(), "UDP", "PortForward UPnP UDP")) {
          println("Error mapping UDP port " + portToMap.portNumber);
          UPnPFinished("Error mapping UDP port " + portToMap.portNumber);
          return;
        }
      }
    }
    println("Mapping SUCCESSFUL!");
    PortForward.setGUIPortStatusText("Ports are currently open!");
    PortForward.setGUIButtonEnabled(true);
    while (!closePorts) {
      Thread.sleep(100);
    }
    portIt = portList.iterator();
    while (portIt.hasNext()) {
      Port portToMap = portIt.next();
      if (portToMap.TCP) {
        if (!activeGW.deletePortMapping(portToMap.portNumber, "TCP")) {
          println("Unable to remove port " + portToMap.portNumber + " TCP");
        }
      }
      if (portToMap.UDP) {
        if (!activeGW.deletePortMapping(portToMap.portNumber, "UDP")) {
          println("Unable to remove port " + portToMap.portNumber + " UDP");
        }
      }
    }
    UPnPFinished("Finished closing ports.");
  }

  private static class Port {

    public int portNumber;
    public boolean TCP;
    public boolean UDP;

    public Port(int pN) {
      portNumber = pN;
      TCP = true;
      UDP = true;
    }

    public Port(int pN, boolean useTCP, boolean useUDP) {
      portNumber = pN;
      TCP = useTCP;
      UDP = useUDP;
    }
  }

}
