package com.team341.daisycv.communication.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple interface that enables any object to be serialized. If an object is able to be
 * serialized, the object should be able to be recreated using some sort of deserialization
 * method on the receiving end. While the serializer does not have a universal mechanism for
 * storing object data, the receiving end should be able to interpret the serialized data and
 * recreate an object from that data.
 */
public interface JsonSerializable {

  String getType();
  String getMessage();
}
