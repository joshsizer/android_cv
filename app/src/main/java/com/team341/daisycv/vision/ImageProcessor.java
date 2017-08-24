package com.team341.daisycv.vision;

/**
 * Created by joshs on 5/16/2017.
 */

public class ImageProcessor {

  public static final int DISP_MODE_RAW = 0;
  public static final int DISP_MODE_THRESH = 1;
  public static final int DISP_MODE_TARGETS = 2;
  public static final int DISP_MODE_TARGETS_PLUS = 3;

  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  public static native void processImage(int texIn, int texOut, int width, int height, int procMode,
      int hMin, int hMax, int sMin, int sMax, int vMin, int vMax,
      TargetsInfo dest);

  /**
   * Cannot change naming without also changing naming in the native code
   */
  public static class TargetsInfo {

    public final Target[] targets;
    public int numTargets;
    public TargetsInfo() {
      targets = new Target[3];
      for (int i = 0; i < targets.length; i++) {
        targets[i] = new Target();
      }
    }

    public static class Target {

      public double x;
      public double y;
      public double width;
      public double height;
    }
  }
}
