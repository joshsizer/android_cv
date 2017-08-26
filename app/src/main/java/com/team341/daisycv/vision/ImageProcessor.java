package com.team341.daisycv.vision;

/**
 * Interfaces with the native C++ opencv code.
 *
 * @author Joshua Sizer
 * @since 5/16/2017.
 */
public class ImageProcessor {

  /**
   * The enumerations for the display modes of the camera.
   */
  public static final int DISP_MODE_RAW = 0;
  public static final int DISP_MODE_THRESH = 1;
  public static final int DISP_MODE_TARGETS = 2;
  public static final int DISP_MODE_TARGETS_PLUS = 3;

  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  /**
   * Calls the native C++ code in the native-lib.cpp. The native-lib.cpp
   * must have a function with the same name as this function, and the same
   * arguments.
   *
   * @param texIn The texture reference to the image taken by the camera.
   * @param texOut The texture reference for the texture that is displayed to
   *    the screen. This can be drawn to and changes to the liking
   *    of the processing algorithm
   * @param width The width of the picture taken by the camera
   * @param height The height of the picture taken by the camera
   * @param procMode Which mode to process with. This mainly changes the
   *    targets that are returned from this function and what is drawn to the
   *    screen
   * @param hMin Hue minimum value for HSV threshholding the image
   * @param hMax Hue maximum value
   * @param sMin Saturation minimum value
   * @param sMax Saturation maximum value
   * @param vMin Value minimum value
   * @param vMax Value maximum value
   * @param dest The reference to the object to insert target information into
   */
  public static native void processImage(int texIn, int texOut, int width,
      int height, int procMode, int hMin, int hMax, int sMin, int sMax,
      int vMin, int vMax, TargetsInfo dest, boolean log);

  /**
   * Cannot change naming without also changing naming in the native code,
   * because this class is references by fully qualified name. If the name
   * changes here, the C++ code will not be able to reference this object and
   * the data stored inside.
   *
   * This is the most basic representation of the target, where width and
   * height are the size of the target, in pixels, and x & y are the x-y
   * location of the target in the camera frame, in pixels.
   */
  public static class TargetsInfo {

    /**
     * In the case that there are multiple targets found, the target array
     * will have multiple target objects.
     */
    public Target[] targets;
    public int numTargets;

    /**
     * Instantiates the targets in the array, assuming a maximum of 3 targets.
     */
    public TargetsInfo() {
      targets = new Target[3];
      for (int i = 0; i < targets.length; i++) {
        targets[i] = new Target();
      }
    }

    /**
     * Basic representation of a target, with values in pixel units.
     */
    public static class Target {

      public double x;
      public double y;
      public double width;
      public double height;
    }
  }
}
