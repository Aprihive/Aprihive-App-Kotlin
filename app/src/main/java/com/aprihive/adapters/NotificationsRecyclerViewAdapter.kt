// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved
package com.aprihive.adapters

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.*
import java.util.*

class NotificationsRecyclerViewAdapter(var context: Context, private var notificationList: List<NotificationModel>, private val listener: MyClickListener) : RecyclerView.Adapter<NotificationsRecyclerViewAdapter.MyViewHolder>() {
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private val reference: DocumentReference? = null
    private var getType: String? = null
    private var getSenderEmail: String? = null
    private var getDeadline: String? = null
    private var getPostId: String? = null
    private var getPostImageLink: String? = null
    private val getPostText: String? = null
    private var getNotificationDate: String? = null
    private val getFullname: String? = null
    private val getUsername: String? = null
    private val getProfileImageUrl: String? = null
    private val registerQuery: ListenerRegistration? = null
    private val senderProfileImage: String? = null
    private val senderFullname: String? = null
    private var senderUsername: String? = null
    private var getRequestText: String? = null
    private val getAuthorUsername: String? = null
    private var receiverUsername: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        //inflate layout(find_users_item)
        val view: View
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.notification_item, parent, false)
        val viewHolder = MyViewHolder(view)
        viewHolder.notificationItem.setOnClickListener {
            listener.onOpenRequestDetails(viewHolder.absoluteAdapterPosition, notificationList[viewHolder.absoluteAdapterPosition].type, notificationList[viewHolder.absoluteAdapterPosition].senderUsername, notificationList[viewHolder.absoluteAdapterPosition].receiverUsername,
                    notificationList[viewHolder.absoluteAdapterPosition].deadline, notificationList[viewHolder.absoluteAdapterPosition].postId, notificationList[viewHolder.absoluteAdapterPosition].postImageLink, notificationList[viewHolder.absoluteAdapterPosition].postText, notificationList[viewHolder.absoluteAdapterPosition].requestText, notificationList[viewHolder.absoluteAdapterPosition].requestedOn,
                    notificationList[viewHolder.absoluteAdapterPosition].senderEmail, notificationList[viewHolder.absoluteAdapterPosition].authorEmail)
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        getType = notificationList[position].type
        getSenderEmail = notificationList[position].senderEmail
        senderUsername = notificationList[position].senderUsername
        receiverUsername = notificationList[position].receiverUsername
        getDeadline = notificationList[position].deadline
        getPostId = notificationList[position].postId
        getPostImageLink = notificationList[position].postImageLink
        getRequestText = notificationList[position].requestText
        getNotificationDate = notificationList[position].requestedOn
        if (getType == "from") {
            holder.typeIcon.setImageResource(R.drawable.ic_arrow_down)
            holder.typeIcon.setColorFilter(context.resources.getColor(R.color.color_success_green_500))
            holder.typeIconBg.setCardBackgroundColor(context.resources.getColor(R.color.color_success_green_050))
            holder.notificationTitle.text = senderUsername!!.substring(0, 1).uppercase(Locale.getDefault()) + senderUsername!!.substring(1).lowercase(Locale.getDefault()) + " sent you a request!"
        } else if (getType == "to") {
            holder.typeIcon.setImageResource(R.drawable.ic_arrow_up)
            holder.notificationTitle.text = "You sent $receiverUsername a request."
        }
        holder.notificationDate.text = getNotificationDate
        holder.notificationSubTitle.text = getRequestText
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var typeIcon: ImageView
        var typeIconBg: CardView
        var notificationTitle: TextView
        var notificationSubTitle: TextView
        var notificationDate: TextView
        var notificationItem: ConstraintLayout

        init {
            typeIcon = itemView.findViewById(R.id.notification_typeIcon)
            typeIconBg = itemView.findViewById(R.id.notification_typeIconBg)
            notificationTitle = itemView.findViewById(R.id.notificationTitle)
            notificationSubTitle = itemView.findViewById(R.id.notificationSubTitle)
            notificationDate = itemView.findViewById(R.id.notificationDate)
            notificationItem = itemView.findViewById(R.id.notificationItem)
        }
    }

    // method for filtering our recyclerview items.
    fun filterList(filterList: ArrayList<NotificationModel>) {

        // below line is to add our filtered list in our course array list.
        notificationList = filterList

        // below line is to notify our adapter of change in recycler view data.
        notifyDataSetChanged()
    }

    interface MyClickListener {
        fun onOpenRequestDetails(position: Int, getType: String?, getSenderUsername: String?, getReceiverUsername: String?, getDeadline: String?, getPostId: String?, getPostImageLink: String?, getPostText: String?, getRequestText: String?, getRequestedOn: String?, getSenderEmail: String?, getReceiverEmail: String?)
    }
}