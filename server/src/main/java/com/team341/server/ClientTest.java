package com.team341.server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by joshs on 7/26/2017.
 */
public class ClientTest {
  private static int port = 8341;

  public static void main(String[] args) {
    Socket mSocket;
    Socket mSocket2;
    try {
      mSocket = new Socket("localhost", port);
      System.out.println("mSocket is connected: " + mSocket.isConnected());
      Thread.sleep(2000);
      mSocket2 = new Socket("localhost", port);
      while (mSocket2.isConnected()) {
        mSocket2.getOutputStream().write("hello!myname is a string\n".getBytes());
      }
      System.out.println("mSocket is connected: " + mSocket2.isConnected());

      mSocket.close();
      mSocket2.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
