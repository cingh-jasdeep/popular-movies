package example.android.com.popularmovies.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import example.android.com.popularmovies.R;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    /**
     * used to setPreferenceSummary of ListPreference summary
     * source : Sunshine Exercise S06.02
     * @param preference Preference object to set summary for
     * @param value Value of the preference from shared preferences
     */
    private void setPreferenceSummary (Preference preference, String value) {
        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);

            /* If there is a valid index, get label to show in summary */
            if(prefIndex >=0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } /* not needed for this project but included for simplicity */
        else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_movies);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        /* iterate through all preferences to set summary */
        for(int i = 0; i < preferenceScreen.getPreferenceCount() ; i++) {
            Preference p = preferenceScreen.getPreference(i);
            /* set summary not being used for checkbox preference
            *  can use summaryOff and summaryOn instead
            */
            if(!(p instanceof CheckBoxPreference)) {
                String prefValue = sharedPreferences.getString(p.getKey(),"");
                setPreferenceSummary(p, prefValue);
            }

        }
    }

    /* listen for shared preference change to update pref summaries for non checkbox preference */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference p = findPreference(key);
        if(p!=null) {
            if(!(p instanceof CheckBoxPreference)) {
                String prefValue = sharedPreferences.getString(p.getKey(),"");
                setPreferenceSummary(p, prefValue);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /* register preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        /* unregister preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }
}
