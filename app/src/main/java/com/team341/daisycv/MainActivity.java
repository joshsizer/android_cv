package com.team341.daisycv;

import android.Manifest.permission;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

  public static int MY_PERMISSIONS_REQUEST_CAMERA = 1;

  private CameraView mCameraView;

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

    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(this,
        permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          permission.CAMERA)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

      } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(this,
            new String[]{permission.CAMERA},
            MY_PERMISSIONS_REQUEST_CAMERA);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    }

    setContentView(R.layout.activity_main);
    mCameraView = (CameraView) findViewById(R.id.camera_view);
    mCameraView.setCameraTextureListener(mCameraView);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mCameraView.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mCameraView.onResume();
  }
}
