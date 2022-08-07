// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.methods;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class SharedPrefs {


    public boolean shownOnboard = false;
    public boolean pushEnabled = true;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public int themeSettings;

    public SharedPrefs(Context context){

        sharedPreferences = context.getSharedPreferences("aprihive", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        themeSettings = sharedPreferences.getInt("theme", 1);
        shownOnboard= sharedPreferences.getBoolean("shownIntro", false);


    }


    public void saveTheme(Context context){

        themeSettings = AppCompatDelegate.getDefaultNightMode();

        sharedPreferences = context.getSharedPreferences("aprihive", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putInt("theme", themeSettings);
        editor.apply();

    }

    public void savePrefs(Context context){

        editor.putBoolean("shownIntro", shownOnboard);
        editor.apply();

    }

    public void savePushSelection(Boolean value){

        editor.putBoolean("enabledPush", value);
        editor.apply();

    }

    public boolean getPushSelection(){
        pushEnabled = sharedPreferences.getBoolean("enabledPush", true);

        return pushEnabled;
    }


}
