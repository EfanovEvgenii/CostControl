package com.jeezsoft.costcontrol;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by jeezic on 04.01.2015.
 */
public class PreferenceFragmentImpl extends PreferenceFragment {

    public static PreferenceFragmentImpl newInstance() {
        return new PreferenceFragmentImpl();
    }

    public PreferenceFragmentImpl() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

}
