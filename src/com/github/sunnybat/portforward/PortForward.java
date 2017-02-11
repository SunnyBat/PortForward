package com.github.sunnybat.portforward;

import com.github.sunnybat.commoncode.error.GUIExceptionHandler;
import com.github.sunnybat.commoncode.utilities.IPAddress;
import com.github.sunnybat.portforward.ui.CLI;
import com.github.sunnybat.portforward.ui.GUI;
import com.github.sunnybat.portforward.ui.Interactor;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class for the project.
 *
 * @author Sunnybat
 */
public class PortForward {

  private static Interactor myUI;
  private static UPnPManager myManager;
  private static final String CLI_FLAG = "-cli";
  private static final String PORT_FLAG = "-port";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String internalIP = IPAddress.getInternalIP();
    if (containsArg(args, CLI_FLAG)) {
      myUI = new CLI(internalIP);
    } else {
      Thread.setDefaultUncaughtExceptionHandler(new GUIExceptionHandler());
      myUI = new GUI(internalIP);
    }
    myManager = new UPnPManager(myUI);

    // Close ports automatically if program is shut down
    // This does not work on forced terminations
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        if (myManager.arePortsOpen()) {
          myManager.closePorts();
        }
      }
    }));

    // Auto-open ports if necessary
    List<Port> portsToOpen = parsePorts(args);
    autoOpenPorts(internalIP, portsToOpen);

    // Main program loop
    mainLoop();
  }

  /**
   * Checks whether or not the given argument is present within the given array.
   *
   * @param args The array to check
   * @param argument The argument to check for
   * @return True if present, false if not
   */
  private static boolean containsArg(String[] args, String argument) {
    return getArgIndex(args, argument) != -1;
  }

  /**
   * Gets the index of argument within args. If it is not present, this returns -1.
   *
   * @param args The array to check
   * @param argument The argument to check for
   * @return The index of the argument, or -1 if not present
   */
  private static int getArgIndex(String[] args, String argument) {
    for (int i = 0; i < args.length; i++) {
      if (argument.equalsIgnoreCase(args[i])) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets a List of Ports present within the given array of args. Note that this only finds the first set of ports and does not check for duplicates.
   * This will never return null, and will instead return an empty List if no ports are specified.
   *
   * @param args The args to check
   * @return The List of ports present in args
   */
  private static List<Port> parsePorts(String[] args) {
    List<Port> portsToOpen = new ArrayList<>();
    int startIndex = getArgIndex(args, PORT_FLAG);
    if (startIndex != -1) {
      int offset = 1;
      while (startIndex + offset + 1 < args.length && !args[startIndex + offset].startsWith("-")) {
        try {
          int iPort = Integer.parseInt(args[startIndex + offset]);
          int ePort = Integer.parseInt(args[startIndex + offset + 1]);
          portsToOpen.add(new Port(iPort, ePort, true, true));
          System.out.println("Added port " + iPort + ":" + ePort);
        } catch (NumberFormatException nfe) {
          System.err.println("Invalid port: " + args[startIndex + offset]);
          System.err.println("Invalid port: " + args[startIndex + offset + 1]);
        } catch (IllegalArgumentException iae) {
          System.err.println(iae.getLocalizedMessage());
        }
        offset += 2;
      }
    }
    return portsToOpen;
  }

  /**
   * Adds the given ports to myManager.
   *
   * @param myManager The UPnPManager to add ports to
   * @param toAdd The List of Ports to add
   * @return True if all ports were added, false if not
   */
  private static boolean addPorts(UPnPManager myManager, List<Port> toAdd) {
    for (Port p : myUI.getPortsToForward()) {
      if (!myManager.addPort(p)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Automatically opens the specified ports to the given IP.
   *
   * @param forwardToIP The IP to open the ports to
   * @param portsToOpen The List of Ports to open
   */
  private static void autoOpenPorts(String forwardToIP, List<Port> portsToOpen) {
    if (!portsToOpen.isEmpty()) {
      try {
        myUI.setPortClosing(false);
        for (Port p : portsToOpen) {
          myManager.addPort(p);
        }
        myManager.closePorts();
        for (Port p : portsToOpen) {
          myManager.addPort(p);
        }
        myManager.openPorts(forwardToIP);
      } catch (IllegalStateException ise) {
      }
    }
  }

  /**
   * The main loop of the program.
   */
  private static void mainLoop() {
    while (!myUI.exitRequested()) {
      myUI.waitForAction();
      if (myUI.exitRequested()) {
        return;
      } else if (myUI.getAction() == Interactor.ACTION.FORCECLOSE) {
        if (!addPorts(myManager, myUI.getPortsToForward())) {
          myUI.updateStatus("Unable to add ports to forward?");
        } else {
          myManager.closePorts();
        }
      } else if (myManager.arePortsOpen()) {
        myManager.closePorts();
      } else if (!addPorts(myManager, myUI.getPortsToForward())) {
        myUI.updateStatus("Unable to add ports to forward?");
      } else {
        myManager.openPorts(myUI.getIPToForward());
      }
    }
  }
}
