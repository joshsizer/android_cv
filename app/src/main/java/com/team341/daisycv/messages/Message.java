package com.team341.daisycv.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshs on 7/26/2017.
 */
public abstract class Message {

  public abstract String getType();

  public abstract String getMessage();

  public String toJSon() {
    JSONObject object = new JSONObject();
    try {
      object.put("type", getType());
      object.put("message", getMessage());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return object.toString();
  }
}
