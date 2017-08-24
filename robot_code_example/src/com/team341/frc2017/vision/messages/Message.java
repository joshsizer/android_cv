package com.team341.frc2017.vision.messages;

import org.json.simple.JSONObject;

/**
 * Created by joshs on 7/26/2017.
 */
public abstract class Message {

  public abstract String getType();

  public abstract String getMessage();

  public String toJSon() {
    JSONObject object = new JSONObject();
    object.put("type", getType());
    object.put("message", getMessage());

    return object.toJSONString();
  }
}
