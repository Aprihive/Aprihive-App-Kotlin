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
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.util.*

class AddPostModal(private val postsRefresh: Runnable) : BottomSheetDialogFragment() {
    private val emailInput: EditText? = null
    private var submit: Button? = null
    private val closeBtn: Button? = null
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
    private var tags: MultiAutoCompleteTextView? = null
    private var imagePreview: ImageView? = null
    private var saveTo: String? = null
    private val imageType: String? = null
    private var imageUri: Uri? = null
    private var storageReference: StorageReference? = null
    private val postImageLink: String? = null
    private var random: Random? = null
    private var addPostText: String? = null
    private var postId: String? = null
    private var mContext: Context? = null
    private var postDetails: MutableMap<String, Any?>? = null
    private var myInflater: LayoutInflater? = null
    private var myContainer: ViewGroup? = null
    private var mySavedInstance: Bundle? = null
    private var likeRef: DocumentReference? = null
    private var setLikes: MutableMap<String, Any?>? = null
    var tagsList: Array<String?>? = null
    private var tagsText: String? = null
    private var textCrawler: TextCrawler? = null
    private var fetchLink: String? = null
    private var linkPreviewData: HashMap<String, String>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_add_post_modal, container, false)
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        tagsList = arrayOf("Catering,", "Graphics Design,", "Development,", "App development,", "Animation,", "Fashion Designing,", "Trading,", "Bitcoin,", "Repairs,", "Clothing,", "Shoes,", "Games,", "Confectioneries,", "Shoe making,", "Tutorials,", "Crypto,", "NFTs,", "Books,", "Forex,", "Digital,")
        myInflater = inflater
        myContainer = container
        mySavedInstance = savedInstanceState
        mContext = requireActivity().applicationContext
        addText = view.findViewById(R.id.addText)
        tags = view.findViewById(R.id.tags)
        imagePreview = view.findViewById(R.id.imagePreview)
        submit = view.findViewById(R.id.submitBtn)
        loading = view.findViewById(R.id.loading)
        info = view.findViewById(R.id.errorFeedback)
        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(mContext!!, android.R.layout.simple_list_item_1, tagsList!!)
        tags!!.setAdapter(adapter)
        tags!!.setTokenizer(CommaTokenizer())
        random = Random()
        linkPreviewData = HashMap()
        linkPreviewData!!["title"] = ""
        linkPreviewData!!["description"] = ""
        linkPreviewData!!["image"] = ""
        linkPreviewData!!["url"] = ""
        postId = random!!.nextInt(1000000).toString()
        imagePreview!!.setOnClickListener(View.OnClickListener {
            val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            openGalleryIntent.type = "image/*"
            startActivityForResult(openGalleryIntent, 1000)
            //CropImage.activity().setAspectRatio(3,2).setFixAspectRatio(true).start(getContext(), AddPostModal.this);
        })
        submit!!.setOnClickListener(View.OnClickListener { checkInputs() })
        return view
    }

    private fun checkInputs() {
        addPostText = addText!!.text.toString().trim()
        tagsText = tags!!.text.toString().trim()

        //check email
        if (addPostText!!.isEmpty()) {
            info!!.text = "Please add a description"
            info!!.visibility = View.VISIBLE
        } else {
            uploadPost()
            submit!!.visibility = View.INVISIBLE
            info!!.visibility = View.GONE
            loading!!.visibility = View.VISIBLE
        }
    }

    private fun uploadPost() {
        postDetails = HashMap()
        if (imageUri != null) {
            val fileRef = storageReference!!.child("posts-images").child(saveTo!!)
            fileRef.putFile(imageUri!!).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    postDetails!!["imageLink"] = uri.toString()
                    proceed()
                }
            }
        } else {
            postDetails!!["imageLink"] = ""
            fetchLinkData()
        }
    }

    private fun fetchLinkData() {
        //find link in post
        val postLinks: MutableList<String> = ArrayList()
        val m = Patterns.WEB_URL.matcher(addPostText)
        while (m.find()) {
            val url = m.group()
            postLinks.add(url)
        }
        if (!postLinks.isEmpty()) {
            fetchLink = postLinks[0]
            textCrawler = TextCrawler()
            val lpCallback: LinkPreviewCallback = object : LinkPreviewCallback {
                override fun onPre() {}
                override fun onPos(metaData: SourceContent, isNull: Boolean) {
                    linkPreviewData!!["title"] = metaData.title
                    linkPreviewData!!["description"] = metaData.description
                    linkPreviewData!!["image"] = metaData.images[0]
                    linkPreviewData!!["url"] = metaData.url
                    postDetails!!["linkPreviewData"] = linkPreviewData
                    proceed()
                }
            }
            textCrawler!!.makePreview(lpCallback, fetchLink)
        } else {
            proceed()
        }
    }

    private fun proceed() {
        postDetails!!["name"] = requireArguments().getString("fullname")
        postDetails!!["tags"] = tagsText
        postDetails!!["location"] = requireArguments().getString("location")
        postDetails!!["username"] = user!!.displayName
        postDetails!!["verified"] = requireArguments().getBoolean("verified")
        postDetails!!["postId"] = user!!.displayName + "_" + postId
        postDetails!!["userEmail"] = user!!.email
        postDetails!!["upvotes"] = "0"
        postDetails!!["postText"] = addPostText
        postDetails!!["linkPreviewData"] = linkPreviewData
        postDetails!!["created on"] = Timestamp(Date())
        reference = db!!.collection("posts").document(user!!.displayName + "_" + postId)
        reference!!.set(postDetails!!).addOnSuccessListener { createLikeDb() }.addOnFailureListener {
            val snackBar = MySnackBar(requireActivity().applicationContext, requireActivity().window.decorView, "Something went wrong. Please Try again", R.color.color_error_red_200, Snackbar.LENGTH_LONG)
            dismiss()
        }
    }

    private fun createLikeDb() {
        likeRef = db!!.collection("upvotes").document(user!!.displayName + "_" + postId)
        setLikes = HashMap()
        setLikes!![user!!.uid] = user!!.email
        likeRef!!.set(setLikes!!).addOnSuccessListener {
            dismiss()
            postsRefresh.run()
            val feedbackBar = requireActivity().findViewById<ConstraintLayout>(R.id.feedbackBar)
            val feedbackBarText = requireActivity().findViewById<TextView>(R.id.textFeedback)
            val feedbackBarAnimation = requireActivity().findViewById<LottieAnimationView>(R.id.animationView)
            val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabAddPost)
            val fadeUp = AnimationUtils.loadAnimation(context, R.anim.fade_up_animation)
            feedbackBarText.text = "Your campaign was successfully created!"
            Handler().postDelayed({
                feedbackBar.visibility = View.VISIBLE
                feedbackBar.animation = fadeUp
                fab.hide()
                feedbackBarAnimation.playAnimation()
            }, 1500)
            Log.d("Debug", "success stuff")
        }.addOnFailureListener { e ->
            e.printStackTrace()
            Log.d("Debug", "failed$e")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        saveTo = user!!.displayName + "_" + postId + "_postImage.jpg"
        if (requestCode == 1000) {
            //CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data!!.data
                //imageUri = result.getUri();
                imagePreview!!.setPadding(0, 0, 0, 0)
                Glide.with(this) //2
                        .load(imageUri) //3
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fallback(R.drawable.user_image_placeholder) //7
                        .into(imagePreview!!) //8
                Log.d("checking stuffs", "the thing 66 reach here ooo")

                //save image to firebase passing the type of image to be profile image (this is my idea, so that i can only use one method everytime)
                //uploadImageToFirestore(imageUri, saveTo);
            }
        }
    }
}