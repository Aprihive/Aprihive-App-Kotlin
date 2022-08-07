package com.aprihive.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.aprihive.MessagingActivity
import com.aprihive.R
import com.aprihive.adapters.MessagedUsersRecyclerAdapter
import com.aprihive.models.MessagedUsersModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class Messages : Fragment(), MessagedUsersRecyclerAdapter.MyClickListener {
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private val reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var getEmail: String? = null
    private val getMessageDetails: HashMap<String, Any>? = null
    private var recyclerView: RecyclerView? = null
    private var usersList: MutableList<MessagedUsersModel>? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var nothingImage: ConstraintLayout? = null
    private var adapter: MessagedUsersRecyclerAdapter? = null
    private val getLastMessage: String? = null
    private var registerQuery: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = auth!!.currentUser
        usersList = ArrayList()
        recyclerView = view.findViewById(R.id.messagedRecyclerView)
        swipeRefresh = view.findViewById(R.id.messaged_swipeRefresh)
        nothingImage = view.findViewById(R.id.notFoundImage)
        swipeRefresh!!.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.bg_color))
        swipeRefresh!!.setColorSchemeColors(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.color_theme_green_100), resources.getColor(R.color.color_theme_green_300))
        swipeRefresh!!.setOnRefreshListener(OnRefreshListener { messagedUsersFromFirebase })
        adapter = MessagedUsersRecyclerAdapter(requireContext(), usersList!!, this)
        swipeRefresh!!.setRefreshing(true)
        messagedUsersFromFirebase
        return view
    }

    private fun setupRecyclerView() {
        try {
            val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)


            //set items to arrange from bottom
            //linearLayoutManager.setReverseLayout(true);
            //linearLayoutManager.setStackFromEnd(true);
            recyclerView!!.layoutManager = linearLayoutManager
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val messagedUsersFromFirebase: Unit
        private get() {
            assert(user != null)
            val messagesQuery = db!!.collection("users").document(user!!.email!!).collection("messages").orderBy("time", Query.Direction.DESCENDING)
            registerQuery = messagesQuery.addSnapshotListener { result, error ->
                usersList!!.clear()
                try {
                    for (value in result!!) {
                        getEmail = value.id
                        val messagedUsersModel = MessagedUsersModel()
                        messagedUsersModel.receiverEmail = getEmail
                        usersList!!.add(messagedUsersModel)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                setupRecyclerView()
                swipeRefresh!!.isRefreshing = false
            }
        }

    override fun onOpen(position: Int, getEmail: String?) {
        val intent = Intent(context, MessagingActivity::class.java)
        intent.putExtra("getEmail", getEmail)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        try {
            registerQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            registerQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        messagedUsersFromFirebase
    }
}