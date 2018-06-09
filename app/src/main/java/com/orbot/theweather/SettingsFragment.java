package com.orbot.theweather;

/**
 * Created by Rahul on 01/06/2016.
 */

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * shows the settings option for choosing the movie categories in ListPreference.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences
        .OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final int REQUEST_CODE_LOCATION = 2;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.app_preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.location_categories_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.temperature_categories_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.wind_speed_categories_key));

        Preference licensePreference = findPreference(getString(R.string.about_license_categories_key));
        licensePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent licenseActivity = new Intent(getContext(), LicenseActivity.class);
                startActivity(licenseActivity);
                return false;
            }
        });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            Boolean checked = checkBoxPreference.isChecked();
            if (key == getString(R.string.location_categories_key)) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat
                        .checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request missing location permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION);
                } else {

                }
            }
        } else {
            preference.setSummary(sharedPreferences.getString(key, ""));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}