// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved
package com.aprihive.adapters

import android.content.Context
import com.aprihive.models.CatalogueModel
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
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
import com.google.firebase.firestore.FirebaseFirestore
import com.leocardz.link.preview.library.TextCrawler
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import android.content.res.Resources.NotFoundException
import com.bumptech.glide.request.RequestListener
import android.graphics.drawable.Drawable
import android.util.Log
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
import com.google.firebase.firestore.QuerySnapshot
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessagingRecyclerAdapter(private val context: Context, private val messagesList: List<MessageModel>, var listener: MyClickListener) : RecyclerView.Adapter<Viewholder>() {
    private var viewHolder: Viewholder? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Viewholder {
        val view: View
        val inflater = LayoutInflater.from(context)
        if (viewType == ITEM_TYPE_FROM) {
            view = inflater.inflate(R.layout.message_container_from, viewGroup, false)
            viewHolder = Viewholder(view)
            viewHolder!!.messageItem.setOnLongClickListener {
                listener.onMessageHold(viewHolder!!.absoluteAdapterPosition, messagesList[viewHolder!!.absoluteAdapterPosition].messageId, messagesList[viewHolder!!.absoluteAdapterPosition].messageText, messagesList[viewHolder!!.absoluteAdapterPosition].otherUserEmail, "from")
                Log.e(TAG, "onLongClick: " + messagesList[viewHolder!!.absoluteAdapterPosition].messageId)
                Log.e(TAG, "onLongClick: " + viewHolder!!.absoluteAdapterPosition)
                false
            }
        } else if (viewType == ITEM_TYPE_TO) {
            view = inflater.inflate(R.layout.message_container_to, viewGroup, false)
            viewHolder = Viewholder(view)
            viewHolder!!.messageItem.setOnLongClickListener {
                listener.onMessageHold(viewHolder!!.absoluteAdapterPosition, messagesList[viewHolder!!.absoluteAdapterPosition].messageId, messagesList[viewHolder!!.absoluteAdapterPosition].messageText, messagesList[viewHolder!!.absoluteAdapterPosition].otherUserEmail, "to")
                Log.e(TAG, "onLongClick: " + messagesList[viewHolder!!.absoluteAdapterPosition].messageId)
                Log.e(TAG, "onLongClick: " + viewHolder!!.absoluteAdapterPosition)
                false
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.messageText.text = messagesList[position].messageText
        if (!messagesList[position].messageImageLink!!.isEmpty()) {
            viewHolder!!.imageHolder.visibility = View.VISIBLE
            Glide.with(context)
                    .load(messagesList[position].messageImageLink)
                    .centerCrop()
                    .error(R.drawable.user_image_placeholder)
                    .fallback(R.drawable.user_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(viewHolder!!.messageImage)
        } else {
            holder.imageHolder.visibility = View.GONE
        }
        try {
            holder.messageTime.text = messageTime(messagesList[position].time as Timestamp)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (messagesList[position].messageType == "from") {
            holder.messageItem.setOnLongClickListener {
                listener.onMessageHold(position, messagesList[position].messageId, messagesList[position].messageText, messagesList[position].otherUserEmail, "from")
                Log.e(TAG, "onLongClick: " + messagesList[position].messageId)
                Log.e(TAG, "onLongClick: $position")
                false
            }
        } else {
            holder.messageItem.setOnLongClickListener {
                listener.onMessageHold(position, messagesList[position].messageId, messagesList[position].messageText, messagesList[position].otherUserEmail, "to")
                Log.e(TAG, "onLongClick: " + messagesList[position].messageId)
                Log.e(TAG, "onLongClick: $position")
                false
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val type = messagesList[position].messageType
        return if (type == "from") {
            ITEM_TYPE_FROM
        } else {
            ITEM_TYPE_TO
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageItem: CardView
        var imageHolder: MaterialCardView
        var messageText: TextView
        var messageTime: TextView
        var messageImage: ImageView

        init {
            messageText = itemView.findViewById(R.id.messageText)
            messageTime = itemView.findViewById(R.id.messageTime)
            messageItem = itemView.findViewById(R.id.messageItem)
            messageImage = itemView.findViewById(R.id.messageImage)
            imageHolder = itemView.findViewById(R.id.messageImage_bg)
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

    interface MyClickListener {
        fun onMessageHold(position: Int, messageId: String?, messageText: String?, messageEmail: String?, type: String?)
    }

    companion object {
        private const val TAG = "debug"
        const val ITEM_TYPE_FROM = 0
        const val ITEM_TYPE_TO = 1
    }
}