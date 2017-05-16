package com.team341.daisycv;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import org.opencv.android.CameraGLSurfaceView;

/**
 * Created by joshs on 5/15/2017.
 */

public class CameraView extends CameraGLSurfaceView implements
    CameraGLSurfaceView.CameraTextureListener {

  public static String LOGTAG = "CameraView";

  private int mFrameCounter;
  private long mLastNanoTime;

  private TextView mFpsText;

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
    // FPS
    mFrameCounter++;
    if (mFrameCounter >= 30) {
      final int fps = (int) (mFrameCounter * 1e9 / (System.nanoTime() - mLastNanoTime));
      Log.i(LOGTAG, "drawFrame() FPS: " + fps);
      if (mFpsText != null) {
        Runnable fpsUpdater = new Runnable() {
          public void run() {
            mFpsText.setText("FPS: " + fps);
          }
        };
        new Handler(Looper.getMainLooper()).post(fpsUpdater);
      } else {
        Log.d(LOGTAG, "mFpsText == null");
        mFpsText = (TextView) ((Activity) getContext()).findViewById(R.id.fps_text_view);
      }
      mFrameCounter = 0;
      mLastNanoTime = System.nanoTime();
    }
    ImageProcessor.processImage(texIn, texOut, width, height);
    return true;
  }
}
