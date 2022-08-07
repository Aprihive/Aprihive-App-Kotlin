package com.aprihive.pages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.aprihive.R
import com.aprihive.RequestDetails
import com.aprihive.adapters.NotificationsRecyclerViewAdapter
import com.aprihive.models.NotificationModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class Requests : Fragment(), NotificationsRecyclerViewAdapter.MyClickListener {
    private var recyclerView: RecyclerView? = null
    var notificationList: MutableList<NotificationModel>? = null
    private var adapter: NotificationsRecyclerViewAdapter? = null
    private var getType: String? = null
    private var getAuthorEmail: String? = null
    private var getDeadline: String? = null
    private var getPostId: String? = null
    private var getPostImageLink: String? = null
    private var getPostText: String? = null
    private var getSenderEmail: String? = null
    private var getRequestText: String? = null
    private var getRequestedOn: Timestamp? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private val getTitle: String? = null
    private val getAuthorUsername: String? = null
    private var getReceiverUsername: String? = null
    private var getSenderUsername: String? = null
    var refreshRequestsRunnable: Runnable? = null
    private var mContext: Context? = null
    private var nothingText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_requests, container, false)
        mContext = requireActivity().applicationContext
        notificationList = ArrayList()
        recyclerView = view.findViewById(R.id.notificationRecyclerView)
        swipeRefresh = view.findViewById(R.id.notification_swipeRefresh)
        nothingText = view.findViewById(R.id.nothingText)

        // //to refresh posts after posting or deleting
        // refreshRequestsRunnable = new Runnable() {
        //     @Override
        //     public void run() {
        //         getRequestsNotifications();
        //     }
        // };
        swipeRefresh!!.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.bg_color))
        swipeRefresh!!.setColorSchemeColors(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.color_theme_green_100), resources.getColor(R.color.color_theme_green_300))
        swipeRefresh!!.setOnRefreshListener(OnRefreshListener { requestsNotifications })
        adapter = NotificationsRecyclerViewAdapter(requireContext(), notificationList!!, this)
        swipeRefresh!!.setRefreshing(true)
        requestsNotifications
        return view
    }

    private fun setupRecyclerView(notificationList: List<NotificationModel>?) {
        if (notificationList!!.isEmpty()) {
            nothingText!!.visibility = View.VISIBLE
        } else {
            nothingText!!.visibility = View.GONE
        }
        try {
            val linearLayoutManager = LinearLayoutManager(activity!!.applicationContext)
            //set items to arrange from bottom
            // linearLayoutManager.setReverseLayout(true);
            //linearLayoutManager.setStackFromEnd(true);
            recyclerView!!.layoutManager = linearLayoutManager
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //.orderBy("registered on", "asc")
    val requestsNotifications: Unit
        get() {
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val db = FirebaseFirestore.getInstance()
            Log.d("debug", "re1 $notificationList")
            assert(user != null)
            val notificationsQuery = db.collection("users").document(user!!.email!!).collection("requests").orderBy("requested on", Query.Direction.DESCENDING)

            //.orderBy("registered on", "asc")
            notificationsQuery.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    notificationList!!.clear()
                    Log.d("debug", "re2 $notificationList")
                    for (value in task.result) {
                        getType = value.getString("type")
                        getPostId = value.getString("postId")
                        getAuthorEmail = value.getString("authorEmail")
                        getReceiverUsername = value.getString("receiverUsername")
                        getSenderUsername = value.getString("senderUsername")
                        getSenderEmail = value.getString("senderEmail")
                        getRequestText = value.getString("requestText")
                        getPostText = value.getString("postText")
                        getPostImageLink = value.getString("postImageLink")
                        getRequestedOn = value.getTimestamp("requested on")
                        getDeadline = value.getString("deadLine")
                        val notificationModel = NotificationModel()
                        notificationModel.type = getType
                        notificationModel.postId = getPostId
                        notificationModel.authorEmail = getAuthorEmail
                        notificationModel.receiverUsername = getReceiverUsername
                        notificationModel.senderUsername = getSenderUsername
                        notificationModel.senderEmail = getSenderEmail
                        notificationModel.requestText = getRequestText
                        notificationModel.postText = getPostText
                        notificationModel.postImageLink = getPostImageLink
                        notificationModel.requestedOn = requestedOn(getRequestedOn)
                        notificationModel.deadline = getDeadline
                        notificationList!!.add(notificationModel)
                        Log.d("debug", "re3 $notificationList")
                    }
                    setupRecyclerView(notificationList)
                    swipeRefresh!!.isRefreshing = false
                }
            }
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        // Inflate the layout for this menu
        inflater.inflate(R.menu.find_bar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search by username or description..."
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
        searchView.setOnSearchClickListener { activity!!.findViewById<View>(R.id.logoImageView).visibility = View.GONE }
        searchView.setOnCloseListener {
            activity!!.findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
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

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredNotificationList: MutableList<NotificationModel> = ArrayList()
        val query = text.lowercase(Locale.getDefault())

        // running a for loop to compare elements.
        for (item in notificationList!!) {

            // checking if the entered string matched with any item of our recycler view.
            if (item.authorEmail!!.lowercase(Locale.getDefault()).contains(query)) {
                filteredNotificationList.add(item)
            }
        }
        if (filteredNotificationList.isEmpty()) {
            // if no item is added in filtered list we are displaying a toast message as no data found.

            //TODO: add image to show that no notification found
        } else {

            // at last we are passing that filtered list to our adapter class.
            adapter!!.filterList((filteredNotificationList as ArrayList<NotificationModel>))
        }
    }

    private fun requestedOn(timestamp: Timestamp?): String {
        val date = timestamp!!.toDate()
        val prettyTime = PrettyTime(Locale.getDefault())
        return prettyTime.format(date)
    }

    override fun onOpenRequestDetails(position: Int, getType: String?, getSenderUsername: String?, getReceiverUsername: String?, getDeadline: String?, getPostId: String?, getPostImageLink: String?, getPostText: String?, getRequestText: String?, getRequestedOn: String?, getSenderEmail: String?, getReceiverEmail: String?) {
        val intent = Intent(mContext, RequestDetails::class.java)
        intent.putExtra("getType", getType)
        intent.putExtra("getSenderName", getSenderUsername)
        intent.putExtra("getReceiverName", getReceiverUsername)
        intent.putExtra("getDeadline", getDeadline)
        intent.putExtra("getPostId", getPostId)
        intent.putExtra("getPostImageLink", getPostImageLink)
        intent.putExtra("getPostText", getPostText)
        intent.putExtra("getRequestText", getRequestText)
        intent.putExtra("getRequestedOn", getRequestedOn)
        intent.putExtra("getSenderEmail", getSenderEmail)
        intent.putExtra("getReceiverEmail", getReceiverEmail)
        // intent.putExtra("refreshAction", (Serializable) refreshRequestsRunnable);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mContext!!.startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        requestsNotifications
    }

    companion object {
        private val TAG: Any = "ok"
    }
}