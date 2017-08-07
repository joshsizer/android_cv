package com.team341.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The threads that are spawned by the vision server to handle new socket connections.
 *
 * @author Joshua Sizer
 */
public class VisionServerThread implements Runnable {

  private Socket mSocket;
  private long lastReceivedHeartbeatTime;

  public VisionServerThread(Socket socket) {
    mSocket = socket;
    System.out.println("Created new vision server thread");
  }

  /**
   * Reads from the socket for strings, which are formated as JSON strings
   */
  @Override
  public void run() {
    try {
      int read;
      InputStream is = null;
      byte[] buffer = new byte[2048];

      is = mSocket.getInputStream();

      while (mSocket.isConnected() && (read = is.read(buffer)) != -1) {
        String[] blah = (new String(buffer)).split("\n");
        //Thread.sleep(1000);
        System.out.print("Read " + read + " bytes:  " + blah[0] + "\n");
        long now = System.nanoTime();
        System.out.println((now - lastReceivedHeartbeatTime) / 1e9);
        lastReceivedHeartbeatTime = now;

        try {
          JSONParser jsonParser = new JSONParser();
          JSONObject j = (JSONObject) jsonParser.parse(blah[0]);
          if ("heartbeat".equals((String) j.get("type"))) {
            mSocket.getOutputStream().write((blah[0] + "\n").getBytes());
            mSocket.getOutputStream().flush();
          }
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }

      System.out.println("Vision client socket disconnected!");
    } catch (IOException e) {
      e.printStackTrace();
    } /* catch (InterruptedException e) {
      e.printStackTrace();
    }*/
  }
}
