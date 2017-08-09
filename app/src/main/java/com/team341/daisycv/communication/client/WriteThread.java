package com.team341.daisycv.communication.client;

import android.util.Log;
import com.team341.daisycv.communication.messages.JsonSerializable;
import com.team341.daisycv.communication.messages.JsonSerializer;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Handles writing to the thread's output stream
 */
public class WriteThread extends ClientThread {

  public static final String LOGTAG = "WriteThread";

  public WriteThread(Client client) {
    super(client);
  }

  @Override
  public void run() {
    while (mClient.isEnabled()) {
      JsonSerializable message = null;
      try {
        message = mClient.getMessageQueue().poll(250, TimeUnit.MILLISECONDS);
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

  public void sendString(String message) {
    if (mClient.getSocket() != null && mClient.getSocket().isConnected()) {
      try {
        mClient.getSocket().getOutputStream().write(message.getBytes());
      } catch (IOException e) {
        e.printStackTrace();
        mClient.setSocketNull();
      }
    }

  }
}
