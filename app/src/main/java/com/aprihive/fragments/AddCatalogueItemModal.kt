// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved
package com.aprihive.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cottacush.android.currencyedittext.CurrencyInputWatcher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*

class AddCatalogueItemModal(private val itemsRefresh: Runnable) : BottomSheetDialogFragment() {
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
    private var addTitle: EditText? = null
    private var addPrice: EditText? = null
    private var addDescription: EditText? = null
    private var addUrl: EditText? = null
    private var imagePreview: ImageView? = null
    private var saveTo: String? = null
    private val imageType: String? = null
    private var imageUri: Uri? = null
    private var storageReference: StorageReference? = null
    private val postImageLink: String? = null
    private var random: Random? = null
    private var addTitleItem: String? = null
    private var addDescriptionItem: String? = null
    private var addPriceItem: String? = null
    private var addUrlItem: String? = null
    private var itemId: String? = null
    private var mContext: Context? = null
    private var itemDetails: MutableMap<String, Any?>? = null
    private var nairaImage: ImageView? = null
    private var dollarImage: ImageView? = null
    private var currency = "N"
    private var mAddPriceItem: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_add_catalogue_item_modal, container, false)
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        mContext = requireActivity().applicationContext
        addTitle = view.findViewById(R.id.addTitle)
        addDescription = view.findViewById(R.id.addDescription)
        addPrice = view.findViewById(R.id.addPrice)
        addUrl = view.findViewById(R.id.addUrl)
        imagePreview = view.findViewById(R.id.imagePreview)
        dollarImage = view.findViewById(R.id.dollarImage)
        nairaImage = view.findViewById(R.id.nairaImage)
        submit = view.findViewById(R.id.submitBtn)
        loading = view.findViewById(R.id.loading)
        info = view.findViewById(R.id.errorFeedback)

        //set naira symbol as active
        nairaImage!!.setColorFilter(resources.getColor(R.color.bg_color))
        nairaImage!!.setBackgroundColor(resources.getColor(R.color.color_theme_blue))
        dollarImage!!.setBackground(resources.getDrawable(R.drawable.bg_left_rounded))
        dollarImage!!.setColorFilter(resources.getColor(R.color.grey_color_100))
        currency = "₦"
        addPrice!!.addTextChangedListener(CurrencyInputWatcher(addPrice!!, "", Locale.getDefault()))
        //
        random = Random()
        itemId = random!!.nextInt(1000000).toString()
        imagePreview!!.setOnClickListener(View.OnClickListener { CropImage.activity().setAspectRatio(3, 2).setActivityTitle("Crop Image").setFixAspectRatio(false).start(requireContext(), this@AddCatalogueItemModal) })
        submit!!.setOnClickListener(View.OnClickListener { checkInputs() })
        dollarImage!!.setOnClickListener(View.OnClickListener {
            dollarImage!!.setColorFilter(resources.getColor(R.color.bg_color))
            dollarImage!!.setBackground(resources.getDrawable(R.drawable.bg_left_rounded_active))
            nairaImage!!.setBackgroundColor(resources.getColor(R.color.text_box_bg))
            nairaImage!!.setColorFilter(resources.getColor(R.color.grey_color_100))
            currency = "$"
        })
        nairaImage!!.setOnClickListener(View.OnClickListener {
            nairaImage!!.setColorFilter(resources.getColor(R.color.bg_color))
            nairaImage!!.setBackgroundColor(resources.getColor(R.color.color_theme_blue))
            dollarImage!!.setBackground(resources.getDrawable(R.drawable.bg_left_rounded))
            dollarImage!!.setColorFilter(resources.getColor(R.color.grey_color_100))
            currency = "₦"
        })
        return view
    }

    private fun checkInputs() {
        addTitleItem = addTitle!!.text.toString().trim { it <= ' ' }
        addDescriptionItem = addDescription!!.text.toString().trim { it <= ' ' }
        mAddPriceItem = addPrice!!.text.toString().trim { it <= ' ' }
        addUrlItem = addUrl!!.text.toString().trim { it <= ' ' }
        if (addTitleItem!!.isEmpty()) {
            info!!.text = "Please add a name"
            info!!.visibility = View.VISIBLE
        } else if (addDescriptionItem!!.isEmpty()) {
            info!!.text = "Please add a description"
            info!!.visibility = View.VISIBLE
        } else if (mAddPriceItem!!.isEmpty()) {
            info!!.text = "Please add a price"
            info!!.visibility = View.VISIBLE
        } else if (imagePreview!!.drawable.constantState == resources.getDrawable(R.drawable.ic_add_circle).constantState) {
            info!!.text = "Please add an Image"
            info!!.visibility = View.VISIBLE
        } else {
            if (addUrlItem!!.isEmpty()) {
                addUrlItem = ""
            }
            addPriceItem = currency + mAddPriceItem!!.replace(",", "")
            uploadPost()
            submit!!.visibility = View.INVISIBLE
            info!!.visibility = View.GONE
            loading!!.visibility = View.VISIBLE
        }
    }

    private fun uploadPost() {
        itemDetails = HashMap()
        if (imageUri != null) {
            val fileRef = storageReference!!.child("catalogue-images").child(user!!.email!!).child(saveTo!!)
            fileRef.putFile(imageUri!!).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    itemDetails!!["imageLink"] = uri.toString()
                    proceed()
                }
            }
        } else {
            itemDetails!!["imageLink"] = ""
            proceed()
        }
    }

    private fun proceed() {
        assert(arguments != null)
        itemDetails!!["name"] = addTitleItem
        itemDetails!!["description"] = addDescriptionItem
        itemDetails!!["userEmail"] = user!!.email
        itemDetails!!["itemId"] = "item_$itemId"
        itemDetails!!["itemPrice"] = formatPrice(addPriceItem)
        itemDetails!!["itemUrl"] = addUrlItem
        itemDetails!!["created on"] = Timestamp(Date())
        reference = db!!.collection("users").document(user!!.email!!).collection("catalogue").document("item_$itemId")
        reference!!.set(itemDetails!!).addOnSuccessListener {
            dismiss()
            itemsRefresh.run()
            Log.d("Debug", "success stuff")
        }.addOnFailureListener {
            val snackBar = MySnackBar(requireActivity().applicationContext, requireActivity().window.decorView, "Something went wrong. Please Try again", R.color.color_error_red_200, Snackbar.LENGTH_LONG)
            dismiss()
        }
    }

    private fun formatPrice(price: String?): String? {
        val output: String
        output = if (price!!.length == 5) {
            price.substring(0, 2) + "," + price.substring(2)
        } else if (price.length == 6) {
            price.substring(0, 3) + "," + price.substring(3)
        } else if (price.length == 7) {
            price.substring(0, 4) + "," + price.substring(4)
        } else if (price.length == 8) {
            price.substring(0, 2) + "." + price.substring(2, 3) + " million"
        } else if (price.length == 9) {
            price.substring(0, 3) + "." + price.substring(3, 4) + " million"
        } else if (price.length == 10) {
            price.substring(0, 4) + "." + price.substring(4, 5) + " million"
        } else if (price.length == 11) {
            price.substring(0, 2) + "." + price.substring(2, 3) + " billion"
        } else {
            price
        }
        return output
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        saveTo = itemId + "_catalogueImage.jpg"
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
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