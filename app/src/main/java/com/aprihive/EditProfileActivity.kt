// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive

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
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.firestore.*
import java.util.HashMap

abstract class EditProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private var reference: DocumentReference? = null
    private var editName: EditText? = null
    private var editBio: EditText? = null
    private var editEmail: EditText? = null
    private var editPhone: EditText? = null
    private var editInstagramName: EditText? = null
    private var editTwitterName: EditText? = null
    private var usernameChangeClick: TextView? = null
    private var editSchool: Spinner? = null
    private val uploadTask: StorageTask<*>? = null
    private var storageReference: StorageReference? = null
    private var imageUri: Uri? = null
    private var getEmail: String? = null
    private var getFullname: String? = null
    private var getInstagramName: String? = null
    private var getUsername: String? = null
    private var getTwitterName: String? = null
    private var getPhone: String? = null
    private var getBio: String? = null
    private var getSchool: String? = null
    private var getUserProfilePic //fetch from firebase into
            : String? = null
    private var getVerified //fetch from firebase into
            : Boolean? = null
    private var saveTo: String? = null
    private var imageType: String? = null
    private var profilePic: ImageView? = null
    private var statusLoadBar: CardView? = null
    private var statusProgressText: TextView? = null
    private var toolbar: Toolbar? = null
    private var registerQuery: ListenerRegistration? = null
    private var saveButton: Button? = null
    private var setName: String? = null
    private var setBio: String? = null
    private var setSchool: String? = null
    private var setPhone: String? = null
    private var setTwitter: String? = null
    private var setInstagram: String? = null
    private var errorFeedback: TextView? = null
    private var networkListener: NetworkListener? = null
    private var page: ConstraintLayout? = null
    private var loading: ConstraintLayout? = null
    private var ccp: CountryCodePicker? = null
    private var canGoBack = true
    private var universitiesList: Array<String?>? = null
    private var editSchoolText: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        val setBarsColor = SetBarsColor(this, window)
        networkListener = NetworkListener(this, page, window)
        universitiesList = arrayOf(
                "",
                "Obafemi Awolowo University, Ife",
                "Convenant University, Ota",
                "Unilag, Lagos",
                "Unilorin, Ilorin",
                "Unibadan, Ibadan",
                "Babcock University, Ogun",
                "Futa, Akure",
                "Redeemers University, Osun",
                "Afe Babalola Universoty, Ado-Ekiti",
                "Bells University, Ota",
                "Bowen university, Iwo",
                "Funnab, Abeokuta",
                "LASU, Lagos",
                "Uniport, Rivers",
                "University of Nigeria, Nsukka",
                "Oduduwa University, Osun",
                "Others"
        )


        //init firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        user = auth!!.currentUser
        reference = firestore!!.collection("users").document(user!!.email!!)
        //
        getEmail = user!!.email
        toolbar = findViewById(R.id.toolbar)
        editName = findViewById(R.id.editFullName)
        editEmail = findViewById(R.id.editEmail)
        editPhone = findViewById(R.id.editPhone)
        editBio = findViewById(R.id.editBio)
        editTwitterName = findViewById(R.id.editTwitterUsername)
        editInstagramName = findViewById(R.id.editInstagramUsername)
        editSchool = findViewById(R.id.editSchoolName)
        usernameChangeClick = findViewById(R.id.changeUsername)
        profilePic = findViewById(R.id.changeImage)
        saveButton = findViewById(R.id.saveButton)
        errorFeedback = findViewById(R.id.errorFeedback)
        loading = findViewById(R.id.loading)
        page = findViewById(R.id.page)
        ccp = findViewById(R.id.ccp)
        editSchool.setOnItemSelectedListener(this)
        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, universitiesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editSchool.setAdapter(adapter)
        statusLoadBar = findViewById(R.id.statusLoadBar)
        statusProgressText = findViewById(R.id.statusProgressText)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("")
        supportActionBar!!.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { super@EditProfileActivity.onBackPressed() })
        editSchoolText = ""
        profileImage
        retrieveDetailsFromFirestore()
        editName.addTextChangedListener(textChangeListener)
        editPhone.addTextChangedListener(textChangeListener)
        editTwitterName.addTextChangedListener(textChangeListener)
        editInstagramName.addTextChangedListener(textChangeListener)
        editBio.addTextChangedListener(textChangeListener)


        //handle profile image change
        profilePic.setOnClickListener(View.OnClickListener { CropImage.activity().setAspectRatio(1, 1).setFixAspectRatio(true).start(this@EditProfileActivity) })
        usernameChangeClick.setOnClickListener(View.OnClickListener {
            val i = Intent(this@EditProfileActivity, SetUsername::class.java)
            startActivity(i)
        })
        ccp.registerPhoneNumberTextView(editPhone)
        ccp.enablePhoneAutoFormatter(true)
        ccp.enableSetCountryByTimeZone(true)
    }

    private val textChangeListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (editName!!.text.toString().trim { it <= ' ' } != getFullname ||
                    editSchoolText != getSchool ||
                    editPhone!!.text.toString().trim { it <= ' ' } != getPhone ||
                    editTwitterName!!.text.toString().trim { it <= ' ' } != getTwitterName ||
                    editInstagramName!!.text.toString().trim { it <= ' ' } != getInstagramName ||
                    editBio!!.text.toString().trim { it <= ' ' } != getBio) {
                saveButton!!.background = resources.getDrawable(R.drawable.blue_button)
                saveButton!!.setOnClickListener { checkInputs() }
            } else {
                saveButton!!.background = resources.getDrawable(R.drawable.disabled_button)
                saveButton!!.setOnClickListener { }
            }
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private fun checkInputs() {


        //get values from input
        setName = editName!!.text.toString().trim { it <= ' ' }
        setSchool = editSchoolText
        setBio = editBio!!.text.toString().trim { it <= ' ' }
        setInstagram = editInstagramName!!.text.toString().trim { it <= ' ' }
        setTwitter = editTwitterName!!.text.toString().trim { it <= ' ' }
        val checkPhone = editPhone!!.text.toString().trim { it <= ' ' }
        val countryCode = ccp!!.selectedCountryCode
        setPhone = if (!checkPhone.isEmpty()) {
            if (!checkPhone.startsWith(countryCode)) {
                countryCode + checkPhone
            } else {
                checkPhone
            }
        } else {
            ""
        }


        //check setBio
        if (setBio!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Description cannot be empty"
        } else if (setName!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Name cannot be empty"
        } else if (!setName!!.matches("[a-zA-Z0-9 ]*")) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Name can only contain alphabets"
        } else if (setSchool!!.isEmpty()) {
            errorFeedback!!.visibility = View.VISIBLE
            errorFeedback!!.text = "Location cannot be empty"
        } else {
            errorFeedback!!.visibility = View.GONE
            if (networkListener!!.connected) {
                updateInfo()
                saveButton!!.visibility = View.INVISIBLE
                loading!!.visibility = View.VISIBLE
            }
        }
    }

    private fun updateInfo() {
        val details: MutableMap<String, Any?> = HashMap()
        details["name"] = setName
        details["bio"] = setBio
        details["school"] = setSchool
        details["phone"] = setPhone
        details["newAccount"] = false
        details["instagram"] = setInstagram
        details["twitter"] = setTwitter
        reference = firestore!!.collection("users").document(user!!.email!!)
        reference!!.update(details).addOnSuccessListener {
            val snackBar = MySnackBar(this@EditProfileActivity, page, "Saved!", R.color.color_success_green_300, Snackbar.LENGTH_LONG)
            loading!!.visibility = View.GONE
            saveButton!!.visibility = View.VISIBLE
            val handler = Handler()
            handler.postDelayed({
                //Intent i = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                //finish();
                //startActivity(i);
            }, 1500)
        }
    }

    //2
    //3
    //4
    //7
    //8
    private val profileImage: Unit
        private get() {
            val userProfilePic = user!!.photoUrl
            Glide.with(this) //2
                    .load(userProfilePic) //3
                    .centerCrop() //4
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fallback(R.drawable.user_image_placeholder) //7
                    .into(profilePic!!) //8
        }

    private fun retrieveDetailsFromFirestore() {
        getEmail = user!!.email


        //retrieve from firestore
        registerQuery = reference!!.addSnapshotListener { documentSnapshot, e ->
            getFullname = documentSnapshot!!.getString("name")
            getPhone = documentSnapshot.getString("phone")
            getUsername = documentSnapshot.getString("username")
            getVerified = documentSnapshot.getBoolean("verified")
            getBio = documentSnapshot.getString("bio")
            getSchool = documentSnapshot.getString("school")
            getUserProfilePic = documentSnapshot.getString("profileImageLink")
            getTwitterName = documentSnapshot.getString("twitter")
            getInstagramName = documentSnapshot.getString("instagram")
            Glide.with(applicationContext) //2
                    .load(getUserProfilePic) //3
                    .centerCrop() //4
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.user_image_placeholder) //7
                    .fallback(R.drawable.user_image_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade()) //7
                    .into(profilePic!!) //8
            editName!!.setText(getFullname)
            editEmail!!.setText(getEmail)
            editBio!!.setText(getBio)
            editSchool!!.setSelection(getIndex(editSchool, getSchool))
            editPhone!!.setText(getPhone)
            editInstagramName!!.setText(getInstagramName)
            editTwitterName!!.setText(getTwitterName)
        }
        //end of retrieve
    }

    private fun getIndex(spinner: Spinner?, myString: String?): Int {
        var index = 0
        for (i in 0 until spinner!!.count) {
            if (spinner.getItemAtPosition(i) == myString) {
                index = i
            }
        }
        return index
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageType = "profile"
        saveTo = user!!.displayName + "_profileImg.jpg"
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                profilePic!!.setImageURI(imageUri)
                statusLoadBar!!.visibility = View.VISIBLE
                val fadeUp = AnimationUtils.loadAnimation(this, R.anim.fade_up_animation)
                statusLoadBar!!.animation = fadeUp
                if (imageType == "profile") {
                    profilePic!!.setImageURI(imageUri)
                    canGoBack = false
                    statusProgressText!!.text = "Updating Profile Picture, Please wait"
                }

                //save image to firebase passing the type of image to be profile image (this is my idea, so that i can only use one method everytime)
                uploadImageToFirestore(imageUri, saveTo!!, imageType!!)
            }
        }
    }

    private fun uploadImageToFirestore(imageUri: Uri?, saveTo: String, imageType: String) {
        val fileRef = storageReference!!.child("profile-images").child(saveTo)
        fileRef.putFile(imageUri!!).addOnSuccessListener {
            val fadeDown = AnimationUtils.loadAnimation(this@EditProfileActivity, R.anim.fade_down_animation)
            statusLoadBar!!.animation = fadeDown
            statusLoadBar!!.visibility = View.INVISIBLE
            canGoBack = true
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                //upload to profile image database if its profile image were trying to change
                if (imageType == "profile") {
                    Log.e("here", "the thing reach here ooo")
                    Glide.with(this@EditProfileActivity) //2
                            .load(uri) //3
                            .centerCrop() //4
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fallback(R.drawable.user_image_placeholder) //7
                            .into(profilePic!!) //8
                    val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri).build()
                    val userEmail = auth!!.currentUser!!.email
                    val reference = firestore!!.collection("users").document(userEmail!!)
                    reference.update("profileImageLink", uri.toString()).addOnSuccessListener { val snackBar = MySnackBar(this@EditProfileActivity, window.decorView.findViewById(R.id.page), "Profile Image updated!", R.color.color_theme_blue, Snackbar.LENGTH_LONG) }
                    Log.e("here", "it even stored it")
                    user!!.updateProfile(profileUpdates)
                    Log.e("here", "it even stored it")
                } else {
                    //nothing
                }
            }.addOnFailureListener {
                //nothing
            }
        }.addOnFailureListener {
            canGoBack = true
            val snackBar = MySnackBar(this@EditProfileActivity, window.decorView, "Updating profile image failed, please try again.", R.color.color_error_red_200, Snackbar.LENGTH_LONG)
        }
    }

    override fun onStart() {
        super.onStart()
        val networkIntentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkListener!!.networkListenerReceiver, networkIntentFilter)
    }

    override fun onStop() {
        super.onStop()
        registerQuery!!.remove()
        unregisterReceiver(networkListener!!.networkListenerReceiver)
    }

    override fun onBackPressed() {
        if (canGoBack) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
        editSchoolText = universitiesList[position]
        textChangeListener.onTextChanged("charSequence", 0, 1, 2)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
}