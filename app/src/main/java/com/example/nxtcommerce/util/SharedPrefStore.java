package com.example.nxtcommerce.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefStore {
    private Context mAct;
    public SharedPrefStore(Context aAct) {
        this.mAct = aAct;
    }

    private SharedPreferences getPref() {
        return PreferenceManager.getDefaultSharedPreferences(mAct);
    }

    public void saveString(String key, String value) {
        SharedPreferences settings = getPref();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultVal) {
        SharedPreferences settings = getPref();
        return (settings.getString(key, defaultVal));
    }

    public void setInt(String subscription_id, int sbu_id) {
        SharedPreferences settings = getPref();
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(subscription_id, sbu_id);
        editor.apply();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultVal) {
        SharedPreferences settings = getPref();
        return settings.getInt(key, defaultVal);
    }
}
