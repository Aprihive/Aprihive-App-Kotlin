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
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import android.widget.CompoundButton
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import java.util.ArrayList

class OnboardingActivity : AppCompatActivity() {
    private var nextBtn: TextView? = null
    private var skipBtn: TextView? = null
    private var tabIndicator: TabLayout? = null
    private var adapter: OnboardingViewAdapter? = null
    private var pager: ViewPager? = null
    private var position = 0
    private var sharedPrefs: SharedPrefs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        tabIndicator = findViewById(R.id.tabLayout)
        nextBtn = findViewById(R.id.nextBtn)
        skipBtn = findViewById(R.id.skipBtn)
        val setBarsColor = SetBarsColor(this, window)
        sharedPrefs = SharedPrefs(this)
        val title1 = "Welcome to Aprihive!"
        val title2 = "Make Connections"
        val title3 = "Buying and Selling made easy"
        val title4 = "Own your Mini Shop!"
        val text1 = "The open social marketing network for everyone"
        val text2 = "Get connected to vendors or customers around you for the products or services you need or provide."
        val text3 = "Send a request for a product or service you are interested in getting or providing, agree on pricing, payment method and delivery method."
        val text4 = "Get started by clicking the continue button!\nJoin other users in building the Aprihive community."
        val text5 = "Create your personalised mini-shops by adding items and services you provide to your catalogue."

        //fill list screen
        val list: MutableList<OnboardingModel> = ArrayList()
        list.add(OnboardingModel(title1, text1, R.drawable.brand_logo, View.INVISIBLE))
        list.add(OnboardingModel(title2, text2, R.drawable.vg_shopping, View.INVISIBLE))
        list.add(OnboardingModel(title3, text3, R.drawable.vg_agreement, View.INVISIBLE))
        list.add(OnboardingModel(title4, text5, R.drawable.vg_boy_skating, View.VISIBLE))


        //setup viewpager
        pager = findViewById(R.id.viewPager)
        adapter = OnboardingViewAdapter(this, list)
        pager!!.setAdapter(adapter)

        //setup tablayout with viewpager
        tabIndicator!!.setupWithViewPager(pager)
        pager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 0 || position == 1 || position == 2) {
                    nextBtn!!.setVisibility(View.VISIBLE)
                    skipBtn!!.setVisibility(View.VISIBLE)
                    tabIndicator!!.setVisibility(View.VISIBLE)
                } else {
                    val fadeAnim = AnimationUtils.loadAnimation(this@OnboardingActivity, R.anim.fade_down_animation)
                    nextBtn!!.setAnimation(fadeAnim)
                    nextBtn!!.setVisibility(View.INVISIBLE)
                    skipBtn!!.setAnimation(fadeAnim)
                    skipBtn!!.setVisibility(View.INVISIBLE)
                    tabIndicator!!.setAnimation(fadeAnim)
                    tabIndicator!!.setVisibility(View.INVISIBLE)
                }
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

        //set btn listener
        nextBtn!!.setOnClickListener(View.OnClickListener { //set position
            position = pager!!.getCurrentItem()
            if (position < list.size) {
                position++
                pager!!.setCurrentItem(position)
            }

            //hide nxt btn
            if (position == list.size - 1) {
                nextBtn!!.setVisibility(View.INVISIBLE)
                position++
                pager!!.setCurrentItem(position)
            }
        })
        skipBtn!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
            startActivity(intent)
        })
        setShowIntro()
    }

    fun setShowIntro() {
        sharedPrefs!!.shownOnboard = true
        sharedPrefs!!.savePrefs(this)
    }
}