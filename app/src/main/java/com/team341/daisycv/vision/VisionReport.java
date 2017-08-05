package com.team341.daisycv.vision;

import com.team341.daisycv.communication.JsonSerializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshs on 8/4/2017.
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
    return "VisionUpdate";
  }

  @Override
  public String getMessage() {
    // todo: return string representation of the VisionUpdate
    return null;
  }
}
