package com.github.sunnybat.portforward;

/**
 *
 * @author SunnyBat
 */
public class Port {

  private final int portNumber;
  private final boolean forwardTCP;
  private final boolean forwardUDP;

  public Port(int portNumber, boolean forwardTCP, boolean forwardUDP) {
    this.portNumber = portNumber;
    this.forwardTCP = forwardTCP;
    this.forwardUDP = forwardUDP;
  }

  public int getPort() {
    return portNumber;
  }

  public boolean shouldForwardTPC() {
    return forwardTCP;
  }

  public boolean shouldForwardUDP() {
    return forwardUDP;
  }

  @Override
  public String toString() {
    return "Port = " + portNumber + " | TCP = " + forwardTCP + " | UDP = " + forwardUDP;
  }

}
