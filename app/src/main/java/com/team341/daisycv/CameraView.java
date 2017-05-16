package com.team341.daisycv;

import static android.util.Config.LOGD;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import java.util.HashMap;
import org.opencv.android.BetterCamera2Renderer;
import org.opencv.android.BetterCameraGLSurfaceView;

/**
 * This is the main view for providing images to the display. Thanks to 254 for providing a
 * "Better" implementation of CameraGLSurfaceView that takes Camera Settings as an input, and
 * provides a relevant timestamp for when the capture was started. This allows you to set the
 * camera exposure, capture size, width, ect. They also do the work of calculating the Camera's
 * field of view.
 */

public class CameraView extends BetterCameraGLSurfaceView implements
    BetterCameraGLSurfaceView.CameraTextureListener {

  public static String LOGTAG = "CameraView";

  private int mFrameCounter;
  private long mLastNanoTime;

  static final int kHeight = 480;
  static final int kWidth = 640;

  private TextView mFpsText;

  static BetterCamera2Renderer.Settings getCameraSettings() {
    BetterCamera2Renderer.Settings settings = new BetterCamera2Renderer.Settings();
    settings.height = kHeight;
    settings.width = kWidth;
    settings.camera_settings = new HashMap<>();
    settings.camera_settings.put(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
    settings.camera_settings.put(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF);
    settings.camera_settings.put(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF);
    settings.camera_settings.put(CaptureRequest.SENSOR_EXPOSURE_TIME, 1000000L);
    settings.camera_settings.put(CaptureRequest.LENS_FOCUS_DISTANCE, .2f);

    return settings;
  }

  public CameraView(Context context, AttributeSet attrs) {
    super(context, attrs, getCameraSettings());
  }

  @Override
  public void onCameraViewStarted(int width, int height) {

  }

  @Override
  public void onCameraViewStopped() {

  }

  @Override
  public boolean onCameraTexture(int texIn, int texOut, int width, int height, long timeStamp) {
    Log.d(LOGTAG, "Timestamp: " + timeStamp);
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
