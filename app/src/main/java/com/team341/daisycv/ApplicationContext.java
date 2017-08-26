package com.team341.daisycv;

import android.app.Application;
import android.content.Context;

/**
 * Allows objects without access to the application context to access it.
 * It'a not really proper android, but... we'll allow it. Alternatively, any
 * object that needs a reference to the context should be instantiated within
 * an android component which already has access to the context and be passed
 * that context as an argument, or be instantiated by an object with the
 * previous paradigm.
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
