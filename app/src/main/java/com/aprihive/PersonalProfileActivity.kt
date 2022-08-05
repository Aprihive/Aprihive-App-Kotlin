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
import android.net.Uri
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import androidx.appcompat.widget.Toolbar
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.firestore.*

class PersonalProfileActivity : AppCompatActivity() {
    //firebase
    private var mAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null
    private var reference: DocumentReference? = null
    private var currentUserId: String? = null
    private val uploadTask: StorageTask<*>? = null
    private var storageReference: StorageReference? = null
    var imageUri: Uri? = null
    private val myUri: String? = null
    private var getEmail: String? = null
    private var getFullname: String? = null
    private var getUsername: String? = null
    private var getPhone: String? = null
    private var getBio: String? = null
    private var getSchool: String? = null
    private var getUserProfilePic //fetch from firebase into
            : String? = null
    private var getVerified //fetch from firebase into
            : Boolean? = null

    //firebase end
    private var toolbar: Toolbar? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var verifiedIcon: ImageView? = null
    private var profilePic: ImageView? = null
    private val email: TextView? = null
    private var fullname: TextView? = null
    private var username: TextView? = null
    private val phone: TextView? = null
    private var bio: TextView? = null
    private var schoolName: TextView? = null
    private var editProfilebutton: TextView? = null
    private var registerQuery: ListenerRegistration? = null
    private var addPostFab: FloatingActionButton? = null
    private var addCatalogueItemFab: FloatingActionButton? = null
    private var bundle: Bundle? = null
    private var upvoteReference: DocumentReference? = null
    private var upvoteRegisterQuery: ListenerRegistration? = null
    private var upvoteText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_profile)
        val sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        val setBarsColor = SetBarsColor(this, window)
        val adapter = ProfileViewPagerAdapter(supportFragmentManager)
        bundle = Bundle()

        //init firebase
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        currentUser = mAuth!!.currentUser
        currentUserId = currentUser!!.uid
        reference = firestore!!.collection("users").document(currentUser!!.email!!)
        upvoteReference = firestore!!.collection("users").document(currentUser!!.email!!).collection("lists").document("following")

        //
        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.profileViewPager)
        tabLayout = findViewById(R.id.tabLayout)
        verifiedIcon = findViewById(R.id.verifiedIcon)
        profilePic = findViewById(R.id.profileImage)
        fullname = findViewById(R.id.fullName)
        username = findViewById(R.id.username)
        bio = findViewById(R.id.description)
        schoolName = findViewById(R.id.schoolName)
        upvoteText = findViewById(R.id.upvoteText)
        editProfilebutton = findViewById(R.id.editProfileButton)
        addPostFab = findViewById(R.id.fabAddPost)
        addCatalogueItemFab = findViewById(R.id.fabAddItem)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setTitle("My Profile")
        addPostFab.setOnClickListener(View.OnClickListener {
            val bottomSheet = AddPostModal(Feed.refreshPostsRunnable)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "TAG")
        })
        addCatalogueItemFab.setOnClickListener(View.OnClickListener {
            val bottomSheet = AddCatalogueItemModal(Catalogue.refreshItemsRunnable)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "TAG")
        })
        toolbar.setNavigationOnClickListener(View.OnClickListener { super@PersonalProfileActivity.onBackPressed() })
        viewPager.setAdapter(adapter)
        viewPager.setTag(mAuth!!.currentUser!!.email)
        tabLayout.setupWithViewPager(viewPager)

        // get tabs from adapter
        val feedTab = tabLayout.getTabAt(0)
        val catalogueTab = tabLayout.getTabAt(1)
        feedTab!!.setIcon(R.drawable.ic_feed).text = "My Feed"
        catalogueTab!!.setIcon(R.drawable.ic_shopping_cart).text = "My Catalogue"
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        addCatalogueItemFab.hide()
                        addPostFab.show()
                    }
                    1 -> {
                        addPostFab.hide()
                        addCatalogueItemFab.show()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        backendCodes()
        checkUpvotes()
        editProfilebutton.setOnClickListener(View.OnClickListener {
            val i = Intent(this@PersonalProfileActivity, EditProfileActivity::class.java)
            i.putExtra("name", getFullname)
            i.putExtra("bio", getBio)
            i.putExtra("school", getSchool)
            startActivity(i)
        })
    }

    private fun backendCodes() {
        if (mAuth!!.currentUser != null) {
            retrieveDetailsFromFirestore()
        }
    }

    private fun retrieveDetailsFromFirestore() {
        getEmail = currentUser!!.email


        //retrieve from firestore
        registerQuery = reference!!.addSnapshotListener { documentSnapshot, e ->
            getFullname = documentSnapshot!!.getString("name")
            getPhone = documentSnapshot.getString("phone")
            getUsername = documentSnapshot.getString("username")
            getVerified = documentSnapshot.getBoolean("verified")
            getBio = documentSnapshot.getString("bio")
            getSchool = documentSnapshot.getString("school")
            getUserProfilePic = documentSnapshot.getString("profileImageLink")
            bundle!!.putString("fullname", getFullname)
            bundle!!.putString("username", getUsername)
            bundle!!.putBoolean("verified", getVerified!!)
            Glide.with(applicationContext) //2
                    .load(getUserProfilePic) //3
                    .centerCrop() //4
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.user_image_placeholder) //7
                    .fallback(R.drawable.user_image_placeholder) //7
                    .into(profilePic!!) //8
            assert(getUserProfilePic != null)
            if (getUserProfilePic != "") {
                profilePic!!.setOnClickListener {
                    val i = Intent(this@PersonalProfileActivity, ImageViewActivity::class.java)
                    i.putExtra("imageUri", getUserProfilePic)
                    startActivity(i)
                }
            }
            if (getVerified!!) {
                verifiedIcon!!.visibility = View.VISIBLE
            } else {
                verifiedIcon!!.visibility = View.INVISIBLE
            }
            fullname!!.text = getFullname
            username!!.text = "@$getUsername"
            bio!!.text = getBio
            schoolName!!.text = getSchool
        }
        //end of retrieve
    }

    private fun checkUpvotes(): Int {
        val count = intArrayOf(0)
        upvoteRegisterQuery = upvoteReference!!.addSnapshotListener { value, error ->
            if (value!!.exists()) {
                val map = value.data
                count[0] = map!!.size
                if (count[0] == 1) {
                    upvoteText!!.text = count[0].toString() + " Upvote."
                } else {
                    upvoteText!!.text = count[0].toString() + " Upvotes."
                }
            }
        }
        return count[0]
    }

    override fun onPause() {
        super.onPause()
        registerQuery!!.remove()
        upvoteRegisterQuery!!.remove()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        registerQuery!!.remove()
        upvoteRegisterQuery!!.remove()
    }
}