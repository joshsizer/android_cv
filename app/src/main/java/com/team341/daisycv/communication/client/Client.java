package com.team341.daisycv.communication.client;

import android.content.Intent;
import android.support.annotation.RequiresPermission.Read;
import android.util.Log;
import com.team341.daisycv.ApplicationContext;
import com.team341.daisycv.R;
import com.team341.daisycv.communication.messages.JsonSerializable;
import com.team341.daisycv.vision.VisionReport;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A client will connect to a server on the robot.
 *
 * The connection thread will monitor the connection status of the socket. The
 * client is connected to the server in the case that there is a heartbeat
 * message being sent and received by the client and server. If the time between
 * when a heartbeat message is sent and received exceeds a threshold, the socket
 * will be closed and a new socket connection will be made.
 *
 * The read thread will read from the socket's input stream and process any
 * messages being sent from the server. These may include requests to change
 * vision mode, on field game state updates, and heartbeat messages.
 *
 * The write thread will write any messages to the sockets output stream,
 * including vision messages and heartbeat messages.
 *
 * @author Joshua Sizer
 * @since 8/8/17
 */
public class Client {

  public static final String LOGTAG = "Client";

  private WriteThread mWriteThread;
  private ReadThread mReadThread;
  private ConnectionThread mConnectionThread;
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

  /**
   * Starts off a connection to the server, which runs on the robot.
   */
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

    synchronized (this) {
      mEnabled = false;
    }

    try {
      if (mSocket != null) {
        mSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    mSocket = null;
    notifyDisconnected();

    if (mConnectionThread != null && mConnectionThread.isAlive()) {
      try {
        mConnectionThread.interrupt();
        mConnectionThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    Log.i(LOGTAG, "Joined Connection thread");

    if (mWriteThread != null && mWriteThread.isAlive()) {
      try {
        mWriteThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    Log.i(LOGTAG, "Joined Write thread");

    if (mReadThread != null && mReadThread.isAlive()) {
      try {
        mReadThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    Log.i(LOGTAG, "Joined Read thread");
  }

  synchronized protected boolean isEnabled() {
    return mEnabled;
  }

  synchronized protected boolean isConnected() {
    return mConnected;
  }

  synchronized protected void notifyConnected() {
    mConnected = true;
    broadcastConnected();
  }

  synchronized protected void notifyDisconnected() {
    mConnected = false;
    broadcastDisconnected();
  }


  synchronized public void updateLastReceivedHeartbeatTime(long time) {
    mConnectionThread.updateLastReceivedHeartbeatTime(time);
  }

  /**
   * Attempts to create a socket with the specified hostname and port.
   */
  synchronized protected void tryConnecting() {
    Log.i(LOGTAG, "tryConnecting()");
    try {
      if (mSocket == null) {
        Log.i(LOGTAG, "Attempting to connect to " + mHostname + ":" + mPort);
        mSocket = new Socket(mHostname, mPort);
        mSocket.setSoTimeout(100);
        Log.i(LOGTAG,
            "Connected to " + mSocket.getInetAddress() + ":" + mSocket
                .getPort());
      }
    } catch (IOException e) {
      Log.e(LOGTAG, "Cannot connect to server!");
      mSocket = null;
    }
  }

  synchronized public void sendVisionReport(VisionReport report) {
    getMessageQueue().offer(report);
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
    Intent broadcastConnected = new Intent(ApplicationContext.get().getString
        (R.string.robot_connected_intent_filter));
    ApplicationContext.get().sendBroadcast(broadcastConnected);
  }

  protected void broadcastDisconnected() {
    Intent broadcastDisconnected = new Intent(ApplicationContext.get()
        .getString(R.string.robot_disconnected_intent_filter));
    ApplicationContext.get().sendBroadcast(broadcastDisconnected);
  }
}
