package com.team341.daisycv.communication;

import android.util.Log;
import java.io.IOException;
import java.net.Socket;

public class Client {

  public static final String LOGTAG = "Client";

  private String mHostname;
  private int mPort;
  private Socket mSocket;
  private boolean mEnabled;
  private boolean mConnected;

  public Client(String hostname, int port) {
    mHostname = hostname;
    mPort = port;
  }

  public void start() {
    Log.d(LOGTAG, "start()");
    mEnabled = true;
    mConnected = false;

    new Thread(new ConnectionThread()).start();
    new Thread(new WriteThread()).start();
    new Thread(new ReadThread()).start();
  }

  public void stop() {
    mEnabled = false;

  }

  public void tryConnecting() {

    if (mSocket == null || !mSocket.isConnected()) {
      try {
        Log.d(LOGTAG, "tryConnecting()");
        mSocket = new Socket(mHostname, mPort);
        Log.d(LOGTAG, "Connected to: "  + mSocket.getInetAddress().toString() + ":" + mSocket
            .getPort());
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

   private class ConnectionThread implements Runnable {

    @Override
    public void run() {
      while (mEnabled) {
        if (!mConnected) {
          tryConnecting();
        }

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class WriteThread implements Runnable {

    @Override
    public void run() {

    }
  }

  public class ReadThread implements Runnable {

    @Override
    public void run() {

    }
  }
}