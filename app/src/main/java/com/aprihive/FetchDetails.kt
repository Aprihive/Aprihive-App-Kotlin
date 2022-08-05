// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import android.widget.TextView
import com.google.firebase.firestore.ListenerRegistration
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
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
import com.google.firebase.firestore.CollectionReference
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot
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
import android.util.Log
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import android.widget.CompoundButton
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.Timestamp
import java.lang.Exception
import java.util.*

class FetchDetails : AppCompatActivity() {
    private var getUserEmail: String? = null
    private var getUsernameData: String? = null
    var db: FirebaseFirestore? = null
    private var getUsername: String? = null
    private var getPhone: String? = null
    private var getBio: String? = null
    private var getSchool: String? = null
    private var getProfileImageUrl: String? = null
    private var getTwitter: String? = null
    private var getInstagram: String? = null
    private var getFullname: String? = null
    private var getVerified: Boolean? = null
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var getType: String? = null
    private var getPostId: String? = null
    private var getAuthorEmail: String? = null
    private var getReceiverUsername: String? = null
    private var getSenderUsername: String? = null
    private var getSenderEmail: String? = null
    private var getRequestText: String? = null
    private var getPostText: String? = null
    private var getPostImageLink: String? = null
    private var getRequestedOn: Timestamp? = null
    private var getDeadline: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_fetch_details)
        val setBarsColor = SetBarsColor(this, window)
        val sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        try {
            val data = intent.data!!
            getUsernameData = data.lastPathSegment!!.lowercase(Locale.getDefault())
            Log.e("debug", "used username")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("debug", "onError: not a user link ")
        }
        if (user!!.displayName!!.lowercase(Locale.getDefault()) == getUsernameData) {
            val i = Intent(this@FetchDetails, PersonalProfileActivity::class.java)
            startActivity(i)
            finish()
        } else if (intent.getStringExtra("type") == "requestNotification") {
            requestInfoFromDb
        } else {
            userInfoFromDbByUsername
        }
    }

    //.orderBy("registered on", "asc")
    private val requestInfoFromDb: Unit
        private get() {
            val notificationsQuery = db!!.collection("users").document(user!!.email!!).collection("requests").document(intent.getStringExtra("requestId")!!)

            //.orderBy("registered on", "asc")
            notificationsQuery.get().addOnSuccessListener { value ->
                getType = value.getString("type")
                getPostId = value.getString("postId")
                getAuthorEmail = value.getString("authorEmail")
                getReceiverUsername = value.getString("receiverUsername")
                getSenderUsername = value.getString("senderUsername")
                getSenderEmail = value.getString("senderEmail")
                getRequestText = value.getString("requestText")
                getPostText = value.getString("postText")
                getPostImageLink = value.getString("postImageLink")
                getRequestedOn = value.getTimestamp("requested on")
                getDeadline = value.getString("deadLine")
                openRequestPage()
            }
        }

    //retrieve from firestore
    private val userInfoFromDbByUsername: Unit
        private get() {

            //retrieve from firestore
            Log.e("debug", "checking username")
            val collectionReference = db!!.collection("users")
            val query = collectionReference.whereEqualTo("username-lower", getUsernameData!!.lowercase(Locale.getDefault()))
            query.get().addOnCompleteListener { task ->
                Log.e("debug", getUsernameData!!.lowercase(Locale.getDefault()) + " checking ongoing")
                if (task.isSuccessful) {
                    for (value in task.result) {
                        Log.e("debug", "check success")
                        getFullname = value.getString("name")
                        getPhone = value.getString("phone")
                        getUsername = value.getString("username")
                        getUserEmail = value.getString("email")
                        getVerified = value.getBoolean("verified")
                        getBio = value.getString("bio")
                        getSchool = value.getString("school")
                        getProfileImageUrl = value.getString("profileImageLink")
                        getTwitter = value.getString("twitter")
                        getInstagram = value.getString("instagram")
                        Log.e("fetch", "fetched finished")
                        openProfile()
                    }
                }
                if (task.result.size() == 0) {
                    Log.d("debug", "User does not exist")
                    Toast.makeText(this@FetchDetails, "User does not exist!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@FetchDetails, Home::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

    private fun openProfile() {
        val intent = Intent(this@FetchDetails, UserProfileActivity::class.java)
        intent.putExtra("getEmail", getUserEmail)
        intent.putExtra("getVerified", getVerified)
        intent.putExtra("getPhone", getPhone)
        intent.putExtra("getTwitter", getTwitter)
        intent.putExtra("getInstagram", getInstagram)
        intent.putExtra("getUsername", getUsername)
        intent.putExtra("getFullName", getFullname)
        intent.putExtra("getProfileImageUrl", getProfileImageUrl)
        intent.putExtra("getBio", getBio)
        intent.putExtra("getSchoolName", getSchool)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun openRequestPage() {
        val intent = Intent(this@FetchDetails, RequestDetails::class.java)
        intent.putExtra("getType", getType)
        intent.putExtra("getSenderName", getSenderUsername)
        intent.putExtra("getReceiverName", getReceiverUsername)
        intent.putExtra("getDeadline", getDeadline)
        intent.putExtra("getPostId", getPostId)
        intent.putExtra("getPostImageLink", getPostImageLink)
        intent.putExtra("getPostText", getPostText)
        intent.putExtra("getRequestText", getRequestText)
        intent.putExtra("getRequestedOn", getRequestedOn)
        intent.putExtra("getSenderEmail", getSenderEmail)
        intent.putExtra("getReceiverEmail", getAuthorEmail)
        // intent.putExtra("refreshAction", (Serializable) refreshRequestsRunnable);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}