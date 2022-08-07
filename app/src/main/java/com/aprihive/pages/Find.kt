package com.aprihive.pages

import android.content.res.Resources.NotFoundException
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.aprihive.Home
import com.aprihive.R
import com.aprihive.adapters.FindRecyclerViewAdapter
import com.aprihive.models.FindModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Find : Fragment(), FindRecyclerViewAdapter.MyClickListener {
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var recyclerView: RecyclerView? = null
    private var usersList: MutableList<FindModel>? = null
    private var adapter: FindRecyclerViewAdapter? = null
    private var getEmail: String? = null
    private var getFullname: String? = null
    private var getUsername: String? = null
    private var getPhone: String? = null
    private var getBio: String? = null
    private var getSchoolName: String? = null
    private var getProfileImageLink //fetch from firebase into
            : String? = null
    private var getVerified //fetch from firebase into
            : Boolean? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var nothingImage: ConstraintLayout? = null
    private var getTwitter: String? = null
    private var getInstagram: String? = null
    private var getThreat: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find, container, false)
        val context = requireActivity().applicationContext
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        usersList = ArrayList()
        recyclerView = view.findViewById(R.id.findRecyclerView)
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh)
        nothingImage = view.findViewById(R.id.notFoundImage)
        swipeRefresh!!.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.bg_color))
        swipeRefresh!!.setColorSchemeColors(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.color_theme_green_100), resources.getColor(R.color.color_theme_green_300))
        swipeRefresh!!.setOnRefreshListener(OnRefreshListener { usersFromFirebase })
        adapter = FindRecyclerViewAdapter(requireContext(), usersList!!, this)
        swipeRefresh!!.setRefreshing(true)
        usersFromFirebase
        return view
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        //set items to arrange from bottom
        // linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = adapter
    }

    //.orderBy("registered on", "asc")
    private val usersFromFirebase: Unit
        private get() {
            val user = auth!!.currentUser
            val db = FirebaseFirestore.getInstance()
            assert(user != null)
            val findQuery = db.collection("users").orderBy("registered on")

            //.orderBy("registered on", "asc")
            findQuery.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    usersList!!.clear()
                    for (value in task.result) {
                        getEmail = value.getString("email")
                        getFullname = value.getString("name")
                        getPhone = value.getString("phone")
                        getTwitter = value.getString("twitter")
                        getInstagram = value.getString("instagram")
                        getVerified = value.getBoolean("verified")
                        getThreat = value.getBoolean("threat")
                        getBio = value.getString("bio")
                        getUsername = value.getString("username")
                        getSchoolName = value.getString("school")
                        getProfileImageLink = value.getString("profileImageLink")
                        val findModel = FindModel()
                        findModel.fullname = getFullname
                        findModel.email = getEmail
                        findModel.bio = getBio
                        findModel.username = getUsername
                        findModel.verified = getVerified
                        findModel.schoolName = getSchoolName
                        findModel.profileImageUrl = getProfileImageLink
                        findModel.phone = getPhone
                        findModel.twitter = getTwitter
                        findModel.instagram = getInstagram
                        findModel.threat = getThreat
                        usersList!!.add(findModel)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            usersList!!.removeIf { p: FindModel -> p.email == user!!.email }
                        }
                    }
                    setupRecyclerView()
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
        searchView.setOnSearchClickListener {
            (activity as Home?)!!.hideHamburger()
            activity!!.findViewById<View>(R.id.logoImageView).visibility = View.GONE
        }
        searchView.setOnCloseListener {
            (activity as Home?)!!.showHamburger()
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
        val filteredUsersList: MutableList<FindModel> = ArrayList()
        val query = text.lowercase(Locale.getDefault())

        // running a for loop to compare elements.
        for (item in usersList!!) {

            // checking if the entered string matched with any item of our recycler view.
            if (item.username!!.lowercase(Locale.getDefault()).contains(query)) {
                filteredUsersList.add(item)
            } else if (item.fullname!!.lowercase(Locale.getDefault()).contains(query)) {
                filteredUsersList.add(item)
            } else if (item.schoolName!!.lowercase(Locale.getDefault()).contains(query)) {
                filteredUsersList.add(item)
            } else if (item.bio!!.lowercase(Locale.getDefault()).contains(query)) {
                filteredUsersList.add(item)
            }
        }
        if (filteredUsersList.isEmpty()) {
            // if no item is added in filtered list we are displaying a toast message as no data found.
            adapter!!.filterList((filteredUsersList as ArrayList<FindModel>))
            nothingImage!!.visibility = View.VISIBLE
        } else {

            // at last we are passing that filtered list to our adapter class.
            adapter!!.filterList((filteredUsersList as ArrayList<FindModel>))
            nothingImage!!.visibility = View.GONE
        }
    }

    override fun onFollow(position: Int, userEmail: String?) {
        val fileRef = db!!.collection("users").document(userEmail!!).collection("lists").document("following")
        val processFollow = arrayOf(true)
        fileRef.addSnapshotListener { value, error ->
            try {
                if (processFollow[0]) {
                    if (value!!.contains(auth!!.currentUser!!.uid)) {
                        fileRef.update(auth!!.currentUser!!.uid, FieldValue.delete())
                        processFollow[0] = false
                    } else {
                        fileRef.update(auth!!.currentUser!!.uid, auth!!.currentUser!!.email)
                        processFollow[0] = false
                    }
                }
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private val TAG: Any = "ok"
    }
}