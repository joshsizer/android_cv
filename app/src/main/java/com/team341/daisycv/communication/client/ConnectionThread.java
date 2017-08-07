package com.team341.daisycv.communication.client;

import android.content.Intent;
import android.util.Log;
import com.team341.daisycv.ApplicationContext;
import com.team341.daisycv.communication.messages.HeartbeatMessage;
import com.team341.daisycv.communication.messages.JsonSerializer;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by joshs on 8/6/2017.
 */

public class ConnectionThread extends ClientThread {

  public static final String LOGTAG = "ConnectionThread";
  private long mLastSentHearbeatTime;
  private long mLastReceivedHearbeatTime;

  public ConnectionThread(Client client) {
    super(client);
  }

  @Override
  public void run() {
    while (getClient().isEnabled()) {
      try {
        if ((getClient().getSocket() == null || !getClient().getSocket().isConnected()) && !getClient().isConnected
            ()) {
          getClient().tryConnecting();
          Thread.sleep(250);
        }

        long now = System.currentTimeMillis();

        if ((now - mLastSentHearbeatTime) > 100) {
          getClient().getMessageQueue().offer(HeartbeatMessage.getInstance());
          mLastSentHearbeatTime = now;
        }

        if (Math.abs(mLastReceivedHearbeatTime - mLastSentHearbeatTime) > 800 && getClient().isConnected()) {
          getClient().notifyDisconnected();
        }

        if (Math.abs(mLastReceivedHearbeatTime - mLastSentHearbeatTime) < 800 && !getClient()
            .isConnected()) {
          getClient().notifyConnected();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void updateLastSentHeartbeatTime(long time) {
    mLastSentHearbeatTime = time;
  }

  public void updateLastReceivedHeartbeatTime(long time) {
    mLastReceivedHearbeatTime = time;
  }
}
