package com.team341.frc2017.vision;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This handles all of the socket connections from the a vision client. Each socket will have its
 * own thread that constantly handles messages it receives. VisionUpdateReceivers can be registered,
 * and their onUpdate() method will be called.
 *
 * @author Joshua Sizer
 */
public class VisionServer implements Runnable {

  private ServerSocket mServerSocket;
  private ArrayList<VisionServerThread> mServerThreads;
  private ArrayList<VisionUpdateReceiver> mReceivers;


  /**
   * Attempts to bind a server socket to the specified port
   *
   * @param port The port the client will attempt to connect to
   */
  public VisionServer(int port) {
    mServerThreads = new ArrayList<>();
    mReceivers = new ArrayList<>();

    try {
      mServerSocket = new ServerSocket(port);
      mServerSocket.setSoTimeout(500);
      ADB.getInstance().start();
      ADB.getInstance().reversePortFoward(8341, 8341);
      new Thread(this).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Awaits for new socket connections to the server, and spawns a server thread to handle
   * incoming messages
   */
  @Override
  public void run() {
    while (true) {
      try {
        ADB.getInstance().start();
        ADB.getInstance().reversePortFoward(8341, 8341);
        Socket currentSocket = mServerSocket.accept();
        VisionServerThread currentServerThread = new VisionServerThread(currentSocket, mReceivers);
        mServerThreads.add(currentServerThread);
        new Thread(currentServerThread, "VServerThread " + mServerThreads.size()).start();
      } catch (IOException e) {
        //System.out.println("IOException on VisionServer");
      }
    }
  }

  public static void main(String[] args) {
    VisionServer vs = new VisionServer(8341);
    vs.registerReceiver(new TestReceiver());
  }

  public void registerReceiver(VisionUpdateReceiver receiver) {
    mReceivers.add(receiver);
  }
}
