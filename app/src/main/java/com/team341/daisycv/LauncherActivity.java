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

/**
 * This class is the launcher of the program. It will handle all "administrative
 * tasks" of this program. This includes ensuring the correct camera permissions
 * are granted to this application. The state flow for granting permissions is |
 * ask -> accept -> camera activity | ask -> deny -> dismiss -> close | ask ->
 * deny -> settings -> user choice |
 *
 * @author Joshua Sizer
 * @since 8/26/17
 */
public class LauncherActivity extends Activity {

  private static final String TAG = "LauncherActivity";
  private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;

  // the alert dialog which shows up if the user denies camera permissions
  private AlertDialog cameraErrorDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_launcher);
    tryStartCameraActivity();
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (cameraErrorDialog != null) {
      cameraErrorDialog.dismiss();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (cameraErrorDialog != null) {
      cameraErrorDialog.dismiss();
    }
  }

  /**
   * This will launch the CameraActivity if the correct camera permissions are
   * granted
   */
  private void tryStartCameraActivity() {
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
   * @param permissions The list of permissions trying to be granted, in this
   * case only camera
   * @param grantResults The results of which permissions were granted
   */
  @Override
  public void onRequestPermissionsResult(int requestCode,
      String permissions[], int[] grantResults) {

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

        break;
      }
    }
  }

  /**
   * Generates the alert dialog when camera permissions are not granted by the
   * user
   *
   * @return the AlertDialog
   */
  private AlertDialog createCameraErrorDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(R.string.dialog_camera_permission_error_title);
    builder.setMessage(R.string.dialog_camera_permission_rationale);
    // opens the application settings and closes the task
    builder.setPositiveButton(R.string.settings,
        new DialogInterface.OnClickListener() {
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
    builder.setNegativeButton(R.string.dismiss,
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            finishAndRemoveTask();
          }
        });

    return builder.create();
  }
}
