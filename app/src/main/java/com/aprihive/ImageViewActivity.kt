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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import android.widget.CompoundButton
import androidx.appcompat.widget.Toolbar
import com.aprihive.OnboardingActivity
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.Timestamp
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*

class ImageViewActivity : AppCompatActivity() {
    var image: PhotoView? = null
    var toolbar: Toolbar? = null
    private var fullScreen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        val setBarsColor = SetBarsColor(this, window)
        val sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        image = findViewById(R.id.image)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(" ")
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Glide.with(this)
                .load(intent.getStringExtra("imageUri"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image!!)
        toolbar?.setNavigationOnClickListener(View.OnClickListener { super@ImageViewActivity.onBackPressed() })
        image?.setOnClickListener(View.OnClickListener {
            fullScreen = if (fullScreen) {
                setBarsColor.disableFullscreen()
                toolbar?.setBackgroundColor(resources.getColor(R.color.bg_color))
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setHomeButtonEnabled(true)
                false
            } else {
                setBarsColor.enableFullscreen()
                toolbar?.setBackgroundColor(resources.getColor(R.color.transparent))
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                supportActionBar!!.setHomeButtonEnabled(false)
                true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.image_view_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_download) {
            Glide.with(this@ImageViewActivity)
                    .asBitmap()
                    .load(intent.getStringExtra("imageUri"))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            saveImage(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}

                    })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveImage(resource: Bitmap) {
        val root = Environment.getExternalStorageDirectory().toString()
        val timestamp = Timestamp(Date())
        val time = timestamp.toDate().toString()
        val fileName = "Aprihive-$time.jpg"
        val myDirectory = File("$root/Aprihive")
        myDirectory.mkdirs()
        val file = File(myDirectory, fileName)
        if (file.exists()) {
            file.delete()
        }
        try {
            val out = FileOutputStream(file)
            resource.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this@ImageViewActivity, "Image saved to storage.", Toast.LENGTH_SHORT).show()
            MediaScannerConnection.scanFile(applicationContext, arrayOf(file.path), arrayOf("image/jpeg"), null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}