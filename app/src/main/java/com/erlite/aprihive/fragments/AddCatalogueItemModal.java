// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved

package com.erlite.aprihive.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cottacush.android.currencyedittext.CurrencyInputWatcher;
import com.erlite.aprihive.R;
import com.erlite.aprihive.methods.MySnackBar;
import com.erlite.aprihive.methods.NetworkListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class AddCatalogueItemModal extends BottomSheetDialogFragment {

    private final Runnable itemsRefresh;
    private EditText emailInput;
    private Button submit, closeBtn;
    private TextView info, title;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;
    private NetworkListener networkListener;
    private ConstraintLayout loading, closeScreen, forgotPasswordScreen;

    
    private EditText addTitle, addPrice, addDescription, addUrl;
    private ImageView imagePreview;
    private  String saveTo, imageType;
    private Uri imageUri;
    private StorageReference storageReference;
    private String postImageLink;
    private Random random;
    private String addTitleItem, addDescriptionItem, addPriceItem, addUrlItem;
    private String itemId;
    private Context context;
    private Map<String, Object>itemDetails;
    private ImageView nairaImage, dollarImage;
    private String currency = "N";
    private String mAddPriceItem;


    public AddCatalogueItemModal(Runnable itemsRefresh) {
        this.itemsRefresh = itemsRefresh;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_add_catalogue_item_modal, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        

        context = getActivity().getApplicationContext();

        addTitle = view.findViewById(R.id.addTitle);
        addDescription = view.findViewById(R.id.addDescription);
        addPrice = view.findViewById(R.id.addPrice);
        addUrl = view.findViewById(R.id.addUrl);
        
        imagePreview = view.findViewById(R.id.imagePreview);
        dollarImage = view.findViewById(R.id.dollarSign);
        nairaImage = view.findViewById(R.id.nairaSign);
        submit = view.findViewById(R.id.submitBtn);
        loading = view.findViewById(R.id.loading);
        info = view.findViewById(R.id.errorFeedback);

        //set naira symbol as active
        nairaImage.setColorFilter(getResources().getColor(R.color.bg_color));
        nairaImage.setBackgroundColor(getResources().getColor(R.color.color_theme_blue));
        dollarImage.setBackground(getResources().getDrawable(R.drawable.bg_left_rounded));
        dollarImage.setColorFilter(getResources().getColor(R.color.grey_color_100));
        currency = "₦";
        addPrice.addTextChangedListener(new CurrencyInputWatcher(addPrice, "", Locale.getDefault()));
        //

        random = new Random();

        itemId = String.valueOf(random.nextInt(1000000));



        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(3, 2).setActivityTitle("Crop Image").setFixAspectRatio(false).start(getContext(), AddCatalogueItemModal.this);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });

        dollarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dollarImage.setColorFilter(getResources().getColor(R.color.bg_color));
                dollarImage.setBackground(getResources().getDrawable(R.drawable.bg_left_rounded_active));
                nairaImage.setBackgroundColor(getResources().getColor(R.color.text_box_bg));
                nairaImage.setColorFilter(getResources().getColor(R.color.grey_color_100));
                currency = "$";

            }
        });

        nairaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nairaImage.setColorFilter(getResources().getColor(R.color.bg_color));
                nairaImage.setBackgroundColor(getResources().getColor(R.color.color_theme_blue));
                dollarImage.setBackground(getResources().getDrawable(R.drawable.bg_left_rounded));
                dollarImage.setColorFilter(getResources().getColor(R.color.grey_color_100));
                currency = "₦";
            }
        });


        return view;
    }

    private void checkInputs(){

        addTitleItem = addTitle.getText().toString().trim();
        addDescriptionItem = addDescription.getText().toString().trim();
        mAddPriceItem = addPrice.getText().toString().trim();
        addUrlItem = addUrl.getText().toString().trim();


        if (addTitleItem.isEmpty()) {
            info.setText("Please add a name");
            info.setVisibility(View.VISIBLE);
        }
        else if (addDescriptionItem.isEmpty()) {
            info.setText("Please add a description");
            info.setVisibility(View.VISIBLE);
        }
        else if (mAddPriceItem.isEmpty()) {
            info.setText("Please add a price");
            info.setVisibility(View.VISIBLE);
        }
        else if (imagePreview.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_add_circle).getConstantState())) {
            info.setText("Please add an Image");
            info.setVisibility(View.VISIBLE);
        }

        else{

            if (addUrlItem.isEmpty()){
                addUrlItem = "";
            }

            addPriceItem = currency + mAddPriceItem.replace(",", "");

            uploadPost();
            submit.setVisibility(View.INVISIBLE);
            info.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

    }

    private void uploadPost() {


        itemDetails = new HashMap<>();

        if (!(imageUri == null)){

            final StorageReference fileRef = storageReference.child("catalogue-images").child(user.getEmail()).child(saveTo);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            itemDetails.put("imageLink", uri.toString());
                            proceed();

                        }
                    });

                }
            });

        }
        else {
            itemDetails.put("imageLink", "");
            proceed();
        }


    }

    private void proceed() {

        assert getArguments() != null;

       
        itemDetails.put("name", addTitleItem);
        itemDetails.put("description", addDescriptionItem);
        itemDetails.put("userEmail", user.getEmail());
        itemDetails.put("itemId", "item_"  + itemId);
        itemDetails.put("itemPrice", formatPrice(addPriceItem));
        itemDetails.put("itemUrl", addUrlItem);
        itemDetails.put("created on", new Timestamp(new Date()));


        reference = db.collection("users").document(user.getEmail()).collection("catalogue").document("item_"  + itemId);
        reference.set(itemDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                dismiss();
                itemsRefresh.run();
                Log.d("Debug", "success stuff");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MySnackBar snackBar = new MySnackBar(getActivity().getApplicationContext(), getActivity().getWindow().getDecorView(), "Something went wrong. Please Try again", R.color.color_error_red_200, Snackbar.LENGTH_LONG);
                dismiss();
            }
        });

    }


    private String formatPrice(String price){
        String output;
        
        if (price.length() == 5) {
            output =  price.substring(0, 2) + "," + price.substring(2);
        }

        else if (price.length() == 6) {
            output =   price.substring(0, 3) + "," + price.substring(3);
        }

        else if (price.length() == 7) {
            output =   price.substring(0,4) + "," + price.substring(4);
        }

        else if (price.length() == 8) {
            output =   price.substring(0,2) + "." + price.substring(2,3) + " million";
        }

        else if (price.length() == 9) {
            output =   price.substring(0,3) + "." + price.substring(3,4) + " million";
        }

        else if (price.length() == 10) {
            output =   price.substring(0,4) + "." + price.substring(4,5) + " million";
        }

        else if (price.length() == 11) {
            output =   price.substring(0,2) + "." + price.substring(2,3) + " billion";
        }
        else {
            output = price;
        }
        
        return output;
        
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        saveTo = itemId + "_catalogueImage.jpg";


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK){
                imageUri = result.getUri();
                imagePreview.setPadding(0,0,0,0);



                Glide.with(this)  //2

                        .load(imageUri) //3
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fallback(R.drawable.user_image_placeholder) //7
                        .into(imagePreview); //8

                Log.d("checking stuffs", "the thing 66 reach here ooo");

                //save image to firebase passing the type of image to be profile image (this is my idea, so that i can only use one method everytime)
                //uploadImageToFirestore(imageUri, saveTo);
            }
        }

    }




}
