package com.team341.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by joshs on 7/26/2017.
 */

public class VisionServerThread implements Runnable {

  private int mId;
  private Socket mSocket;
  private boolean mRunning;

  public VisionServerThread(Socket socket, int id) {
    mSocket = socket;
    mRunning = true;
    mId = id;
  }

  @Override
  public void run() {
    int read;
    InputStream is = null;
    byte[] buffer = new byte[2048];
    try {
      is = mSocket.getInputStream();

      while (mSocket.isConnected() && (read = is.read(buffer)) != -1) {
        try {
          System.out.println("ID: " + mId + " is running");
          System.out.println(new String(buffer));
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
