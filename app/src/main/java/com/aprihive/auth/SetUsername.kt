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
import android.util.Log
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
import java.util.*

class SetUsername : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private val feedback: TextView? = null
    private var submitBtn: Button? = null
    private var usernameInput: EditText? = null
    private var username: String? = null
    private var userId: String? = null
    private var userEmail: String? = null
    private var errorFeedback: TextView? = null
    private var loading: ConstraintLayout? = null
    private var page: ConstraintLayout? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var profileUpdates: UserProfileChangeRequest? = null
    var newlyCreated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_set_username)


        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser
        userId = user!!.uid
        userEmail = user!!.email
        val setBarsColor = SetBarsColor(this, window)
        toolbar = findViewById(R.id.toolbar)
        submitBtn = findViewById(R.id.saveButton)
        usernameInput = findViewById(R.id.username)
        errorFeedback = findViewById(R.id.errorFeedback)
        loading = findViewById(R.id.loading)
        page = findViewById(R.id.page)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        toolbar!!.setNavigationOnClickListener(View.OnClickListener { super@SetUsername.onBackPressed() })
        submitBtn!!.setOnClickListener(View.OnClickListener { checkInputs() })
        val intent = intent
        newlyCreated = intent.getBooleanExtra("newlyCreated", false)
        if (newlyCreated) {
            usernameInput!!.setText(user!!.uid)
        } else {
            usernameInput!!.setText(user!!.displayName)
        }
    }

    private fun checkInputs() {
        username = usernameInput!!.text.toString().trim { it <= ' ' }
        if (username!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Username cannot be empty"
        } else if (username!!.length < 5 || username!!.length > 15) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "username should be between 5 to 10 characters"
        } else if (!username!!.matches(Regex("[a-zA-Z0-9._]*"))) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "username should not contain spaces and special characters except \"_\" and \".\". "
        } else {
            errorFeedback!!.visibility = View.GONE
            checkUsername()
            loading!!.visibility = View.VISIBLE
            disableButton()
        }
    }

    private fun checkUsername() {
        Log.d(TAG, "checking username")
        val collectionReference = db!!.collection("users")
        val query = collectionReference.whereEqualTo("username".lowercase(Locale.getDefault()), username!!.lowercase(Locale.getDefault()))
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (documentSnapshot in task.result) {
                    val existingUsernames = documentSnapshot.getString("username")
                    if (username.equals(existingUsernames, ignoreCase = true)) {
                        //username does exist
                        val snackBar = MySnackBar(this@SetUsername, page, "This username is not available", R.color.color_error_red_200, Snackbar.LENGTH_LONG)
                        loading!!.visibility = View.GONE
                        enableButton()
                        Log.d(TAG, "username exists")
                    }
                }
            }
            if (task.result.size() == 0) {
                Log.d(TAG, "User does not exist")
                changeUsername()
            }
        }
    }

    private fun changeUsername() {
        Log.d(TAG, "changing username")
        profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(username).build()
        user!!.updateProfile(profileUpdates!!)
        val details: MutableMap<String, Any?> = HashMap()
        details["username"] = username
        details["username-lower"] = username!!.lowercase(Locale.getDefault())
        reference = db!!.collection("users").document(userEmail!!)
        reference!!.update(details).addOnSuccessListener {
            val snackBar = MySnackBar(this@SetUsername, page, "Username updated!", R.color.color_success_green_300, Snackbar.LENGTH_INDEFINITE)
            loading!!.visibility = View.GONE
            val handler = Handler()
            handler.postDelayed({
                if (newlyCreated) {
                    val i = Intent(this@SetUsername, VerifyEmail::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val i = Intent(this@SetUsername, EditProfileActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }, 1500)
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
        private const val TAG = "checks"
    }
}