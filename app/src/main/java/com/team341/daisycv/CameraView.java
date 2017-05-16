package com.team341.daisycv;

import android.content.Context;
import android.util.AttributeSet;
import org.opencv.android.CameraGLSurfaceView;

/**
 * Created by joshs on 5/15/2017.
 */

public class CameraView extends CameraGLSurfaceView implements
    CameraGLSurfaceView.CameraTextureListener {

  public CameraView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void onCameraViewStarted(int width, int height) {

  }

  @Override
  public void onCameraViewStopped() {

  }

  @Override
  public boolean onCameraTexture(int texIn, int texOut, int width, int height) {
    MainActivity.matrixSize();
    return false;
  }
}
