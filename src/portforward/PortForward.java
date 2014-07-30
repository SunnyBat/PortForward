/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portforward;

import java.net.InetAddress;
import java.util.Map;
import org.bitlet.weupnp.*;

/**
 *
 * @author Sunnybat
 */
public class PortForward {

  private static GUI myGUI;
  private static volatile int portOpen;
  private static volatile boolean threadCreated;
  private static volatile boolean useTCP = true;
  private static volatile boolean useUDP = true;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    myGUI = new GUI();
    myGUI.setVisible(true);
  }

  public static void exitProgram() {
    myGUI.dispose();
  }

  public static void showProgramWindow() {
    myGUI.maximizeWindow();
  }

  /**
   * Opens the specified port using UPnP. If a port is already open OR is being opened, this method returns false. Note that this has a minimum port
   * of 1 and a maximum port of 65535 -- if it is outside of these bounds, it changes the port to the nearest valid port number (either 1 or 65535).
   *
   * @param port The port number to open
   * @return True if port opening THREAD was started, false if not
   */
  public static boolean openPort(int port) {
    return openPort(port, useTCP, useUDP);
  }

  /**
   * Opens the specified port using UPnP. If a port is already open OR is being opened, this method returns false. Note that this has a minimum port
   * of 1 and a maximum port of 65535 -- if it is outside of these bounds, it changes the port to the nearest valid port number (either 1 or 65535).
   *
   * @param port The port number to open
   * @param tcp Use TCP or not
   * @param udp Use UDP or not
   * @return True if port opening THREAD was started, false if not
   */
  public static boolean openPort(int port, boolean tcp, boolean udp) {
    if (threadCreated) {
      return false;
    }
    if (isPortOpen()) {
      System.out.println("ERROR: PortFoward.openPort() called, but port already open? Port parameter: " + port);
      return false;
    }
    if (port > 65535) {
      port = 65535;
    } else if (port < 1) {
      port = 1;
    }
    useTCP = tcp;
    useUDP = udp;
    portOpen = port;
    PortForward.startNewThread(new Runnable() {
      @Override
      public void run() {
        try {
          threadCreated = true;
          enableUPnP();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    return true;
  }

  public static boolean openPorts(final int[] list) {
    System.out.println("~~~~~~~~~~~~~~~~~openPorts(int[]) called!");
    if (threadCreated) {
      return false;
    }
    if (isPortOpen()) {
      System.out.println("ERROR: PortFoward.openPort() called, but port already open?");
      return false;
    }
    portOpen = 1;
    PortForward.startNewThread(new Runnable() {
      @Override
      public void run() {
        try {
          threadCreated = true;
          enableUPnP(list);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    return true;
  }

  private static void UPnPFinished(String message) {
    setGUIPortStatusText(message);
    myGUI.setButtonEnabled(true);
    myGUI.callButtonAction();
    myGUI.maximizeWindow();
    closePort();
    threadCreated = false;
  }

  public static boolean isPortOpen() {
    return portOpen != 0;
  }

  public static void setGUIPortStatusText(String text) {
    myGUI.setPortStatusText("Port Status: " + text);
  }

  public static void closePort() {
    portOpen = 0;
    //threadCreated = false;
  }

  public static void println(String line) {
    System.out.println(line);
  }

  /**
   * This makes a new daemon, low-priority Thread and runs it.
   *
   * @param run The Runnable to make into a Thread and run
   */
  public static void startNewThread(Runnable run) {
    startNewThread(run, "General Background Thread");
  }

  public static void startNewThread(Runnable run, String name) {
    Thread newThread = new Thread(run);
    newThread.setName(name);
    newThread.start(); // Start the Thread
  }

  public static void enableUPnP() throws Exception {
    println("Starting weupnp");
    myGUI.setButtonEnabled(false);
    int portToUse = portOpen;
    setGUIPortStatusText("Opening port " + portToUse + "...");
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
    println("Querying device to see if a port mapping already exists for port " + portToUse);
    PortMappingEntry portMapping = new PortMappingEntry();
    if (activeGW.getSpecificPortMappingEntry(portToUse, "TCP", portMapping)) {
      println("Port " + portToUse + " is already mapped. Aborting test.");
      UPnPFinished("ERROR: Port " + portToUse + " already mapped");
      return;
    } else {
      println("Mapping free. Sending port mapping request for port " + portToUse);
      // enableUPnP static lease duration mapping
      if ((!useTCP || activeGW.addPortMapping(portToUse, portToUse, localAddress.getHostAddress(), "TCP", "ShowclixScanner UPnP TCP"))
          && (!useUDP || activeGW.addPortMapping(portToUse, portToUse, localAddress.getHostAddress(), "UDP", "ShowclixScanner UPnP UDP"))) {
        println("Mapping SUCCESSFUL!");
        setGUIPortStatusText("Port " + portToUse + " is currently open!");
        myGUI.setButtonEnabled(true);
        while (isPortOpen()) {
          Thread.sleep(100);
        }
        if (useTCP && activeGW.deletePortMapping(portToUse, "TCP")) {
          println("Removed port mapping (TCP).");
          setGUIPortStatusText("Port " + portToUse + " (TCP) closed");
        } else if (!useTCP) {
          System.out.println("!useTCP");
        } else {
          println("Unable to remove port mapping :(");
          setGUIPortStatusText("Unable to remove port " + portToUse + " (TCP) mapping :(");
        }
        if (useUDP && activeGW.deletePortMapping(portToUse, "UDP")) {
          println("Removed port mapping (UDP).");
          setGUIPortStatusText("Port " + portToUse + " (UDP) closed");
        } else if (!useUDP) {
          System.out.println("!useUDP");
        } else {
          println("Unable to remove port mapping :(");
          setGUIPortStatusText("Unable to remove port " + portToUse + " (UDP) mapping :(");
        }
        threadCreated = false;
//        if (myGUI.isDisplayable()) {
//          myGUI.maximizeWindow();
//        }
      } else {
        println("Unable to map port. Connections from outside your LAN may be unavailable :(");
        UPnPFinished("ERROR: Failed to map port. Reason unknown.");
      }
    }
  }

  public static void enableUPnP(int[] list) throws Exception {
    if (list.length % 3 != 0) {
      System.out.println("ERROR: List array is of invalid length! (len=" + list.length + ")");
      return;
    }
    println("Starting weupnp");
    myGUI.setButtonEnabled(false);
    setGUIPortStatusText("Opening multiple ports...");
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
    println("Querying device to see if a port mapping already exists for ports...");
    PortMappingEntry portMapping = new PortMappingEntry();
    for (int a = 0; a < list.length; a += 3) {
      if (activeGW.getSpecificPortMappingEntry(list[a], "TCP", portMapping)) {
        println("Port " + list[a] + " is already mapped!");
        UPnPFinished("ERROR: Port " + list[a] + " already mapped.");
        return;
      }
    }
    println("Mapping free. Sending port mapping request for ports.");
    // enableUPnP static lease duration mapping
    int success = 0;
    for (int a = 0; a < list.length; a += 3) {
      if ((list[a+1] == 0 || activeGW.addPortMapping(list[a], list[a], localAddress.getHostAddress(), "TCP", "ShowclixScanner UPnP TCP"))
          && (list[a+2] == 0 || activeGW.addPortMapping(list[a], list[a], localAddress.getHostAddress(), "UDP", "ShowclixScanner UPnP UDP"))) {
        System.out.println("Port " + list[a] + " mapped!");
        success++;
      }
    }
    if (success == list.length / 3) {
      println("Mapping SUCCESSFUL!");
      portOpen = 1;
      setGUIPortStatusText("Ports are currently open!");
      myGUI.setButtonEnabled(true);
      while (isPortOpen()) {
        Thread.sleep(100);
      }
      for (int a = 0; a < list.length; a += 3) {
        if (list[a+1] == 1 && activeGW.deletePortMapping(list[a], "TCP")) {
          println("Removed port mapping (TCP).");
          setGUIPortStatusText("Port " + list[a] + " (TCP) closed");
        } else if (list[a+1] == 0) {
          System.out.println("!useTCP");
        } else {
          println("Unable to remove port mapping :(");
          setGUIPortStatusText("Unable to remove port " + list[a] + " (TCP) mapping :(");
        }
        if (list[a+2] == 1 && activeGW.deletePortMapping(list[a], "UDP")) {
          println("Removed port mapping (UDP).");
          setGUIPortStatusText("Port " + list[a] + " (UDP) closed");
        } else if (list[a+2] == 0) {
          System.out.println("!useUDP");
        } else {
          println("Unable to remove port mapping :(");
          setGUIPortStatusText("Unable to remove port " + list[a] + " (UDP) mapping :(");
        }
      }
      threadCreated = false;
//        if (myGUI.isDisplayable()) {
//          myGUI.maximizeWindow();
//        }
    } else {
      println("Unable to map port. Connections from outside your LAN may be unavailable :(");
      UPnPFinished("ERROR: Failed to map port. Reason unknown.");
    }
  }

  private static boolean printTrue() {
    System.out.println("PRINTING TRUE!!!");
    return true;
  }

}
