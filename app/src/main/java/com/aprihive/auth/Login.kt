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
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
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
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.*
import java.util.HashMap

class Login : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var forgotPassword: TextView? = null
    private var errorFeedback: TextView? = null
    private var submitBtn: Button? = null
    private var emailInput: EditText? = null
    private var passwordInput: EditText? = null
    private var loading: ConstraintLayout? = null
    private var page: ConstraintLayout? = null
    private var email: String? = null
    private var password: String? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private val profileUpdates: UserProfileChangeRequest? = null
    private var networkListener: NetworkListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val setBarsColor = SetBarsColor(this, window)
        networkListener = NetworkListener(this, page, window)


        ///declare
        toolbar = findViewById(R.id.toolbar)
        submitBtn = findViewById(R.id.submitBtn)
        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        errorFeedback = findViewById(R.id.errorFeedback)
        loading = findViewById(R.id.loading)
        page = findViewById(R.id.page)
        toolbar = findViewById(R.id.toolbar)
        forgotPassword = findViewById(R.id.forgotPassword)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        toolbar!!.setNavigationOnClickListener(View.OnClickListener { super@Login.onBackPressed() })
        val privacy = findViewById<TextView>(R.id.privacy)
        privacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/privacy"))
            startActivity(intent)
        }
        forgotPassword!!.setOnClickListener(View.OnClickListener {
            val bottomSheet = ForgotPassword()
            bottomSheet.show(supportFragmentManager, "TAG")
        })
        submitBtn!!.setOnClickListener(View.OnClickListener { checkInputs() })
        val networkIntentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkListener!!.networkListenerReceiver, networkIntentFilter)
    }

    /* @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(net, intentFilter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver();
    }*/
    private fun checkInputs() {
        //get values from input
        email = emailInput!!.text.toString().trim { it <= ' ' }
        password = passwordInput!!.text.toString().trim { it <= ' ' }

        //check email
        if (email!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Email field cannot be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Please input a valid email address"
        } else if (password!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Password cannot be empty"
        } else {
            errorFeedback!!.visibility = View.GONE
            if (networkListener!!.connected!!) {
                checkUser()
                disableButton()
                loading!!.visibility = View.VISIBLE
            }
        }
    }

    private fun checkUser() {
        auth!!.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user = auth!!.currentUser
                fCMToken
                assert(user != null)
                if (user!!.isEmailVerified) {
                    //redirect to home
                    val i = Intent(this@Login, Home::class.java)
                    startActivity(i)
                    finish()
                } else {
                    //redirect to email verification
                    val i = Intent(this@Login, VerifyEmail::class.java)
                    startActivity(i)
                    finish()
                }
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "could not login")
            enableButton()
            loading!!.visibility = View.GONE
            var errorMsg = "We could not log you in at this time.\nPlease try again later."
            if (e.localizedMessage.contains("The user account has been disabled")) {
                errorMsg = "This account has been disabled.\nPlease contact an administrator."
            } else if (e.localizedMessage.contains("The password is invalid")) {
                errorMsg = "The Email/Password combination you have entered is invalid"
            } else if (e.localizedMessage.contains("There is no user record corresponding to this identifier")) {
                errorMsg = "We could not find any account matching the details you provided"
            }
            Log.e(TAG, "Error Logging in: " + e.localizedMessage)
            val snackBar = MySnackBar(this@Login, page, errorMsg, R.color.color_error_red_200, Snackbar.LENGTH_LONG)
        }
    }

    // Get new FCM registration token
    private val fCMToken: Unit
        private get() {
            FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result
                        val details: MutableMap<String, Any?> = HashMap()
                        details["fcm-token"] = token
                        reference = db!!.collection("users").document(auth!!.currentUser!!.email!!)
                        reference!!.update(details).addOnSuccessListener { Log.e(TAG, "onSuccess: Stored new token") }.addOnFailureListener { e -> Log.e(TAG, "onFailed: failed to store new token$e") }
                    })
        }

    private fun enableButton() {
        submitBtn!!.visibility = View.VISIBLE
        submitBtn!!.background = resources.getDrawable(R.drawable.blue_button)
        submitBtn!!.setOnClickListener { checkInputs() }
    }

    private fun disableButton() {
        submitBtn!!.visibility = View.INVISIBLE
        submitBtn!!.background = resources.getDrawable(R.drawable.disabled_button)
        submitBtn!!.setOnClickListener {
            //nothing;
        }
    }

    companion object {
        private const val TAG = "ok"
    }
}