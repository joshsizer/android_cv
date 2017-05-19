package com.team341.daisycv;

import android.util.Log;
import java.io.IOException;
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
  private Thread mConnectionThread;

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

  protected class ConnectionThread implements Runnable {

    @Override
    public void run() {
      while (mEnabled) {
        try {
          if (mSocket == null || (!mConnected && !mSocket.isConnected())) {
            tryConnecting();
            Thread.sleep(100);
          } else {
            Log.i("Client", "STATUS: CONNECTED");
            Thread.sleep(5000);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
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
      e.printStackTrace();
      mConnected = false;
    }
  }
}
