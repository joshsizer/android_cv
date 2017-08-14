package com.team341.server;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class that represents the android debug bridge. You can access a few ADB commands from this
 * interface. Namely, you can reverse port forward, start, and stop the ADB server.
 *
 * For more information, see https://developer.android.com/studio/command-line/adb.html
 *
 * @author Joshua Sizer
 */
public class ADB {

  /**
   * There should only ever be one ADB running at a time (infact, this is the case on the
   * RoboRIO, only one instance can be running at a time).
   */
  private static ADB instance = new ADB();

  /**
   * This is where the ADB binary is located on the RoboRIO
   */
  //private Path binPath = Paths.get("/Users/josh/Library/Android/sdk/platform-tools/adb");
  private Path binPath = Paths.get("C:\\Users\\joshs\\AppData\\Local\\Android\\sdk\\platform"
          + "-tools\\adb.exe");

  /**
   * @return the only instance of this class
   */
  public static ADB getInstance() {
    return instance;
  }

  private boolean consoleOut = false;

  /**
   * So this class cannot be instantiated
   */
  private ADB() {

  }

  public void setConsoleOut(boolean out) {
    consoleOut = out;
  }
  /**
   * Executes an ADB command on the RoboRIO shell
   *
   * @param args The command line arguments to ADB
   * @return true for success, false for failure
   */
  private boolean execCommand(String args) {
    Runtime runtime = Runtime.getRuntime();

    String command = binPath.toString() + " " + args;
    if (consoleOut) {
      System.out.println("Executing command:\n\t" + command);
    }

    try {
      Process process = runtime.exec(command);
      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Executes an ADB command, and outputs a success or failure message to the console
   *
   * @param args the command line arguments for the adb command
   * @param successMssg The message to print in the case of success
   * @param failureMssg The message to print in the case of failure
   * @return true for success, false for failure
   */
  private boolean execCommandConsoleOut(String args, String successMssg, String failureMssg) {
    boolean success = execCommand(args);
    String mssg;

    if (success) {
      mssg = successMssg;
    } else {
      mssg = failureMssg;
    }
    System.out.println(mssg);

    return success;
  }

  /**
   * Starts the ADB server
   *
   * @return true for success, false for failure
   */
  public boolean start() {
    if (consoleOut) {
      return execCommandConsoleOut("start-server", "Starting ADB server",
                "Could not start ADB server");
    } else {
      return execCommand("start-server");
    }
  }

  /**
   * Stops the ADB server
   *
   * @return true for success, false for failure
   */
  public boolean stop() {
    if (consoleOut) {
      return execCommandConsoleOut("kill-server", "Stopping ADB server",
              "Could not stop ADB server");
    } else {
      return execCommand("kill-server");
    }
  }

  /**
   * Restarts the ADB server
   *
   * @return true for success, false for failure
   */
  public boolean restart() {
    if (consoleOut) {
      System.out.println("Attempting to restart the ADB server");
    }

    boolean stop = stop();
    boolean start = start();

    return stop && start;
  }

  /**
   * Port forwards the two specified ports
   *
   * @return true for success, false for failure
   */
  public boolean portFoward(int port1, int port2) {
    if (consoleOut) {
      return execCommandConsoleOut("forward tcp:" + port1 + " tcp:" + port2, "Port "
          + "forwarded tcp:" + port1 + " and tcp:" + port2, "Could not port "
          + "forward tcp:" + port1 + " and tcp:" + port2);
    } else {
      return execCommand("forward tcp:" + port1 + " tcp:" + port2);
    }
  }

  /**
   * Reverse port forwards the two specified ports
   *
   * @return true for success, false for failure
   */
  public boolean reversePortFoward(int port1, int port2) {
    if (consoleOut) {
      return execCommandConsoleOut("reverse tcp:" + port1 + " tcp:" + port2, "Reverse port "
              + "forwarded tcp:" + port1 + " and tcp:" + port2, "Could not reverse port "
              + "forward tcp:" + port1 + " and tcp:" + port2);
    } else {
      return execCommand("reverse tcp:" + port1 + " tcp:" + port2);
    }
  }
}
