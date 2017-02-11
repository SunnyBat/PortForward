package com.github.sunnybat.portforward;

/**
 *
 * @author SunnyBat
 */
public class Port {

  private final int internalPort;
  private final int externalPort;
  private final boolean forwardTCP;
  private final boolean forwardUDP;

  /**
   * Creates a new Port.
   *
   * @param internalPort The internal port to forward to
   * @param externalPort The external port to forward
   * @param forwardTCP True to forward TCP, false to not
   * @param forwardUDP True to forward UDP, false to not
   */
  public Port(int internalPort, int externalPort, boolean forwardTCP, boolean forwardUDP) {
    if (!isValidPort(internalPort)) {
      throw new IllegalArgumentException("Invalid internal port");
    }
    if (!isValidPort(externalPort)) {
      throw new IllegalArgumentException("Invalid external port");
    }
    if (!forwardTCP && !forwardUDP) {
      throw new IllegalArgumentException("TCP and/or UDP must be forwarded");
    }
    this.internalPort = internalPort;
    this.externalPort = externalPort;
    this.forwardTCP = forwardTCP;
    this.forwardUDP = forwardUDP;
  }

  public int getInternalPort() {
    return internalPort;
  }

  public int getExternalPort() {
    return externalPort;
  }

  public boolean shouldForwardTPC() {
    return forwardTCP;
  }

  public boolean shouldForwardUDP() {
    return forwardUDP;
  }

  @Override
  public String toString() {
    return "internalPort = " + internalPort + " | externalPort = " + externalPort + " | TCP = " + forwardTCP + " | UDP = " + forwardUDP;
  }

  /**
   * Checks whether or not the given port number is valid.
   *
   * @param port The port number to check
   * @return True if valid, false if not
   */
  public static boolean isValidPort(int port) {
    return port > 0 && port < 65536;
  }

}
