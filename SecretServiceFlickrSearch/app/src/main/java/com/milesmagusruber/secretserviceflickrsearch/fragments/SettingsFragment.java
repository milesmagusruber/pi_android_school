package com.milesmagusruber.secretserviceflickrsearch.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.workers.BackgroundPhotoUpdatesWorker;

import java.util.concurrent.TimeUnit;


public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String KEY_THEME = "dark_theme";
    public static final String KEY_ALLOW_UPDATES = "settings_check_box_allow_updates";
    public static final String KEY_SEARCH_REQUEST="settings_edit_text_search_request";
    public static final String KEY_INTERVAL="settings_background_updates_interval";

    public static final String DEFAULT_KEY_SEARCH_REQUEST="cat";
    public static final String DEFAULT_KEY_INTERVAL="15";

    //Background updates
    private WorkManager workManager;
    private PeriodicWorkRequest workerRequest;
    private Constraints constraints;

    //all Preference elements
    private SwitchPreferenceCompat themeSwitch;
    private CheckBoxPreference allowUpdates;
    private EditTextPreference editTextSearchRequest;
    private ListPreference backgroundUpdatesInterval;

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.title_settings);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        //Getting all Preference elements
        themeSwitch = findPreference(KEY_THEME);
        allowUpdates = findPreference(KEY_ALLOW_UPDATES);
        editTextSearchRequest =findPreference(KEY_SEARCH_REQUEST);
        backgroundUpdatesInterval=findPreference(KEY_INTERVAL);

        //Background uploads
        //Setting workManager
        workManager = WorkManager.getInstance();
        //Setting constraint (network connection: connected)
        constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();


        //if allow updates is checked
        if(allowUpdates.isChecked()){
            editTextSearchRequest.setVisible(true);
            backgroundUpdatesInterval.setVisible(true);
        }

        //Checking themeSwitch
        themeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object isDarkThemeOnObject) {
                boolean isDarkThemeOn = (Boolean) isDarkThemeOnObject;
                if (isDarkThemeOn) {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });

        //Adding listener to checkbox where we allow background updates
        allowUpdates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object isAllowUpdatesObject) {
                boolean isAllowUpdates = (Boolean) isAllowUpdatesObject;
                if (isAllowUpdates) {
                    editTextSearchRequest.setVisible(true);
                    backgroundUpdatesInterval.setVisible(true);
                    setBackgroundUpdates();
                } else {
                    editTextSearchRequest.setVisible(false);
                    backgroundUpdatesInterval.setVisible(false);
                    if(workerRequest!=null){
                        workManager.cancelWorkById(workerRequest.getId());
                    }
                }
                return true;
            }
        });

        //setting new background updates if we change search request
        editTextSearchRequest.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String searchRequest = (String) newValue;
                searchRequest=searchRequest.trim();
                if(searchRequest.length()>=2) {
                    setBackgroundUpdates();
                    return true;
                }else{
                    return false;
                }
            }
        });

        //setting new background updates if we change interval
        backgroundUpdatesInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setBackgroundUpdates();
                return true;
            }
        });

    }

    private void setBackgroundUpdates(){

        //input data for our worker
        Data data = new Data.Builder()
                .putString(BackgroundPhotoUpdatesWorker.WORKER_SEARCH_REQUEST,editTextSearchRequest.getText())
                .build();

        if(workerRequest!=null) {
            workManager.cancelWorkById(workerRequest.getId());
        }
        //Setting work request
        workerRequest = new PeriodicWorkRequest
                .Builder(BackgroundPhotoUpdatesWorker.class,Integer.parseInt(backgroundUpdatesInterval.getValue()), TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        workManager.enqueue(workerRequest);
    }
}


