// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
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
import android.graphics.*
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
import android.util.Log
import androidx.appcompat.widget.SwitchCompat
import com.aprihive.AboutApp
import com.aprihive.fragments.SetThemeModal
import android.widget.CompoundButton
import androidx.core.app.NotificationCompat
import com.aprihive.OnboardingActivity
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import java.util.*

class PushNotificationService : FirebaseMessagingService() {
    private var userImage: Bitmap? = null
    private var sharedPrefs: SharedPrefs? = null
    private val sharedPreferences: SharedPreferences? = null
    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPrefs(this)
        if (sharedPrefs!!.pushSelection) {
            val msg = "streaming service"
            Log.e(TAG, "PushNotificationService: $msg")
        } else {
            stopSelf()
            removeNotification()
            val msg = "stopped service"
            Log.e(TAG, "PushNotificationService: $msg")
        }
    }

    private fun removeNotification() {}

    /*public PushNotificationService() {





        //send notification if user enabled push notification on this device
        if (sharedPrefs.getPushSelection()){
            String msg = "streaming service";
            Log.e(TAG, "PushNotificationService: " + msg);


        }
        else {
            stopSelf();
            String msg = "stopped service";
            Log.e(TAG, "PushNotificationService: " + msg);


        }





    }*/
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "PushNotificationService: " + "message received")
        sharedPrefs = SharedPrefs(this@PushNotificationService)


        //send notification if user enabled push notification on this device
        if (sharedPrefs!!.pushSelection) {
            super.onMessageReceived(remoteMessage)
            if (remoteMessage.data["type"] == "message") {
                userImage = BitmapFactory.decodeResource(resources, R.drawable.user_image_placeholder)
                Glide.with(this)
                        .asBitmap()
                        .load(remoteMessage.data["senderImage"])
                        .centerCrop()
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder)
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                userImage = resource
                                sendMessageNotification(remoteMessage.notification!!.title, remoteMessage.notification!!.body, userImage!!, remoteMessage.data["senderEmail"], remoteMessage.data["receiverName"], 1)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
            } else if (remoteMessage.data["type"] == "requests") {
                sendRequestNotification(remoteMessage.notification!!.title, remoteMessage.notification!!.body, remoteMessage.data["senderEmail"], remoteMessage.data["receiverName"], remoteMessage.data["requestId"], 1)
            }
            Log.e(TAG, "PushNotificationService: " + "message to notify")
        } else {
            Log.e(TAG, "PushNotificationService: Push notification turned off on this device")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "PushNotificationService: " + "stopped service")
    }

    private fun sendMessageNotification(title: String?, content: String?, userImageBitmap: Bitmap, senderEmail: String?, receiverName: String?, id: Int) {
        createNotificationChannel()
        val random = Random()
        val notify_id = random.nextInt(10000)
        val group_id = receiverName.hashCode()
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MessagingActivity::class.java).putExtra("getEmail", senderEmail), 0)
        val groupBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon).setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setGroupSummary(true)
                .setStyle(NotificationCompat.InboxStyle().setBigContentTitle("You have new messages").setSummaryText(receiverName!!.substring(0, 1).uppercase(Locale.getDefault()) + receiverName.substring(1).lowercase(Locale.getDefault())))
                .setGroup(receiverName)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        val builder = NotificationCompat.Builder(this@PushNotificationService, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message_filled).setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setContentTitle(title)
                .setLargeIcon(getCircleBitmap(userImageBitmap))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setContentText(content)
                .setGroup(receiverName)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPublicVersion(groupBuilder)
                .addAction(R.drawable.ic_message_filled, "Open Chat", contentIntent)
                .build()
        val notificationManagerCompat = NotificationManagerCompat.from(this@PushNotificationService)
        notificationManagerCompat.notify(group_id, groupBuilder)
        notificationManagerCompat.notify(notify_id, builder)
    }

    private fun sendRequestNotification(title: String?, content: String?, senderEmail: String?, receiverName: String?, requestId: String?, id: Int) {
        createNotificationChannel()
        val random = Random()
        val notify_id = random.nextInt(10000)
        val group_id = receiverName.hashCode()
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, FetchDetails::class.java).putExtra("type", "requestNotification").putExtra("requestId", requestId), 0)
        val groupBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon).setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setGroupSummary(true)
                .setStyle(NotificationCompat.InboxStyle().setBigContentTitle("You have new requests").setSummaryText(receiverName!!.substring(0, 1).uppercase(Locale.getDefault()) + receiverName.substring(1).lowercase(Locale.getDefault())))
                .setGroup(receiverName)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        val builder = NotificationCompat.Builder(this@PushNotificationService, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_active).setColor(ContextCompat.getColor(this, R.color.color_theme_blue))
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setContentText(content)
                .setGroup(receiverName)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPublicVersion(groupBuilder)
                .addAction(R.drawable.ic_notifications_active, "Open request", contentIntent)
                .build()
        val notificationManagerCompat = NotificationManagerCompat.from(this@PushNotificationService)
        notificationManagerCompat.notify(group_id, groupBuilder)
        notificationManagerCompat.notify(notify_id, builder)
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = Color.GRAY
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Push Notify Service"
            val description = "Push notifications from server"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "debug"
        private const val CHANNEL_ID = "Push Notification"
    }
}