package com.team341.daisycv;

/**
 * A representation of a target. There are many ways to represent a target. A target will always
 * have some amount of properties and they should be sendable via JSON object model.
 *
 */
public class Target {

  private double mRange;
  private double mAzimuth;
  private double mWidth;
  private double mSize;

  public Target(double range, double azimuth, double width, double size) {
    this.mRange = range;
    this.mAzimuth = azimuth;
    this.mWidth = width;
    this.mSize = size;
  }

  public double getmRange() {
    return mRange;
  }

  public void setmRange(double mRange) {
    this.mRange = mRange;
  }

  public double getmAzimuth() {
    return mAzimuth;
  }

  public void setmAzimuth(double mAzimuth) {
    this.mAzimuth = mAzimuth;
  }

  public double getmWidth() {
    return mWidth;
  }

  public void setmWidth(double mWidth) {
    this.mWidth = mWidth;
  }

  public double getmSize() {
    return mSize;
  }

  public void setmSize(double mSize) {
    this.mSize = mSize;
  }
}
