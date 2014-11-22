/**
 aReminder - an Android + Google wear application test for I/O 2014

 Copyright (C) 2014  Toni Martinez / Adam Doan Kim

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
