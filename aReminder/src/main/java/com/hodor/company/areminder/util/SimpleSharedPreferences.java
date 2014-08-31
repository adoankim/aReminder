package com.hodor.company.areminder.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by toni on 31/08/14.
 */
public class SimpleSharedPreferences {
    private static SimpleSharedPreferences sInstance;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SimpleSharedPreferences(Context context) {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mEditor = this.mSharedPreferences.edit();
    }

    public static SimpleSharedPreferences getSimpleSharedPreference(Context context) {
        if(SimpleSharedPreferences.sInstance == null) {
            SimpleSharedPreferences.sInstance = new SimpleSharedPreferences(context);
        }
        return SimpleSharedPreferences.sInstance;
    }

    public int read(String key, int defValue) {
        return this.mSharedPreferences.getInt(key, defValue);
    }

    public String read(String key, String defValue) {
        return this.mSharedPreferences.getString(key, defValue);
    }

    public void save(String key, int value) {
        this.mEditor.putInt(key, value);
        this.mEditor.commit();
    }

    public void save(String key, String value) {
        this.mEditor.putString(key, value);
        this.mEditor.commit();
    }
}
