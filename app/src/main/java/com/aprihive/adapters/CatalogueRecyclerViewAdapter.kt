// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
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

class CatalogueRecyclerViewAdapter(var context: Context, private val itemList: List<CatalogueModel>) : RecyclerView.Adapter<CatalogueRecyclerViewAdapter.MyViewHolder>() {
    @JvmField
    var registerQuery: ListenerRegistration? = null
    private var getItemName: String? = null
    private var getItemImageLink: String? = null
    private var getItemDescription: String? = null
    private var getItemId: String? = null
    private var getItemPrice: String? = null
    private var getItemUrl: String? = null
    private var getItemAuthor: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        //inflate layout(find_users_item)
        val view: View
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.catalogue_item, parent, false)
        val viewHolder = MyViewHolder(view)
        viewHolder.itemContainer.setOnClickListener {
            val intent = Intent(context, CatalogueItemDetails::class.java)
            intent.putExtra("getSellerEmail", itemList[viewHolder.absoluteAdapterPosition].itemAuthor)
            intent.putExtra("getItemUrl", itemList[viewHolder.absoluteAdapterPosition].itemUrl)
            intent.putExtra("getItemImageLink", itemList[viewHolder.absoluteAdapterPosition].itemImageLink)
            intent.putExtra("getItemName", itemList[viewHolder.absoluteAdapterPosition].itemName)
            intent.putExtra("getItemPrice", itemList[viewHolder.absoluteAdapterPosition].itemPrice)
            intent.putExtra("getItemDescription", itemList[viewHolder.absoluteAdapterPosition].itemDescription)
            intent.putExtra("getItemId", itemList[viewHolder.absoluteAdapterPosition].itemId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItemName = itemList[position].itemName
        getItemId = itemList[position].itemId
        getItemImageLink = itemList[position].itemImageLink
        getItemPrice = itemList[position].itemPrice
        getItemDescription = itemList[position].itemDescription
        getItemUrl = itemList[position].itemUrl
        getItemAuthor = itemList[position].itemAuthor
        holder.catalogueItemName.text = getItemName
        holder.catalogueItemDescription.text = getItemDescription
        holder.catalogueItemPrice.text = getItemPrice

        /*if (getItemAuthor.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            holder.moreIcon.setVisibility(View.VISIBLE);
        }*/Glide.with(context)
                .load(getItemImageLink)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView
        var moreIcon: CardView
        var catalogueItemName: TextView
        var catalogueItemDescription: TextView
        var catalogueItemPrice: TextView
        var itemContainer: ConstraintLayout

        init {
            itemImage = itemView.findViewById(R.id.itemImage)
            moreIcon = itemView.findViewById(R.id.optionsIcon)
            catalogueItemName = itemView.findViewById(R.id.itemName)
            catalogueItemPrice = itemView.findViewById(R.id.itemPrice)
            catalogueItemDescription = itemView.findViewById(R.id.itemDescription)
            itemContainer = itemView.findViewById(R.id.containerItem)
        }
    }

    interface MyClickListener {
        fun onPostMenuClick(position: Int, postId: String?, postAuthorEmail: String?, postText: String?, postImage: String?)
    }
}