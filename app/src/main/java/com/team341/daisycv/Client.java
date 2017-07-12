package com.team341.daisycv;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Joshua Sizer on 5/19/17.
 *
 * A simple client, which attempts to connect to a server at the specified hostname and port.
 * A reverse port forward is needed to forward all data from the specified port on the phone to the
 * port the server is operating on (on the other end of the android debug bridge). If a
 * java.net.ConnectException is thrown, it is likely that the port forwarding is not set up
 * correctly. To fix this, apply the command: "adb reverse tcp:8341 tcp:8341" without the quotes,
 * and replace 8341 with the correct port to forward
 */

public class Client {

  public static final String LOGTAG = "Client";

  private Socket mSocket;
  private boolean mConnected;
  private boolean mEnabled;
  private final String mHostName;
  private final int mPort;
  private Thread mConnectionThread, mWriteThread, mReadThread;

  private static final int kHeartBeatPeriod = 100; //ms
  private static final int kMaxAcceptableHeartBeatPeriod = 300; //ms

  private static final int kConnectionThreadSleep = 100; //ms
  private static final int kWriteThreadSleep = 10;
  private static final int kReadThreadSleep = 100;

  public Client(String hostName, int port) {
    mHostName = hostName;
    mPort = port;
    mEnabled = true;
  }

  public synchronized void start() {
    mEnabled = true;
    mConnectionThread = new Thread(new ConnectionThread());

    mConnectionThread.start();
  }

  public synchronized void stop() {
    mEnabled = false;
    mConnected = false;
    try {
      mConnectionThread.join();
      //mWriteThread.join();
      //mReadThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    try {
      if (mSocket != null) {
        mSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    mSocket = null;
  }

  protected class ConnectionThread implements Runnable {

    @Override
    public void run() {
      while (mEnabled) {
        try {
          if (mSocket == null || (!mConnected && !mSocket.isConnected())) {
            tryConnecting();
          } else {
            Log.i("Client", "STATUS: CONNECTED");
          }
          Thread.sleep(kConnectionThreadSleep);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  protected class WriteThread implements Runnable {

    @Override
    public void run() {

    }
  }

  protected class ReadThread implements Runnable {

    @Override
    public void run() {

    }
  }

  /**
   * Attempts to create a socket with the specified hostname and port.
   */
  private synchronized void tryConnecting() {
    try {
      if (mSocket == null) {
        mSocket = new Socket(mHostName, mPort);
        mConnected = mSocket.isConnected();
        Log.i(LOGTAG,
            "Connected to " + mSocket.getInetAddress() + " on port " + mSocket.getPort());
      }
    } catch (IOException e) {
      Log.e(LOGTAG, "Cannot connect to server!");
    }
  }
}
