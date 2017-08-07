package com.team341.daisycv.communication.client;

import android.util.Log;
import com.team341.daisycv.communication.messages.JsonSerializable;
import com.team341.daisycv.communication.messages.JsonSerializer;
import com.team341.daisycv.communication.messages.HeartbeatMessage;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Handles writing to the thread's output stream
 */
public class WriteThread extends ClientThread {

  public static final String LOGTAG = "WriteThread";
  private long lastSentHeartbeatTime;

  public WriteThread(Client client) {
    super(client);
  }

  @Override
  public void run() {
    while (getClient().isEnabled()) {
      if (getClient().isConnected()) {
        JsonSerializable message = null;
        try {
          message = getClient().getMessageQueue().poll(250, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (message == null) {
          continue;
        }
        Log.i(LOGTAG, "Sending message " + JsonSerializer.toJson(message));
        sendString(JsonSerializer.toJson(message) + "\n");
      }
    }
  }

  public void sendString(String message) {
    try {
      if (getClient().getSocket() != null && !getClient().getSocket().isClosed()) {
        getClient().getSocket().getOutputStream().write(message.getBytes());
        getClient().getSocket().getOutputStream().flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
