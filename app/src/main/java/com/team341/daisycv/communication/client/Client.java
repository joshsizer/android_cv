package com.team341.daisycv.communication.client;

import android.content.Intent;
import android.provider.Settings.System;
import android.util.Log;
import com.team341.daisycv.ApplicationContext;
import com.team341.daisycv.communication.messages.JsonSerializable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by joshs on 8/6/2017.
 */

public class Client {

  public static final String LOGTAG = "Client";

  private ClientThread mConnectionThread, mWriteThread, mReadThread;

  private ArrayBlockingQueue<JsonSerializable> mToSend;
  private Socket mSocket;

  private boolean mEnabled;
  private boolean mConnected;

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

    mConnected = false;
    mEnabled = true;

    mConnectionThread.start();
    mWriteThread.start();
    mReadThread.start();
  }

  public synchronized void stop() {
    Log.i(LOGTAG, "stop()");

    mEnabled = false;

    mConnectionThread = null;
    mWriteThread = null;
    mReadThread = null;

    try {
      if (mSocket != null) {
        mSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    mSocket = null;
  }

  synchronized protected boolean isEnabled() {
    return mEnabled;
  }

  synchronized protected boolean isConnected() {
    return mConnected;
  }

  synchronized protected void notifyConnected(Socket socket) {
    mConnectionThread.clientConnected(socket);
    mWriteThread.clientConnected(socket);
    mReadThread.clientConnected(socket);
  }

  protected void notifyDisconnected() {
    mConnectionThread.clientDisconnected();
    mWriteThread.clientDisconnected();
    mReadThread.clientDisconnected();
  }

  synchronized public void updateLastSentHeartbeatTime(long time) {
    ((ConnectionThread) mConnectionThread).updateLastSentHeartbeatTime(time);
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
        mConnected = true;
        notifyConnected(mSocket);

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

  protected void broadcastConnected() {
    Intent broadcastConnected = new Intent("com.team341.daisycv.ROBOT_CONNECTED");
    ApplicationContext.get().sendBroadcast(broadcastConnected);
  }

  protected void broadcastDisconnected() {
    Intent broadcastDisconnected = new Intent("com.team341.daisycv.ROBOT_DISCONNECTED");
    ApplicationContext.get().sendBroadcast(broadcastDisconnected);
  }
}
