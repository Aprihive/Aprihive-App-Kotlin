// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.StorageReference
import androidx.cardview.widget.CardView
import com.aprihive.methods.NetworkListener
import com.rilixtech.CountryCodePicker
import com.google.firebase.storage.FirebaseStorage
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
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import androidx.appcompat.widget.Toolbar
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.firestore.*
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RequestDetails : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var getFullname: String? = null
    private var getUsername: String? = null
    private var getProfilePic: String? = null
    private var getVerified = false
    private val refreshNotifications: Runnable? = null
    private var toolbar: Toolbar? = null
    private var getType: String? = null
    private var getSenderUsername: String? = null
    private var getReceiverUsername: String? = null
    private var getDeadline: String? = null
    private var getPostId: String? = null
    private var getPostImageLink: String? = null
    private var getPostText: String? = null
    private var getRequestText: String? = null
    private var getRequestedOn: String? = null
    private var getSenderEmail: String? = null
    private var getReceiverEmail: String? = null
    private var postImage: ImageView? = null
    private var profileImage: ImageView? = null
    private var verificationIcon: ImageView? = null
    private var senderName: TextView? = null
    private var senderUsername: TextView? = null
    private var postText: TextView? = null
    private var requestText: TextView? = null
    private var deadline: TextView? = null
    private var requestedDate: TextView? = null
    private var actionButton: TextView? = null
    private var registerQuery: ListenerRegistration? = null
    private var bundle: Bundle? = null
    private var getTwitterName: String? = null
    private var getInstagramName: String? = null
    private var threatIcon: ImageView? = null
    private var getThreat: Boolean? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)
        val setBarsColor = SetBarsColor(this, window)
        val sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser
        bundle = Bundle()
        val i = intent
        getType = i.getStringExtra("getType")
        getSenderUsername = i.getStringExtra("getSenderName")
        getReceiverUsername = i.getStringExtra("getReceiverName")
        getDeadline = i.getStringExtra("getDeadline")
        getPostId = i.getStringExtra("getPostId")
        getPostImageLink = i.getStringExtra("getPostImageLink")
        getPostText = i.getStringExtra("getPostText")
        getRequestText = i.getStringExtra("getRequestText")
        getRequestedOn = i.getStringExtra("getRequestedOn")
        getSenderEmail = i.getStringExtra("getSenderEmail")
        getReceiverEmail = i.getStringExtra("getReceiverEmail")
        toolbar = findViewById(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.title)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("")
        profileImage = findViewById(R.id.sender_profileImage)
        senderName = findViewById(R.id.sender_fullName)
        senderUsername = findViewById(R.id.sender_username)
        postImage = findViewById(R.id.postImage)
        postText = findViewById(R.id.postText)
        requestText = findViewById(R.id.requestText)
        deadline = findViewById(R.id.deadline)
        verificationIcon = findViewById(R.id.sender_verifiedIcon)
        threatIcon = findViewById(R.id.sender_warningIcon)
        actionButton = findViewById(R.id.actionButton)
        requestedDate = findViewById(R.id.sentOn)
        if (getType == "from") {
            toolbarTitle.text = getSenderUsername!!.substring(0, 1).uppercase(Locale.getDefault()) + getSenderUsername!!.substring(1).lowercase(Locale.getDefault()) + "\'s request"
            try {
                senderDetails
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            toolbarTitle.text = "Request to $getReceiverUsername"
            try {
                receiverDetails
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        toolbar.setNavigationOnClickListener(View.OnClickListener { super@RequestDetails.onBackPressed() })
        setUpActionButton(getType)


        //load post text and image into view
        if (getPostImageLink != "") {
            Glide.with(this)
                    .load(getPostImageLink)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(postImage)
        } else {
            postImage.setVisibility(View.GONE)
        }
        postText.setText(getPostText)
        //

        //load request text
        requestText.setText(getRequestText)
        bundle!!.putString("requestText", getRequestText)


        //load deadline date
        try {
            deadline.setText("Deadline: " + deadlineOn(getDeadline))
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        //load request date
        requestedDate.setText("Sent: $getRequestedOn")
    }

    private fun setUpActionButton(getType: String?) {
        if (getType == "from") {
            actionButton!!.text = "Contact"
            actionButton!!.setBackgroundColor(resources.getColor(R.color.color_theme_blue))
        } else {
            actionButton!!.text = "Cancel Request"
            actionButton!!.setBackgroundColor(resources.getColor(R.color.color_error_red_200))
        }
        val action = Runnable { deleteRequestAction() }
        actionButton!!.setOnClickListener {
            if (getType == "from") {
                contactAction()
            } else {
                val confirm = MyActionDialog(this@RequestDetails, "Confirm Delete", "Are you sure you want to delete this request? \n Note: This is a permanent action!", R.drawable.vg_delete, action)
                confirm.show()
            }
        }
    }

    private fun deleteRequestAction() {
        val senderReference = db!!.collection("users").document(user!!.email!!).collection("requests").document("requestTo:-$getReceiverUsername-for:-$getPostId")
        reference = db!!.collection("users").document(getReceiverEmail!!).collection("requests").document("requestFrom:-" + user!!.displayName + "-for:-" + getPostId)
        reference!!.delete().addOnSuccessListener {
            senderReference.delete()
            super@RequestDetails.onBackPressed()
        }
    }

    private fun contactAction() {
        val bottomSheet = ContactMethodModal()
        bottomSheet.arguments = bundle
        bottomSheet.show(supportFragmentManager, "TAG")
    }

    //4
    //7
    private val senderDetails: Unit
        private get() {
            reference = db!!.collection("users").document(getSenderEmail!!)
            registerQuery = reference!!.addSnapshotListener { value, error ->
                getFullname = value!!.getString("name")
                getUsername = value.getString("username")
                getProfilePic = value.getString("profileImageLink")
                getVerified = value.getBoolean("verified")!!
                getThreat = value.getBoolean("threat")
                getTwitterName = value.getString("twitter")
                getInstagramName = value.getString("instagram")
                bundle!!.putString("instagramName", getInstagramName)
                bundle!!.putString("twitterName", getTwitterName)
                bundle!!.putString("phoneNumber", value.getString("phone"))
                senderName!!.text = getFullname
                senderUsername!!.text = "@$getUsername"
                if (getVerified) {
                    verificationIcon!!.visibility = View.VISIBLE
                } else {
                    verificationIcon!!.visibility = View.GONE
                }
                if (getThreat!!) {
                    threatIcon!!.visibility = View.VISIBLE
                } else {
                    threatIcon!!.visibility = View.GONE
                }
                Glide.with(applicationContext)
                        .load(getProfilePic)
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder) //7
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(profileImage!!)
            }
        }

    //4
    //7
    private val receiverDetails: Unit
        private get() {
            reference = db!!.collection("users").document(getReceiverEmail!!)
            registerQuery = reference!!.addSnapshotListener { value, error ->
                try {
                    getFullname = value!!.getString("name")
                    getUsername = value.getString("username")
                    getProfilePic = value.getString("profileImageLink")
                    getVerified = value.getBoolean("verified")!!
                    senderName!!.text = getFullname
                    senderUsername!!.text = "@$getUsername"
                    if (getVerified) {
                        verificationIcon!!.visibility = View.VISIBLE
                    } else {
                        verificationIcon!!.visibility = View.GONE
                    }
                    Glide.with(applicationContext)
                            .load(getProfilePic)
                            .centerCrop() //4
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.user_image_placeholder)
                            .fallback(R.drawable.user_image_placeholder) //7
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(profileImage!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    @Throws(ParseException::class)
    private fun deadlineOn(deadline: String?): String {
        val fetchDate = "$deadline 24:59:59"
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val date = sdf.parse(fetchDate)
        val prettyTime = PrettyTime(Locale.getDefault())
        return prettyTime.format(date)
    }

    override fun onStop() {
        super.onStop()
        registerQuery!!.remove()
    }

    public override fun onPause() {
        super.onPause()
        finish()
    }
}