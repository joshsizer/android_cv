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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.team341.daisycv.communication.client.Client;
import com.team341.daisycv.communication.ClientTest;

/**
 * Created by joshs on 7/23/2017.
 */

public class CameraActivity extends Activity {

  public static final String LOGTAG = "CameraActivity";
  private CameraView mCameraView;
  private Client client;

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

    registerReceiver(new RobotConnectedBroadcastReceiver(),
        new IntentFilter("com.team341.daisycv.ROBOT_CONNECTED"));
    client = new Client("localhost", 8341);
    //ClientTest clientTest = new ClientTest(8341);
    client.start();

    setContentView(R.layout.activity_camera);
    mCameraView = (CameraView) findViewById(R.id.camera_view);
    mCameraView.setCameraTextureListener(mCameraView);
  }

  @Override
  protected void onPause() {
    Log.i(LOGTAG, "onPause()");
    super.onPause();
    mCameraView.onPause();
    client.stop();
  }

  @Override
  protected void onResume() {
    super.onResume();

    // there's a possibility the user removes permissions while the app is running in the background
    if (ContextCompat.checkSelfPermission(this, permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      Intent startPermissionActivity = new Intent(this, LauncherActivity.class);
      startActivity(startPermissionActivity);
      finish();
      return;
    }

    mCameraView.onResume();
    client.start();
  }


  public class RobotConnectedBroadcastReceiver extends BroadcastReceiver {

    public RobotConnectedBroadcastReceiver () {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
      TextView connectedText = (TextView) findViewById(R.id.connected_text_view);
      if (intent.getBooleanExtra("connected", false)) {
        connectedText.setText("Connected!");
      } else {
        connectedText.setText("Not Connected!");
      }
    }
  }
}

