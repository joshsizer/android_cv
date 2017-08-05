package com.team341.daisycv.communication;

import android.content.Intent;
import android.util.Log;
import com.team341.daisycv.ApplicationContext;
import com.team341.daisycv.communication.JsonSerializer;
import com.team341.daisycv.communication.messages.HeartbeatMessage;
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

  private static final long kHeartBeatPeriod = 100; //ms
  private static final long kMaxAcceptableHeartBeatPeriod = 300; //ms
  private long lastSentHeartbeatTime;

  private static final int kConnectionThreadSleep = 100; //ms
  private static final int kWriteThreadSleep = 100;
  private static final int kReadThreadSleep = 100;

  public Client(String hostName, int port) {
    mHostName = hostName;
    mPort = port;
    mEnabled = true;
  }

  public synchronized void start() {
    mEnabled = true;

    mConnectionThread = new Thread(new ConnectionThread());
    mWriteThread = new Thread(new WriteThread());

    mConnectionThread.start();
    mWriteThread.start();
  }

  public synchronized void stop() {
    mEnabled = false;
    mConnected = false;
    try {
      mConnectionThread.join();
      mWriteThread.join();
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
      while (mEnabled) {
        try {
          if (mConnected) {
            long now = System.nanoTime();
            if ((now - lastSentHeartbeatTime) / 1e6 > kHeartBeatPeriod) {
              sendString(JsonSerializer.toJson(HeartbeatMessage.getInstance()) + "\n");
              lastSentHeartbeatTime = now;
            }
          }
          Thread.sleep(kWriteThreadSleep);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
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
    Intent broadcastConnected = new Intent("com.team341.daiscv.ROBOT_CONNECTED");
    try {
      if (mSocket == null || !mConnected) {
        Log.i(LOGTAG, "Attempting to connect to " + mHostName + ":" + mPort);
        mSocket = new Socket(mHostName, mPort);
        mConnected = mSocket.isConnected();

        Log.i(LOGTAG,
            "Connected to " + mSocket.getInetAddress() + ":" + mSocket.getPort());
        broadcastConnected.putExtra("connected", true);
      }
    } catch (IOException e) {
      Log.e(LOGTAG, "Cannot connect to server!");
      broadcastConnected.putExtra("connected", false);
    }
    ApplicationContext.get().sendBroadcast(broadcastConnected);
  }

  public void sendString(String message) {
    try {
      mSocket.getOutputStream().write(message.getBytes());
      mSocket.getOutputStream().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
