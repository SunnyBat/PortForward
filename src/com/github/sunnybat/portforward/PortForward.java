package com.github.sunnybat.portforward;

import com.github.sunnybat.commoncode.utilities.IPAddress;
import com.github.sunnybat.portforward.ui.CLI;
import com.github.sunnybat.portforward.ui.GUI;
import com.github.sunnybat.portforward.ui.Interactor;

/**
 *
 * @author Sunnybat
 */
public class PortForward {

  private static Interactor myUI;
  private static UPnPManager myManager;
  private static boolean exit;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    String defaultIP = IPAddress.getInternalIP();
    if (args.length > 0 && args[0].equals("-cli")) {
      myUI = new CLI(defaultIP);
    } else {
      myUI = new GUI(defaultIP);
    }
    myManager = new UPnPManager(myUI);
    while (!exit) {
      myUI.waitForAction();
      if (exit) {
        return;
      } else if (myManager.isOpen()) {
        myManager.closePorts();
      } else {
        myManager.addPorts(myUI.getPortsToForward());
        myManager.openPorts(myUI.getIPToForward());
      }
    }
  }

  public static void exitProgram() {
    exit = true;
    if (myManager.isOpen()) {
      myManager.closePorts();
    }
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
}