package com.team341.daisycv;

/**
 * Created by joshs on 5/16/2017.
 */

public class ImageProcessor {
  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }


  /**
   * A native method that is implemented by the 'native-lib' native library,
   * which is packaged with this application.
   */
  public static native String stringFromJNI();

  public static native void proccessImage(int texIn, int texOut, int width, int height);
}
