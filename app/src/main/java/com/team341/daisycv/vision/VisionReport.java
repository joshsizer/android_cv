package com.team341.daisycv.vision;

import com.team341.daisycv.communication.messages.JsonSerializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the message that will be sent to the robot!
 */
public class VisionReport implements JsonSerializable {

  private List<Target> mTargets;
  private long mTimestamp;

  public VisionReport() {
    mTargets = new ArrayList<>();
  }

  public List<Target> getTargets() {
    return mTargets;
  }

  public void addTarget(Target t) {
    mTargets.add(t);
  }

  public long getTimestamp() {
    return mTimestamp;
  }

  public void setTimestamp(long ts) {
    mTimestamp = ts;
  }

  @Override
  public String getType() {
    return "VisionReport";
  }

  @Override
  public String getMessage() {
    JSONObject message = new JSONObject();
    JSONArray targets = new JSONArray();
    for (Target target : mTargets) {
      if (target != null) {
        targets.put(target.getJsonObject());
      }
    }
    try {
      message.put("targets", targets);
      message.put("timestamp", getTimestamp());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return message.toString();
  }
}
