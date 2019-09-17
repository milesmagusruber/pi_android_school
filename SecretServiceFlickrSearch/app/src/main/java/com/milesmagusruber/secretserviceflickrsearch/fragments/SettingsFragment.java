package com.milesmagusruber.secretserviceflickrsearch.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.milesmagusruber.secretserviceflickrsearch.R;


public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String KEY_THEME="dark_theme";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        SwitchPreferenceCompat themeSwitch = findPreference(KEY_THEME);

        if (themeSwitch != null) {
            themeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference arg0, Object isDarkThemeOnObject) {
                    boolean isDarkThemeOn = (Boolean) isDarkThemeOnObject;
                    if (isDarkThemeOn) {
                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES);
                    }else{
                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                }
            });
        }
    }
}


