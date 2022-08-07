package com.aprihive.adapters

import android.animation.Animator
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.*
import java.lang.Exception
import java.util.*

class DiscoverRecyclerAdapter(var context: Context, private var postList: List<DiscoverPostsModel>, private val listener: MyClickListener) : RecyclerView.Adapter<DiscoverRecyclerAdapter.ViewHolder>() {
    private var authorUsername: String? = null
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var getFullname: String? = null
    private var getUsername: String? = null
    private var getPostImageLink: String? = null
    private var getPostText: String? = null
    private var getUpvotes = 0
    private var getAuthorEmail: String? = null
    private var getProfileImageUrl: String? = null
    private var getVerified: Boolean? = null
    @JvmField
    var registerQuery: ListenerRegistration? = null
    @JvmField
    var likeRegisterQuery: ListenerRegistration? = null
    private var getPostId: String? = null
    private val processLike = false
    private var likeRef: DocumentReference? = null
    private var getPostTags: String? = null
    private var getPostTime: String? = null
    private var getLocation: String? = null
    private val textCrawler: TextCrawler? = null
    private var getLinkData: HashMap<String, String>? = null
    private var stripHttp: String? = null
    private var stripPaths: String? = null
    private var getThreat: Boolean? = null
    private var getToken: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflate layout(find_users_item)
        val view: View
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.discover_post_item_alt, parent, false)
        val viewHolder: ViewHolder = ViewHolder(view, listener)
        viewHolder.upvoteIcon.setOnClickListener { listener.onVote(viewHolder.absoluteAdapterPosition, postList[viewHolder.absoluteAdapterPosition].postId, viewHolder.upvoteIcon) }
        viewHolder.postText.setOnClickListener { listener.onTextExpandClick(viewHolder.absoluteAdapterPosition, viewHolder.postText) }
        viewHolder.optionsIcon.setOnClickListener { listener.onPostMenuClick(viewHolder.absoluteAdapterPosition, postList[viewHolder.absoluteAdapterPosition].postId, postList[viewHolder.absoluteAdapterPosition].authorEmail, postList[viewHolder.absoluteAdapterPosition].postText, postList[viewHolder.absoluteAdapterPosition].postImageLink) }
        viewHolder.postImage.setOnClickListener { listener.onImageClick(viewHolder.absoluteAdapterPosition, postList[viewHolder.absoluteAdapterPosition].postImageLink) }
        viewHolder.postFullName.setOnClickListener { listener.onProfileOpen(viewHolder.absoluteAdapterPosition, postList[viewHolder.absoluteAdapterPosition].authorEmail, authorUsername) }
        viewHolder.profileImage.setOnClickListener { listener.onProfileOpen(viewHolder.absoluteAdapterPosition, postList[viewHolder.absoluteAdapterPosition].authorEmail, authorUsername) }
        viewHolder.requestButton.setOnClickListener {
            listener.onSendRequest(viewHolder.absoluteAdapterPosition,
                    postList[viewHolder.absoluteAdapterPosition].postId,
                    postList[viewHolder.absoluteAdapterPosition].authorEmail,
                    postList[viewHolder.absoluteAdapterPosition].postText,
                    postList[viewHolder.absoluteAdapterPosition].postImageLink,
                    getToken,
                    postList[viewHolder.absoluteAdapterPosition].postId!!.substring(0, postList[viewHolder.absoluteAdapterPosition].postId!!.length - 7))
        }
        viewHolder.linkPreviewCard.setOnClickListener { listener.onLinkOpen(viewHolder.absoluteAdapterPosition, postList[viewHolder.absoluteAdapterPosition].linkData!!["url"]) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        getAuthorEmail = postList[position].authorEmail
        getPostText = postList[position].postText!!.replace("   ", "\n")
        getLocation = postList[position].location
        getPostTime = postList[position].timePosted
        getPostId = postList[position].postId
        getPostImageLink = postList[position].postImageLink
        getPostTags = postList[position].postTags
        getLinkData = postList[position].linkData
        reference = db!!.collection("users").document(getAuthorEmail!!)
        registerQuery = reference!!.addSnapshotListener { value, error ->
            //get user details to post
            try {
                getFullname = value!!.getString("name")
                getUsername = value.getString("username")
                getVerified = value.getBoolean("verified")
                getThreat = value.getBoolean("threat")
                getToken = value.getString("fcm-token")
                getProfileImageUrl = value.getString("profileImageLink")
                holder.postFullName.text = getFullname
                holder.postUsername.text = "@$getUsername"
                if (getUsername != FirebaseAuth.getInstance().currentUser!!.displayName) {
                    holder.requestButton.visibility = View.VISIBLE
                } else {
                    holder.requestButton.visibility = View.INVISIBLE
                }
                
                this.authorUsername = getUsername

                Log.e(TAG, "onBindViewHolder: The username is $authorUsername", )

                //profile dp
                Glide.with(context)
                        .load(getProfileImageUrl)
                        .centerCrop()
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.profileImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //verification icon
            try {
                if (getVerified!!) {
                    holder.verifiedIcon.visibility = View.VISIBLE
                } else {
                    holder.verifiedIcon.visibility = View.GONE
                }
            } catch (e: Exception) {
                //nothing
            }

            //threat icon
            try {
                if (getThreat!!) {
                    holder.threatIcon.visibility = View.VISIBLE
                } else {
                    holder.threatIcon.visibility = View.GONE
                }
            } catch (e: Exception) {
                //nothing
            }
        }
        likeRef = db!!.collection("upvotes").document(getPostId!!)
        likeRegisterQuery = likeRef!!.addSnapshotListener { value, error ->
            try {
                assert(user != null)
                val uid = user!!.uid
                getLikeCount(holder, value)
                var check = false
                try {
                    check = value!!.contains(uid)
                } catch (e: Exception) {
                }
                if (check) {
                    //holder.upvoteIcon.setColorFilter(context.getResources().getColor(R.color.color_success_green_200));
                    holder.upvoteIcon.setAnimation("lottie_upvote_active.json")
                    holder.upvoteIcon.frame = 24
                } else {
                    //holder.upvoteIcon.setColorFilter(context.getResources().getColor(R.color.grey_color_100));
                    holder.upvoteIcon.setAnimation("lottie_upvote_default.json")
                    holder.upvoteIcon.frame = 24
                }
                holder.upvoteIcon.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        //DiscoverRecyclerAdapter.this.notifyDataSetChanged();
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        holder.upvoteIcon.frame = 24
                        //getLikeCount(holder, value);
                    }

                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                })
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
        }
        holder.postText.text = getPostText
        holder.postTime.text = "$getPostTime "
        if (getPostImageLink === "" || getPostImageLink == null) {
            holder.postImage.visibility = View.GONE
            holder.postImage_bg.visibility = View.GONE
        } else {
            //post image
            holder.postImage.visibility = View.VISIBLE
            holder.postImage_bg.visibility = View.VISIBLE
            Glide.with(context)
                    .load(getPostImageLink)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.postImage)
        }
        try {
            if (getLinkData!!.get("title") != "") {
                holder.linkTitle.text = getLinkData!!.get("title")
                holder.linkDescription.text = getLinkData!!.get("description")
                Glide.with(context)
                        .load(getLinkData!!.get("image"))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                                holder.linkImage.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                holder.linkImage.visibility = View.VISIBLE
                                return false
                            }
                        })
                        .into(holder.linkImage)
                if (getLinkData!!.get("url")!!.lowercase(Locale.getDefault()).contains("https://")) {
                    stripHttp = getLinkData!!.get("url")!!.replaceFirst("https://".toRegex(), "")
                } else if (getLinkData!!.get("url")!!.lowercase(Locale.getDefault()).contains("http://")) {
                    stripHttp = getLinkData!!.get("url")!!.replaceFirst("http://".toRegex(), "")
                }
                stripPaths = if (stripHttp!!.contains("/")) {
                    stripHttp!!.split("/".toRegex()).toTypedArray()[0]
                } else {
                    stripHttp
                }
                holder.linkUrl.text = stripPaths
                holder.linkPreviewBar.visibility = View.VISIBLE
            }
            if (getLinkData!!.get("title") == "") {
                holder.linkPreviewBar.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLikeCount(holder: ViewHolder, value: DocumentSnapshot?) {
        try {
            val map = value!!.data
            getUpvotes = map!!.size
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (getUpvotes == 1) {
            holder.trustedByText.text = "  Upvoted by $getUpvotes person"
        } else {
            holder.trustedByText.text = "  Upvoted by $getUpvotes people"
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(itemView: View, listener: MyClickListener?) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView
        val verifiedIcon: ImageView
        val threatIcon: ImageView
        val postImage: ImageView
        val linkImage: ImageView
        val upvoteIcon: LottieAnimationView
        val optionsIcon: CardView
        val postImage_bg: CardView
        val postFullName: TextView
        val postUsername: TextView
        val postText: TextView
        val trustedByText: TextView
        val requestButton: TextView
        val postTime: TextView
        val linkTitle: TextView
        val linkDescription: TextView
        val linkUrl: TextView
        private val postItem: ConstraintLayout
        val linkPreviewBar: ConstraintLayout
        val linkPreviewCard: MaterialCardView

        init {
            profileImage = itemView.findViewById(R.id.post_profileImage)
            verifiedIcon = itemView.findViewById(R.id.post_verifiedIcon)
            threatIcon = itemView.findViewById(R.id.post_warningIcon)
            postFullName = itemView.findViewById(R.id.post_fullName)
            postUsername = itemView.findViewById(R.id.post_username)
            postText = itemView.findViewById(R.id.post_text)
            postItem = itemView.findViewById(R.id.postItem)
            postTime = itemView.findViewById(R.id.postTime)
            postImage = itemView.findViewById(R.id.postImage)
            postImage_bg = itemView.findViewById(R.id.postImage_bg)
            optionsIcon = itemView.findViewById(R.id.optionsIcon)
            trustedByText = itemView.findViewById(R.id.trustedBy)
            requestButton = itemView.findViewById(R.id.requestButton)
            upvoteIcon = itemView.findViewById(R.id.upvoteIcon)
            linkTitle = itemView.findViewById(R.id.linkTitle)
            linkDescription = itemView.findViewById(R.id.linkDescription)
            linkUrl = itemView.findViewById(R.id.linkUrl)
            linkImage = itemView.findViewById(R.id.linkImage)
            linkPreviewBar = itemView.findViewById(R.id.linkPreviewBar)
            linkPreviewCard = itemView.findViewById(R.id.linkPreviewCard)
        }
    }

    interface MyClickListener {
        fun onVote(position: Int, postId: String?, icon: LottieAnimationView?)
        fun onSendRequest(position: Int, postId: String?, postAuthorEmail: String?, postText: String?, postImage: String?, token: String?, postAuthor: String?)
        fun onPostMenuClick(position: Int, postId: String?, postAuthorEmail: String?, postText: String?, postImage: String?)
        fun onTextExpandClick(position: Int, textView: TextView?)
        fun onProfileOpen(position: Int, postAuthor: String?, postAuthorUsername: String?)
        fun onImageClick(position: Int, postimage: String?)
        fun onLinkOpen(position: Int, link: String?)
    }

    override fun getItemId(position: Int): Long {
        return postList[position].positionId.toLong()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        likeRegisterQuery!!.remove()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        try {
            likeRegisterQuery!!.remove()
            textCrawler!!.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // method for filtering our recyclerview items.
    fun filterList(filterList: ArrayList<DiscoverPostsModel>) {

        // below line is to add our filtered list in our course array list.
        postList = filterList

        // below line is to notify our adapter of change in recycler view data.
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "debug"
    }
}