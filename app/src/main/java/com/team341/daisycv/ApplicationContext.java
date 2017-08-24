package com.team341.daisycv;

import android.app.Application;
import android.content.Context;

/**
 * Allows objects without access to the application context to access it.
 *
 * @author Joshua Sizer
 * @since 7/31/17
 */
public class ApplicationContext extends Application {

  private static Context context;

  public static Context get() {
    return ApplicationContext.context;
  }

  public void onCreate() {
    super.onCreate();
    ApplicationContext.context = getApplicationContext();
  }
}
