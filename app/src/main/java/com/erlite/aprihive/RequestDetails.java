// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.erlite.aprihive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlite.aprihive.fragments.AddPostModal;
import com.erlite.aprihive.fragments.ContactMethodModal;
import com.erlite.aprihive.fragments.DialogModal;
import com.erlite.aprihive.methods.MyActionDialog;
import com.erlite.aprihive.methods.SetBarsColor;
import com.erlite.aprihive.pages.Discover;
import com.erlite.aprihive.pages.Requests;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.protobuf.StringValue;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestDetails extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;

    private String getFullname, getUsername, getProfilePic;
    private boolean getVerified;
    private Runnable refreshNotifications;

    private Toolbar toolbar;
    private String getType, getSenderUsername, getReceiverUsername, getDeadline, getPostId, getPostImageLink, getPostText, getRequestText, getRequestedOn, getSenderEmail, getReceiverEmail;
    private ImageView postImage, profileImage, verificationIcon;
    private TextView senderName, senderUsername, postText, requestText, deadline, requestedDate, actionButton;
    private ListenerRegistration registerQuery;
    private Bundle bundle;
    private String getTwitterName;
    private String getInstagramName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);


        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();


        bundle = new Bundle();

        Intent i = getIntent();
        getType = i.getStringExtra("getType");
        getSenderUsername = i.getStringExtra("getSenderName");
        getReceiverUsername = i.getStringExtra("getReceiverName");
        getDeadline = i.getStringExtra("getDeadline");
        getPostId = i.getStringExtra("getPostId");
        getPostImageLink = i.getStringExtra("getPostImageLink");
        getPostText = i.getStringExtra("getPostText");
        getRequestText = i.getStringExtra("getRequestText");
        getRequestedOn = i.getStringExtra("getRequestedOn");
        getSenderEmail = i.getStringExtra("getSenderEmail");
        getReceiverEmail = i.getStringExtra("getReceiverEmail");






        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        profileImage = findViewById(R.id.sender_profileImage);
        senderName = findViewById(R.id.sender_fullName);
        senderUsername = findViewById(R.id.sender_username);
        postImage = findViewById(R.id.postImage);
        postText = findViewById(R.id.postText);
        requestText = findViewById(R.id.requestText);
        deadline = findViewById(R.id.deadline);
        verificationIcon = findViewById(R.id.sender_verifiedIcon);
        actionButton = findViewById(R.id.actionButton);
        requestedDate = findViewById(R.id.sentOn);


        if (getType.equals("from")){
            getSupportActionBar().setTitle(getSenderUsername.substring(0,1).toUpperCase() + getSenderUsername.substring(1).toLowerCase() + "\'s request");
            getSenderDetails();
        }
        else {
            getSupportActionBar().setTitle("Request to " + getReceiverUsername);
            getReceiverDetails();
        }




        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestDetails.super.onBackPressed();
            }
        });

        setUpActionButton(getType);


        //load post text and image into view
        if (!getPostImageLink.equals("")){

            Glide.with(this)
                    .load(getPostImageLink)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(postImage);

        }
        else {
            postImage.setVisibility(View.GONE);
        }
        postText.setText(getPostText);
        //

        //load request text
        requestText.setText(getRequestText);
        bundle.putString("requestText", getRequestText);


        //load deadline date
        try {
            deadline.setText("Deadline: " + deadlineOn(getDeadline));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //load request date
        requestedDate.setText("Sent: " +  getRequestedOn);


    }

    private void setUpActionButton(String getType) {

        if (getType.equals("from")){
            actionButton.setText("Contact");
            actionButton.setBackgroundColor(getResources().getColor(R.color.color_theme_blue));
        } else {
            actionButton.setText("Cancel Request");
            actionButton.setBackgroundColor(getResources().getColor(R.color.color_error_red_200));
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                deleteRequestAction();
            }
        };

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getType.equals("from")){
                    contactAction();
                } else {
                    MyActionDialog confirm = new MyActionDialog(RequestDetails.this, "Confirm Delete", "Are you sure you want to delete this request? \n Note: This is a permanent action!", R.drawable.vg_delete, action);
                    confirm.show();
                }
            }
        });

    }

    private void deleteRequestAction() {

        DocumentReference senderReference = db.collection("users").document(user.getEmail()).collection("requests").document("requestTo:-" + getReceiverUsername + "-for:-" + getPostId);
        reference = db.collection("users").document(getReceiverEmail).collection("requests").document("requestFrom:-" + user.getDisplayName() + "-for:-" + getPostId);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void aVoid) {
                senderReference.delete();
                RequestDetails.super.onBackPressed();

            }
        });




    }

    private void contactAction() {
        ContactMethodModal bottomSheet = new ContactMethodModal();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getSupportFragmentManager(), "TAG");
    }

    private void getSenderDetails(){

        reference = db.collection("users").document(getSenderEmail);
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


                senderName.setText(getFullname);
                senderUsername.setText("@" + getUsername);
                if (getVerified){
                    verificationIcon.setVisibility(View.VISIBLE);
                } else {
                    verificationIcon.setVisibility(View.GONE);
                }

                Glide.with(getApplicationContext())
                        .load(getProfilePic)
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder) //7
                        .into(profileImage);

            }
        });


    }

    private void getReceiverDetails(){

        reference = db.collection("users").document(getReceiverEmail);
        registerQuery = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                getFullname = value.getString("name");
                getUsername = value.getString("username");
                getProfilePic = value.getString("profileImageLink");
                getVerified = value.getBoolean("verified");


                senderName.setText(getFullname);
                senderUsername.setText("@" + getUsername);
                if (getVerified){
                    verificationIcon.setVisibility(View.VISIBLE);
                } else {
                    verificationIcon.setVisibility(View.GONE);
                }

                Glide.with(getApplicationContext())
                        .load(getProfilePic)
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder) //7
                        .into(profileImage);

            }
        });


    }

    private String deadlineOn(String deadline) throws ParseException {


        String fetchDate = deadline + " " + "24:59:59";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        Date date = sdf.parse(fetchDate);

        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(date);
        return ago;

    }

    @Override
    protected void onStop() {
        super.onStop();
        registerQuery.remove();
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }


}
