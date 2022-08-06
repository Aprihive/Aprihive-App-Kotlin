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
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import java.lang.Exception

class ContactMethodModal : BottomSheetDialogFragment() {
    private var whatsapp: ConstraintLayout? = null
    private var call: ConstraintLayout? = null
    private var sms: ConstraintLayout? = null
    private var twitter: ConstraintLayout? = null
    private var instagram: ConstraintLayout? = null
    private var phoneNumber: String? = null
    private val requestInfo: String? = null
    private var instagramUsername: String? = null
    private var twitterUsername: String? = null
    private var nothingText: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_contact_method, container, false)
        val activity: Activity? = activity
        val context = requireActivity().applicationContext
        whatsapp = view.findViewById(R.id.viaWhatsapp)
        call = view.findViewById(R.id.viaPhoneCall)
        sms = view.findViewById(R.id.viaSms)
        twitter = view.findViewById(R.id.viaTwitter)
        instagram = view.findViewById(R.id.viaInstagram)
        nothingText = view.findViewById(R.id.nothingText)


        // get details.
        try {
            assert(arguments != null)
            phoneNumber = requireArguments().getString("phoneNumber")
            instagramUsername = requireArguments().getString("instagramName")
            twitterUsername = requireArguments().getString("twitterName")
        } catch (e: Exception) {
            e.printStackTrace()
        }


        //set visibility
        try {
            if (twitterUsername!!.isEmpty() || twitterUsername == "null") {
                twitter!!.setVisibility(View.GONE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            twitter!!.setVisibility(View.GONE)
        }
        try {
            if (instagramUsername!!.isEmpty() || instagramUsername == "null") {
                instagram!!.setVisibility(View.GONE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            instagram!!.setVisibility(View.GONE)
        }
        try {
            if (phoneNumber!!.isEmpty() || phoneNumber == "null") {
                whatsapp!!.setVisibility(View.GONE)
                call!!.setVisibility(View.GONE)
                sms!!.setVisibility(View.GONE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            whatsapp!!.setVisibility(View.GONE)
            call!!.setVisibility(View.GONE)
            sms!!.setVisibility(View.GONE)
        }
        if (whatsapp!!.getVisibility() == View.GONE && twitter!!.getVisibility() == View.GONE && instagram!!.getVisibility() == View.GONE) {
            nothingText!!.setVisibility(View.VISIBLE)
        } else {
            nothingText!!.setVisibility(View.GONE)
        }
        val sendText = "Hello, I got your request on Aprihive. \n (Your Text Here)"
        whatsapp!!.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + sendText.replace("\n", " ").replace(" ", "%20"))
            startActivity(i)
        })
        call!!.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_DIAL)
            i.data = Uri.parse("tel:" + phoneNumber!!.trim { it <= ' ' })
            startActivity(i)
        })
        sms!!.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.fromParts("sms", phoneNumber, null)
            i.putExtra("sms_body", sendText.replace("\n", " "))
            startActivity(i)
        })
        instagram!!.setOnClickListener(View.OnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/$instagramUsername"))
                intent.setPackage("com.instagram.android")
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/$instagramUsername"))
                startActivity(intent)
            }
        })
        twitter!!.setOnClickListener(View.OnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=$twitterUsername"))
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$twitterUsername"))
                startActivity(intent)
            }
        })
        return view
    }
}