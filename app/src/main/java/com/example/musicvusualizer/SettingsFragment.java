package com.example.musicvusualizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener,Preference.OnPreferenceChangeListener {
    private static final String TAG = "SettingsFragment";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_visualizer, rootKey);
        // get shared preference and preference screen when we add our summary
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count  =preferenceScreen.getPreferenceCount();
        for(int i =0;i<count;i++){
            Preference p = preferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)){
                String value  = sharedPreferences.getString(p.getKey(),"");
                setPreferenceSummary(p,value);
            }
        }
        // prefrence of size
        Preference sizePrefrence = findPreference(getString(R.string.pref_size_key));
        sizePrefrence.setOnPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(Preference p, String value) {
        if (p instanceof ListPreference){
            ListPreference listPreference = (ListPreference) p;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex>=0){
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference p = findPreference(key);
        if (p!=null){
            if (!(p instanceof  CheckBoxPreference)){
                String value = sharedPreferences.getString(p.getKey(),"");
                setPreferenceSummary(p,value);
            }
        }
    }

    //-----------------------------------fragment Lifecycle------------------------

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), "select no between 0 and 3", Toast.LENGTH_SHORT);
        //check if the prefrence is size
        if (preference.getKey().equals(getString(R.string.pref_size_key))) {
            String sizeString = (String) newValue;
            try {
                float size = Float.parseFloat(sizeString);
                if (size > 3 || size <= 0) {
                    error.show();
                    return false;
                }
            }
                catch (NumberFormatException e){
                error.show();
                return false;

            }
        }
        return true;
    }
}