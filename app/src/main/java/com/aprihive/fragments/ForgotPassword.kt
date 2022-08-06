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
import com.aprihive.methods.MyActionDialog
import com.aprihive.methods.SharedPrefs
import android.content.SharedPreferences
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.firestore.DocumentReference

class ForgotPassword : BottomSheetDialogFragment() {
    private var emailInput: EditText? = null
    private var submit: Button? = null
    private var closeBtn: Button? = null
    private var info: TextView? = null
    private var title: TextView? = null
    private var image: ImageView? = null
    private var auth: FirebaseAuth? = null
    private val db: FirebaseFirestore? = null
    private val reference: DocumentReference? = null
    private val user: FirebaseUser? = null
    private val profileUpdates: UserProfileChangeRequest? = null
    private val networkListener: NetworkListener? = null
    private var loading: ConstraintLayout? = null
    private var closeScreen: ConstraintLayout? = null
    private var forgotPasswordScreen: ConstraintLayout? = null
    private var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_forgot_password, container, false)
        auth = FirebaseAuth.getInstance()
        emailInput = view.findViewById(R.id.email)
        submit = view.findViewById(R.id.submitBtn)
        info = view.findViewById(R.id.textView4)
        image = view.findViewById(R.id.imageView4)
        title = view.findViewById(R.id.textView)
        loading = view.findViewById(R.id.loading)
        closeScreen = view.findViewById(R.id.closeScreen)
        forgotPasswordScreen = view.findViewById(R.id.forgotScreen)
        closeBtn = view.findViewById(R.id.closeBtn)
        submit!!.setOnClickListener(View.OnClickListener { checkInputs() })
        closeBtn!!.setOnClickListener(View.OnClickListener { dismiss() })
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    private fun checkInputs() {
        email = emailInput!!.text.toString().trim { it <= ' ' }

        //check email
        if (email!!.isEmpty()) {
            info!!.text = "Please enter an email address to continue"
            info!!.setTextColor(resources.getColor(R.color.color_error_red_200))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            info!!.text = "Please enter a correct email address"
            info!!.setTextColor(resources.getColor(R.color.color_error_red_200))
        } else {
            resetPassword()
            disableButton()
        }
    }

    private fun disableButton() {
        submit!!.background = resources.getDrawable(R.drawable.disabled_button)
        submit!!.setOnClickListener {
            //nothing
        }
        submit!!.visibility = View.INVISIBLE
        loading!!.visibility = View.VISIBLE
    }

    private fun resetPassword() {
        auth!!.sendPasswordResetEmail(email!!).addOnSuccessListener {
            forgotPasswordScreen!!.visibility = View.INVISIBLE
            closeScreen!!.visibility = View.VISIBLE
        }.addOnFailureListener {
            enableButton()
            info!!.text = "Somehow, somewhere, something went wrong."
            info!!.setTextColor(resources.getColor(R.color.grey_color_200))
        }
    }

    private fun enableButton() {
        submit!!.background = resources.getDrawable(R.drawable.blue_button)
        submit!!.setOnClickListener { checkInputs() }
        submit!!.visibility = View.VISIBLE
        loading!!.visibility = View.GONE
    }
}