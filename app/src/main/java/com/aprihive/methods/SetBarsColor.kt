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
import android.view.View
import android.view.Window

class SetBarsColor(var context: Context, var window: Window) {
    fun enableFullscreen() {

        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun disableFullscreen() {
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        setColors()
    }

    fun setColors() {
        window.statusBarColor = context.resources.getColor(R.color.bg_color)
        window.navigationBarColor = context.resources.getColor(R.color.bg_color)
        if (window.statusBarColor == context.resources.getColor(R.color.white_color)) {

            //make nav bar and status bar icons dark
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    //  window.getDecorView().setSystemUiVisibility(View.| View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
        }
    }

    init {
        setColors()
    }
}