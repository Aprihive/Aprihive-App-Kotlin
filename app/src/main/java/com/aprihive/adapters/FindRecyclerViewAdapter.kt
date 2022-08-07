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
import com.google.firebase.firestore.*
import java.lang.Exception
import java.util.ArrayList

class FindRecyclerViewAdapter(var context: Context, private var userList: List<FindModel>, private val listener: MyClickListener) : RecyclerView.Adapter<FindRecyclerViewAdapter.MyViewHolder>() {
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var getFullname: String? = null
    private var getUsername: String? = null
    private var getBio: String? = null
    private var getSchoolName: String? = null
    private var getProfileImageUrl: String? = null
    private var getVerified: Boolean? = null
    private var likeRegisterQuery: ListenerRegistration? = null
    private var getUserEmail: String? = null
    private var getThreat: Boolean? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        //inflate layout(find_users_item)
        val view: View
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.find_users_item, parent, false)
        val viewHolder = MyViewHolder(view)
        viewHolder.userItem.setOnClickListener {
            if (userList[viewHolder.absoluteAdapterPosition].username != FirebaseAuth.getInstance().currentUser!!.displayName) {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("getEmail", userList[viewHolder.absoluteAdapterPosition].email)
                intent.putExtra("getVerified", userList[viewHolder.absoluteAdapterPosition].verified)
                intent.putExtra("getThreat", userList[viewHolder.absoluteAdapterPosition].threat)
                intent.putExtra("getPhone", userList[viewHolder.absoluteAdapterPosition].phone)
                intent.putExtra("getTwitter", userList[viewHolder.absoluteAdapterPosition].twitter)
                intent.putExtra("getInstagram", userList[viewHolder.absoluteAdapterPosition].instagram)
                intent.putExtra("getUsername", userList[viewHolder.absoluteAdapterPosition].username)
                intent.putExtra("getFullName", userList[viewHolder.absoluteAdapterPosition].fullname)
                intent.putExtra("getProfileImageUrl", userList[viewHolder.absoluteAdapterPosition].profileImageUrl)
                intent.putExtra("getBio", userList[viewHolder.absoluteAdapterPosition].bio)
                intent.putExtra("getSchoolName", userList[viewHolder.absoluteAdapterPosition].schoolName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
        viewHolder.followButton.setOnClickListener { listener.onFollow(viewHolder.absoluteAdapterPosition, userList[viewHolder.absoluteAdapterPosition].email) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        getFullname = userList[position].fullname
        getUserEmail = userList[position].email
        getSchoolName = userList[position].schoolName
        getUsername = userList[position].username
        getProfileImageUrl = userList[position].profileImageUrl
        getBio = userList[position].bio
        getVerified = userList[position].verified
        getThreat = userList[position].threat
        holder.findFullName.text = getFullname
        holder.findBio.text = getBio
        holder.findUsername.text = "@$getUsername"
        if (getUsername == FirebaseAuth.getInstance().currentUser!!.displayName) {
            holder.followButton.visibility = View.INVISIBLE
        }
        checkIfUserIsInFavourites(holder)
        Glide.with(context)
                .load(getProfileImageUrl)
                .centerCrop()
                .error(R.drawable.user_image_placeholder)
                .fallback(R.drawable.user_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.profileImage)
        if (getVerified!!) {
            holder.verifiedIcon.visibility = View.VISIBLE
        } else {
            holder.verifiedIcon.visibility = View.GONE
        }
        if (getThreat!!) {
            holder.threatIcon.visibility = View.VISIBLE
        } else {
            holder.threatIcon.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView
        var verifiedIcon: ImageView
        var threatIcon: ImageView
        var findFullName: TextView
        var findUsername: TextView
        var findBio: TextView
        var followButton: TextView
        var userItem: ConstraintLayout

        init {
            profileImage = itemView.findViewById(R.id.find_profileImage)
            verifiedIcon = itemView.findViewById(R.id.find_verifiedIcon)
            threatIcon = itemView.findViewById(R.id.find_warningIcon)
            followButton = itemView.findViewById(R.id.connectButton)
            findFullName = itemView.findViewById(R.id.find_fullName)
            findUsername = itemView.findViewById(R.id.find_username)
            findBio = itemView.findViewById(R.id.find_bio)
            userItem = itemView.findViewById(R.id.userItem)
        }
    }

    interface MyClickListener {
        fun onFollow(position: Int, userEmail: String?)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterList: ArrayList<FindModel>) {

        // below line is to add our filtered list in our course array list.
        userList = filterList

        // below line is to notify our adapter of change in recycler view data.
        notifyDataSetChanged()
    }

    private fun checkIfUserIsInFavourites(holder: MyViewHolder) {
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
                    holder.followButton.background = context.resources.getDrawable(R.drawable.connect_active_button)
                    holder.followButton.text = "Upvoted"
                    holder.followButton.setTextColor(context.resources.getColor(R.color.bg_color))
                } else {
                    holder.followButton.background = context.resources.getDrawable(R.drawable.connect_default_button)
                    holder.followButton.text = "Upvote"
                    holder.followButton.setTextColor(context.resources.getColor(R.color.color_theme_blue))
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
}