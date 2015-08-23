package portforward.ui;

import portforward.Port;

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
    ((javax.swing.JSpinner.NumberEditor) JSPortNumber.getEditor()).getTextField().setDisabledTextColor(java.awt.Color.BLACK);
  }

  public boolean checkBoxesValid() {
    return JCBTCP.isSelected() || JCBUDP.isSelected();
  }

  public void setPortOptionsEnabled(boolean enable) {
    JCBTCP.setEnabled(enable);
    JCBUDP.setEnabled(enable);
    JBRemove.setEnabled(enable);
    JSPortNumber.setEnabled(enable);
  }

  public Port getPort() {
    return new Port((int) JSPortNumber.getValue(), JCBTCP.isSelected(), JCBUDP.isSelected());
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel2 = new javax.swing.JLabel();
    JSPortNumber = new javax.swing.JSpinner();
    JCBTCP = new javax.swing.JCheckBox();
    JCBUDP = new javax.swing.JCheckBox();
    JBRemove = new javax.swing.JButton();

    jLabel2.setText("Port to Portforward:");

    JSPortNumber.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));
    JSPortNumber.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    JSPortNumber.setEditor(new javax.swing.JSpinner.NumberEditor(JSPortNumber, "0"));

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

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JSPortNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JCBTCP)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(JCBUDP)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JBRemove))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jLabel2)
        .addComponent(JSPortNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
  public javax.swing.JSpinner JSPortNumber;
  private javax.swing.JLabel jLabel2;
  // End of variables declaration//GEN-END:variables
}