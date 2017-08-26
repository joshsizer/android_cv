package com.team341.daisycv.communication.client;

import android.util.Log;
import com.team341.daisycv.communication.messages.HeartbeatMessage;
import java.util.concurrent.TimeUnit;

/**
 * Monitors the connection between the phone and the Roborio. Due to the way
 * the ADB port forwarding is setup, even if the server is not running, the
 * socket will connect to... something. Therefore, we have to have our own
 * method for keeping track of connection status.
 *
 * @author Joshua Sizer
 * @since 8/6/2017.
 */
public class ConnectionThread extends Thread {

  public static final String LOGTAG = "ConnectionThread";

  private Client mClient;
  private long mLastSentHearbeatTime = System.currentTimeMillis();
  private long mLastReceivedHearbeatTime = 0;

  public ConnectionThread(Client client) {
    mClient = client;
  }

  @Override
  public void run() {
    while (mClient.isEnabled()) {
      try {
        if ((mClient.getSocket() == null || !mClient.getSocket().isConnected
            ()) && !mClient.isConnected()) {
          mClient.tryConnecting();
          Thread.sleep(250);
        }

        long now = System.currentTimeMillis();

        if ((now - mLastSentHearbeatTime) > 100) {
          mClient.getMessageQueue()
              .offer(HeartbeatMessage.getInstance(), 100, TimeUnit.MILLISECONDS);
          mLastSentHearbeatTime = now;
        }

        //Log.i(LOGTAG, "l sent hb time: " + mLastSentHearbeatTime);
        //Log.i(LOGTAG, "l received - sent hb time: " + (mLastReceivedHearbeatTime -
        //    mLastSentHearbeatTime));

        if (Math.abs(mLastReceivedHearbeatTime - mLastSentHearbeatTime) > 800 && mClient
            .isConnected()) {
          mClient.notifyDisconnected();
        }

        if (Math.abs(mLastReceivedHearbeatTime - mLastSentHearbeatTime) < 800 && !mClient
            .isConnected()) {
          mClient.notifyConnected();
        }

        Thread.sleep(100);

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public synchronized void updateLastReceivedHeartbeatTime(long time) {
    mLastReceivedHearbeatTime = time;
  }
}
