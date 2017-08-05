package com.team341.daisycv.communication.messages;

import com.team341.daisycv.communication.JsonSerializable;
/**
 * Created by joshs on 7/26/2017.
 */

public class HeartbeatMessage implements JsonSerializable {

  private static HeartbeatMessage instance = new HeartbeatMessage();

  public static HeartbeatMessage getInstance() {
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
