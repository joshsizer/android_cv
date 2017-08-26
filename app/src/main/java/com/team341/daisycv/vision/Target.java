package com.team341.daisycv.vision;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A representation of a target. There are many ways to represent a target. A
 * target will always have some amount of properties and they should be sendable
 * via JSON object model.
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

  /**
   * This is to make sure that our double has some decimal part so that
   * it is parsed as a double on the receiving end.
   *
   * @param value The double to make extra doubly
   * @return A double with some decimal ending
   */
  private double doubleize(double value) {
    double leftover = value % 1;
    if (leftover < 1e-7) {
      value += 1e-7;
    }
    return value;
  }

  public JSONObject getJsonObject() {
    JSONObject target = new JSONObject();
    try {
      target.put("range", doubleize(getRange()));
      target.put("azimuth", doubleize(getAzimuth()));
      target.put("width", doubleize(getWidth()));
      target.put("height", doubleize(getHeight()));
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return target;
  }
}
