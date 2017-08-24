package com.team341.server;

import org.json.simple.JSONObject;

/**
 * A representation of a target. There are many ways to represent a target. A target will always
 * have some amount of properties and they should be sendable via JSON object model.
 *
 */
public class Target {

  private double mRange;
  private double mAzimuth;
  private double mWidth;
  private double mHeight;

  public Target(double range, double azimuth, double width, double height) {
    this.mRange = range;
    this.mAzimuth = azimuth;
    this.mWidth = width;
    this.mHeight = height;
  }

  public Target() {

  }

  public double getRange() {
    return mRange;
  }

  public void setRange(double mRange) {
    this.mRange = mRange;
  }

  public double getAzimuth() {
    return mAzimuth;
  }

  public void setAzimuth(double mAzimuth) {
    this.mAzimuth = mAzimuth;
  }

  public double getWidth() {
    return mWidth;
  }

  public void setWidth(double mWidth) {
    this.mWidth = mWidth;
  }

  public double getHeight() {
    return mHeight;
  }

  public void setHeight(double mSize) {
    this.mHeight = mSize;
  }
}
