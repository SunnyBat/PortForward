package com.github.sunnybat.portforward;

import com.github.sunnybat.commoncode.error.GUIExceptionHandler;
import com.github.sunnybat.commoncode.utilities.IPAddress;
import com.github.sunnybat.portforward.ui.CLI;
import com.github.sunnybat.portforward.ui.GUI;
import com.github.sunnybat.portforward.ui.Interactor;

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
    String defaultIP = IPAddress.getInternalIP();
    if (args.length > 0 && args[0].equals("-cli")) {
      myUI = new CLI(defaultIP);
    } else {
      Thread.setDefaultUncaughtExceptionHandler(new GUIExceptionHandler());
      myUI = new GUI(defaultIP);
    }
    myManager = new UPnPManager(myUI);
    while (!myUI.exitRequested()) {
      myUI.waitForAction();
      if (myUI.exitRequested()) {
        return;
      } else if (myManager.isOpen()) {
        myManager.closePorts();
      } else {
        myManager.addPorts(myUI.getPortsToForward());
        myManager.openPorts(myUI.getIPToForward());
      }
    }
  }
}
