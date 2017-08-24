package com.team341.frc2017.vision;

import com.team341.frc2017.vision.messages.HeartbeatMessage;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by joshs on 7/26/2017.
 */
public class ClientTest {
  private static int port = 8341;

  public static void main(String[] args) {
    System.out.println(HeartbeatMessage.get().toJSon());
    Socket mSocket;
    long lastSentHeartBeatTime = System.nanoTime();
    try {
      mSocket = new Socket("localhost", port);
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
