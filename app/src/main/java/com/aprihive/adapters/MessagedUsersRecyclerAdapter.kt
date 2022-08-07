// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved
package com.aprihive.adapters

import android.content.Context
import com.aprihive.models.CatalogueModel
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.aprihive.R
import android.content.Intent
import com.aprihive.CatalogueItemDetails
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import androidx.cardview.widget.CardView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aprihive.models.DiscoverPostsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.leocardz.link.preview.library.TextCrawler
import android.content.res.Resources.NotFoundException
import com.bumptech.glide.request.RequestListener
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.load.engine.GlideException
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.aprihive.models.FindModel
import com.aprihive.UserProfileActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.aprihive.pages.Discover
import com.aprihive.pages.Find
import com.aprihive.pages.Requests
import com.aprihive.models.MessagedUsersModel
import kotlin.Throws
import org.ocpsoft.prettytime.PrettyTime
import com.aprihive.models.MessageModel
import com.aprihive.adapters.MessagingRecyclerAdapter.Viewholder
import com.aprihive.adapters.MessagingRecyclerAdapter
import android.view.View.OnLongClickListener
import android.widget.ImageView
import com.aprihive.models.NotificationModel
import com.aprihive.models.OnboardingModel
import androidx.viewpager.widget.PagerAdapter
import com.aprihive.MainActivity
import com.aprihive.pages.Feed
import com.aprihive.pages.Catalogue
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessagedUsersRecyclerAdapter(var context: Context, var userList: List<MessagedUsersModel>, var listener: MyClickListener) : RecyclerView.Adapter<MessagedUsersRecyclerAdapter.ViewHolder>() {
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private var usersName: String? = null
    private var usersImageLink: String? = null
    private var usersBio: String? = null
    private var threat = false
    private var verified = false
    private var registerQuery: ListenerRegistration? = null
    private var lastMessageRegQuery: ListenerRegistration? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        //inflate layout(find_users_item)
        val view: View
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.messaged_users_item, viewGroup, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser
        val viewHolder: ViewHolder = ViewHolder(view)
        viewHolder.userItem.setOnClickListener { listener.onOpen(viewHolder.absoluteAdapterPosition, userList[viewHolder.absoluteAdapterPosition].receiverEmail) }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val documentReference = db!!.collection("users").document(userList[i].receiverEmail!!)
        registerQuery = documentReference.addSnapshotListener { value, error ->
            usersName = value!!.getString("name")
            usersImageLink = value.getString("profileImageLink")
            usersBio = value.getString("bio")
            threat = value.getBoolean("threat")!!
            verified = value.getBoolean("verified")!!
            viewHolder.fullName.text = usersName

            //profile dp
            Glide.with(context)
                    .load(usersImageLink)
                    .centerCrop()
                    .error(R.drawable.user_image_placeholder)
                    .fallback(R.drawable.user_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.profileImage)

            //verification icon
            try {
                if (verified) {
                    viewHolder.verifiedIcon.visibility = View.VISIBLE
                } else {
                    viewHolder.verifiedIcon.visibility = View.GONE
                }
            } catch (e: Exception) {
                //nothing
            }

            //threat icon
            try {
                if (threat) {
                    viewHolder.threatIcon.visibility = View.VISIBLE
                } else {
                    viewHolder.threatIcon.visibility = View.GONE
                }
            } catch (e: Exception) {
                //nothing
            }
        }
        val lastMessageQuery = db!!.collection("users").document(user!!.email!!).collection("messages").document(userList[i].receiverEmail!!).collection("messageBox").orderBy("time", Query.Direction.DESCENDING).limit(1)
        lastMessageRegQuery = lastMessageQuery.addSnapshotListener { value, error ->
            try {
                for (details in value!!.documents) {
                    viewHolder.lastMessage.text = details.getString("messageText")
                    try {
                        viewHolder.lastMessageTime.text = lastMessageTime(details.getTimestamp("time"))
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @Throws(ParseException::class)
    private fun lastMessageTime(timestamp: Timestamp?): String {
        var time = ""
        val date = timestamp!!.toDate()
        val prettyTime = PrettyTime(Locale.getDefault())
        val ago = prettyTime.format(date)
        val sdfToday = SimpleDateFormat("hh:mm aaa")
        val sdfDate = SimpleDateFormat("dd/MM/yy")
        val c1 = Calendar.getInstance() // today
        c1.add(Calendar.DAY_OF_YEAR, -1) // yesterday
        val c2 = Calendar.getInstance()
        c2.time = date // your date
        time = if (c1[Calendar.YEAR] == c2[Calendar.YEAR] && c1[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR]) {
            "yesterday"
        } else if (ago.contains("day")) {
            sdfDate.format(date)
        } else if (ago.contains("month") || ago.contains("week")) {
            //time = String.valueOf(sdf.parse(String.valueOf(date)));
            sdfDate.format(date)
        } else {
            sdfToday.format(date)
        }
        return time
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView
        var verifiedIcon: ImageView
        var threatIcon: ImageView
        var fullName: TextView
        var lastMessage: TextView
        var lastMessageTime: TextView
        var userItem: ConstraintLayout

        init {
            fullName = itemView.findViewById(R.id.messaged_fullName)
            profileImage = itemView.findViewById(R.id.messaged_profileImage)
            verifiedIcon = itemView.findViewById(R.id.messaged_verifiedIcon)
            threatIcon = itemView.findViewById(R.id.messaged_warningIcon)
            lastMessage = itemView.findViewById(R.id.messaged_recent)
            lastMessageTime = itemView.findViewById(R.id.messaged_recentTime)
            userItem = itemView.findViewById(R.id.userItem)
        }
    }

    interface MyClickListener {
        fun onOpen(position: Int, getEmail: String?)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        try {
            registerQuery!!.remove()
            lastMessageRegQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}