package com.team341.daisycv;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import org.json.*;

/**
 * This class is the launcher of the program. It will handle all "administrative tasks" of this
 * program. This includes ensuring the correct camera permissions are granted to this application.
 * The state flow for granting permissions is | ask -> accept -> camera activity | ask -> deny ->
 * dismiss -> close | ask -> deny -> settings -> user choice |
 */
public class LauncherActivity extends Activity {

  public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
  public static final String TAG = "LauncherActivity";

  // the alert dialog which shows up if the user denies camera permissions
  private AlertDialog cameraErrorDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate()");
    setContentView(R.layout.activity_launcher);
    tryStartCameraActivity();
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause()");
    if (cameraErrorDialog != null) {
      cameraErrorDialog.dismiss();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy()");
    if (cameraErrorDialog != null) {
      cameraErrorDialog.dismiss();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume()");
  }

  /**
   * This will launch the CameraActivity if the correct camera permissions are granted
   */
  public void tryStartCameraActivity() {
    if (ContextCompat.checkSelfPermission(this, permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {

      Intent startCameraActivityIntent = new Intent(this, CameraActivity.class);
      startActivity(startCameraActivityIntent);
      finish();

    } else {

      ActivityCompat.requestPermissions(this,
          new String[]{permission.CAMERA},
          MY_PERMISSIONS_REQUEST_CAMERA);
    }
  }

  /**
   * Handles when a user either denies or accepts camera permission
   *
   * @param requestCode The request code associated with camera permission
   * @param permissions The list of permissions trying to be granted, in this case only camera
   * @param grantResults The results of which permissions were granted
   */
  @Override
  public void onRequestPermissionsResult(int requestCode,
      String permissions[], int[] grantResults) {
    Log.d(TAG, "onRequestPermissionResult()");
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_CAMERA: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          tryStartCameraActivity();

        } else {

          if (cameraErrorDialog == null) {
            cameraErrorDialog = createCameraErrorDialog();
          }

          cameraErrorDialog.show();
        }

        return;
      }
    }
  }

  /**
   * Generates the alert dialog when camera permissions are not granted by the user
   *
   * @return the AlertDialog
   */
  private AlertDialog createCameraErrorDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle("Camera error");
    builder.setMessage("To function properly, Camera needs your permission. Go to settings to"
        + " allow permission for Camera");
    // opens the application settings and closes the task
    builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAndRemoveTask();
      }
    });

    // closes this task
    builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        finishAndRemoveTask();
      }
    });

    return builder.create();
  }
}
