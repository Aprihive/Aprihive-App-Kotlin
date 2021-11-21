// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved

package com.aprihive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aprihive.methods.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.R;
import com.aprihive.fragments.ContactMethodModal;
import com.aprihive.methods.MyActionDialog;
import com.aprihive.methods.SetBarsColor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class CatalogueItemDetails extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;

    private String getFullname, getUsername, getProfilePic;
    private boolean getVerified;
    private Runnable refreshNotifications;

    private Toolbar toolbar;
    private String getItemId, getItemImageLink, getItemName, getItemDescription, getRequestedOn, getSellerEmail, getReceiverEmail;
    private ImageView itemImage;
    private TextView itemName, itemDescription, actionButton, itemPrice;
    private ListenerRegistration registerQuery;
    private Bundle bundle;
    private String getTwitterName;
    private String getInstagramName;
    private String getItemPrice;
    private String getItemUrl;
    private ConstraintLayout sheet;
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private View cover;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_item_details);

        SharedPrefs sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();


        bundle = new Bundle();

        Intent i = getIntent();
        getItemId = i.getStringExtra("getItemId");
        getItemImageLink = i.getStringExtra("getItemImageLink");
        getItemUrl = i.getStringExtra("getItemUrl");
        getItemName = i.getStringExtra("getItemName");
        getItemPrice = i.getStringExtra("getItemPrice");
        getItemDescription = i.getStringExtra("getItemDescription");
        getSellerEmail = i.getStringExtra("getSellerEmail");






        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getItemName);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemDescription = findViewById(R.id.itemDescription);
        actionButton = findViewById(R.id.actionButton);
        sheet = findViewById(R.id.contentLayout);
        cover = findViewById(R.id.cover);


        getSellerDetails();

        bottomSheetBehavior = BottomSheetBehavior.from(sheet);





        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    toolbar.setBackgroundColor(getResources().getColor(R.color.bg_color));
                    toolbar.setTitleTextColor(getResources().getColor(R.color.color_text_blue_500));
                    Animation fadeIn = AnimationUtils.loadAnimation(CatalogueItemDetails.this, R.anim.fade_in_animation);
                    toolbar.setAnimation(fadeIn);
                    itemName.setVisibility(View.GONE);
                    cover.setVisibility(View.VISIBLE);
                }
                else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    toolbar.setTitleTextColor(getResources().getColor(R.color.transparent));
                    itemName.setVisibility(View.VISIBLE);


                    cover.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CatalogueItemDetails.super.onBackPressed();
            }
        });



        setUpActionButton(getSellerEmail);


        //load item text and image into view
        if (!getItemImageLink.equals("")){

            itemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(CatalogueItemDetails.this, ImageViewActivity.class);
                    i.putExtra("imageUri", getItemImageLink);
                    startActivity(i);
                }
            });

            Glide.with(this)
                    .load(getItemImageLink)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemImage);


        } else {
            itemImage.setVisibility(View.GONE);
        }
        itemName.setText(getItemName);
        itemPrice.setText(getItemPrice);
      

        //load description text
        itemDescription.setText(getItemDescription);
        bundle.putString("itemDescription", getItemDescription);
        


    }

    private void setUpActionButton(String email) {

        if (!email.equals(auth.getCurrentUser().getEmail())){
            actionButton.setText("Order");
            actionButton.setBackground(getResources().getDrawable(R.drawable.blue_button));
        } else {
            actionButton.setText("Delete Item");
            actionButton.setBackground(getResources().getDrawable(R.drawable.red_button));
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                deleteItemAction();
            }
        };

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.equals(auth.getCurrentUser().getEmail())){
                    orderAction();
                } else {
                    MyActionDialog confirm = new MyActionDialog(CatalogueItemDetails.this, "Confirm Delete", "Are you sure you want to delete this item? \n Note: This is a permanent action!", R.drawable.vg_delete, action);
                    confirm.show();
                }
            }
        });

    }

    private void deleteItemAction() {

        reference = db.collection("users").document(user.getEmail()).collection("catalogue").document(getItemId);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void aVoid) {
                CatalogueItemDetails.super.onBackPressed();

            }
        });




    }

    private void orderAction() {
        if (!getItemUrl.isEmpty() || !getItemUrl.equals("")){

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getItemUrl));
            startActivity(i);

        } else {
            ContactMethodModal bottomSheet = new ContactMethodModal();
            bottomSheet.setArguments(bundle);
            bottomSheet.show(getSupportFragmentManager(), "TAG");
        }

    }

    private void getSellerDetails(){

        reference = db.collection("users").document(getSellerEmail);
        registerQuery = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                getFullname = value.getString("name");
                getUsername = value.getString("username");
                getProfilePic = value.getString("profileImageLink");
                getVerified = value.getBoolean("verified");
                getTwitterName = value.getString("twitter");
                getInstagramName = value.getString("instagram");



                bundle.putString("instagramName", getInstagramName);
                bundle.putString("twitterName", getTwitterName);
                bundle.putString("phoneNumber", value.getString("phone"));
                

            }
        });


    }
    

    @Override
    protected void onStop() {
        super.onStop();
        registerQuery.remove();
    }

    @Override
    public void onPause() {
        super.onPause();
        //finish();
    }


}
