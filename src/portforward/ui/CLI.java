package portforward.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import portforward.Port;
import portforward.PortForward;

/**
 *
 * @author SunnyBat
 */
public class CLI implements Interactor {

  private List<Port> portsToOpen = new ArrayList<>();
  private Scanner in = new Scanner(System.in);
  private String forwardIP;

  @Override
  public String getIPToForward() {
    return null; // TODO: Return proper IP
  }

  @Override
  public List<Port> getPortsToForward() {
    return portsToOpen;
  }

  @Override
  public void waitForAction() {
    while (true) {
      String next = in.nextLine();
      parseCommand(next);
    }
  }

  private void parseCommand(String command) { // In case we need to recurse
    switch (command.toLowerCase()) {
      case "open":
        if (portsToOpen.isEmpty()) {
          System.out.println("You must specify ports to open before opening them!");
        } else {
          return;
        }
        break;
      case "close":
        return;
      case "addport":
        System.out.print("Port number (1-65535)? ");
        int port = promptForPortNumber();
        System.out.print("Forward TCP (Y/N)? ");
        boolean tcp = parseYesNo(in.nextLine(), true);
        System.out.print("Forward UDP (Y/N)? ");
        boolean udp = parseYesNo(in.nextLine(), true);
        removePort(port);
        portsToOpen.add(new Port(port, tcp, udp));
        break;
      case "removeport":
        parseCommand("listports");
        System.out.println();
        System.out.println("Port number to remove (1-65535)? ");
        int portNumber = promptForPortNumber();
        removePort(portNumber);
        break;
      case "clearports":
        System.out.println("Confirm clear ports (Y/N): ");
        if (parseYesNo(in.nextLine(), false)) {
          portsToOpen.clear();
          System.out.println("Removed all ports.");
        } else {
          System.out.println("Did not remove any ports.");
        }
        break;
      case "listports":
        System.out.println("Current Ports:");
        for (Port p : portsToOpen) {
          System.out.println(p);
        }
        break;
      case "setip":
        System.out.println("IP address to forward to? ");
        String ip = in.nextLine();
        if (!ip.contains(".")) {
          System.out.println("Please enter a valid IP address.");
          parseCommand(command);
        }
        forwardIP = ip;
        break;
      case "listip":
        System.out.println("Forwarding To: " + forwardIP);
        break;
      case "exit":
        PortForward.exitProgram();
        return;
      default:
        System.out.println("Unknown command!");
        System.out.println();
        System.out.println("Currently supported commands are as follows:");
        System.out.println("Open           -- Opens the currently added ports if closed");
        System.out.println("Close          -- Closes the currently added ports if open");
        System.out.println("AddPort        -- Adds a port to the program (does not open automatically)");
        System.out.println("RemovePort     -- Removes a port from the program (only when ports are closed)");
        System.out.println("ClearPorts     -- Removes all ports from the program (only when ports are closed)");
        System.out.println("ListPorts      -- Lists all the ports currently in the program");
        System.out.println("SetIP          -- Sets the internal IP address to forward ports to (defaults to this computer)");
        System.out.println("ListIP         -- Lists the current IP address to forward ports to");
        System.out.println("Exit           -- Exits the program and closes the ports if they are open");
        System.out.println("These commands are NOT case-sensitive.");
        break;
    }
    System.out.println();
    System.out.println();
  }

  private int promptForPortNumber() {
    try {
      int num = Integer.parseInt(in.nextLine());
      if (num < 1 || num > 65535) {
        System.out.println("You must enter a valid port number!");
        return promptForPortNumber();
      } else {
        return num;
      }
    } catch (NumberFormatException e) {
      System.out.println("You must enter a valid port number!");
      return promptForPortNumber();
    }
  }

  private void removePort(int portNum) {
    for (Port p : portsToOpen) {
      if (p.getPort() == portNum) {
        portsToOpen.remove(p);
        break;
      }
    }
  }

  private boolean parseYesNo(String parse, boolean unknownReturn) {
    if (parse == null || parse.length() == 0) {
      return unknownReturn;
    } else {
      char letter = parse.charAt(0);
      if (letter == 'n' || letter == 'N') {
        return false;
      } else if (letter == 'y' || letter == 'Y') {
        return true;
      } else {
        return unknownReturn;
      }
    }
  }

  @Override
  public void updateStatus(String text) {
    System.out.println(text);
  }

  @Override
  public void setPortOpening(final boolean enabled) {
  }

  @Override
  public void setPortClosing(final boolean enabled) {
  }

}
