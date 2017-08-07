package com.team341.daisycv.communication.client;

import java.net.Socket;

/**
 * Created by joshs on 8/6/2017.
 */

public abstract class ClientThread extends Thread {

  private Client mClient;

  public ClientThread(Client client) {
    mClient = client;
  }

  /**
   * This is where all thread code should run. It is the method that is called on a new thread
   */
  @Override
  public abstract void run();

  /**
   * Callback for when the client has connected to the server
   *
   */
  public void clientConnected() {
  }

  /**
   * Callback for when the client is disconnected from the server
   */
  public void clientDisconnected() {
  }

  /**
   * Callback for when the client is stopped. This is called before this thread is joined. This
   * should perform any cleanup code.
   */
  public void clientStopped() {

  }

  /**
   * Gets the client context
   *
   * @return The client which should handle connecting to the server
   */
  public Client getClient() {
    return mClient;
  }
}
