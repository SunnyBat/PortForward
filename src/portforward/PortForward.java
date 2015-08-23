package portforward;

/**
 *
 * @author Sunnybat
 */
public class PortForward {

  private static GUI myGUI;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    myGUI = new GUI();
    myGUI.setVisible(true);
  }

  public static void exitProgram() {
    myGUI.dispose();
  }

  public static void showProgramWindow() {
    myGUI.maximizeWindow();
  }

  public static void UPnPFinished(String message) {
    if (message != null) {
      setGUIPortStatusText(message);
    }
    myGUI.setButtonEnabled(true);
    //myGUI.callButtonAction();
    if (myGUI.isDisplayable()) {
      myGUI.maximizeWindow();
    }
    myGUI.setPortOptionsEnabled(true);
  }

  public static void setGUIPortStatusText(String text) {
    myGUI.setPortStatusText("Port Status: " + text);
  }

  public static void setGUIButtonEnabled(boolean enabled) {
    myGUI.setButtonEnabled(enabled);
  }

  public static void setPortOptionsEnabled(Boolean enabled) {
    myGUI.setPortOptionsEnabled(enabled);
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
