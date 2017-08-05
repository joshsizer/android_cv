package com.team341.daisycv.communication;

import com.team341.daisycv.communication.JsonSerializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A very simplistic serialization method that will create a JSON object that holds a type value
 * and a message value. The type should indicate the class of object this data should
 * instantiate on the receiving end. The message can be another JSON object, or whatever the user
 * feels is necessary to communicate some information.
 */
public class JsonSerializer {

  /**
   * Serializes some serializable object.
   *
   * @param toSerial The object to serialize
   * @return the string representation of this object serialized
   */
  public static String toJson(JsonSerializable toSerial) {
    JSONObject object = new JSONObject();
    try {
      object.put("type", toSerial.getType());
      object.put("message", toSerial.getMessage());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return object.toString();
  }
}
