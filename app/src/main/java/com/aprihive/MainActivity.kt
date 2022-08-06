// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.TextView
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.annotation.SuppressLint
import com.aprihive.R
import com.aprihive.methods.SharedPrefs
import androidx.appcompat.app.AppCompatDelegate
import com.aprihive.methods.SetBarsColor
import android.content.Intent
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import android.view.animation.Animation
import com.aprihive.ImageViewActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.aprihive.methods.MyActionDialog
import com.google.android.gms.tasks.OnSuccessListener
import com.aprihive.fragments.ContactMethodModal
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.StorageReference
import androidx.cardview.widget.CardView
import com.aprihive.methods.NetworkListener
import com.rilixtech.CountryCodePicker
import com.google.firebase.storage.FirebaseStorage
import android.widget.ArrayAdapter
import com.theartofdev.edmodo.cropper.CropImage
import com.aprihive.auth.SetUsername
import android.text.TextWatcher
import android.text.Editable
import com.aprihive.methods.MySnackBar
import com.google.android.material.snackbar.Snackbar
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import android.app.Activity
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.android.gms.tasks.OnFailureListener
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.widget.Toast
import com.aprihive.PersonalProfileActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.aprihive.Home
import com.aprihive.UserProfileActivity
import com.aprihive.RequestDetails
import com.google.android.material.navigation.NavigationView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.SharedPreferences
import com.aprihive.adapters.HomeViewPagerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.aprihive.MainActivity
import com.aprihive.pages.Discover
import com.aprihive.fragments.AddPostModal
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.core.content.ContextCompat
import android.graphics.BitmapFactory
import android.app.PendingIntent
import com.aprihive.EditProfileActivity
import androidx.core.app.NotificationManagerCompat
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.res.Resources.NotFoundException
import com.aprihive.auth.Login
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.aprihive.SettingsActivity
import com.aprihive.PaymentActivity
import androidx.core.view.GravityCompat
import android.os.PersistableBundle
import com.github.chrisbanes.photoview.PhotoView
import com.bumptech.glide.request.target.CustomTarget
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
import android.media.MediaScannerConnection
import com.aprihive.auth.SignUp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthResult
import com.aprihive.adapters.MessagingRecyclerAdapter
import com.aprihive.models.MessageModel
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import com.aprihive.backend.RetrofitInterface
import retrofit2.converter.gson.GsonConverterFactory
import com.aprihive.MessagingActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aprihive.fragments.ReportModal
import kotlin.Throws
import org.ocpsoft.prettytime.PrettyTime
import com.aprihive.fragments.OptionsDialogModal
import android.content.ClipData
import com.google.android.material.tabs.TabLayout
import com.aprihive.adapters.OnboardingViewAdapter
import com.aprihive.models.OnboardingModel
import com.aprihive.adapters.ProfileViewPagerAdapter
import com.aprihive.pages.Feed
import com.aprihive.fragments.AddCatalogueItemModal
import com.aprihive.pages.Catalogue
import com.google.firebase.messaging.FirebaseMessagingService
import com.aprihive.PushNotificationService
import com.google.firebase.messaging.RemoteMessage
import com.aprihive.FetchDetails
import android.graphics.RectF
import android.graphics.PorterDuffXfermode
import android.graphics.PorterDuff
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import android.widget.CompoundButton
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var create: TextView? = null
    private var withGoogle: TextView? = null
    private var login: TextView? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var profileUpdates: UserProfileChangeRequest? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var sharedPrefs: SharedPrefs? = null
    private var user: FirebaseUser? = null
    var load: ConstraintLayout? = null
    private var emptyListener: View.OnClickListener? = null
    private var activeListeners: View.OnClickListener? = null
    private var token: String? = null
    private var page: ConstraintLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)


        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val setBarsColor = SetBarsColor(this, window)
        create = findViewById(R.id.create)
        login = findViewById(R.id.loginBtn)
        withGoogle = findViewById(R.id.googleBtn)
        load = findViewById(R.id.progressTab)
        page = findViewById(R.id.page)
        sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs!!.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        emptyListener = View.OnClickListener { }
        activeListeners = View.OnClickListener { view ->
            when (view.id) {
                R.id.create -> {
                    val i = Intent(this@MainActivity, SignUp::class.java)
                    startActivity(i)
                }
                R.id.loginBtn -> {
                    val intent = Intent(this@MainActivity, Login::class.java)
                    startActivity(intent)
                }
                R.id.googleBtn -> {
                    showOverlay()
                    signIn()
                }
            }
        }
        create?.setOnClickListener(activeListeners)
        login?.setOnClickListener(activeListeners)
        withGoogle?.setOnClickListener(activeListeners)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.notification_channel_id)
            val channelName = getString(R.string.notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW))
        }
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    token = if (!task.isSuccessful) {
                        hideOverlay()
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    } else {
                        // Get new FCM registration token
                        task.result
                    }
                })

        //Intent intentBgService = new Intent(this, FirebaseMessagingServiceSetup.class);
        //startService(intentBgService);
    }

    private fun showOverlay() {
        login!!.setOnClickListener(emptyListener)
        create!!.setOnClickListener(emptyListener)
        withGoogle!!.setOnClickListener(emptyListener)
        load!!.visibility = View.VISIBLE
    }

    private fun hideOverlay() {
        login!!.setOnClickListener(activeListeners)
        create!!.setOnClickListener(activeListeners)
        withGoogle!!.setOnClickListener(activeListeners)
        load!!.visibility = View.GONE
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                hideOverlay()
                Toast.makeText(this@MainActivity, "Login with Google Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        user = auth!!.currentUser
                        Log.e(TAG, "onComplete: ready to move")
                        checkPreviousLogin()
                    } else {
                        // If sign in fails, display a message to the user.
                        hideOverlay()
                        val e = task.exception
                        Log.e(TAG, "signInWithGoogle:failure " + e!!.localizedMessage)
                        var errorMsg = "Could not log you in with Google at this time.\nPlease try again later."
                        if (e.localizedMessage.contains("The user account has been disabled")) {
                            errorMsg = "This account has been disabled.\nPlease contact an administrator."
                        }
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                        val snackBar = MySnackBar(this@MainActivity, page, errorMsg, R.color.color_error_red_200, Snackbar.LENGTH_LONG)


                        //
                    }
                }
    }

    private fun checkPreviousLogin() {
        user = auth!!.currentUser
        val docReference = db!!.collection("users").document(user!!.email!!)
        docReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    fCMToken
                    val name = task.result.getString("username")
                    val imageLink = task.result.getString("profileImageLink")
                    if (name != user!!.displayName) {
                        profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                        user!!.updateProfile(profileUpdates!!)
                    }
                    if (!imageLink!!.equals(user?.photoUrl) ) {
                        profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(imageLink)).build()
                        user!!.updateProfile(profileUpdates!!)
                    }
                    val i = Intent(this@MainActivity, Home::class.java)
                    startActivity(i)
                    finish()
                } else {
                    storeDetails()
                }
            }
        }
    }

    private fun storeDetails() {
        user = auth!!.currentUser
        profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(user!!.uid.substring(0, 7)).build()
        user!!.updateProfile(profileUpdates!!)
        ////
        val details: MutableMap<String, Any?> = HashMap()


        details["name"] = user!!.displayName
        details["email"] = user!!.email
        details["uid"] = user!!.uid
        details["admin-level"] = 0
        details["username"] = user!!.uid.substring(0, 7)
        details["username-lower"] = user!!.uid.lowercase(Locale.getDefault()).substring(0, 7)
        details["bio"] = "This is what I do!"
        details["school"] = ""
        details["isAdmin"] = false
        details["newAccount"] = true
        details["phone"] = ""
        details["profileImageLink"] = user!!.photoUrl.toString()
        details["verified"] = false
        details["threat"] = false
        details["fcm-token"] = token
        details["registered on"] = Timestamp(Date())
        reference = db!!.collection("users").document(user!!.email!!)
        reference!!.set(details).addOnSuccessListener {
            Log.d(TAG, "onSuccess: user details stored")
            createUserListsDb()
            //waiting to send email b4 redirect
        }.addOnFailureListener {
            Log.e(TAG, "Failed")
            Toast.makeText(this@MainActivity, "Something went wrong.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUserListsDb() {
        val listsRef = db!!.collection("users").document(user!!.email!!).collection("lists").document("following")
        val setDefault: Map<String, Any> = HashMap()
        listsRef.set(setDefault).addOnSuccessListener {
            Log.d("Debug", "success creating lists db for user")
            //redirect to set username
            val i = Intent(this@MainActivity, SetUsername::class.java)
            i.putExtra("newlyCreated", true)
            startActivity(i)
            finish()
        }.addOnFailureListener { e ->
            e.printStackTrace()
            Log.d("Debug", "failed$e")
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

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth!!.currentUser
        val account = GoogleSignIn.getLastSignedInAccount(this)
        sharedPrefs = SharedPrefs(this@MainActivity)
        val getTheme = sharedPrefs!!.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        if (currentUser != null) {
            if (auth!!.currentUser!!.isEmailVerified) {
                val intent = Intent(this@MainActivity, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        } else if (account != null) {
            val intent = Intent(this@MainActivity, Home::class.java)
            startActivity(intent)
        } else {
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPrefs = SharedPrefs(this@MainActivity)
        val getTheme = sharedPrefs!!.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
    }

    override fun onRestart() {
        super.onRestart()
        sharedPrefs = SharedPrefs(this@MainActivity)
        val getTheme = sharedPrefs!!.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
    }

    companion object {
        private const val TAG = "ok"
        private const val RC_SIGN_IN = 123
    }
}