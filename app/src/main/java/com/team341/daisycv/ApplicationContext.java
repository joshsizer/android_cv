package com.team341.daisycv;

import android.app.Application;
import android.content.Context;

/**
 * Created by joshs on 8/5/2017.
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
