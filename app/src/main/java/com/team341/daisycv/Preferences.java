package com.team341.daisycv;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

/**
 * A set of key value pairs for settings of this program. Used to store and
 * manipulate HSV values.
 *
 * @author Joshua Sizer
 * @since 8/26/17
 */
public class Preferences {

  private SharedPreferences mPreferences;
  private Context mContext;

  private Pair<Integer, Integer> hue;
  private Pair<Integer, Integer> sat;
  private Pair<Integer, Integer> val;

  public Preferences(Context context) {
    mContext = context;
    mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
  }

  public void setInt(String key, int value) {
    mPreferences.edit().putInt(key, value);
    boolean success = mPreferences.edit().commit();
    Log.d("Preferences", "" + success);
  }

  public int getInt(String value, int defaultValue) {
    return mPreferences.getInt(value, defaultValue);
  }

  public Pair<Integer, Integer> getHSVHue() {
    if (hue == null) {
      int hueMin = getInt(mContext.getString(R.string.hue_min_key), mContext
          .getResources().getInteger(R.integer.hue_min_default));
      int hueMax = getInt(mContext.getString(R.string.hue_max_key), mContext
          .getResources().getInteger(R.integer.hue_max_default));
      hue = new Pair<>(hueMin, hueMax);
    }
    return hue;
  }

  public Pair<Integer, Integer> getHSVSaturation() {
    if (sat == null) {
      int satMin = getInt(mContext.getString(R.string.sat_min_key), mContext
          .getResources().getInteger(R.integer.sat_min_default));
      int satMax = getInt(mContext.getString(R.string.sat_max_key), mContext
          .getResources().getInteger(R.integer.sat_max_default));
      sat = new Pair<>(satMin, satMax);
    }
    return sat;
  }

  public Pair<Integer, Integer> getHSVValue() {
    if (val == null) {
      int valMin = getInt(mContext.getString(R.string.val_min_key), mContext
          .getResources().getInteger(R.integer.val_min_default));
      int valMax = getInt(mContext.getString(R.string.val_max_key), mContext
          .getResources().getInteger(R.integer.val_max_default));
      val = new Pair<>(valMin, valMax);
    }
    return val;
  }

  public void setHSVHue(int min, int max) {
    setInt(mContext.getString(R.string.hue_min_key), min);
    setInt(mContext.getString(R.string.hue_max_key), max);
    hue = new Pair<>(min, max);
  }

  public void setHSVSaturation(int min, int max) {
    setInt(mContext.getString(R.string.sat_min_key), min);
    setInt(mContext.getString(R.string.sat_max_key), max);
    sat = new Pair<>(min, max);
  }

  public void setHSVValue(int min, int max) {
    setInt(mContext.getString(R.string.val_min_key), min);
    setInt(mContext.getString(R.string.val_max_key), max);
    val = new Pair<>(min, max);
  }
}
