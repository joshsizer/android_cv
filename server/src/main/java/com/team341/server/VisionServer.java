package com.team341.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;

/**
 * This handles all of the socket connections from the a vision client. Each socket will have its
 * own thread that constantly handles messages it receives. VisionUpdateReceivers can be registered,
 * and their onUpdate() method will be called.
 */
public class VisionServer implements Runnable {

  private ServerSocket mServerSocket;
  private int mPort;
  private int numServerThreads;

  private boolean mRunning;

  public VisionServer(int port) {
    mPort = port;
    numServerThreads = 0;

    try {
      mServerSocket = new ServerSocket(port);
      mRunning = true;
      new Thread(this).run();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (mRunning) {
      try {
        Socket currentSocket = mServerSocket.accept();
        if (currentSocket.isConnected()) {
          numServerThreads++;
        }
        VisionServerThread currentServerThread = new VisionServerThread(currentSocket, numServerThreads);
        new Thread(currentServerThread).start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    VisionServer vs = new VisionServer(8341);
  }
}
