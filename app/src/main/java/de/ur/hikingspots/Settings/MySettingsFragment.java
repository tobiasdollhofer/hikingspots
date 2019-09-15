package de.ur.hikingspots.Settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import de.ur.hikingspots.R;

public class MySettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
