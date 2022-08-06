// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.fragments

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.aprihive.methods.NetworkListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.storage.StorageReference
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.aprihive.R
import com.google.firebase.storage.FirebaseStorage
import com.cottacush.android.currencyedittext.CurrencyInputWatcher
import com.theartofdev.edmodo.cropper.CropImage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.aprihive.methods.MySnackBar
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.app.Activity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.widget.MultiAutoCompleteTextView
import com.leocardz.link.preview.library.TextCrawler
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.provider.MediaStore
import com.leocardz.link.preview.library.LinkPreviewCallback
import com.leocardz.link.preview.library.SourceContent
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.animation.Animation
import android.view.WindowManager
import com.aprihive.backend.RetrofitInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.ClipData
import com.aprihive.methods.MyActionDialog
import android.widget.Toast
import android.widget.RadioGroup
import android.widget.RadioButton
import com.aprihive.methods.SharedPrefs
import android.widget.DatePicker
import android.content.SharedPreferences
import android.view.View
import androidx.appcompat.app.AppCompatDelegate

class SetThemeModal : BottomSheetDialogFragment() {
    private var btnGroup: RadioGroup? = null
    private var darkBtn: RadioButton? = null
    private var lightBtn: RadioButton? = null
    private var systemBtn: RadioButton? = null
    private var batteryBtn: RadioButton? = null
    var sharedPrefs: SharedPrefs? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_set_theme, container, false)
        val activity: Activity? = activity
        val context = requireActivity().applicationContext
        sharedPrefs = SharedPrefs(context)
        btnGroup = view.findViewById(R.id.themeRdbGroup)
        darkBtn = view.findViewById(R.id.rdbDark)
        lightBtn = view.findViewById(R.id.rdbLight)
        systemBtn = view.findViewById(R.id.rdbFollowSystem)
        batteryBtn = view.findViewById(R.id.rdbBatteryAuto)
        when (sharedPrefs!!.themeSettings) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                darkBtn!!.setChecked(true)
                lightBtn!!.setChecked(false)
                batteryBtn!!.setChecked(false)
                systemBtn!!.setChecked(false)
            }
            AppCompatDelegate.MODE_NIGHT_NO -> {
                darkBtn!!.setChecked(false)
                lightBtn!!.setChecked(true)
                batteryBtn!!.setChecked(false)
                systemBtn!!.setChecked(false)
            }
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> {
                darkBtn!!.setChecked(false)
                lightBtn!!.setChecked(false)
                batteryBtn!!.setChecked(true)
                systemBtn!!.setChecked(false)
            }
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                darkBtn!!.setChecked(false)
                lightBtn!!.setChecked(false)
                batteryBtn!!.setChecked(false)
                systemBtn!!.setChecked(true)
            }
        }
        btnGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            when (btnGroup!!.getCheckedRadioButtonId()) {
                R.id.rdbDark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPrefs!!.saveTheme(context)
                    activity!!.recreate()
                }
                R.id.rdbLight -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPrefs!!.saveTheme(context)
                    activity!!.recreate()
                }
                R.id.rdbBatteryAuto -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    sharedPrefs!!.saveTheme(context)
                }
                R.id.rdbFollowSystem -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    sharedPrefs!!.saveTheme(context)
                }
            }
            dismiss()
        })
        return view
    }
}