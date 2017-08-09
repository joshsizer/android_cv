package com.team341.daisycv.communication.client;

import java.net.Socket;

/**
 * Created by joshs on 8/6/2017.
 */

public abstract class ClientThread extends Thread {

  protected Client mClient;

  public ClientThread(Client client) {
    mClient = client;
  }

  /**
   * This is where all thread code should run. It is the method that is called on a new thread
   */
  @Override
  public abstract void run();
}
