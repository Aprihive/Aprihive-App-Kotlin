// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.fragments

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.EditText
import android.widget.TextView
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
import android.app.Activity
import android.content.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.widget.MultiAutoCompleteTextView
import com.leocardz.link.preview.library.TextCrawler
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
import android.provider.MediaStore
import android.util.Log
import com.leocardz.link.preview.library.LinkPreviewCallback
import com.leocardz.link.preview.library.SourceContent
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.animation.Animation
import android.view.WindowManager
import com.aprihive.backend.RetrofitInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.aprihive.methods.MyActionDialog
import android.widget.Toast
import android.widget.RadioGroup
import android.widget.RadioButton
import com.aprihive.methods.SharedPrefs
import android.widget.DatePicker
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class PostOptionsModal(private val postsRefresh: Runnable) : BottomSheetDialogFragment() {
    private var editPostClick: ConstraintLayout? = null
    private var deletePostClick: ConstraintLayout? = null
    private var copyPostClick: ConstraintLayout? = null
    private var sharePostClick: ConstraintLayout? = null
    private var removeImageClick: ConstraintLayout? = null
    private var postIdDisplay: TextView? = null
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var fetchPostId: String? = null
    private var storageReference: StorageReference? = null
    private val bundle: Bundle? = null
    private var mContext: Context? = null
    private var fetchPostImage: String? = null
    private var action: Runnable? = null
    private var removeImageAction: Runnable? = null
    private var retrofitInterface: RetrofitInterface? = null
    private var retrofit: Retrofit? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_post_options, container, false)
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofitInterface = retrofit!!.create(RetrofitInterface::class.java)
        mContext = requireActivity().applicationContext


        //create runnable action to pass as argument to the custom dialog modal
        action = Runnable { confirmDelete() }
        removeImageAction = Runnable { confirmImageRemove() }
        editPostClick = view.findViewById(R.id.editPostClick)
        removeImageClick = view.findViewById(R.id.removeImageClick)
        deletePostClick = view.findViewById(R.id.deletePostClick)
        val deletePostText = view.findViewById<TextView>(R.id.textView3)
        copyPostClick = view.findViewById(R.id.copyPostClick)
        sharePostClick = view.findViewById(R.id.sharePostClick)
        postIdDisplay = view.findViewById(R.id.postId)
        fetchPostId = requireArguments().getString("postId")
        fetchPostImage = requireArguments().getString("postImage")
        postIdDisplay!!.setText(fetchPostId)
        if (requireArguments().getString("postAuthorEmail") == user!!.email) {
            deletePostClick!!.setVisibility(View.VISIBLE)
            //editPostClick.setVisibility(View.VISIBLE);
        } else if (requireArguments().getBoolean("isAdmin")) {
            deletePostText.text = "Delete (Admin)"
            deletePostClick!!.setVisibility(View.VISIBLE)
            if (fetchPostImage != "") {
                removeImageClick!!.setVisibility(View.VISIBLE)
            }
        }
        copyPostClick!!.setOnClickListener(View.OnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("post", requireArguments().getString("postText"))
            clipboard.setPrimaryClip(clip)
            val snackBar = MySnackBar(requireContext(), requireActivity().window.decorView.findViewById(android.R.id.content), "Copied to clipboard!", R.color.color_theme_blue, Snackbar.LENGTH_SHORT)
            dismiss()
        })
        deletePostClick!!.setOnClickListener(View.OnClickListener {
            assert(arguments != null)
            if (requireArguments().getString("postAuthorEmail") == user!!.email) {
                val dialog = MyActionDialog(context, "Delete Post?", "Are you sure you want to delete this post?", R.drawable.vg_delete, action!!)
                dialog.show()
            } else if (requireArguments().getBoolean("isAdmin")) {
                val dialog = MyActionDialog(context, "Delete this Post?", "Are you sure you want to delete this post for violation of guidelines?", R.drawable.vg_delete, action!!)
                dialog.show()
            }
        })
        removeImageClick!!.setOnClickListener(View.OnClickListener {
            val dialog = MyActionDialog(context, "Remove Image?", "Are you sure you want to remove this post's image for containing graphic violence or explicit content? \n (This action cannot be undone!)", R.drawable.vg_delete, removeImageAction!!, "Yes, remove.", "No, cancel.")
            dialog.show()
        })
        return view
    }

    private fun confirmDelete() {
        val db = FirebaseFirestore.getInstance()
        val storageReference = FirebaseStorage.getInstance().reference

        //delete post itself
        val postReference = db.collection("posts").document(fetchPostId!!)
        postReference.delete()

        //delete image if any
        if (fetchPostImage != "") {
            val fileRef = storageReference.child("posts-images").child(fetchPostId + "_postImage.jpg")
            fileRef.delete()
        }
        if (requireArguments().getBoolean("isAdmin") && requireArguments().getString("postAuthorEmail") != user!!.email) {
            sendDeleteNotification()
        }


        //delete post upvotes document
        val postLikesReference = db.collection("upvotes").document(fetchPostId!!)
        postLikesReference.delete().addOnSuccessListener {
            val snackBar = MySnackBar(requireContext(), requireActivity().window.decorView.findViewById(android.R.id.content), "Delete Successful", R.color.color_theme_blue, Snackbar.LENGTH_SHORT)
            postsRefresh.run()
            dismiss()
        }
    }

    private fun sendDeleteNotification() {
        val map = HashMap<String?, String?>()
        map["receiverEmail"] = requireArguments().getString("postAuthorEmail")
        map["postId"] = fetchPostId
        map["postText"] = requireArguments().getString("postText")
        val call = retrofitInterface!!.executePostRemovalNotification(map)
        call!!.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.code() == 200) {
                    Log.d("email-status", "email sent")
                } else if (response.code() == 400) {
                    Log.d("email-status", "failure: email not sent")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed: " + t.message, Toast.LENGTH_LONG).show()
                Log.e("error", t.message!!)
                Log.e("error", t.localizedMessage)
            }
        })
    }

    private fun confirmImageRemove() {
        val db = FirebaseFirestore.getInstance()
        val storageReference = FirebaseStorage.getInstance().reference
        val details: MutableMap<String, Any> = HashMap()
        details["imageLink"] = ""

        //delete image itself
        val postReference = db.collection("posts").document(fetchPostId!!)
        postReference.update(details).addOnSuccessListener {
            val fileRef = storageReference.child("posts-images").child(fetchPostId + "_postImage.jpg")
            fileRef.delete().addOnSuccessListener {
                val snackBar = MySnackBar(requireContext(), requireActivity().window.decorView.findViewById(android.R.id.content), "Image removed successfully", R.color.color_theme_blue, Snackbar.LENGTH_SHORT)
                postsRefresh.run()
                dismiss()
            }
        }
                .addOnFailureListener {
                    val snackBar = MySnackBar(requireContext(), requireActivity().window.decorView.findViewById(android.R.id.content), "Something went wrong, Please try again.", R.color.color_error_red_200, Snackbar.LENGTH_SHORT)
                    postsRefresh.run()
                    dismiss()
                }
    }
}