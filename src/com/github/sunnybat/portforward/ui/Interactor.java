package com.github.sunnybat.portforward.ui;

import com.github.sunnybat.portforward.Port;
import java.util.List;

/**
 *
 * @author SunnyBat
 */
public interface Interactor {

  /**
   * Gets the local IP address to forward ports to. This may or may not be a valid IP address.
   *
   * @return The local IP address to forward to
   */
  public String getIPToForward();

  /**
   * Gets all the Ports to forward to. This may return any type of List.
   *
   * @return All the Ports to forward to
   */
  public List<Port> getPortsToForward();

  /**
   * Sets whether or not the user should be able to change port values and open the input ports.
   *
   * @param enabled True to enable input, false to disable
   */
  public void setPortOpening(boolean enabled);

  /**
   * Sets whether or not the user should be able to close the ports. This disables changing port values no matter what.
   *
   * @param enabled True to enable closing ports, false to disable
   */
  public void setPortClosing(boolean enabled);

  /**
   * Updates the user on the current status of the program.
   *
   * @param notification The status to display
   */
  public void updateStatus(String notification);

  /**
   * Waits for the user to tell the program to proceed with the next action. This will block until the user proceeds.
   */
  public void waitForAction();

  /**
   * Checks whether or not the user has told the program to exit.
   *
   * @return True to exit, false to continue
   */
  public boolean exitRequested();

}
