// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.fragments

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.aprihive.methods.NetworkListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.storage.StorageReference
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.aprihive.R
import com.google.firebase.storage.FirebaseStorage
import com.cottacush.android.currencyedittext.CurrencyInputWatcher
import com.theartofdev.edmodo.cropper.CropImage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.aprihive.methods.MySnackBar
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.app.Activity
import android.app.DatePickerDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.leocardz.link.preview.library.TextCrawler
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.provider.MediaStore
import com.leocardz.link.preview.library.LinkPreviewCallback
import com.leocardz.link.preview.library.SourceContent
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.animation.Animation
import android.view.WindowManager
import com.aprihive.backend.RetrofitInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.ClipData
import android.content.Context
import com.aprihive.methods.MyActionDialog
import com.aprihive.methods.SharedPrefs
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SendRequestModal : BottomSheetDialogFragment() {
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
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null
    private var sharedPreferences: SharedPreferences? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_send_request_modal, container, false)
        mContext = requireActivity().applicationContext
        homeView = requireActivity().window.decorView.findViewById(R.id.page)
        sharedPreferences = mContext!!.getSharedPreferences("aprihive", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofitInterface = retrofit!!.create(RetrofitInterface::class.java)
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
        modalSubtitle!!.setText("Send a request to $sendTo for this service")
        selectDate!!.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR] // Initial year selection
            val month = calendar[Calendar.MONTH] // Initial month selection
            val day = calendar[Calendar.DAY_OF_MONTH] // Initial day selection
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
            info!!.text = "Please add a short brief"
            info!!.visibility = View.VISIBLE
        } else {
            uploadRequest()
            submit!!.visibility = View.INVISIBLE
            info!!.visibility = View.GONE
            loading!!.visibility = View.VISIBLE
            Log.d("debug", "test 0")
        }
    }

    private fun uploadRequest() {
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
                    createPersonalRequestInstance()
                    sendNotifications()
                    Log.d("debug", "test 2")
                }
                .addOnFailureListener { e ->
                    val snackBar = MySnackBar(requireActivity().applicationContext, homeView, "failed", R.color.color_error_red_100, Snackbar.LENGTH_LONG)
                    dismiss()
                    Log.d("debug", "test 3$e")
                }
    }

    private fun sendNotifications() {
        val map = HashMap<String, String?>()
        map["token"] = requireArguments().getString("token")
        map["senderEmail"] = user!!.email
        map["receiverEmail"] = requireArguments().getString("postAuthorEmail")
        map["senderUsername"] = user!!.displayName
        map["receiverUsername"] = sendTo
        map["postId"] = postId
        map["postText"] = requireArguments().getString("postText")
        map["deadline"] = deadlineText
        map["requestId"] = "requestTo:-$sendTo-for:-$postId"
        val call = retrofitInterface!!.executeRequestNotification(map)
        call.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.code() == 200) {
                    Log.e("request-push-status", "sent")
                } else if (response.code() == 400) {
                    Log.e("request-push-status", "failure: not sent")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(mContext, "Failed: " + t.message, Toast.LENGTH_LONG).show()
                Log.e("error", t.message!!)
                Log.e("error", t.localizedMessage)
            }
        })
        val callPushNotifications = retrofitInterface!!.executeRequestPushNotification(map)
        Log.e("debug", "sendNotifications: about to send request notification")
        callPushNotifications.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.code() == 200) {
                    Log.e("request-notify-status", "sent request notification")
                } else if (response.code() == 400) {
                    Log.e("request-notify-status", "failure: not sent")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e("error", t.message!!)
                Log.e("error", t.localizedMessage)
            }
        })
    }

    private fun createPersonalRequestInstance() {
        requestDetails = HashMap()
        requestDetails!!["type"] = "to"
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

        // Map<String, Object> setDetails = new HashMap<>();
        // setDetails.put("request-to:_" + sendTo + "_for:_" + postId, requestDetails);
        reference = db!!.collection("users").document(user!!.email!!).collection("requests").document("requestTo:-$sendTo-for:-$postId")
        reference!!.set(requestDetails!!)
                .addOnSuccessListener {
                    val snackBar = MySnackBar(requireActivity().applicationContext, homeView, "Request sent to $sendTo successfully", R.color.color_theme_blue, Snackbar.LENGTH_LONG)
                    dismiss()
                }
    }
}