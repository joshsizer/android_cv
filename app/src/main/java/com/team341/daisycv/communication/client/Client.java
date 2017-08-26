package com.team341.daisycv.communication.client;

import android.content.Intent;
import android.util.Log;
import com.team341.daisycv.ApplicationContext;
import com.team341.daisycv.R;
import com.team341.daisycv.communication.messages.JsonSerializable;
import com.team341.daisycv.vision.VisionReport;
import java.io.IOException;
import java.net.InetSocketAddress;
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
  public void start() {
    mConnectionThread = new ConnectionThread(this);
    mWriteThread = new WriteThread(this);
    mReadThread = new ReadThread(this);

    mConnected = false;
    mEnabled = true;

    mConnectionThread.start();
    mWriteThread.start();
    mReadThread.start();
  }

  /**
   * This method should not inherit this object's lock (ie, be synchronized)
   * because any thread that needs to be joined in this method call may call
   * a method on this Client's object that requires this objects lock, and
   * therefore will be in a dead lock. The thread that we want to join after
   * it dies will try to synchronize on (this), but this method will already
   * have that lock if it is synchronized.
   */
  public void stop() {
    // make any thread trying to access this variable block while we modify
    // its value. Also, block until all threads are done using this variable
    synchronized (this) {
      mEnabled = false;
    }

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

    notifyDisconnected();
  }

  synchronized protected boolean isEnabled() {
    return mEnabled;
  }

  synchronized protected boolean isConnected() {
    return mConnected;
  }

  protected void notifyConnected() {
    synchronized (this) {
      mConnected = true;
    }
    broadcastConnected();
  }

  protected void notifyDisconnected() {
    synchronized (this) {
      mConnected = false;
    }
    broadcastDisconnected();
  }

  synchronized public void updateLastReceivedHeartbeatTime(long time) {
    mConnectionThread.updateLastReceivedHeartbeatTime(time);
  }

  /**
   * Attempts to create a socket with the specified hostname and port.
   */
  synchronized protected void tryConnecting() {
    try {
      if (mSocket == null) {
        Log.i(LOGTAG, "Attempting to connect to " + mHostname + ":" + mPort);
        mSocket = new Socket();
        // a heartbeat message should be sent and recieved every 100ms
        mSocket.setSoTimeout(250);
        mSocket.connect(new InetSocketAddress(mHostname, mPort));
        Log.i(LOGTAG, "Successfully connected");
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
