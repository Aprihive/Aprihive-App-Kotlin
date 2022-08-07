package com.aprihive.pages

import android.content.Context
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.airbnb.lottie.LottieAnimationView
import com.aprihive.ImageViewActivity
import com.aprihive.R
import com.aprihive.adapters.DiscoverRecyclerAdapter
import com.aprihive.fragments.PostOptionsModal
import com.aprihive.fragments.SendRequestModal
import com.aprihive.models.DiscoverPostsModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Feed : Fragment(), DiscoverRecyclerAdapter.MyClickListener {
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private val reference: DocumentReference? = null
    private val user: FirebaseUser? = null
    private var recyclerView: RecyclerView? = null
    private var postList: MutableList<DiscoverPostsModel>? = null
    private var adapter: DiscoverRecyclerAdapter? = null
    private var mContext: Context? = null
    private var getPostId: String? = null
    private var getLocation: String? = null
    private var getPostText: String? = null
    private var getPostImageLink //fetch from firebase into
            : String? = null
    private val getVerified //fetch from firebase into
            : Boolean? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private val fab: FloatingActionButton? = null
    private val registerQuery: ListenerRegistration? = null
    private var discoverModel: DiscoverPostsModel? = null
    private var getPostUserEmail: String? = null
    private val postAuthorList: List<DiscoverPostsModel>? = null
    private val TAG = "debug"
    private var nothingText: TextView? = null
    private var profileEmail: String? = null
    private var getTags: String? = null
    private var getTime: Timestamp? = null
    private val fetchLink: String? = null
    private var getLinkData: HashMap<String, String>? = null
    private var getPositionId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.findRecyclerView)
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh)
        mContext = requireActivity().applicationContext
        nothingText = view.findViewById(R.id.nothingText)


        //to refresh posts after posting or deleting
        refreshPostsRunnable = Runnable { fetchPostsFromBackend() }
        swipeRefresh!!.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.bg_color))
        swipeRefresh!!.setColorSchemeColors(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.color_theme_green_100), resources.getColor(R.color.color_theme_green_300))
        swipeRefresh!!.setOnRefreshListener(OnRefreshListener { fetchPostsFromBackend() })
        postList = ArrayList()
        adapter = DiscoverRecyclerAdapter(requireContext(), postList!!, this)
        Log.d(TAG, "attached adapter")
        profileEmail = container!!.tag.toString()
        swipeRefresh!!.setRefreshing(true)
        fetchPostsFromBackend()
        return view
    }

    private fun setupRecyclerView() {
        assert(activity != null)
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        //set items to arrange from bottom
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = adapter
    }

    fun fetchPostsFromBackend() {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            Log.d("checking posts", "ok talk")
            val discoverQuery = db.collection("posts").orderBy("created on")
            discoverQuery.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    postList!!.clear()
                    for (value in task.result) {
                        getLocation = value.getString("location")
                        getTime = value.getTimestamp("created on")
                        getPostText = value.getString("postText")
                        getPostImageLink = value.getString("imageLink")
                        getPostUserEmail = value.getString("userEmail")
                        getPostId = value.getString("postId")
                        getPositionId = postList!!.size + 1
                        discoverModel = DiscoverPostsModel()
                        discoverModel!!.authorEmail = getPostUserEmail
                        discoverModel!!.location = getLocation
                        try {
                            discoverModel!!.timePosted = postTime(getTime)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        discoverModel!!.postText = getPostText
                        discoverModel!!.postImageLink = getPostImageLink
                        discoverModel!!.postId = getPostId
                        try {
                            getLinkData = value["linkPreviewData"] as HashMap<String, String>?
                            discoverModel!!.linkData = getLinkData
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            getTags = value.getString("tags")
                            discoverModel!!.postTags = getTags
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        discoverModel!!.positionId = getPositionId
                        postList!!.add(discoverModel!!)
                    }
                    setupRecyclerView()
                    swipeRefresh!!.isRefreshing = false
                }
                filter(profileEmail)
            }
        }

    override fun onResume() {
        super.onResume()
        fetchPostsFromBackend()
    }

    override fun onStop() {
        super.onStop()
        try {
            adapter!!.registerQuery!!.remove()
            adapter!!.likeRegisterQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            adapter!!.registerQuery!!.remove()
            adapter!!.likeRegisterQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onVote(position: Int, postId: String?, icon: LottieAnimationView?) {
        val processLike = arrayOf(true)
        val fileRef = db!!.collection("upvotes").document(postId!!)
        fileRef.addSnapshotListener { value, error ->
            try {
                if (processLike[0]) {
                    if (value!!.contains(auth!!.currentUser!!.uid)) {
                        fileRef.update(auth!!.currentUser!!.uid, FieldValue.delete())
                        icon!!.playAnimation()
                        processLike[0] = false
                    } else {
                        fileRef.update(auth!!.currentUser!!.uid, auth!!.currentUser!!.email)
                        icon!!.playAnimation()
                        processLike[0] = false
                    }
                }
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onSendRequest(position: Int, postId: String?, postAuthorEmail: String?, postText: String?, postImage: String?, token: String?, postAuthor: String?) {
        val bottomSheet = SendRequestModal()
        val bundle = Bundle()
        bundle.putString("postAuthorEmail", postAuthorEmail)
        bundle.putString("postAuthor", postAuthor)
        bundle.putString("postText", postText)
        bundle.putString("postImage", postImage)
        bundle.putString("postId", postId)
        bundle.putString("token", token)
        bottomSheet.arguments = bundle
        bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onPostMenuClick(position: Int, postId: String?, postAuthorEmail: String?, postText: String?, postImage: String?) {
        val bottomSheet = PostOptionsModal(refreshPostsRunnable!!)
        val bundle = Bundle()
        bundle.putString("postAuthorEmail", postAuthorEmail)
        bundle.putString("postText", postText)
        bundle.putString("postId", postId)
        bundle.putString("postImage", postImage)
        bottomSheet.arguments = bundle
        bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onTextExpandClick(position: Int, textView: TextView?) {
        if (textView!!.maxLines < 4) {
            textView.maxLines = Int.MAX_VALUE
        } else {
            textView.maxLines = 3
        }
    }

    override fun onProfileOpen(position: Int, postAuthor: String?, postAuthorUsername: String?) {
        //nothing
    }

    override fun onImageClick(position: Int, postImage: String?) {
        val i = Intent(mContext, ImageViewActivity::class.java)
        i.putExtra("imageUri", postImage)
        startActivity(i)
    }

    override fun onLinkOpen(position: Int, link: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    private fun filter(text: String?) {
        // creating a new array list to filter our data.
        val filteredList: MutableList<DiscoverPostsModel> = ArrayList()
        val query = text!!.lowercase(Locale.getDefault())


        // running a for loop to compare elements.
        for (item in postList!!) {


            // checking if the entered string matched with any item of our recycler view.
            if (item.authorEmail!!.lowercase(Locale.getDefault()).contains(query)) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            adapter!!.filterList((filteredList as ArrayList<DiscoverPostsModel>))
            nothingText!!.visibility = View.VISIBLE
        } else {
            nothingText!!.visibility = View.GONE
            //at last we are passing that filtered list to our adapter class.
            adapter!!.filterList((filteredList as ArrayList<DiscoverPostsModel>))
        }
    }

    @Throws(ParseException::class)
    private fun postTime(timestamp: Timestamp?): String {
        var time = ""
        val date = timestamp!!.toDate()
        val prettyTime = PrettyTime(Locale.getDefault())
        val ago = prettyTime.format(date)
        val sdf = SimpleDateFormat("dd MMM, yyyy")
        time = if (ago.contains("1 day ago")) {
            "yesterday"
        } else if (ago.contains("month")) {
            //time = String.valueOf(sdf.parse(String.valueOf(date)));
            sdf.format(date)
        } else {
            ago
        }
        return time
    }

    companion object {
        var refreshPostsRunnable: Runnable? = null
    }
}