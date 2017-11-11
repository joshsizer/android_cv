package com.team341.daisycv;

import android.Manifest.permission;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

/**
 * The activity that is launched when camera permissions are granted. This is
 * the main activity of the program, where the CameraView is manipulated and
 * the GUI is modified. The CameraView houses the Client and the callback
 * for when a frame is captured, ready to be modified.
 *
 * @author Joshua Sizer
 * @since 7/23/2017.
 */
public class CameraActivity extends Activity {

  public static final String LOGTAG = "CameraActivity";

  private CameraView mCameraView;
  private Preferences mPreferences;
  private BroadcastReceiver robotConnectedReceiver;
  private BroadcastReceiver robotDisconnectedReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    mPreferences = new Preferences(getApplicationContext());

    robotConnectedReceiver = new RobotConnectedBroadcastReceiver();
    robotDisconnectedReceiver = new RobotDisconnectedBroadcastReceiver();

    setContentView(R.layout.activity_camera);
    mCameraView = (CameraView) findViewById(R.id.camera_view);
    mCameraView.setCameraTextureListener(mCameraView);

    /*
    This is for dynamic changing of HSV
    setUpRangeSeekBar("hue", R.id.hueRangeSeekBar,
        R.id.hueMinTextView,
        R.id.hueMaxTextView);
    setUpRangeSeekBar("saturation", R.id.satRangeSeekBar,
        R.id.satMinTextView,
        R.id.satMaxTextView);
    setUpRangeSeekBar("value", R.id.valRangeSeekBar,
        R.id.valMinTextView,
        R.id.valMaxTextView);

    updateCameraViewHSV();
    */
  }

  @Override
  protected void onPause() {
    super.onPause();
    mCameraView.onPause();

    unregisterReceiver(robotConnectedReceiver);
    unregisterReceiver(robotDisconnectedReceiver);
  }

  @Override
  protected void onResume() {
    super.onResume();

    /*
     * There's a possibility the user removes permissions while the app is
     * running in the background, so we'll launch the LauncherActivity in
     * that case
     */
    if (ContextCompat.checkSelfPermission(this, permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      Intent startPermissionActivity = new Intent(this, LauncherActivity.class);
      startActivity(startPermissionActivity);
      finish();
      return;
    }

    registerReceiver(robotConnectedReceiver, new IntentFilter
        (getApplicationContext().getString(R.string.robot_connected_intent_filter)));
    registerReceiver(robotDisconnectedReceiver, new IntentFilter
        (getApplicationContext().getString(R.string.robot_disconnected_intent_filter)));

    mCameraView.onResume();
  }


  public class RobotConnectedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      TextView connectedText = (TextView) findViewById(R.id.connected_text_view);
      connectedText.setText(R.string.connection_status_connected);
    }
  }

  public class RobotDisconnectedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      TextView connectedText = (TextView) findViewById(R.id.connected_text_view);
      connectedText.setText(R.string.connection_status_default);
    }
  }

  private CrystalRangeSeekbar setUpRangeSeekBar(final String name, int
      seekBarId, int
      minTextId, int maxTextId) {
    // get seekbar from view
    final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar)
        findViewById(seekBarId);

    // get min and max text view
    final TextView tvMin = (TextView) findViewById(minTextId);
    final TextView tvMax = (TextView) findViewById(maxTextId);

    Pair<Integer, Integer> startingValues = null;

    if ("hue".equals(name)) {
      startingValues = mPreferences.getHSVHue();
      rangeSeekbar.setMinStartValue(startingValues.first);
      rangeSeekbar.setMaxStartValue(startingValues.second);
      rangeSeekbar.apply();

      // set listener
      rangeSeekbar.setOnRangeSeekbarChangeListener(
          new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
              tvMin.setText(String.valueOf(minValue));
              tvMax.setText(String.valueOf(maxValue));
              Log.i("CameraActivity", "Setting hue");
              mPreferences.setHSVHue(minValue.intValue(), maxValue.intValue());
              updateCameraViewHSV();
            }
          });
    } else if ("saturation".equals(name)) {
      startingValues = mPreferences.getHSVSaturation();
      rangeSeekbar.setMinStartValue(startingValues.first);
      rangeSeekbar.setMaxStartValue(startingValues.second);
      rangeSeekbar.apply();

      rangeSeekbar.setOnRangeSeekbarChangeListener(
          new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
              tvMin.setText(String.valueOf(minValue));
              tvMax.setText(String.valueOf(maxValue));
              Log.i("CameraActivity", "Setting saturation");
              mPreferences.setHSVSaturation(minValue.intValue(), maxValue
                  .intValue());
              updateCameraViewHSV();
            }
          });
    } else if ("value".equals(name)) {
      startingValues = mPreferences.getHSVValue();
      rangeSeekbar.setMinStartValue(startingValues.first);
      rangeSeekbar.setMaxStartValue(startingValues.second);
      rangeSeekbar.apply();

      rangeSeekbar.setOnRangeSeekbarChangeListener(
          new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
              tvMin.setText(String.valueOf(minValue));
              tvMax.setText(String.valueOf(maxValue));
              Log.i("CameraActivity", "Setting Value");
              mPreferences.setHSVValue(minValue.intValue(), maxValue.intValue());
              updateCameraViewHSV();
            }
          });
    }

    return rangeSeekbar;
  }

  private void updateCameraViewHSV() {
    Pair<Integer, Integer> hue = mPreferences.getHSVHue();
    Pair<Integer, Integer> sat = mPreferences.getHSVSaturation();
    Pair<Integer, Integer> val = mPreferences.getHSVValue();

    if (mCameraView != null) {
      mCameraView.setHSV(hue.first, hue.second, sat.first, sat.second, val
          .first, val.second);
    }
  }
}

