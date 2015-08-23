package portforward.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import portforward.PortForward;
import portforward.UPnPManager;

/**
 *
 * @author Sunnybat
 */
public class IconMenu extends PopupMenu {

  private MenuItem openWindow;
//  private MenuItem closePort;
//  private MenuItem openPort;
  private MenuItem closeProgram;
  private final GUI myGUI;

  public IconMenu(GUI myGUI) {
    init();
    this.myGUI = myGUI;
  }

  private void init() {
    openWindow = new MenuItem("Restore Window");
//    openPort = new java.awt.MenuItem("Open Port X");
//    closePort = new java.awt.MenuItem("Close Port");
    closeProgram = new java.awt.MenuItem("Close Program");
    closeProgram.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        PortForward.exitProgram();
        synchronized (myGUI) {
          myGUI.notify();
        }
      }
    });
    openWindow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        myGUI.maximizeWindow();
      }
    });
    add(openWindow);
    add(closeProgram);
  }

//  public void addOpenPortButton() {
//    openPort.setLabel("Open Ports");
//    openPort.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(java.awt.event.ActionEvent evt) {
//        if (UPnPManager.openPorts()) {
//          addClosePortButton();
//        }
//      }
//    });
//    removeAll();
//    add(openPort);
//    addDefaults();
//  }
//
//  public void removeOpenPortButton() {
//    remove(openPort);
//  }
//
//  public void addClosePortButton() {
//    closePort.setLabel("Close Ports");
//    closePort.addActionListener(new java.awt.event.ActionListener() {
//      public void actionPerformed(java.awt.event.ActionEvent evt) {
//        UPnPManager.closePorts();
//        addOpenPortButton();
//      }
//    });
//    removeAll();
//    add(closePort);
//    addDefaults();
//  }
//
//  public void removeClosePortButton() {
//    remove(closePort);
//  }
}
