package com.team341.daisycv.communication.client;

import android.content.Intent;
import android.provider.Settings.System;
import android.util.Log;
import com.team341.daisycv.ApplicationContext;
import com.team341.daisycv.communication.messages.JsonSerializable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by joshs on 8/6/2017.
 */

public class Client {

  public static final String LOGTAG = "Client";

  private ClientThread mConnectionThread, mWriteThread, mReadThread;
  private ArrayBlockingQueue<JsonSerializable> mToSend;
  private Socket mSocket;
  private boolean mEnabled, mConnected;
  private String mHostname;
  private int mPort;

  public Client(String hostname, int port) {
    mHostname = hostname;
    mPort = port;
    mToSend = new ArrayBlockingQueue<>(20);
  }

  synchronized public void start() {
    Log.i(LOGTAG, "start()");

    mConnectionThread = new ConnectionThread(this);
    mWriteThread = new WriteThread(this);
    mReadThread = new ReadThread(this);

    mEnabled = true;

    mConnectionThread.start();
    mWriteThread.start();
    mReadThread.start();
  }

  public synchronized void stop() {
    Log.i(LOGTAG, "stop()");

    mEnabled = false;
    mConnected = false;

    if (mConnectionThread != null && mConnectionThread.isAlive()) {
      try {
        mConnectionThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    if (mWriteThread != null && mWriteThread.isAlive()) {
      try {
        mWriteThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    if (mReadThread != null && mReadThread.isAlive()) {
      try {
        mReadThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
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

  protected boolean isEnabled() {
    return mEnabled;
  }

  protected boolean isConnected() {
    return mConnected;
  }

  synchronized protected void notifyConnected() {
    mConnected = true;
    mConnectionThread.clientConnected();
    mWriteThread.clientConnected();
    mReadThread.clientConnected();
  }

  protected void notifyDisconnected() {
    mConnected = false;
    mConnectionThread.clientDisconnected();
    mWriteThread.clientDisconnected();
    mReadThread.clientDisconnected();
  }

  synchronized public void updateLastReceivedHeartbeatTime(long time) {
    ((ConnectionThread) mConnectionThread).updateLastReceivedHeartbeatTime(time);
  }

  /**
   * Attempts to create a socket with the specified hostname and port.
   */
  synchronized protected void tryConnecting() {
    Log.i(LOGTAG, "tryConnecting()");
    try {
      if (mSocket == null || !mSocket.isConnected()) {
        Log.i(LOGTAG, "Attempting to connect to " + mHostname + ":" + mPort);
        mSocket = new Socket(mHostname, mPort);
        notifyConnected();

        Log.i(LOGTAG,
            "Connected to " + mSocket.getInetAddress() + ":" + mSocket.getPort());
      }
    } catch (IOException e) {
      Log.e(LOGTAG, "Cannot connect to server!");
    }
  }

  synchronized protected ArrayBlockingQueue<JsonSerializable> getMessageQueue() {
    return mToSend;
  }

  synchronized protected Socket getSocket() {
    return mSocket;
  }

  synchronized protected void setSocketNull() {
    mSocket = null;
  }

  protected void broadcastConnected() {
    Intent broadcastConnected = new Intent("com.team341.daisycv.ROBOT_CONNECTED");
    ApplicationContext.get().sendBroadcast(broadcastConnected);
  }

  protected void broadcastDisconnected() {
    Intent broadcastDisconnected = new Intent("com.team341.daisycv.ROBOT_DISCONNECTED");
    ApplicationContext.get().sendBroadcast(broadcastDisconnected);
  }
}
