// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive

import androidx.appcompat.app.AppCompatActivity
import com.aprihive.methods.SharedPrefs
import android.os.Bundle
import com.aprihive.R
import com.aprihive.methods.SetBarsColor
import androidx.appcompat.app.AppCompatDelegate
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_about_app.*
import java.lang.Exception

class AboutApp : AppCompatActivity() {

    /*private var toolbar: Toolbar? = null
    private var sharedPrefs: SharedPrefs? = null
    private var instagram: ImageView? = null
    private var twitter: ImageView? = null
    private var linkedin: ImageView? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_about_app)

        val setBarsColor = SetBarsColor(this, window)
        val sharedPrefs = SharedPrefs(this)

        val getTheme = sharedPrefs!!.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)


        /*toolbar = findViewById(R.id.toolbar)
        instagram = findViewById(R.id.instagram)
        twitter = findViewById(R.id.twitter)
        linkedin = findViewById(R.id.linkedIn)*/

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val eula = findViewById<TextView>(R.id.eula)
        eula.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/eula"))
            startActivity(intent)
        }

        val privacy = findViewById<TextView>(R.id.privacy)
        privacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/privacy"))
            startActivity(intent)
        }

        toolbar.setNavigationOnClickListener(View.OnClickListener { super@AboutApp.onBackPressed() })

        instagram.setOnClickListener(View.OnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/aprihive"))
                intent.setPackage("com.instagram.android")
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/aprihive"))
                startActivity(intent)
            }
        })

        twitter.setOnClickListener(View.OnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=aprihiveapp"))
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/aprihiveapp"))
                startActivity(intent)
            }
        })

        linkedIn.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/aprihive/"))
            startActivity(intent)
        })
    }
}