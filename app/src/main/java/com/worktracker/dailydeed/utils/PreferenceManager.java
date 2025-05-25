package com.worktracker.dailydeed.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "DailyDeedPrefs";
    private static final String KEY_AUTO_LOCATION = "auto_location";
    private static final String KEY_24_HOUR_FORMAT = "24_hour_format";
    private static final String KEY_CURRENT_PROFILE = "current_profile";
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    public void setAutoLocation(boolean enabled) {
        editor.putBoolean(KEY_AUTO_LOCATION, enabled);
        editor.apply();
    }
    
    public boolean isAutoLocationEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_LOCATION, false);
    }
    
    public void set24HourFormat(boolean enabled) {
        editor.putBoolean(KEY_24_HOUR_FORMAT, enabled);
        editor.apply();
    }
    
    public boolean is24HourFormat() {
        return sharedPreferences.getBoolean(KEY_24_HOUR_FORMAT, true);
    }
    
    public void setCurrentProfile(String profileName) {
        editor.putString(KEY_CURRENT_PROFILE, profileName);
        editor.apply();
    }
    
    public String getCurrentProfile() {
        return sharedPreferences.getString(KEY_CURRENT_PROFILE, "Default");
    }
    
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}