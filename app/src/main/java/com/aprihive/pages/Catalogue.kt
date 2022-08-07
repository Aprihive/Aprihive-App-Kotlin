package com.aprihive.pages

import android.content.Context
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
import com.aprihive.R
import com.aprihive.adapters.CatalogueRecyclerViewAdapter
import com.aprihive.fragments.PostOptionsModal
import com.aprihive.models.CatalogueModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class Catalogue : Fragment(), CatalogueRecyclerViewAdapter.MyClickListener {
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private val reference: DocumentReference? = null
    private val user: FirebaseUser? = null
    private var recyclerView: RecyclerView? = null
    private var itemList: MutableList<CatalogueModel>? = null
    private var adapter: CatalogueRecyclerViewAdapter? = null
    private var mContext: Context? = null
    private var getItemId: String? = null
    private var getItemName: String? = null
    private var getItemDescription: String? = null
    private var getItemImageLink: String? = null
    private var getItemPrice: String? = null
    private var getItemUrl //fetch from firebase into
            : String? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private val fab: FloatingActionButton? = null
    private val registerQuery: ListenerRegistration? = null
    private var catalogueModel: CatalogueModel? = null
    private var getItemAuthorEmail: String? = null
    private val postAuthorList: List<CatalogueModel>? = null
    private val TAG = "debug"
    private var nothingText: TextView? = null
    private var profileEmail: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_catalogue, container, false)

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.findRecyclerView)
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh)
        mContext = requireActivity().applicationContext
        nothingText = view.findViewById(R.id.nothingText)
        profileEmail = container!!.tag.toString()


        //to refresh posts after posting or deleting
        refreshItemsRunnable = Runnable { itemsFromBackend }
        swipeRefresh!!.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.bg_color))
        swipeRefresh!!.setColorSchemeColors(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.color_theme_green_100), resources.getColor(R.color.color_theme_green_300))
        swipeRefresh!!.setOnRefreshListener(OnRefreshListener { itemsFromBackend })
        itemList = ArrayList()
        adapter = CatalogueRecyclerViewAdapter(requireContext(), itemList!!)
        Log.d(TAG, "attached adapter")
        swipeRefresh!!.setRefreshing(true)
        itemsFromBackend
        return view
    }

    private fun setupRecyclerView() {
        if (itemList!!.isEmpty()) {
            nothingText!!.visibility = View.VISIBLE
        } else {
            nothingText!!.visibility = View.GONE
        }
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        //set items to arrange from bottom
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = adapter
    }

    val itemsFromBackend: Unit
        get() {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            Log.d("checking posts", "ok talk")
            val itemQuery = db.collection("users").document(profileEmail!!).collection("catalogue").orderBy("created on", Query.Direction.DESCENDING)
            itemQuery.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    itemList!!.clear()
                    for (value in task.result) {
                        getItemName = value.getString("name")
                        getItemDescription = value.getString("description")
                        getItemImageLink = value.getString("imageLink")
                        getItemAuthorEmail = value.getString("userEmail")
                        getItemId = value.getString("itemId")
                        getItemPrice = value.getString("itemPrice")
                        getItemUrl = value.getString("itemUrl")
                        catalogueModel = CatalogueModel()
                        catalogueModel!!.itemAuthor = getItemAuthorEmail
                        catalogueModel!!.itemName = getItemName
                        catalogueModel!!.itemDescription = getItemDescription
                        catalogueModel!!.itemImageLink = getItemImageLink
                        catalogueModel!!.itemId = getItemId
                        catalogueModel!!.itemPrice = getItemPrice
                        catalogueModel!!.itemUrl = getItemUrl
                        itemList!!.add(catalogueModel!!)
                    }
                    setupRecyclerView()
                    swipeRefresh!!.isRefreshing = false
                }
            }
        }

    override fun onResume() {
        super.onResume()
        itemsFromBackend
    }

    override fun onStop() {
        super.onStop()
        try {
            adapter!!.registerQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            adapter!!.registerQuery!!.remove()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPostMenuClick(position: Int, postId: String?, postAuthorEmail: String?, postText: String?, postImage: String?) {
        val bottomSheet = PostOptionsModal(refreshItemsRunnable!!)
        val bundle = Bundle()
        bundle.putString("postAuthorEmail", postAuthorEmail)
        bundle.putString("postText", postText)
        bundle.putString("postId", postId)
        bundle.putString("postImage", postImage)
        bottomSheet.arguments = bundle
        bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
    }

    companion object {
        var refreshItemsRunnable: Runnable? = null
    }
}