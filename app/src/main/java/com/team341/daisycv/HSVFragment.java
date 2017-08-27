package com.team341.daisycv;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Joshua Sizer
 * @since 8/26/17
 */
public class HSVFragment extends Fragment {

  public HSVFragment() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_hsv_slider, container, false);
  }
}
