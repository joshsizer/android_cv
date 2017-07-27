package com.team341.daisycv;

import com.team341.daisycv.messages.HeartbeatMessage;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by joshs on 7/26/2017.
 */

public class ClientTest {

  protected class ConnectionThread implements Runnable {

    @Override
    public void run() {
      Socket mSocket;
      long lastSentHeartBeatTime = System.nanoTime();
      try {
        mSocket = new Socket("localhost", 8341);
        int count = 0;
        while (mSocket.isConnected()) {
          if (System.nanoTime() - lastSentHeartBeatTime > (1000 * 1000 * 1000)) {
            mSocket.getOutputStream().write((HeartbeatMessage.get().toJSon() + "\n").getBytes());
            mSocket.getOutputStream().flush();
            lastSentHeartBeatTime = System.nanoTime();
            count++;
          }
          Thread.sleep(50);
        }

        mSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public ClientTest(int port) {
    new Thread(new ConnectionThread()).start();
  }

}
