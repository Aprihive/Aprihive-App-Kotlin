// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.erlite.aprihive.methods;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class SharedPrefs {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public int themeSettings;

    public SharedPrefs(Context context){

        sharedPreferences = context.getSharedPreferences("aprihive", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        themeSettings = sharedPreferences.getInt("theme", 1);

    }


    public void saveTheme(Context context){

        themeSettings = AppCompatDelegate.getDefaultNightMode();

        sharedPreferences = context.getSharedPreferences("aprihive", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putInt("theme", themeSettings);
        editor.apply();

    }


}
