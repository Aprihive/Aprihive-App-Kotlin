package com.aprihive.pages

import android.content.Context
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.airbnb.lottie.LottieAnimationView
import com.aprihive.*
import com.aprihive.adapters.DiscoverRecyclerAdapter
import com.aprihive.fragments.PostOptionsModal
import com.aprihive.fragments.SendRequestModal
import com.aprihive.models.DiscoverPostsModel
import com.facebook.shimmer.ShimmerFrameLayout
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

class Discover : Fragment(), DiscoverRecyclerAdapter.MyClickListener {
    var connectButton: TextView? = null
    var callButton: TextView? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private val reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var recyclerView: RecyclerView? = null
    private var postList: MutableList<DiscoverPostsModel>? = null
    private var adapter: DiscoverRecyclerAdapter? = null
    private var mContext: Context? = null
    private var getPostId: String? = null
    private val getUpvotes: String? = null
    private var getPostText: String? = null
    private var getPostImageLink //fetch from firebase into
            : String? = null
    private val getVerified //fetch from firebase into
            : Boolean? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var fab: FloatingActionButton? = null
    private val registerQuery: ListenerRegistration? = null
    private var discoverModel: DiscoverPostsModel? = null
    private var getPostUserEmail: String? = null
    private val postAuthorList: List<DiscoverPostsModel>? = null
    private val TAG = "debug"
    private var getTags: String? = null
    private var nothingImage: ConstraintLayout? = null
    private var getLocation: String? = null
    private var getTime: Timestamp? = null
    private val fetchLink: String? = null
    private var getLinkData: HashMap<String, String>? = null
    private var shimmer: ShimmerFrameLayout? = null
    private var getPositionId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser
        recyclerView = view.findViewById(R.id.findRecyclerView)
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh)
        mContext = requireActivity().applicationContext
        nothingImage = view.findViewById(R.id.notFoundImage)
        shimmer = view.findViewById(R.id.shimmerLayout)
        shimmer!!.startShimmer()
        fab = requireActivity().findViewById(R.id.fabAddPost)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && fab!!.isShown()) {
                    fab!!.hide()
                } else if (dy < 0 && !fab!!.isShown()) {
                    fab!!.show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        requireActivity().findViewById<View>(R.id.refreshButton).setOnClickListener {
            swipeRefresh!!.setRefreshing(true)
            postsFromBackend
        }

        //to refresh posts after posting or deleting
        refreshPostsRunnable = Runnable {
            swipeRefresh!!.setRefreshing(true)
            postsFromBackend
        }
        swipeRefresh!!.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.bg_color))
        swipeRefresh!!.setColorSchemeColors(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.color_theme_green_100), resources.getColor(R.color.color_theme_green_300))
        swipeRefresh!!.setOnRefreshListener(OnRefreshListener { postsFromBackend })
        postList = ArrayList()
        adapter = DiscoverRecyclerAdapter(requireContext(), postList!!, this)
        adapter!!.setHasStableIds(true)
        Log.d(TAG, "attached adapter")
        postsFromBackend
        return view
    }

    private fun setupRecyclerView() {
        if (activity != null) {
            val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
            //set items to arrange from bottom
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true
            recyclerView!!.layoutManager = linearLayoutManager
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.adapter = adapter
        }
    }

    val postsFromBackend: Unit
        get() {
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
                        discoverModel!!.positionId = getPositionId
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
                        postList!!.add(discoverModel!!)
                    }
                    shimmer!!.stopShimmer()
                    shimmer!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                    setupRecyclerView()
                    swipeRefresh!!.isRefreshing = false
                }
            }
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
                        //icon.setAnimation("lottie_upvote_default.json");
                        fileRef.update(auth!!.currentUser!!.uid, FieldValue.delete())
                        adapter!!.notifyDataSetChanged()
                        icon!!.playAnimation()
                        processLike[0] = false
                    } else {
                        //icon.setAnimation("lottie_upvote_active.json");
                        fileRef.update(auth!!.currentUser!!.uid, auth!!.currentUser!!.email)
                        adapter!!.notifyDataSetChanged()
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
        val admin = (activity as Home?)!!.isAdmin!!
        val bottomSheet = PostOptionsModal(refreshPostsRunnable!!)
        val bundle = Bundle()
        bundle.putString("postAuthorEmail", postAuthorEmail)
        bundle.putString("postText", postText)
        bundle.putString("postId", postId)
        bundle.putString("postImage", postImage)
        bundle.putBoolean("isAdmin", admin)
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
        if (postAuthor == user!!.email) {
            val i = Intent(mContext, PersonalProfileActivity::class.java)
            i.putExtra("getEmail", postAuthor)
            startActivity(i)
        } else {
            val i = Intent(mContext, UserProfileActivity::class.java)
            i.putExtra("getEmail", postAuthor)
            i.putExtra("getUsername", postAuthorUsername)
            startActivity(i)
        }
    }

    override fun onImageClick(position: Int, postImage: String?) {
        val i = Intent(mContext, ImageViewActivity::class.java)
        i.putExtra("imageUri", postImage)
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // Inflate the layout for this menu
        inflater.inflate(R.menu.discover_bar_menu, menu)

        //MenuItem filterItem = menu.findItem(R.id.action_filter);
//
        //PostsFilterModal bottomSheet = new PostsFilterModal();
        //bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search by keywords, tags, location etc."
        searchView.maxWidth = Int.MAX_VALUE
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        val viewEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        viewEditText.setHintTextColor(resources.getColor(R.color.grey_color_100))
        viewEditText.setTextColor(resources.getColor(R.color.color_text_blue_500))
        val viewIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        viewIcon.setColorFilter(resources.getColor(R.color.color_text_blue_500))
        val viewCloseIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        viewCloseIcon.setColorFilter(resources.getColor(R.color.color_text_blue_500))
        val viewBg = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        viewBg.background = resources.getDrawable(R.drawable.text_box)
        searchView.setOnSearchClickListener {
            (activity as Home?)!!.hideHamburger()
            requireActivity().findViewById<View>(R.id.logoImageView).visibility = View.GONE
        }
        searchView.setOnCloseListener {
            (activity as Home?)!!.showHamburger()
            requireActivity().findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
            false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.isIconified = true
                searchView.isIconified = true
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (adapter != null) {
                    filter(newText)
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onLinkOpen(position: Int, link: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredPostLists: MutableList<DiscoverPostsModel> = ArrayList()
        val query = text.lowercase(Locale.getDefault())

        // running a for loop to compare elements.
        for (item in postList!!) {

            // checking if the entered string matched with any item of our recycler view.
            try {
                if (item.postTags!!.lowercase(Locale.getDefault()).contains(query)) {
                    filteredPostLists.add(item)
                } else if (item.postId!!.lowercase(Locale.getDefault()).contains(query)) {
                    filteredPostLists.add(item)
                } else if (item.location!!.lowercase(Locale.getDefault()).contains(query)) {
                    filteredPostLists.add(item)
                } else if (item.postText!!.lowercase(Locale.getDefault()).contains(query)) {
                    filteredPostLists.add(item)
                } else if (item.authorEmail!!.lowercase(Locale.getDefault()).contains(query)) {
                    filteredPostLists.add(item)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (filteredPostLists.isEmpty()) {
            // if no item is added in filtered list we are displaying a toast message as no data found.
            adapter!!.filterList((filteredPostLists as ArrayList<DiscoverPostsModel>))
            nothingImage!!.visibility = View.VISIBLE
        } else {

            // at last we are passing that filtered list to our adapter class.
            adapter!!.filterList((filteredPostLists as ArrayList<DiscoverPostsModel>))
            nothingImage!!.visibility = View.GONE
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