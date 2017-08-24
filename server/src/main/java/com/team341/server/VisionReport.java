package com.team341.server;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the message that will be sent to the robot!
 */
public class VisionReport {

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
}
