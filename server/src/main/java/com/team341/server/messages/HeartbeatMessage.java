package com.team341.server.messages;

/**
 * Created by joshs on 7/26/2017.
 */

public class HeartbeatMessage extends Message {

  private static HeartbeatMessage instance = new HeartbeatMessage();

  public static HeartbeatMessage get() {
    return instance;
  }

  private HeartbeatMessage() {}

  @Override
  public String getType() {
    return "heartbeat";
  }

  @Override
  public String getMessage() {
    return "thump";
  }
}
