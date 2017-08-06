package com.team341.daisycv.communication.client;

import java.net.Socket;

/**
 * Handles reading from the socket's input stream
 */
public class ReadThread extends ClientThread {

  public ReadThread(Client client) {
    super(client);
  }

  @Override
  public void run() {
    while (getClient().isEnabled()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void clientConnected(Socket socket) {

  }

  @Override
  public void clientDisconnected() {

  }
}
