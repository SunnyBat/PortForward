package com.github.sunnybat.portforward.ui;

import com.github.sunnybat.portforward.Port;

/**
 *
 * @author Sunnybat
 */
public class PortPanel extends javax.swing.JPanel {

  private final GUI myGUI;

  /**
   * Creates new form PortPanel
   *
   * @param gui The GUI object that this panel has been added to
   */
  public PortPanel(GUI gui) {
    initComponents();
    myGUI = gui;
    ((javax.swing.JSpinner.NumberEditor) JSIPort.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
  }

  /**
   * Checks whether or not this PortPanel's checkboxes are valid
   *
   * @return True if they are valid, false if not
   */
  public boolean checkBoxesValid() {
    return JCBTCP.isSelected() || JCBUDP.isSelected();
  }

  /**
   * Enables or disabled modification of the current values in this PortPanel.
   *
   * @param enable True to enable, false to disable
   */
  public void setPortOptionsEnabled(boolean enable) {
    JCBTCP.setEnabled(enable);
    JCBUDP.setEnabled(enable);
    JBRemove.setEnabled(enable);
    JSIPort.setEnabled(enable);
  }

  /**
   * Gets the Port currently specified in this PortPanel. This will never return null.
   *
   * @return The Port specified
   */
  public Port getPort() {
    return new Port((int) JSIPort.getValue(), (int) JSEPort.getValue(), JCBTCP.isSelected(), JCBUDP.isSelected());
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel2 = new javax.swing.JLabel();
    JSIPort = new javax.swing.JSpinner();
    JCBTCP = new javax.swing.JCheckBox();
    JCBUDP = new javax.swing.JCheckBox();
    JBRemove = new javax.swing.JButton();
    jLabel3 = new javax.swing.JLabel();
    JSEPort = new javax.swing.JSpinner();

    jLabel2.setText("Internal Port");

    JSIPort.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));
    JSIPort.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    JSIPort.setEditor(new javax.swing.JSpinner.NumberEditor(JSIPort, "0"));

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

    JBRemove.setText("Remove Port");
    JBRemove.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBRemoveActionPerformed(evt);
      }
    });

    jLabel3.setText("External Port");

    JSEPort.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));
    JSEPort.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    JSEPort.setEditor(new javax.swing.JSpinner.NumberEditor(JSEPort, "0"));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JSIPort, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JSEPort, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
        .addComponent(JCBTCP)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(JCBUDP)
        .addGap(18, 18, 18)
        .addComponent(JBRemove))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jLabel3)
        .addComponent(JSEPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jLabel2)
        .addComponent(JSIPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(JCBTCP)
        .addComponent(JCBUDP)
        .addComponent(JBRemove))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void JCBTCPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JCBTCPActionPerformed
    // TODO add your handling code here:
    myGUI.validateProgramSettings();
  }//GEN-LAST:event_JCBTCPActionPerformed

  private void JCBUDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JCBUDPActionPerformed
    // TODO add your handling code here:
    myGUI.validateProgramSettings();
  }//GEN-LAST:event_JCBUDPActionPerformed

  private void JBRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBRemoveActionPerformed
    // TODO add your handling code here:
    myGUI.removePortPanel(this);
  }//GEN-LAST:event_JBRemoveActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton JBRemove;
  public javax.swing.JCheckBox JCBTCP;
  public javax.swing.JCheckBox JCBUDP;
  public javax.swing.JSpinner JSEPort;
  public javax.swing.JSpinner JSIPort;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  // End of variables declaration//GEN-END:variables
}
