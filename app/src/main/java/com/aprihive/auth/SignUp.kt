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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.util.*

class SignUp : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var errorFeedback: TextView? = null
    private var submitBtn: Button? = null
    private var emailInput: EditText? = null
    private var nameInput: EditText? = null
    private var passwordInput: EditText? = null
    private var loading: ConstraintLayout? = null
    private var page: ConstraintLayout? = null
    private var email: String? = null
    private var password: String? = null
    private var name: String? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var profileUpdates: UserProfileChangeRequest? = null
    private var networkListener: NetworkListener? = null
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_signup)


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
        nameInput = findViewById(R.id.fullName)
        passwordInput = findViewById(R.id.password)
        errorFeedback = findViewById(R.id.errorFeedback)
        loading = findViewById(R.id.loading)
        page = findViewById(R.id.page)


        //
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        toolbar!!.setNavigationOnClickListener(View.OnClickListener { super@SignUp.onBackPressed() })
        submitBtn!!.setOnClickListener(View.OnClickListener { checkInputs() })
        val privacy = findViewById<TextView>(R.id.privacy)
        privacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/privacy"))
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val networkIntentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkListener!!.networkListenerReceiver, networkIntentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkListener!!.networkListenerReceiver)
    }

    private fun checkInputs() {

        //get values from input
        email = emailInput!!.text.toString().trim { it <= ' ' }
        password = passwordInput!!.text.toString().trim { it <= ' ' }
        name = nameInput!!.text.toString().trim { it <= ' ' }

        //check email
        if (email!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Email field cannot be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Please input a valid email address"
        } else if (name!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Name cannot be empty"
        } else if (!name!!.matches(Regex("[a-zA-Z0-9 ]*"))) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Name can only contain alphabets"
        } else if (password!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Password cannot be empty"
        } else if (password!!.length < 8 || password!!.matches(Regex("[\\d]*"))) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Your password must be at least 8 characters and must contain at least one number "
        } else {
            errorFeedback!!.visibility = View.GONE
            if (networkListener!!.connected!!) {
                createUser()
                disableButton()
                loading!!.visibility = View.VISIBLE
            }
        }
    }

    private fun createUser() {

        //get values from input
        email = emailInput!!.text.toString().trim { it <= ' ' }
        password = passwordInput!!.text.toString().trim { it <= ' ' }
        name = nameInput!!.text.toString().trim { it <= ' ' }
        auth!!.createUserWithEmailAndPassword(email!!, password!!).addOnSuccessListener { authResult ->
            user = authResult.user
            fCMToken
            assert(user != null)

            //set display name
            profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            user!!.updateProfile(profileUpdates!!)
            ////
            val details: MutableMap<String, Any?> = HashMap()
            details["name"] = name
            details["email"] = email
            details["uid"] = user!!.uid
            details["admin-level"] = 0
            details["username"] = user!!.uid.substring(0, 7)
            details["username-lower"] = user!!.uid.lowercase(Locale.getDefault()).substring(0, 7)
            details["bio"] = "This is what I do!"
            details["school"] = ""
            details["isAdmin"] = false
            details["newAccount"] = true
            details["phone"] = ""
            details["profileImageLink"] = "-"
            details["verified"] = false
            details["threat"] = false
            details["fcm-token"] = token
            details["registered on"] = Timestamp(Date())
            reference = db!!.collection("users").document(email!!)
            reference!!.set(details).addOnSuccessListener {
                Log.d(TAG, "onSuccess: user details stored")
                createUserListsDb()
                //waiting to send email b4 redirect
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed")
                val snackBar = MySnackBar(this@SignUp, page, "An error occurred while saving profile.", R.color.color_error_red_200, Snackbar.LENGTH_LONG)
                enableButton()
                Log.e(TAG, "Saving user details failed: " + e.localizedMessage)
                loading!!.visibility = View.GONE
            }
            user!!.sendEmailVerification().addOnSuccessListener {
                Log.d(TAG, "email link sent")

                //redirect to set username
                val i = Intent(this@SignUp, SetUsername::class.java)
                i.putExtra("newlyCreated", true)
                startActivity(i)
                finish()
            }.addOnFailureListener {
                Log.d(TAG, "email link not sent")
                val snackBar = MySnackBar(this@SignUp, page, "Email verification failed", R.color.color_error_red_200, Snackbar.LENGTH_LONG)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Creating account failed: " + e.localizedMessage)
            var errorMessage = "We could not sign you up at this time.\nPlease try again later."
            if (e.localizedMessage.contains("email address is already in use")) {
                errorMessage = "The email address entered is not available.\nPlease use another."
            }
            val snackBar = MySnackBar(this@SignUp, page, errorMessage, R.color.color_error_red_200, Snackbar.LENGTH_LONG)
            enableButton()
            loading!!.visibility = View.GONE
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
                        token = task.result
                    })
        }

    private fun createUserListsDb() {
        val listsRef = db!!.collection("users").document(email!!).collection("lists").document("following")
        val setDefault: Map<String, Any> = HashMap()
        listsRef.set(setDefault).addOnSuccessListener { Log.d("Debug", "success creating lists db for user") }.addOnFailureListener { e ->
            e.printStackTrace()
            Log.d("Debug", "failed$e")
        }
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