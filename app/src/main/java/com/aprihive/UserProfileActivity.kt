// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import com.aprihive.MessagingActivity
import com.aprihive.adapters.ProfileViewPagerAdapter
import com.aprihive.fragments.ContactMethodModal
import com.aprihive.fragments.ReportModal
import com.aprihive.methods.MySnackBar
import com.aprihive.methods.SetBarsColor
import com.aprihive.methods.SharedPrefs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserProfileActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var toolbar: Toolbar? = null
    private var connectButton: TextView? = null
    private var callButton: TextView? = null
    private var verifiedIcon: ImageView? = null
    private var profilePic: ImageView? = null
    private val email: TextView? = null
    private var fullname: TextView? = null
    private var username: TextView? = null
    private val phone: TextView? = null
    private var bio: TextView? = null
    private var schoolName: TextView? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var bundle: Bundle? = null
    private var likeRegisterQuery: ListenerRegistration? = null
    private var getUserEmail: String? = null
    private var registerQuery: ListenerRegistration? = null
    private var getUsername: String? = null
    private var getPhone: String? = null
    private var getBio: String? = null
    private var getSchool: String? = null
    private var getProfileImageUrl: String? = null
    private var getTwitter: String? = null
    private var getInstagram: String? = null
    private var getFullname: String? = null
    private var getVerified = false
    private val getUsernameData: String? = null
    private var adapter: ProfileViewPagerAdapter? = null
    private var threatIcon: ImageView? = null
    private var getThreat: Boolean? = null
    private var warningBar: ConstraintLayout? = null
    private var sendMsgBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        val sharedPrefs = SharedPrefs(this)
        val getTheme = sharedPrefs.themeSettings
        AppCompatDelegate.setDefaultNightMode(getTheme)
        val setBarsColor = SetBarsColor(this, window)
        adapter = ProfileViewPagerAdapter(supportFragmentManager)
        bundle = Bundle()
        viewPager = findViewById(R.id.profileViewPager)
        toolbar = findViewById(R.id.toolbar)
        connectButton = findViewById(R.id.connectButton)
        callButton = findViewById(R.id.callButton)
        tabLayout = findViewById(R.id.tabLayout)
        warningBar = findViewById(R.id.warningBar)
        verifiedIcon = findViewById(R.id.verifiedIcon)
        threatIcon = findViewById(R.id.warningIcon)
        profilePic = findViewById(R.id.user_profileImage)
        fullname = findViewById(R.id.fullName)
        username = findViewById(R.id.username)
        bio = findViewById(R.id.description)
        schoolName = findViewById(R.id.schoolName)
        sendMsgBtn = findViewById(R.id.messageButton)
        val intent = intent
        try {
            val data = getIntent().data!!
            getUserEmail = data.lastPathSegment
            Log.e("debug", "used email")
            userInfoFromDbByEmail
        } catch (e: Exception) {
            getUserEmail = intent.getStringExtra("getEmail")
            getUsername = intent.getStringExtra("getUsername")
            Log.e("debug", "used intent info")

            userInfoFromDbByEmail
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        sendMsgBtn!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@UserProfileActivity, MessagingActivity::class.java)
            intent.putExtra("getEmail", getUserEmail)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })
        toolbar!!.setNavigationOnClickListener(View.OnClickListener { super@UserProfileActivity.onBackPressed() })
        connectButton!!.setOnClickListener(View.OnClickListener {
            val fileRef = db!!.collection("users").document(getUserEmail!!).collection("lists").document("following")
            val processFollow = arrayOf(true)
            fileRef.addSnapshotListener { value, error ->
                try {
                    if (processFollow[0]) {
                        if (value!!.contains(auth!!.currentUser!!.uid)) {
                            fileRef.update(auth!!.currentUser!!.uid, FieldValue.delete())
                            processFollow[0] = false
                        } else {
                            fileRef.update(auth!!.currentUser!!.uid, auth!!.currentUser!!.email)
                            processFollow[0] = false
                        }
                    }
                } catch (e: NotFoundException) {
                    e.printStackTrace()
                }
            }
        })
        callButton!!.setOnClickListener(View.OnClickListener {
            val bottomSheet = ContactMethodModal()
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "TAG")
        })

        //set viewpager
        viewPager!!.setAdapter(adapter)
        viewPager!!.setTag(getUserEmail) //pass data to viewpager

        //set listener to viewpager from tabLayout
        viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.setupWithViewPager(viewPager)

        // get tabs from adapter
        val feedTab = tabLayout!!.getTabAt(0)
        val catalogueTab = tabLayout!!.getTabAt(1)
        feedTab!!.setIcon(R.drawable.ic_feed)
        catalogueTab!!.setIcon(R.drawable.ic_shopping_cart)
        Log.e(TAG, "onCreate: $getUsername")
        supportActionBar!!.setTitle(getUsername)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profileLink -> {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("profile-link", "https://aprihive.com/user/$getUsername")
                clipboard.setPrimaryClip(clip)
                val snackBar = MySnackBar(this, window.decorView.findViewById(R.id.page), "Copied to clipboard!", R.color.color_theme_blue, Snackbar.LENGTH_SHORT)
                return true
            }
            R.id.report -> {
                val bottomSheet = ReportModal()
                val bundle = Bundle()
                bundle.putString("postAuthorEmail", "ericx.group@gmail.com")
                bundle.putString("postAuthor", "Support")
                bundle.putString("postText", "Reporting $getFullname ($getUsername)")
                bundle.putString("postImage", getProfileImageUrl)
                bundle.putString("postId", getUsername)
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager, "TAG")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun useLocalUserData(intent: Intent) {
        getUsername = intent.getStringExtra("getUsername")
        getVerified = intent.getBooleanExtra("getVerified", false)
        getUserEmail = intent.getStringExtra("getEmail")
        getFullname = intent.getStringExtra("getFullName")
        getProfileImageUrl = intent.getStringExtra("getProfileImageUrl")
        getBio = intent.getStringExtra("getBio")
        getSchool = intent.getStringExtra("getSchoolName")
        getPhone = intent.getStringExtra("getPhone")
        getTwitter = intent.getStringExtra("getTwitter")
        getInstagram = intent.getStringExtra("getInstagram")
        populateViews()
    }

    private fun populateViews() {
        Glide.with(applicationContext) //2
                .load(getProfileImageUrl) //3
                .centerCrop() //4
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.user_image_placeholder) //7
                .fallback(R.drawable.user_image_placeholder)
                .into(profilePic!!) //8
        fullname!!.text = getFullname
        username!!.text = "@$getUsername"
        bio!!.text = getBio
        schoolName!!.text = getSchool
        if (getVerified) {
            verifiedIcon!!.visibility = View.VISIBLE
        } else {
            verifiedIcon!!.visibility = View.GONE
        }
        if (getThreat!!) {
            threatIcon!!.visibility = View.VISIBLE
            warningBar!!.visibility = View.VISIBLE
        } else {
            threatIcon!!.visibility = View.GONE
            warningBar!!.visibility = View.GONE
        }
        assert(getProfileImageUrl != null)
        if (getProfileImageUrl != "") {
            profilePic!!.setOnClickListener {
                val i = Intent(this@UserProfileActivity, ImageViewActivity::class.java)
                i.putExtra("imageUri", getProfileImageUrl)
                startActivity(i)
            }
        }
        bundle!!.putString("phoneNumber", getPhone)
        bundle!!.putString("instagramName", getInstagram)
        bundle!!.putString("twitterName", getTwitter)
        checkIfUserIsInFavourites()
    }

    //retrieve from firestore
    private val userInfoFromDbByEmail: Unit
        //end of retrieve
        private get() {

            //retrieve from firestore
            val fetchReference: DocumentReference
            fetchReference = db!!.collection("users").document(getUserEmail!!)
            registerQuery = fetchReference.addSnapshotListener { documentSnapshot, e ->
                getFullname = documentSnapshot!!.getString("name")
                getPhone = documentSnapshot.getString("phone")
                getUsername = documentSnapshot.getString("username")
                getVerified = documentSnapshot.getBoolean("verified")!!
                getThreat = documentSnapshot.getBoolean("threat")
                getBio = documentSnapshot.getString("bio")
                getSchool = documentSnapshot.getString("school")
                getProfileImageUrl = documentSnapshot.getString("profileImageLink")
                getTwitter = documentSnapshot.getString("twitter")
                getInstagram = documentSnapshot.getString("instagram")
                populateViews()
            }
            //end of retrieve
        }

    private fun checkIfUserIsInFavourites() {
        reference = db!!.collection("users").document(getUserEmail!!).collection("lists").document("following")
        likeRegisterQuery = reference!!.addSnapshotListener { value, error ->
            try {
                assert(user != null)
                val uid = user!!.uid
                var check = false
                try {
                    check = value!!.contains(uid)
                } catch (e: Exception) {
                }
                if (check) {
                    connectButton!!.background = resources.getDrawable(R.drawable.connect_active_button)
                    connectButton!!.text = "Upvoted"
                    connectButton!!.setTextColor(resources.getColor(R.color.bg_color))
                } else {
                    connectButton!!.background = resources.getDrawable(R.drawable.connect_default_button)
                    connectButton!!.text = "Upvote"
                    connectButton!!.setTextColor(resources.getColor(R.color.color_theme_blue))
                }

                /*
                    try {
                        Map<String, Object> map = value.getData();
                        getUpvotes = map.size();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (getUpvotes == 1){
                        holder.trustedByText.setText("  Trusted by " + getUpvotes + " person");
                    }
                    else {
                        holder.trustedByText.setText("  Trusted by " + getUpvotes + " people");
                    }
                    */
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "debug"
    }
}