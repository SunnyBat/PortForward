package com.github.sunnybat.portforward.ui;

import com.github.sunnybat.portforward.Port;
import com.github.sunnybat.portforward.PortForward;
import java.awt.Color;
import java.awt.Image;
import java.awt.MenuItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Graphical User Interface class to interact with the user through an interface.
 *
 * @author Sunnybat
 */
public class GUI extends com.github.sunnybat.commoncode.javax.swing.JFrame implements Interactor {

  private final List<PortPanel> portPanelList = new ArrayList<>();
  private boolean exitRequested;
  private boolean forceClose;

  /**
   * Creates new form GUI
   *
   * @param defaultIP The default IP address to forward to
   */
  public GUI(final String defaultIP) {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        initComponents();
        customComponents();
        JTFIPToForwardTo.setText(defaultIP);
      }
    });
  }

  private void customComponents() {
    this.setLocationRelativeTo(null);
    try {
      Image myImage = javax.imageio.ImageIO.read(PortForward.class.getResourceAsStream("/resources/Icon.png"));
      setTrayIcon("PortForward", myImage);
      setIconImage(myImage);
      setVisible(true);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
    ((javax.swing.JSpinner.NumberEditor) JSIPort.getEditor()).getTextField().setDisabledTextColor(Color.DARK_GRAY);
    JTFIPToForwardTo.setDisabledTextColor(Color.DARK_GRAY);
    MenuItem openWindow = new MenuItem("Restore Window");
    MenuItem closeProgram = new MenuItem("Close Program");
    closeProgram.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
      }
    });
    openWindow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        restoreFromTray();
      }
    });
    getPopupMenu().add(openWindow);
    getPopupMenu().add(closeProgram);
  }

  @Override
  public void minimizeToTray() {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        try {
          JSIPort.commitEdit(); // Update the spinner value. If invalid, uses the last known valid value (keypresses don't update the last known value)
        } catch (Exception e) {
          e.printStackTrace();
        }
        setVisible(false);
      }
    });
    // TODO: Add open/close port buttons
    super.minimizeToTray();
  }

  @Override
  public void dispose() {
    super.dispose(); // CHECK: Synchronization?
    exitRequested = true;
    continuePressed();
  }

  @Override
  public String getIPToForward() {
    return JTFIPToForwardTo.getText();
  }

  @Override
  public List<Port> getPortsToForward() {
    List<Port> ports = new ArrayList<>();
    Port mainPort = new Port((int) JSIPort.getValue(), (int) JSEPort.getValue(), JCBTCP.isSelected(), JCBUDP.isSelected());
    ports.add(mainPort);
    for (PortPanel panel : portPanelList) {
      if (panel.checkBoxesValid()) {
        ports.add(panel.getPort());
      }
    }
    return ports;
  }

  @Override
  public void waitForAction() {
    synchronized (this) {
      try {
        this.wait();
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    }
  }

  @Override
  public ACTION getAction() {
    if (forceClose) {
      forceClose = false;
      return ACTION.FORCECLOSE;
    }
    return ACTION.DEFAULT;
  }

  @Override
  public void updateStatus(final String text) {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        JLPortStatus.setText(text);
      }
    });
  }

  @Override
  public void setPortOpening(final boolean enabled) {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        JBAction.setText("Open Ports");
        JBAction.setEnabled(enabled);
        JBForceClose.setEnabled(enabled);
        JCBTCP.setEnabled(enabled);
        JCBUDP.setEnabled(enabled);
        JSIPort.setEnabled(enabled);
        JTFIPToForwardTo.setEnabled(enabled);
        JBAddPort.setEnabled(enabled);
        for (PortPanel panel : portPanelList) {
          panel.setPortOptionsEnabled(enabled);
        }
      }
    });
  }

  @Override
  public void setPortClosing(final boolean enabled) {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        JBAction.setText("Close Ports");
        JBAction.setEnabled(enabled);
        JBForceClose.setEnabled(false);
        JCBTCP.setEnabled(false);
        JCBUDP.setEnabled(false);
        JSIPort.setEnabled(false);
        JTFIPToForwardTo.setEnabled(false);
        JBAddPort.setEnabled(false);
        for (PortPanel panel : portPanelList) {
          panel.setPortOptionsEnabled(false);
        }
      }
    });
  }

  @Override
  public boolean exitRequested() {
    return exitRequested;
  }

  /**
   * Adds a port panel to the GUI and to
   *
   * @param panel
   */
  void addPortPanel(final PortPanel panel) {
    portPanelList.add(panel);
    JPPortPanels.add(panel);
    JPPortPanels.revalidate();
    pack();
  }

  void removePortPanel(PortPanel panel) {
    portPanelList.remove(panel);
    JPPortPanels.remove(panel);
    JPPortPanels.revalidate();
    pack();
  }

  /**
   * Validates the current user-inputted program settings. If any of the settings are invalid, this disables the Action button, otherwise this enables
   * it.
   */
  void validateProgramSettings() {
    // TODO: Verify port number
    if (!JCBTCP.isSelected() && !JCBUDP.isSelected()) {
      JBAction.setEnabled(false);
      return;
    }
    for (PortPanel panel : portPanelList) {
      if (!panel.checkBoxesValid()) {
        JBAction.setEnabled(false);
        return;
      }
    }
    JBAction.setEnabled(true);
  }

  private void continuePressed() {
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    JSIPort = new javax.swing.JSpinner();
    JBAction = new javax.swing.JButton();
    JLPortStatus = new javax.swing.JLabel();
    JCBTCP = new javax.swing.JCheckBox();
    JCBUDP = new javax.swing.JCheckBox();
    JPPortPanels = new javax.swing.JPanel();
    JBAddPort = new javax.swing.JButton();
    jLabel3 = new javax.swing.JLabel();
    JTFIPToForwardTo = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    JBForceClose = new javax.swing.JButton();
    JSEPort = new javax.swing.JSpinner();
    jLabel5 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setResizable(false);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowIconified(java.awt.event.WindowEvent evt) {
        formWindowIconified(evt);
      }
    });

    jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Portforwarding");

    jLabel2.setText("Internal Port");

    JSIPort.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));
    JSIPort.setEditor(new javax.swing.JSpinner.NumberEditor(JSIPort, "0"));

    JBAction.setText("GO!");
    JBAction.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBActionActionPerformed(evt);
      }
    });

    JLPortStatus.setText(" ");

    JCBTCP.setSelected(true);
    JCBTCP.setText("TCP");
    JCBTCP.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JCBTCPActionPerformed(evt);
      }
    });

    JCBUDP.setSelected(true);
    JCBUDP.setText("UDP");
    JCBUDP.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JCBUDPActionPerformed(evt);
      }
    });

    JPPortPanels.setLayout(new javax.swing.BoxLayout(JPPortPanels, javax.swing.BoxLayout.Y_AXIS));

    JBAddPort.setText("Add a Port");
    JBAddPort.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBAddPortActionPerformed(evt);
      }
    });

    jLabel3.setText("IP to Forward To:");

    jLabel4.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
    jLabel4.setText("(Defaults to this computer)");

    JBForceClose.setText("Force Close Ports");
    JBForceClose.setToolTipText("<html>\nTries to remove port mappings for listed ports,<br>\neven if they're not opened by this program.<br>\nThis can sometimes fix issues when the program<br>\nspits back a \"Cannot open Port X\" error.\n</html>");
    JBForceClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBForceCloseActionPerformed(evt);
      }
    });

    JSEPort.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));
    JSEPort.setEditor(new javax.swing.JSpinner.NumberEditor(JSEPort, "0"));

    jLabel5.setText("External Port");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(JLPortStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(JBAction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(JPPortPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(JBForceClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JTFIPToForwardTo, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JSIPort, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JSEPort, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(JCBTCP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(JCBUDP)
                .addGap(18, 18, 18)
                .addComponent(JBAddPort)))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(JSIPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(JCBTCP)
          .addComponent(JCBUDP)
          .addComponent(JBAddPort)
          .addComponent(JSEPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5))
        .addGap(0, 0, 0)
        .addComponent(JPPortPanels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(JTFIPToForwardTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel4))
        .addGap(18, 18, 18)
        .addComponent(JBAction)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JBForceClose)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(JLPortStatus)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void JBActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBActionActionPerformed
    //TODO: Check if settings are valid before proceeding, otherwise display error
    continuePressed();
  }//GEN-LAST:event_JBActionActionPerformed

  private void JCBTCPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JCBTCPActionPerformed
    // TODO add your handling code here:
    validateProgramSettings();
  }//GEN-LAST:event_JCBTCPActionPerformed

  private void JCBUDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JCBUDPActionPerformed
    // TODO add your handling code here:
    validateProgramSettings();
  }//GEN-LAST:event_JCBUDPActionPerformed

  private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
    // TODO add your handling code here:
    minimizeToTray();
  }//GEN-LAST:event_formWindowIconified

  private void JBAddPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBAddPortActionPerformed
    // TODO add your handling code here:
    addPortPanel(new PortPanel(this));
  }//GEN-LAST:event_JBAddPortActionPerformed

  private void JBForceCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBForceCloseActionPerformed
    // TODO add your handling code here:
    forceClose = true;
    continuePressed();
  }//GEN-LAST:event_JBForceCloseActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton JBAction;
  private javax.swing.JButton JBAddPort;
  private javax.swing.JButton JBForceClose;
  private javax.swing.JCheckBox JCBTCP;
  private javax.swing.JCheckBox JCBUDP;
  private volatile javax.swing.JLabel JLPortStatus;
  private javax.swing.JPanel JPPortPanels;
  private volatile javax.swing.JSpinner JSEPort;
  private volatile javax.swing.JSpinner JSIPort;
  private javax.swing.JTextField JTFIPToForwardTo;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  // End of variables declaration//GEN-END:variables
}
