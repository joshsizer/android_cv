package com.team341.daisycv.communication.client;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.Buffer;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles reading from the socket's input stream. This will attempt to read from the socket,
 * regardless of connection status.
 */
public class ReadThread extends ClientThread {

  public static final String LOGTAG = "ReadThread";
  private BufferedReader reader;

  public ReadThread(Client client) {
    super(client);
  }

  @Override
  public void run() {
    while (mClient.isEnabled()) {
      if (mClient.getSocket() != null) {
        BufferedReader reader;
        try {
          InputStream is = mClient.getSocket().getInputStream();
          reader = new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            e.printStackTrace();
          continue;
        }
        String message;
        try {
          message = reader.readLine();
          Log.i(LOGTAG, "Received message: " + message);
        } catch (IOException e) {
          e.printStackTrace();
          continue;
        }
        if (message == null) {
          continue;
        }
        try {
          JSONObject object = new JSONObject(message);
          if ("heartbeat".equals(object.getString("type"))) {
            long now = System.currentTimeMillis();
            mClient.updateLastReceivedHeartbeatTime(now);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } else {
        try {
          Thread.sleep(100, 0);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
