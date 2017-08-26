package com.team341.daisycv;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.team341.daisycv.communication.client.Client;
import com.team341.daisycv.vision.ImageProcessor;
import com.team341.daisycv.vision.Target;
import com.team341.daisycv.vision.VisionReport;
import java.util.HashMap;
import org.opencv.android.BetterCamera2Renderer;
import org.opencv.android.BetterCameraGLSurfaceView;

/**
 * This is the main view for providing images to the display. Thanks to 254 for
 * providing a "Better" implementation of CameraGLSurfaceView that takes Camera
 * Settings as an input, and provides a relevant timestamp for when the capture
 * was started. This allows you to set the camera exposure, capture size, width,
 * ect. They also do the work of calculating the Camera's field of view.
 *
 * @author Joshua Sizer
 * @since 8/26/17
 */
public class CameraView extends BetterCameraGLSurfaceView implements
    BetterCameraGLSurfaceView.CameraTextureListener {

  private static final String LOGTAG = "CameraView";
  private static final int kWidth = 640;
  private static final int kHeight = 480;
  private static final boolean logNative = false; // log info in native c++ code

  private final Client client;
  private TextView mFpsText;
  private int mFrameCounter;
  private int procMode = ImageProcessor.DISP_MODE_TARGETS;
  private long mLastNanoTime;

  public CameraView(Context context, AttributeSet attrs) {
    super(context, attrs, getCameraSettings());
    this.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if (procMode == ImageProcessor.DISP_MODE_RAW) {
          procMode = ImageProcessor.DISP_MODE_TARGETS;
        } else {
          procMode = ImageProcessor.DISP_MODE_RAW;
        }
      }
    });

    client = new Client("localhost", 8341);
  }

  /**
   * An interface for specifying camera settings, like the exposure value,
   * image stabilization, auto focus, focus distance, ect.
   *
   * @return The camera settings to capture images with
   */
  private static BetterCamera2Renderer.Settings getCameraSettings() {
    BetterCamera2Renderer.Settings settings = new BetterCamera2Renderer.Settings();
    settings.height = kHeight;
    settings.width = kWidth;
    settings.camera_settings = new HashMap<>();
    settings.camera_settings
        .put(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
    settings.camera_settings
        .put(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
            CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF);
    settings.camera_settings.put(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
        CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF);
    settings.camera_settings
        .put(CaptureRequest.SENSOR_EXPOSURE_TIME, 10000000L);
    settings.camera_settings.put(CaptureRequest.LENS_FOCUS_DISTANCE, .2f);

    return settings;
  }

  /**
   * A callback for when the CameraView starts to display images.
   *
   * @param width -  the width of the frames that will be delivered
   * @param height - the height of the frames that will be delivered
   */
  @Override
  public void onCameraViewStarted(int width, int height) {
    // nothing to do!
  }

  /**
   * A callback for when the CameraView stops receiving display images
   */
  @Override
  public void onCameraViewStopped() {
    // nothing to do!
  }

  /**
   * A callback for when  a new camera frame is available
   *
   * @param texIn -  the OpenGL texture ID that contains frame in RGBA format
   * @param texOut - the OpenGL texture ID that can be used to store modified
   * frame image t display
   * @param width -  the width of the frame
   * @param height - the height of the frame
   * @return True if we have processed the frame and want to draw the processed
   * frame, false otherwise
   */
  @Override
  public boolean onCameraTexture(int texIn, int texOut, int width, int height,
      long timeStamp) {
    // as soon as we hit 30 frames, let's calculate frames/second
    mFrameCounter++;
    if (mFrameCounter >= 30) {
      final int fps = (int) (mFrameCounter * 1e9 / (System.nanoTime()
          - mLastNanoTime));

      /*
       * If mFpsText is null, grab the instance from the layout.
       * Otherwise, add the fpsUpdater to the Main looper's runnable queue (all UI must be done
       * on the UI thread, AKA the main thread
       */
      if (mFpsText != null) {
        Runnable fpsUpdater = new Runnable() {
          public void run() {
            mFpsText.setText("FPS: " + fps);
          }
        };
        new Handler(Looper.getMainLooper()).post(fpsUpdater);
      } else {
        Log.d(LOGTAG, "mFpsText == null");
        mFpsText = (TextView) ((Activity) getContext())
            .findViewById(R.id.fps_text_view);
      }
      mFrameCounter = 0;
      mLastNanoTime = System.nanoTime();
    }

    //todo: make a hsv slider/prefrences that are saved
    ImageProcessor.TargetsInfo dest = new ImageProcessor.TargetsInfo();
    // finally, process the image! This calls our native C++ code
    ImageProcessor
        .processImage(texIn, texOut, width, height, procMode, 40, 65, 30, 255,
            100, 255, dest, logNative);

    VisionReport report = new VisionReport();

    for (int i = 0; i < dest.numTargets; i++) {
      Target target = new Target();
      target.setAzimuth(dest.targets[i].x);
      target.setRange(dest.targets[i].y);
      target.setHeight(dest.targets[i].height);
      target.setWidth(dest.targets[i].width);
      report.addTarget(target);
      report.setTimestamp(timeStamp);
    }

    client.sendVisionReport(report);

    return true;
  }

  /**
   * Destroys the client threads when the app is out of focus.
   */
  @Override
  public void onPause() {
    super.onPause();
    client.stop();
  }

  /**
   * Restarts the client threads when the app returns to focus
   */
  @Override
  public void onResume() {
    super.onResume();
    client.start();
  }

  public void setProcessingMode(int mode) {
    procMode = mode;
  }
}
