package com.team341.daisycv;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by josh on 8/26/17.
 */
public class Preferences {

  private SharedPreferences mPreferences;
  private Context mContext;

  public Preferences(Context context) {
    mContext = context;
    mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
  }

  public void setInt(String key, int value) {
    mPreferences.edit().putInt(key, value);
    mPreferences.edit().commit();
  }

  public int getInt(String value, int defaultValue) {
    return mPreferences.getInt(value, defaultValue);
  }
}
