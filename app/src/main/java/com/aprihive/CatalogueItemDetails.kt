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
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import androidx.appcompat.widget.Toolbar
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_catalogue_item_details.*
import kotlinx.android.synthetic.main.content_catalogue_item_details.*

class CatalogueItemDetails : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var getFullname: String? = null
    private var getUsername: String? = null
    private var getProfilePic: String? = null
    private var getVerified = false
    private val refreshNotifications: Runnable? = null
    private val toolbar: Toolbar? = null
    private var getItemId: String? = null
    private var getItemImageLink: String? = null
    private var getItemName: String? = null
    private var getItemDescription: String? = null
    private val getRequestedOn: String? = null
    private var getSellerEmail: String? = null
    private val getReceiverEmail: String? = null
    private val itemImage: ImageView? = null
    private var itemName: TextView? = null
    private var itemDescription: TextView? = null
    private var actionButton: TextView? = null
    private var itemPrice: TextView? = null
    private var registerQuery: ListenerRegistration? = null
    private var bundle: Bundle? = null
    private var getTwitterName: String? = null
    private var getInstagramName: String? = null
    private var getItemPrice: String? = null
    private var getItemUrl: String? = null
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout?>? = null
    private var cover: View? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue_item_details)

        val sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)

        SetBarsColor(this, window)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser

        bundle = Bundle()

        val i = intent
        getItemId = i.getStringExtra("getItemId")
        getItemImageLink = i.getStringExtra("getItemImageLink")
        getItemUrl = i.getStringExtra("getItemUrl")
        getItemName = i.getStringExtra("getItemName")
        getItemPrice = i.getStringExtra("getItemPrice")
        getItemDescription = i.getStringExtra("getItemDescription")
        getSellerEmail = i.getStringExtra("getSellerEmail")

        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(getItemName)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        sellerDetails()

        val bottomSheetBehavior = BottomSheetBehavior.from(sheet)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    toolbar?.setBackgroundColor(resources.getColor(R.color.bg_color))
                    toolbar?.setTitleTextColor(resources.getColor(R.color.color_text_blue_500))
                    val fadeIn = AnimationUtils.loadAnimation(this@CatalogueItemDetails, R.anim.fade_in_animation)
                    toolbar?.setAnimation(fadeIn)
                    itemName?.setVisibility(View.GONE)
                    cover?.setVisibility(View.VISIBLE)
                } else if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    toolbar?.setBackgroundColor(resources.getColor(R.color.transparent))
                    toolbar?.setTitleTextColor(resources.getColor(R.color.transparent))
                    itemName?.setVisibility(View.VISIBLE)
                    cover?.setVisibility(View.INVISIBLE)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        toolbar!!.setNavigationOnClickListener(View.OnClickListener { super@CatalogueItemDetails.onBackPressed() })
        setUpActionButton(getSellerEmail)


        //load item text and image into view
        if (getItemImageLink != "") {
            itemImage!!.setOnClickListener(View.OnClickListener {
                val i = Intent(this@CatalogueItemDetails, ImageViewActivity::class.java)
                i.putExtra("imageUri", getItemImageLink)
                startActivity(i)
            })
            Glide.with(this)
                    .load(getItemImageLink)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemImage)
        } else {
            itemImage!!.visibility = View.GONE
        }
        itemName?.text = getItemName
        itemPrice?.text = getItemPrice


        //load description text
        itemDescription!!.text = getItemDescription
        bundle!!.putString("itemDescription", getItemDescription)
    }

    private fun setUpActionButton(email: String?) {
        if (email != auth!!.currentUser!!.email) {
            actionButton!!.text = "Order"
            actionButton!!.background = resources.getDrawable(R.drawable.blue_button)
        } else {
            actionButton!!.text = "Delete Item"
            actionButton!!.background = resources.getDrawable(R.drawable.red_button)
        }
        val action = Runnable { deleteItemAction() }
        actionButton!!.setOnClickListener {
            if (email != auth!!.currentUser!!.email) {
                orderAction()
            } else {
                val confirm = MyActionDialog(this@CatalogueItemDetails, "Confirm Delete", "Are you sure you want to delete this item? \n Note: This is a permanent action!", R.drawable.vg_delete, action)
                confirm.show()
            }
        }
    }

    private fun deleteItemAction() {
        reference = db!!.collection("users").document(user!!.email!!).collection("catalogue").document(getItemId!!)
        reference!!.delete().addOnSuccessListener { super@CatalogueItemDetails.onBackPressed() }
    }

    private fun orderAction() {
        if (!getItemUrl!!.isEmpty() || getItemUrl != "") {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getItemUrl)
            startActivity(i)
        } else {
            val bottomSheet = ContactMethodModal()
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "TAG")
        }
    }

    private fun sellerDetails() {
        reference = db!!.collection("users").document(getSellerEmail!!)
        registerQuery = reference!!.addSnapshotListener { value, error ->
            getFullname = value!!.getString("name")
            getUsername = value.getString("username")
            getProfilePic = value.getString("profileImageLink")
            getVerified = value.getBoolean("verified")!!
            getTwitterName = value.getString("twitter")
            getInstagramName = value.getString("instagram")
            bundle!!.putString("instagramName", getInstagramName)
            bundle!!.putString("twitterName", getTwitterName)
            bundle!!.putString("phoneNumber", value.getString("phone"))

        }
    }

    override fun onStop() {
        super.onStop()
        registerQuery!!.remove()
    }

    public override fun onPause() {
        super.onPause()
        //finish();
    }
}