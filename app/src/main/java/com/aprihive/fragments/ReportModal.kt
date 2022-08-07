// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aprihive.R
import com.aprihive.methods.MySnackBar
import com.aprihive.methods.NetworkListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.*

class ReportModal : BottomSheetDialogFragment() {
    private val emailInput: EditText? = null
    private var submit: Button? = null
    private var info: TextView? = null
    private val title: TextView? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var reference: DocumentReference? = null
    private var user: FirebaseUser? = null
    private val profileUpdates: UserProfileChangeRequest? = null
    private val networkListener: NetworkListener? = null
    private var loading: ConstraintLayout? = null
    private val closeScreen: ConstraintLayout? = null
    private val forgotPasswordScreen: ConstraintLayout? = null
    private val email: String? = null
    private var addText: EditText? = null
    private val imagePreview: ImageView? = null
    private val saveTo: String? = null
    private val imageType: String? = null
    private val imageUri: Uri? = null
    private val storageReference: StorageReference? = null
    private val postImageLink: String? = null
    private val random: Random? = null
    private var addPostText: String? = null
    private var postId: String? = null
    private var mContext: Context? = null
    private var requestDetails: MutableMap<String, Any?>? = null
    private val myInflater: LayoutInflater? = null
    private val myContainer: ViewGroup? = null
    private val mySavedInstance: Bundle? = null
    private val likeRef: DocumentReference? = null
    private val setLikes: Map<String, Any>? = null
    private var previewText: TextView? = null
    private var deadline: EditText? = null
    private var selectDate: TextView? = null
    private var onDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var modalSubtitle: TextView? = null
    private var sendTo: String? = null
    private var sendToAuthorEmail: String? = null
    private var deadlineText: String? = null
    private var homeView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_report_modal, container, false)
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        mContext = requireActivity().applicationContext
        homeView = requireActivity().window.decorView.findViewById(R.id.page)
        addText = view.findViewById(R.id.addText)
        previewText = view.findViewById(R.id.previewText)
        submit = view.findViewById(R.id.submitBtn)
        loading = view.findViewById(R.id.loading)
        info = view.findViewById(R.id.errorFeedback)
        deadline = view.findViewById(R.id.deadline)
        selectDate = view.findViewById(R.id.selectDate)
        modalSubtitle = view.findViewById(R.id.modalSubtitle)
        sendTo = requireArguments().getString("postAuthor")
        sendToAuthorEmail = requireArguments().getString("postAuthorEmail")
        postId = requireArguments().getString("postId")
        previewText!!.setText(requireArguments().getString("postText"))
        modalSubtitle!!.setText("We promise to make our findings and take necessary actions.")
        selectDate!!.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR] // Initial year selection
            val month = calendar[Calendar.MONTH] // Initial month selection
            val day = calendar[Calendar.DAY_OF_MONTH] // Inital day selection
            val datePickerDialog = DatePickerDialog(requireContext(), onDateSetListener, year, month, day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            //datePickerDialog.getDatePicker().setMaxDate(maxDateConfig.getTimeInMillis());
            datePickerDialog.show()
        })
        onDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, getMonth, day ->
            val month = getMonth + 1
            val date = "$day/$month/$year"
            deadline!!.setText(date)
        }
        submit!!.setOnClickListener(View.OnClickListener { checkInputs() })
        return view
    }

    private fun checkInputs() {
        addPostText = addText!!.text.toString().trim { it <= ' ' }
        deadlineText = deadline!!.text.toString().trim { it <= ' ' }

        //check email
        if (addPostText!!.isEmpty()) {
            info!!.text = "Please indicate why you are reporting."
            info!!.visibility = View.VISIBLE
        } else {
            uploadPost()
            submit!!.visibility = View.INVISIBLE
            info!!.visibility = View.GONE
            loading!!.visibility = View.VISIBLE
            Log.d("debug", "test 0")
        }
    }

    private fun uploadPost() {
        requestDetails = HashMap()
        requestDetails!!["type"] = "from"
        requestDetails!!["postId"] = postId
        requestDetails!!["authorEmail"] = requireArguments().getString("postAuthorEmail")
        requestDetails!!["receiverUsername"] = sendTo
        requestDetails!!["senderUsername"] = user!!.displayName
        requestDetails!!["senderEmail"] = user!!.email
        requestDetails!!["requestText"] = addPostText
        requestDetails!!["postText"] = requireArguments().getString("postText")
        requestDetails!!["postImageLink"] = requireArguments().getString("postImage")
        requestDetails!!["requested on"] = Timestamp(Date())
        requestDetails!!["deadLine"] = deadlineText

        //Map<String, Object> setDetails = new HashMap<>();
        //setDetails.put("requestFrom:-" + user.getDisplayName() + "-for:-" + postId, requestDetails);
        Log.d("debug", "test 1")
        reference = db!!.collection("users").document(sendToAuthorEmail!!).collection("requests").document("requestFrom:-" + user!!.displayName + "-for:-" + postId)
        reference!!.set(requestDetails!!)
                .addOnSuccessListener {
                    val snackBar = MySnackBar(requireActivity().applicationContext, homeView, "Report Successful", R.color.color_theme_blue, Snackbar.LENGTH_LONG)
                    dismiss()
                    Log.d("debug", "test 2")
                }
                .addOnFailureListener { e ->
                    val snackBar = MySnackBar(requireActivity().applicationContext, homeView, "failed", R.color.color_error_red_100, Snackbar.LENGTH_LONG)
                    dismiss()
                    Log.d("debug", "test 3$e")
                }
    }
}