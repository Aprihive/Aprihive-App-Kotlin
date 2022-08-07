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
import java.lang.Exception

class NetworkListener(context: Context, mview: View?, window: Window) {
    @JvmField
    var connected: Boolean? = null
    var view: View
    private val customSnackBar: MySnackBar? = null
    private val time = 0
    private var snackbar: Snackbar? = null
    @JvmField
    var networkListenerReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
            if (!connected!!) {
                snackbar!!.show()
            } else {
                snackbar!!.dismiss()
            }
        }
    }

    private fun showNetworkStatus(context: Context, display: View, msg: String, color: Int, time: Int) {
        snackbar = Snackbar.make(display, msg, time)
        snackbar!!.setBackgroundTint(context.resources.getColor(color))
        val view = snackbar!!.view
        try {
            view.findViewById<View>(R.id.snackbar_text).textAlignment = View.TEXT_ALIGNMENT_CENTER
        } catch (e1: Exception) { //nothing
        }
    }

    init {
        view = window.decorView.findViewById(android.R.id.content)
        showNetworkStatus(context, view, "No Connection!", R.color.color_error_red_200, Snackbar.LENGTH_INDEFINITE)
    }
}