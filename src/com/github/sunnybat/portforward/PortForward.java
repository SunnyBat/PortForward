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

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String initialIP = IPAddress.getInternalIP();
    if (containsArg(args, "-cli")) {
      myUI = new CLI(initialIP);
    } else {
      Thread.setDefaultUncaughtExceptionHandler(new GUIExceptionHandler());
      myUI = new GUI(initialIP);
    }
    myManager = new UPnPManager(myUI);

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        if (myManager.arePortsOpen()) {
          myManager.closePorts();
        }
      }
    }));

    List<Port> portsToOpen = new ArrayList<>();
    if (containsArg(args, "-port")) {
      int startIndex = getArgIndex(args, "-port");
      int offset = 1;
      while (startIndex + offset < args.length && !args[startIndex + offset].startsWith("-")) {
        try {
          int portNum = Integer.parseInt(args[startIndex + offset]);
          portsToOpen.add(new Port(portNum, true, true));
          System.out.println("Added port " + portNum);
        } catch (NumberFormatException nfe) {
          System.out.println("Invalid port: " + args[startIndex + offset]);
        }
        offset++;
      }
    }
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
        myManager.openPorts(initialIP);
      } catch (IllegalStateException ise) {
      }
    }
    while (!myUI.exitRequested()) {
      myUI.waitForAction();
      if (myUI.exitRequested()) {
        return;
      } else if (myUI.getAction() == Interactor.ACTION.FORCECLOSE) {
        for (Port p : myUI.getPortsToForward()) {
          if (!myManager.addPort(p)) {
            myUI.updateStatus("Unable to add port " + p.getPort());
          }
        }
        myManager.closePorts();
      } else if (myManager.arePortsOpen()) {
        myManager.closePorts();
      } else {
        for (Port p : myUI.getPortsToForward()) {
          if (!myManager.addPort(p)) {
            myUI.updateStatus("Unable to add port " + p.getPort());
          }
        }
        myManager.openPorts(myUI.getIPToForward());
      }
    }
  }

  private static boolean containsArg(String[] args, String argument) {
    for (String s : args) {
      if (argument.equalsIgnoreCase(s)) {
        return true;
      }
    }
    return false;
  }

  private static int getArgIndex(String[] args, String argument) {
    for (int i = 0; i < args.length; i++) {
      if (argument.equalsIgnoreCase(args[i])) {
        return i;
      }
    }
    return -1;
  }
}
