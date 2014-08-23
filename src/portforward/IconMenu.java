/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portforward;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 *
 * @author Sunnybat
 */
public class IconMenu extends PopupMenu {

  private MenuItem openWindow;
  private MenuItem closePort;
  private MenuItem openPort;
  private MenuItem closeProgram;

  public IconMenu() {
    initializeComponents();
    customComponents();
  }

  private void initializeComponents() {
    openWindow = new MenuItem("Restore Window");
    openPort = new java.awt.MenuItem("Open Port X");
    closePort = new java.awt.MenuItem("Close Port");
    closeProgram = new java.awt.MenuItem("Close Program");
    closeProgram.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        PortForward.exitProgram();
        removeOpenPortButton();
        addClosePortButton();
      }
    });
    openWindow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        PortForward.showProgramWindow();
      }
    });
    //add(closePort);
    add(openWindow);
    add(closeProgram);
  }

  private void customComponents() {
  }

  private void addDefaults() {
    addSeparator();
    add(openWindow);
    add(closeProgram);
  }

  public void addOpenPortButton() {
    openPort.setLabel("Open Ports");
    openPort.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (UPnPManager.openPorts()) {
          addClosePortButton();
        }
      }
    });
    removeAll();
    add(openPort);
    addDefaults();
  }

  public void removeOpenPortButton() {
    remove(openPort);
  }

  public void addClosePortButton() {
    closePort.setLabel("Close Ports");
    closePort.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        UPnPManager.closePorts();
        addOpenPortButton();
      }
    });
    removeAll();
    add(closePort);
    addDefaults();
  }

  public void removeClosePortButton() {
    remove(closePort);
  }

}
