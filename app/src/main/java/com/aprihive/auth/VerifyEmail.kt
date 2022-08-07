package com.aprihive.auth

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.aprihive.methods.NetworkListener
import android.os.Bundle
import com.aprihive.R
import com.aprihive.methods.SetBarsColor
import android.content.Intent
import com.aprihive.fragments.ForgotPassword
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.aprihive.Home
import com.aprihive.auth.VerifyEmail
import com.google.android.gms.tasks.OnFailureListener
import com.aprihive.auth.Login
import com.aprihive.methods.MySnackBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnSuccessListener
import com.aprihive.auth.SetUsername
import com.aprihive.EditProfileActivity
import com.aprihive.auth.SignUp
import android.widget.Toast
import com.google.firebase.firestore.*

class VerifyEmail : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private val reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private val profileUpdates: UserProfileChangeRequest? = null
    private var userId: String? = null
    private var userEmail: String? = null
    private val emailVerified = false
    private var errorFeedback: TextView? = null
    private var loading: ConstraintLayout? = null
    private var page: ConstraintLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_verify_email)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser
        userId = user!!.uid
        userEmail = user!!.email
        val setBarsColor = SetBarsColor(this, window)
        errorFeedback = findViewById(R.id.errorFeedback)
        loading = findViewById(R.id.loading)
        page = findViewById(R.id.page)
        errorFeedback!!.setOnClickListener(View.OnClickListener {
            checkVerification()
            user!!.sendEmailVerification()
            disableButton()
            val handler = Handler()
            handler.postDelayed({ enableButton() }, 100000)
        })
    }

    private fun checkVerification() {
        errorFeedback!!.visibility = View.GONE
        loading!!.visibility = View.VISIBLE
        user!!.reload().addOnCompleteListener {
            if (user!!.isEmailVerified) {
                errorFeedback!!.visibility = View.GONE
                loading!!.visibility = View.VISIBLE
                val i = Intent(this@VerifyEmail, Home::class.java)
                i.putExtra("newly created", true)
                startActivity(i)
            } else {
                errorFeedback!!.visibility = View.VISIBLE
                loading!!.visibility = View.GONE
                Toast.makeText(this@VerifyEmail, "Email not verified", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableButton() {
        errorFeedback!!.setTextColor(resources.getColor(R.color.color_chip_blue_300))
        errorFeedback!!.background = resources.getDrawable(R.drawable.light_blue_bg_chip)
        errorFeedback!!.setOnClickListener {
            user!!.sendEmailVerification()
            disableButton()
            val handler = Handler()
            handler.postDelayed({ enableButton() }, 100000)
        }
    }

    private fun disableButton() {
        errorFeedback!!.setTextColor(resources.getColor(R.color.white_text_color))
        errorFeedback!!.background = resources.getDrawable(R.drawable.disabled_button)
        errorFeedback!!.setOnClickListener {
            //nothing
        }
    }

    override fun onResume() {
        super.onResume()
        checkVerification()
    }
}