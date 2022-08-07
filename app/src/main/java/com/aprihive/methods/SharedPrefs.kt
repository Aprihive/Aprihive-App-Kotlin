// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.methods

import com.aprihive.R
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.aprihive.methods.MySnackBar
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    var shownOnboard = false
    var pushNotificationEnabled = true
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    var themeSettings: Int
    fun saveTheme(context: Context) {
        themeSettings = AppCompatDelegate.getDefaultNightMode()
        sharedPreferences = context.getSharedPreferences("aprihive", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt("theme", themeSettings)
        editor.apply()
    }

    fun savePrefs(context: Context?) {
        editor.putBoolean("shownIntro", shownOnboard)
        editor.apply()
    }

    fun savePushSelection(value: Boolean?) {
        editor.putBoolean("enabledPush", value!!)
        editor.apply()
    }

    val pushSelection: Boolean
        get() {
            pushNotificationEnabled = sharedPreferences.getBoolean("enabledPush", true)
            return pushNotificationEnabled
        }

    init {
        sharedPreferences = context.getSharedPreferences("aprihive", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        themeSettings = sharedPreferences.getInt("theme", 1)
        shownOnboard = sharedPreferences.getBoolean("shownIntro", false)
    }
}