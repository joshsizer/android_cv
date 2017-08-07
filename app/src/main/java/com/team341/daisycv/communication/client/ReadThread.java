package com.team341.daisycv.communication.client;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles reading from the socket's input stream
 */
public class ReadThread extends ClientThread {

  public static final String LOGTAG = "ReadThread";
  private BufferedReader reader;

  public ReadThread(Client client) {
    super(client);
  }

  @Override
  public void run() {
    while (getClient().isEnabled()) {
      if (getClient().isConnected()) {
        try {
          if (getClient().getSocket() != null && !getClient().getSocket().isClosed()) {
            InputStream is = getClient().getSocket().getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            if (reader.ready()) {
              String message = reader.readLine();
              Log.i(LOGTAG, "Received message: " + message);
              try {
                JSONObject object = new JSONObject(message);
                if ("heartbeat".equals(object.getString("type"))) {
                  getClient().updateLastReceivedHeartbeatTime(System.currentTimeMillis());
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
