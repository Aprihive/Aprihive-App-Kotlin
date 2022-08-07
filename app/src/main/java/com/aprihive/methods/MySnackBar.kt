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
import java.lang.Exception

class MySnackBar(context: Context, display: View?, msg: String?, color: Int, time: Int) {
    var snackbar: Snackbar

    init {
        snackbar = Snackbar.make(display!!, msg!!, time)
        snackbar.setBackgroundTint(context.resources.getColor(color))
        val view = snackbar.view
        try {
            view.findViewById<View>(R.id.snackbar_text).textAlignment = View.TEXT_ALIGNMENT_CENTER
        } catch (e1: Exception) { //nothing
        }
        snackbar.show()
    }
}