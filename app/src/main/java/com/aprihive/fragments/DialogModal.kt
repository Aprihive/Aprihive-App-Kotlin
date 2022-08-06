// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved
package com.aprihive.fragments

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
import com.leocardz.link.preview.library.TextCrawler
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
import android.content.Context
import com.aprihive.methods.MyActionDialog
import com.aprihive.methods.SharedPrefs
import android.content.SharedPreferences
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment

class DialogModal(private val mContext: Context) : DialogFragment() {
    var actionImage: ImageView? = null
    var actionTitle: TextView? = null
    var actionText: TextView? = null
    var positiveBtn: TextView? = null
    var negativeBtn: TextView? = null
    private val positiveAction: Void? = null
    private val negativeAction: Void? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.custom_dialog, container, false)
        val actionText = view.findViewById<TextView>(R.id.dialogText)
        val actionTitle = view.findViewById<TextView>(R.id.dialogTitle)
        val positiveBtn = view.findViewById<TextView>(R.id.positiveButton)
        val negativeBtn = view.findViewById<TextView>(R.id.negativeButton)
        actionText.text = "Are you sure you want to delete this post?"
        actionTitle.text = "Delete Post"
        positiveBtn.setOnClickListener { confirmDelete() }
        negativeBtn.setOnClickListener { dismiss() }
        return view
    }

    private fun confirmDelete() {}
}