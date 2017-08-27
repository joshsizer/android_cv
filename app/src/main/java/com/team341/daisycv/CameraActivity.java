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
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

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
  private BottomSheetBehavior mBottomSheetBehavior;

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

    View bottomSheet = findViewById(R.id.bottom_sheet1);
    mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

    mButton1 = (Button) findViewById(R.id.button_1);
    mButton1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
          mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
          mButton1.setText(R.string.collapse_button1);
        }
        else {
          mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
          mButton1.setText(R.string.button1);
        }
      }
    });
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
}

