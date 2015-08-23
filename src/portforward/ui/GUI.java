package portforward.ui;

import portforward.Port;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.*;
import portforward.PortForward;
import portforward.UPnPManager;

/**
 *
 * @author Sunnybat
 */
public class GUI extends javax.swing.JFrame implements Interactor {

  private SystemTray tray;
  private TrayIcon myIcon;
  private java.awt.Image myImage;
  private IconMenu menu;
  private final List<PortPanel> portPanelList = new ArrayList<>();

  /**
   * Creates new form GUI
   *
   * @param defaultIP The default IP address to forward to
   */
  public GUI(String defaultIP) {
    initComponents();
    customComponents();
    JTFIPToForwardTo.setText(defaultIP);
  }

  private void customComponents() {
    this.setLocationRelativeTo(null);
    tray = SystemTray.getSystemTray();
    menu = new IconMenu(this);
    try {
      myImage = javax.imageio.ImageIO.read(PortForward.class.getResourceAsStream("/resources/Icon.png"));
      setIconImage(myImage);
      myIcon = new TrayIcon(myImage, "Portfowarding Tool", menu);
      myIcon.setImageAutoSize(true);
      myIcon.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          maximizeWindow();
        }
      });
      setVisible(true);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
    ((javax.swing.JSpinner.NumberEditor) JSPort.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
  }

  public void minimizeWindow() {
    try {
      JSPort.commitEdit(); // Update the spinner value. If invalid, uses the last known valid value (keypresses don't update the last known value)
      // TODO: Add open/close button
      tray.add(myIcon);
    } catch (Exception e) {
      e.printStackTrace();
    }
    setVisible(false);
  }

  public void maximizeWindow() {
    setExtendedState(javax.swing.JFrame.NORMAL);
    setVisible(true);
    this.setLocationRelativeTo(null);
    this.toFront();
    tray.remove(myIcon);
  }

  @Override
  public void dispose() {
    tray.remove(myIcon);
    super.dispose();
    PortForward.exitProgram();
    synchronized (this) {
      this.notify();
    }
  }

  @Override
  public String getIPToForward() {
    return JTFIPToForwardTo.getText();
  }

  @Override
  public List<Port> getPortsToForward() {
    List<Port> ports = new ArrayList<>();
    Port mainPort = new Port((int) JSPort.getValue(), JCBTCP.isSelected(), JCBUDP.isSelected());
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
  public void updateStatus(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JLPortStatus.setText(text);
      }
    });
  }

  @Override
  public void setPortOpening(final boolean enabled) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JBAction.setText("Open Ports");
        JBAction.setEnabled(enabled);
        JCBTCP.setEnabled(enabled);
        JCBUDP.setEnabled(enabled);
        JSPort.setEnabled(enabled);
        JBAddPort.setEnabled(enabled);
        for (PortPanel panel : portPanelList) {
          panel.setPortOptionsEnabled(enabled);
        }
      }
    });
  }

  @Override
  public void setPortClosing(final boolean enabled) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JBAction.setText("Close Ports");
        JBAction.setEnabled(enabled);
        JCBTCP.setEnabled(false);
        JCBUDP.setEnabled(false);
        JSPort.setEnabled(false);
        JBAddPort.setEnabled(false);
        for (PortPanel panel : portPanelList) {
          panel.setPortOptionsEnabled(false);
        }
      }
    });
  }

  protected void addPortPanel(final PortPanel panel) {
    portPanelList.add(panel);
    JPPortPanels.add(panel);
    JPPortPanels.revalidate();
    pack();
  }

  protected void removePortPanel(PortPanel panel) {
    portPanelList.remove(panel);
    JPPortPanels.remove(panel);
    JPPortPanels.revalidate();
    pack();
  }

  /**
   * Validates the current user-inputted program settings. If any of the settings are invalid, this disables the Action button, otherwise this enables
   * it.
   */
  protected void validateProgramSettings() {
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

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    JSPort = new javax.swing.JSpinner();
    JBAction = new javax.swing.JButton();
    JLPortStatus = new javax.swing.JLabel();
    JCBTCP = new javax.swing.JCheckBox();
    JCBUDP = new javax.swing.JCheckBox();
    JPPortPanels = new javax.swing.JPanel();
    JBAddPort = new javax.swing.JButton();
    jLabel3 = new javax.swing.JLabel();
    JTFIPToForwardTo = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();

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

    jLabel2.setText("Port to Portforward:");

    JSPort.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));
    JSPort.setEditor(new javax.swing.JSpinner.NumberEditor(JSPort, "0"));

    JBAction.setText("GO!");
    JBAction.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBActionActionPerformed(evt);
      }
    });

    JLPortStatus.setText("[Port Status]");

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
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(JSPort, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(JCBTCP)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(JCBUDP)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(JBAddPort, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
          .addComponent(JPPortPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(JTFIPToForwardTo, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel4)
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
          .addComponent(JSPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(JCBTCP)
          .addComponent(JCBUDP)
          .addComponent(JBAddPort))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(JPPortPanels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(JTFIPToForwardTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel4))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(JLPortStatus)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(JBAction)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void JBActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBActionActionPerformed
    //TODO: Check if settings are valid before proceeding, otherwise display error
    synchronized (this) {
      this.notify();
    }
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
    minimizeWindow();
  }//GEN-LAST:event_formWindowIconified

  private void JBAddPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBAddPortActionPerformed
    // TODO add your handling code here:
    addPortPanel(new PortPanel(this));
  }//GEN-LAST:event_JBAddPortActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton JBAction;
  private javax.swing.JButton JBAddPort;
  private javax.swing.JCheckBox JCBTCP;
  private javax.swing.JCheckBox JCBUDP;
  private volatile javax.swing.JLabel JLPortStatus;
  private javax.swing.JPanel JPPortPanels;
  private volatile javax.swing.JSpinner JSPort;
  private javax.swing.JTextField JTFIPToForwardTo;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  // End of variables declaration//GEN-END:variables
}
