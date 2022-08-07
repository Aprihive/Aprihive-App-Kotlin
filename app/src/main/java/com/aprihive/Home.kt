// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive

import android.Manifest
import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.res.Resources.NotFoundException
import com.aprihive.auth.Login
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.aprihive.SettingsActivity
import com.aprihive.PaymentActivity
import androidx.core.view.GravityCompat
import com.github.chrisbanes.photoview.PhotoView
import com.bumptech.glide.request.target.CustomTarget
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import android.os.*
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.firestore.*
import java.lang.Exception

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var viewPager: ViewPager? = null
    private var tabLayout: BottomNavigationView? = null
    private var networkListener: NetworkListener? = null
    private val loading: ConstraintLayout? = null
    private var page: ConstraintLayout? = null
    private var toolbar: Toolbar? = null
    private var navigView: NavigationView? = null
    private var drawer: DrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private val profileUpdates: UserProfileChangeRequest? = null
    private var navName: TextView? = null
    private var navUsername: TextView? = null
    private var navImage: ImageView? = null
    private var verificationIcon: ImageView? = null
    private var registerQuery: ListenerRegistration? = null
    private var logo: ImageView? = null
    var fab: FloatingActionButton? = null
    private var getFullname: String? = null
    private var getUsername: String? = null
    private var getProfilePic: String? = null
    private var getVerified = false
    var bundle: Bundle? = null
    private val postRefresh: Runnable? = null
    private var sharedPrefs: SharedPrefs? = null
    private val CHANNEL_ID = "welcome_notification"
    private var isUserNew: Boolean? = null
    var isAdmin: Boolean? = null
        private set
    private var signOut: TextView? = null
    private var support: TextView? = null
    private var faq: TextView? = null
    private var action: Runnable? = null
    private var getLocation: String? = null
    private var getAction: String? = null
    private var getTitle: String? = null
    private var getText: String? = null
    private var getActionText: String? = null
    private var getCloseable: Boolean? = null
    private var getActionType: String? = null
    private var getActive: Boolean? = null
    private var getIconType: String? = null
    private var getIconColor: String? = null
    private var getVersion: String? = null
    private var notifyReference: DocumentReference? = null
    private var notifyRegisterQuery: ListenerRegistration? = null
    private var notificationGotten = false
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var notified = false
    private var adapter: HomeViewPagerAdapter? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var getThreat: Boolean? = null
    private var threatIcon: ImageView? = null
    var analytics: FirebaseAnalytics? = null
    var feedbackBar: ConstraintLayout? = null
    var feedbackBarText: TextView? = null
    var feedbackBarAnimation: LottieAnimationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_home)
        sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs!!.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        analytics = FirebaseAnalytics.getInstance(this)
        val altBundle = Bundle()
        altBundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id")
        altBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name")
        altBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        analytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, altBundle)
        sharedPreferences = getSharedPreferences("aprihive", MODE_PRIVATE)
        editor = sharedPreferences?.edit()

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
        val authStateListener = AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                val intent = Intent(this@Home, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
                //nothing
            }
        }
        user = auth!!.currentUser

        //reference = db.collection("users").document(user.getEmail());
        val setBarsColor = SetBarsColor(this, window)
        adapter = HomeViewPagerAdapter(supportFragmentManager)
        bundle = Bundle()


        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.bar)
        page = findViewById(R.id.page)
        navigView = findViewById(R.id.nav_view)
        signOut = navigView?.findViewById(R.id.logout)
        support = navigView?.findViewById(R.id.support)
        faq = navigView?.findViewById(R.id.faqs)
        drawer = findViewById(R.id.drawer)
        toolbar = findViewById(R.id.toolbar)
        fab = findViewById(R.id.fabAddPost)
        feedbackBar = findViewById(R.id.feedbackBar)
        feedbackBarText = findViewById(R.id.textFeedback)
        feedbackBarAnimation = findViewById(R.id.animationView)

        feedbackBarAnimation?.setMinFrame(56)
        feedbackBarAnimation?.setMaxFrame(156)
        feedbackBarAnimation?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                feedbackBarAnimation?.setFrame(156)
                Handler().postDelayed({
                    val fadeDown = AnimationUtils.loadAnimation(this@Home, R.anim.fade_down_animation)
                    feedbackBar?.setAnimation(fadeDown)
                    feedbackBar?.setVisibility(View.GONE)
                    fab?.show()
                }, 2000)
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        logo = findViewById(R.id.logoImageView)
        navName = navigView?.getHeaderView(0)?.findViewById(R.id.nav_profile_name)
        navUsername = navigView?.getHeaderView(0)?.findViewById(R.id.nav_profile_username)
        navImage = navigView?.getHeaderView(0)?.findViewById(R.id.nav_profile_pic)
        verificationIcon = navigView?.getHeaderView(0)?.findViewById(R.id.verifiedIcon)
        threatIcon = navigView?.getHeaderView(0)?.findViewById(R.id.warningIcon)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navImage?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@Home, PersonalProfileActivity::class.java)
            startActivity(i)
        })
        navName?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@Home, PersonalProfileActivity::class.java)
            startActivity(i)
        })
        action = Runnable {
            auth!!.signOut()
            try {
                mGoogleSignInClient!!.signOut().addOnCompleteListener {
                    db!!.clearPersistence()
                    val discover = Discover()
                    discover.onStop()
                    registerQuery!!.remove()
                    notifyRegisterQuery!!.remove()
                    val intent = Intent(this@Home, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            db!!.clearPersistence()
            val discover = Discover()
            discover.onStop()
            registerQuery!!.remove()
            notifyRegisterQuery!!.remove()
            val intent = Intent(this@Home, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
        signOut?.setOnClickListener(View.OnClickListener {
            val dialog = MyActionDialog(this@Home, "Sign Out?", "Are you sure you want to sign out?", R.drawable.ic_exit, R.color.color_error_red_200, action!!, "Yes, Sign out", "No, Just kidding.")
            dialog.show()
        })
        support?.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.com/support"))
            startActivity(i)
        })
        faq?.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.com/faqs"))
            startActivity(i)
        })
        fab?.setOnClickListener(View.OnClickListener {
            val bottomSheet = AddPostModal(Discover.refreshPostsRunnable!!)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "TAG")
        })

        //init action bar drawer toggle
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        drawer?.addDrawerListener(toggle!!)
        toggle!!.isDrawerIndicatorEnabled = true
        toggle!!.syncState()
        navigView?.setNavigationItemSelectedListener(this)
        auth!!.addAuthStateListener {
            if (user == null) {
                auth!!.signOut()
                db!!.clearPersistence()
                finish()
                val intent = Intent(this@Home, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        //setUserDetailsInNavbar();
        networkListener = NetworkListener(this, page, window)
        val networkIntentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkListener!!.networkListenerReceiver, networkIntentFilter)

        //viewPager.setAdapter(adapter);
        tabLayout?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> viewPager?.setCurrentItem(0)
                R.id.find -> viewPager?.setCurrentItem(1)
                R.id.requests -> viewPager?.setCurrentItem(2)
                R.id.messages -> viewPager?.setCurrentItem(3)
            }
            true
        })
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        tabLayout?.menu?.findItem(R.id.home)?.isChecked = true
                        if (viewPager?.getCurrentItem() != 0) {
                            showHamburger()
                            findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
                        }
                        fab?.show()
                    }
                    1 -> {
                        tabLayout?.menu?.findItem(R.id.find)?.isChecked = true
                        if (viewPager?.currentItem != 1) {
                            showHamburger()
                            findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
                        }
                        fab?.hide()
                    }
                    2 -> {
                        tabLayout?.menu?.findItem(R.id.requests)?.isChecked = true
                        showHamburger()
                        findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
                        fab?.hide()
                    }
                    3 -> {
                        tabLayout?.menu?.findItem(R.id.messages)?.isChecked = true
                        showHamburger()
                        findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
                        fab?.hide()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        if (intent.getBooleanExtra("newly created", false)) {
            sendNotification()
        }
        if (savedInstanceState != null) {
            notificationGotten = savedInstanceState.getBoolean("notified", false)
            if (!notificationGotten) {
                notification
            }
        } else {
            notification
        }
    }

    private fun sendNotification() {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this@Home, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.notification_icon).color = ContextCompat.getColor(this, R.color.color_theme_blue)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon))
        builder.setContentTitle("Welcome to Aprihive!")
        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Welcome to the network for social marketing!\nPlease complete your profile then go ahead to create your first campaign to start networking.\nDon't forget to add your items to your catalogue!"))
        builder.priority = NotificationCompat.PRIORITY_HIGH
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, EditProfileActivity::class.java), 0)
        builder.setContentIntent(contentIntent)
        builder.setAutoCancel(true)
        val notificationManagerCompat = NotificationManagerCompat.from(this@Home)
        notificationManagerCompat.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Reminder Service"
            val description = "Basic information and app reminders."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun setUserDetailsInNavbar() {
        reference = db!!.collection("users").document(user!!.email!!)
        registerQuery = reference!!.addSnapshotListener { value, error ->
            try {
                getFullname = value!!.getString("name")
                getUsername = value.getString("username")
                getLocation = value.getString("school")
                getProfilePic = value.getString("profileImageLink")
                getVerified = value.getBoolean("verified")!!
                getThreat = value.getBoolean("threat")
                val token = value.getString("fcm-token")
                editor!!.putString("fcm-token", token)
                editor!!.apply()
                isAdmin = value.getBoolean("isAdmin")
                isUserNew = value.getBoolean("newAccount")
                val action = Runnable { startActivity(Intent(this@Home, EditProfileActivity::class.java)) }
                if (isUserNew!!) {
                    sendNotification()
                    val confirm = MyActionDialog(this@Home, "Welcome to Aprihive!", "Welcome to the social marketing network.\nPlease click continue to finish setting up your account", R.drawable.vg_boy_skating, action, "Continue", R.color.color_text_blue_500)
                    confirm.show()
                }
                bundle!!.putString("fullname", getFullname)
                bundle!!.putString("username", getUsername)
                bundle!!.putBoolean("verified", getVerified)
                bundle!!.putString("location", getLocation)
                navName!!.text = getFullname
                navUsername!!.text = "@$getUsername"
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
                        .into(navImage!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }//clear existing dialog//nothing

    //nothing
    private val notification: Unit
        private get() {
            notifyReference = db!!.collection("general").document("app notifications")
            notifyRegisterQuery = notifyReference!!.addSnapshotListener { value, error ->
                try {
                    getActive = value!!.getBoolean("active")
                    getTitle = value.getString("title")
                    getText = value.getString("text")
                    getIconType = value.getString("iconType")
                    getIconColor = value.getString("iconColor")
                    getCloseable = value.getBoolean("close")
                    getActionText = value.getString("actionText")
                    getActionType = value.getString("actionType")
                    getAction = value.getString("action")
                    getVersion = value.getString("version")
                    val action = Runnable {
                        if (getActionType == "url") {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getAction))
                            startActivity(intent)
                        } else if (getActionType == "okay") {
                            //nothing
                        } else if (getActionType == "update") {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getAction))
                            startActivity(intent)
                        } else if (getActionType == "closeApp") {
                            finishAndRemoveTask()
                        } else if (getActionType == "intent") {
                            try {
                                val i = Intent(this@Home, Class.forName(getAction))
                            } catch (e: ClassNotFoundException) {
                                e.printStackTrace()
                            }
                        } else {
                            //nothing
                        }
                    }
                    val dialog = MyActionDialog(this@Home, getTitle, getText, icon(getIconType), iconColor(getIconColor), action, getActionText, getCloseable)
                    try {
                        //clear existing dialog
                        dialog.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (getActionType == "update" && getActive!!) {
                        if (getVersion != resources.getString(R.string.version)) {
                            dialog.show()
                        }
                    } else if (getActionType != "update" && getActive!!) {
                        dialog.show()
                    }
                    if (getCloseable!!) {
                        setUserDetailsInNavbar()
                        viewPager!!.adapter = adapter
                    } else if (getActionType == "update" && getVersion == resources.getString(R.string.version)) {
                        setUserDetailsInNavbar()
                        viewPager!!.adapter = adapter
                    } else {
                        fab!!.hide()
                    }
                } catch (e: NotFoundException) {
                    e.printStackTrace()
                }
            }
            notified = true
        }

    private fun iconColor(getIconColor: String?): Int {
        val i: Int
        i = when (getIconColor) {
            "red" -> R.color.color_error_red_300
            "yellow" -> R.color.color_yellow
            "blue" -> R.color.color_theme_blue
            "green" -> R.color.color_green
            else -> R.color.color_theme_blue
        }
        return i
    }

    private fun icon(getIconType: String?): Int {
        val i: Int
        i = when (getIconType) {
            "warning" -> R.drawable.ic_warning
            "info" -> R.drawable.ic_info
            "alert" -> R.drawable.ic_alert
            "done" -> R.drawable.ic_done
            "cancel" -> R.drawable.ic_cancel
            "notify" -> R.drawable.ic_notifications_active
            "logo" -> R.drawable.icon
            "fire" -> R.drawable.ic_feed
            else -> R.drawable.ic_info
        }
        return i
    }

    fun showHamburger() {
        toggle!!.isDrawerIndicatorEnabled = true
    }

    fun hideHamburger() {
        toggle!!.isDrawerIndicatorEnabled = false
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        //permissions
        verifyPermissions()
        if (user != null) {
            //nothing
            if (user!!.displayName == user!!.uid) {
                val intent = Intent(this@Home, SetUsername::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this@Home, Login::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            // Discover.refreshPostsRunnable.run();
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun verifyPermissions(): Boolean {

        // This will return the current Status
        val permissionExternalMemory = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val PERMISSIONS = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
            return false
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.myProfile) {
            val i = Intent(this@Home, PersonalProfileActivity::class.java)
            startActivity(i)
        }
        if (item.itemId == R.id.infoActions) {
            val i = Intent(this@Home, SettingsActivity::class.java)
            startActivity(i)
        }
        if (item.itemId == R.id.payments) {
            //TODO: integrate payments
            val i = Intent(this@Home, PaymentActivity::class.java)
            startActivity(i)
        }

        // if (item.getItemId() == R.id.signOut){
        //     auth.signOut();
        //     db.clearPersistence();
        //     Discover discover = new Discover();
        //     discover.onStop();
        //     registerQuery.remove();
        //     Intent intent = new Intent(Home.this, MainActivity.class);
        //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //     startActivity(intent);
        //     finish();
        // }
        drawer!!.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("notified", notified)
    }
}