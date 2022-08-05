// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
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
import android.content.*
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import androidx.appcompat.widget.Toolbar
import com.aprihive.OnboardingActivity
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessagingActivity : AppCompatActivity(), MessagingRecyclerAdapter.MyClickListener {
    private var toolbar: Toolbar? = null
    private var backButton: ConstraintLayout? = null
    private var receiverName: TextView? = null
    private var receiverBio: TextView? = null
    private val receiverEmail: TextView? = null
    private val verified: Boolean? = null
    private var receiverProfileImage: ImageView? = null
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var messagesList: MutableList<MessageModel>? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: MessagingRecyclerAdapter? = null
    private var addMessage: EditText? = null
    private var addPostText: String? = null
    private var sendButton: ImageView? = null
    private var messageDetails: HashMap<Any, Any?>? = null
    private val lastMessageNumber = 0
    private var reference: DocumentReference? = null
    private var random: Random? = null
    private var messageId: String? = null
    private var registerQuery: ListenerRegistration? = null
    private var getMessageText: String? = null
    private var getTime: Timestamp? = null
    private var getMessageImageLink: String? = null
    private var getMessageType: String? = null
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null
    private var token: String? = null
    private var receiverUserName: String? = null
    private var getMessageId: String? = null
    private var msgRef: DocumentReference? = null
    private var receiverProfileImageLink: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_messaging)


        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser
        retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofitInterface = retrofit.create(RetrofitInterface::class.java)
        random = Random()
        val setBarsColor = SetBarsColor(this, window)
        messagesList = ArrayList()
        adapter = MessagingRecyclerAdapter(this, messagesList, this)
        toolbar = findViewById(R.id.toolbar)
        backButton = findViewById(R.id.backButton)
        receiverName = findViewById(R.id.receiverName)
        receiverBio = findViewById(R.id.receiverBio)
        receiverProfileImage = findViewById(R.id.receiverImage)
        recyclerView = findViewById(R.id.recyclerView)
        addMessage = findViewById(R.id.editText)
        sendButton = findViewById(R.id.sendButton)
        sendButton.setOnClickListener(View.OnClickListener { checkInputs() })
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        userDetails
        messages
        backButton.setOnClickListener(View.OnClickListener { super@MessagingActivity.onBackPressed() })
    }

    private fun checkInputs() {
        addPostText = addMessage!!.text.toString().trim { it <= ' ' }

        //check email
        if (addPostText!!.isEmpty()) {
        } else {
            messageId = random!!.nextInt(1000000).toString()
            sendMessage()
            addMessage!!.setText("")
        }
    }

    private val messages: Unit
        private get() {
            assert(user != null)
            try {
                val messagesQuery = db!!.collection("users").document(user!!.email!!).collection("messages").document(intent.getStringExtra("getEmail")!!).collection("messageBox").orderBy("time")
                registerQuery = messagesQuery.addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "Error: " + error.message)
                    } else {
                        messagesList!!.clear()
                        for (details in value!!.documents) {
                            val messageModel = MessageModel()
                            try {
                                getMessageText = details.getString("messageText")
                                getMessageId = details.id
                                getTime = details.getTimestamp("time")
                                getMessageImageLink = details.getString("messageImageLink")
                                getMessageType = details.getString("type")
                                messageModel.messageText = getMessageText
                                messageModel.messageImageLink = getMessageImageLink
                                messageModel.messageType = getMessageType
                                messageModel.time = getTime
                                messageModel.messageId = getMessageId
                                messageModel.otherUserEmail = intent.getStringExtra("getEmail")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            messagesList!!.add(messageModel)
                        }
                        setupRecyclerView()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun sendMessage() {
        messageDetails = HashMap()
        messageDetails!!["type"] = "to"
        messageDetails!!["time"] = Timestamp(Date())
        messageDetails!!["messageText"] = addPostText
        messageDetails!!["messageImageLink"] = ""


        //Map<String, Object> setDetails = new HashMap<>();
        //setDetails.put("requestFrom:-" + user.getDisplayName() + "-for:-" + postId, requestDetails);
        Log.e("debug", "sending")
        reference = db!!.collection("users").document(user!!.email!!).collection("messages").document(intent.getStringExtra("getEmail")!!).collection("messageBox").document(messageId!!)
        reference!!.set(messageDetails!!)
                .addOnSuccessListener {
                    createSendRequestInstance()
                    //sendPushNotification();
                    Log.d("debug", "test 2")
                    val timeAndRead = HashMap<String, Any>()
                    timeAndRead["time"] = Timestamp(Date())
                    timeAndRead["read"] = true
                    val addTimeReference = db!!.collection("users").document(user!!.email!!).collection("messages").document(intent.getStringExtra("getEmail")!!)
                    addTimeReference.set(timeAndRead).addOnSuccessListener { }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@MessagingActivity, "Failed: $e", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "onFailure: $e")
                }
    }

    private fun createSendRequestInstance() {
        messageDetails = HashMap()
        messageDetails!!["type"] = "from"
        messageDetails!!["time"] = Timestamp(Date())
        messageDetails!!["messageText"] = addPostText
        messageDetails!!["messageImageLink"] = ""


        //Map<String, Object> setDetails = new HashMap<>();
        //setDetails.put("requestFrom:-" + user.getDisplayName() + "-for:-" + postId, requestDetails);
        Log.e("debug", "sending to receiver")
        reference = db!!.collection("users").document(intent.getStringExtra("getEmail")!!).collection("messages").document(user!!.email!!).collection("messageBox").document(messageId!!)
        reference!!.set(messageDetails!!)
                .addOnSuccessListener {
                    val timeAndRead = HashMap<String, Any>()
                    timeAndRead["time"] = Timestamp(Date())
                    timeAndRead["read"] = false

                    //sendPushNotification();
                    reference = db!!.collection("users").document(intent.getStringExtra("getEmail")!!).collection("messages").document(user!!.email!!)
                    val addTimeReference: DocumentReference = reference
                    addTimeReference.set(timeAndRead).addOnSuccessListener { sendMessagePushNotification() }
                    Log.e("debug", "sent to receiver")
                }
                .addOnFailureListener { Log.e(TAG, "onFailure: failed to send message") }
    }

    //threat = value.getBoolean("threat");
    //verified = value.getBoolean("verified");
    private val userDetails: Unit
        private get() {
            val documentReference = db!!.collection("users").document(intent.getStringExtra("getEmail")!!)
            documentReference.addSnapshotListener { value, error -> //threat = value.getBoolean("threat");
                //verified = value.getBoolean("verified");
                receiverUserName = value!!.getString("username")
                receiverProfileImageLink = value.getString("profileImageLink")
                receiverName!!.text = value.getString("name")
                token = value.getString("fcm-token")
                try {
                    receiverBio!!.text = value.getString("bio")!!.substring(0, 30) + "..."
                } catch (e: Exception) {
                    receiverBio!!.text = value.getString("bio")
                }
                Glide.with(this@MessagingActivity)
                        .load(receiverProfileImageLink)
                        .centerCrop()
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(receiverProfileImage!!)
            }
        }

    private fun sendMessagePushNotification() {
        val map = HashMap<String, String?>()
        map["senderName"] = user!!.displayName
        map["receiverName"] = receiverUserName
        map["receiverToken"] = token
        map["senderEmail"] = user!!.email
        map["senderProfilePicture"] = user!!.photoUrl.toString()
        map["message"] = addPostText
        try {
            map["time"] = messageTime(Timestamp(Date()))
        } catch (e: ParseException) {
            e.printStackTrace()
            map["time"] = "Today"
        }
        val call = retrofitInterface!!.executeMessagePushNotification(map)
        call.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.code() == 200) {
                    Log.e("message-push-status", "message push notification sent")
                } else if (response.code() == 400) {
                    Log.e("message-push-status", "failure: msg push not sent")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(this@MessagingActivity, "Failed: " + t.message, Toast.LENGTH_LONG).show()
                Log.e("error", t.message!!)
                Log.e("error", t.localizedMessage)
            }
        })
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        //set items to arrange from bottom
        // linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView!!.layoutManager = linearLayoutManager
        try {
            linearLayoutManager.smoothScrollToPosition(recyclerView, null, messagesList!!.size - 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteConvo -> {
                deleteConversation()
                return true
            }
            R.id.report -> {
                val bottomSheet = ReportModal()
                val bundle = Bundle()
                bundle.putString("postAuthorEmail", "ericx.group@gmail.com")
                bundle.putString("postAuthor", "Support")
                bundle.putString("postText", "Reporting conversation with " + receiverName!!.text + ".")
                bundle.putString("postImage", receiverProfileImageLink)
                bundle.putString("postId", receiverUserName)
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager, "TAG")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteConversation() {
        msgRef = db!!.collection("users").document(user!!.email!!).collection("messages").document(intent.getStringExtra("getEmail")!!)
        msgRef!!.delete().addOnSuccessListener {
            Toast.makeText(this@MessagingActivity, "Deleted conversation with " + receiverName!!.text, Toast.LENGTH_LONG).show()
            onBackPressed()
            val batchSize = 500
            val query = db!!.collection("users").document(user!!.email!!).collection("messages").document(intent.getStringExtra("getEmail")!!).collection("messageBox").limit(batchSize.toLong())
            try {
                // retrieve a small batch of documents to avoid out-of-memory errors
                query.get().addOnCompleteListener { task ->
                    var deleted = 0
                    val documents = task.result.documents
                    for (document in documents) {
                        document.reference.delete()
                        ++deleted
                    }
                    if (deleted >= batchSize) {
                        // retrieve and delete another batch
                        deleteConversation()
                    }
                }
            } catch (e: Exception) {
                System.err.println("Error deleting collection : " + e.message)
            }
        }.addOnFailureListener { Toast.makeText(this@MessagingActivity, "Something went wrong\nPlease retry the action", Toast.LENGTH_SHORT).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            registerQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(ParseException::class)
    private fun messageTime(timestamp: Timestamp): String {
        var time = ""
        val date = timestamp.toDate()
        val prettyTime = PrettyTime(Locale.getDefault())
        val ago = prettyTime.format(date)
        val sdf = SimpleDateFormat("dd/MM/yy, hh:mm aaa")
        val sdfYesterday = SimpleDateFormat("hh:mm aaa")
        val sdfDays = SimpleDateFormat("EEEE, hh:mm aaa")
        val c1 = Calendar.getInstance() // today
        c1.add(Calendar.DAY_OF_YEAR, -1) // yesterday
        val c2 = Calendar.getInstance()
        c2.time = date // your date
        val c3 = Calendar.getInstance() // today
        c3.add(Calendar.DAY_OF_YEAR, 0) // today
        time = if (c1[Calendar.YEAR] == c2[Calendar.YEAR] && c1[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR]) {
            "Yesterday, " + sdfYesterday.format(date)
        } else if (ago.contains("month") || ago.contains("week")) {
            //time = String.valueOf(sdf.parse(String.valueOf(date)));
            sdf.format(date)
        } else if (ago.contains("day")) {
            sdfDays.format(date)
        } else if (ago.contains("hour")) {
            if (c3[Calendar.YEAR] == c2[Calendar.YEAR] && c3[Calendar.DAY_OF_YEAR] == c3[Calendar.DAY_OF_YEAR]) {
                sdfYesterday.format(date)
            } else {
                "Yesterday, " + sdfYesterday.format(date)
            }
        } else {
            ago
        }
        return time
    }

    override fun onMessageHold(position: Int, messageId: String, messageText: String, messageEmail: String, type: String) {
        val copyMessageAction = Runnable { copyMessage(messageText) }
        val deleteForMeAction = Runnable { deleteMessageForMe(messageEmail, messageId) }
        val unsendMessageAction = Runnable { unsendMessage(messageEmail, messageId) }
        val shareAction = Runnable { shareMessage(messageText) }
        if (type == "to") {
            val optionsDialogModal = OptionsDialogModal(this, "Share", shareAction, "Copy message", copyMessageAction, "Unsend message", unsendMessageAction, "Delete for me", deleteForMeAction)
        } else {
            val optionsDialogModal = OptionsDialogModal(this, "Share", shareAction, "Copy message", copyMessageAction, "Delete for me", deleteForMeAction)
        }
    }

    private fun unsendMessage(participant: String, messageId: String) {
        val myMsgRef = db!!.collection("users").document(user!!.email!!).collection("messages").document(participant).collection("messageBox").document(messageId)
        val otherMsgRef = db!!.collection("users").document(participant).collection("messages").document(user!!.email!!).collection("messageBox").document(messageId)
        myMsgRef.delete()
                .addOnSuccessListener { otherMsgRef.delete().addOnSuccessListener { Toast.makeText(this@MessagingActivity, "Message has been unsent.", Toast.LENGTH_SHORT).show() } }
                .addOnFailureListener { Toast.makeText(this@MessagingActivity, "Something went wrong\nPlease retry the action", Toast.LENGTH_SHORT).show() }
    }

    private fun copyMessage(message: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("message", message)
        clipboard.setPrimaryClip(clip)
        val snackBar = MySnackBar(this, window.decorView.findViewById(R.id.page), "Message copied to clipboard!", R.color.color_theme_blue, Snackbar.LENGTH_SHORT)
    }

    private fun shareMessage(message: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun deleteMessageForMe(participant: String, messageId: String) {
        msgRef = db!!.collection("users").document(user!!.email!!).collection("messages").document(participant).collection("messageBox").document(messageId)
        msgRef!!.delete()
                .addOnSuccessListener { Toast.makeText(this@MessagingActivity, "Message deleted.", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(this@MessagingActivity, "Something went wrong\nPlease retry the action", Toast.LENGTH_SHORT).show() }
    }

    companion object {
        private const val TAG = "debug"
    }
}