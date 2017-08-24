package com.team341.frc2017.vision;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONArray;
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

  private ArrayList<VisionUpdateReceiver> mReceivers;

  public VisionServerThread(Socket socket, ArrayList<VisionUpdateReceiver> receivers) {
    mReceivers = receivers;
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
        String[] splitMessages = (new String(buffer, 0, read)).split("\n");
        //Thread.sleep(1000);

        for (String line : splitMessages) {
          //System.out.print("Read " + read + " bytes:  " + line + "\n");
          long now = System.nanoTime();
          //System.out.println((now - lastReceivedHeartbeatTime) / 1e9);
          lastReceivedHeartbeatTime = now;

          try {
            JSONParser jsonParser = new JSONParser();
            JSONObject j = (JSONObject) jsonParser.parse(line);
            if ("heartbeat".equals(j.get("type"))) {
              mSocket.getOutputStream().write((line + "\n").getBytes());
              mSocket.getOutputStream().flush();
            } else if ("VisionReport".equals(j.get("type"))) {
              JSONObject message = (JSONObject) jsonParser.parse((String) j.get("message"));
              JSONArray arr = (JSONArray) message.get("targets");


              VisionReport report = new VisionReport();

              for (int i = 0; i < arr.size(); i++) {
                JSONObject jsonTarget = (JSONObject) arr.get(i);
                Target target = new Target();
                target.setRange((double) jsonTarget.get("range"));
                target.setAzimuth((double) jsonTarget.get("azimuth"));
                target.setWidth((double) jsonTarget.get("width"));
                target.setHeight((double) jsonTarget.get("height"));
                report.addTarget(target);
              }

              for (VisionUpdateReceiver receiver : mReceivers) {
                receiver.updateReceived(report);
              }
            }
          } catch (ParseException e) {
            e.printStackTrace();
          }
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

