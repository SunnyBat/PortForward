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

  public Port(int internalPort, int externalPort, boolean forwardTCP, boolean forwardUDP) {
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

}
