// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprihive.methods.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.R;
import com.aprihive.auth.SetUsername;
import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.NetworkListener;
import com.aprihive.methods.SetBarsColor;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rilixtech.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    private DocumentReference reference;


    private EditText editName, editSchool, editBio, editEmail, editPhone, editInstagramName, editTwitterName;
    private TextView usernameChangeClick;

    private StorageTask uploadTask;
    private StorageReference storageReference;
    private Uri imageUri;

    private String getEmail, getFullname, getInstagramName, getUsername, getTwitterName, getPhone, getBio, getSchool, getUserProfilePic; //fetch from firebase into
    private Boolean getVerified; //fetch from firebase into

    private  String saveTo, imageType;
    private ImageView profilePic;
    private CardView statusLoadBar;
    private TextView statusProgressText;
    private Toolbar toolbar;
    private ListenerRegistration registerQuery;
    private Button saveButton;
    private String setName, setBio, setSchool, setPhone, setTwitter, setInstagram;
    private TextView errorFeedback;
    private NetworkListener networkListener;
    private ConstraintLayout page, loading;

    private CountryCodePicker ccp;
    private boolean canGoBack = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPrefs sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        networkListener = new NetworkListener(this, page, getWindow());



        //init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        reference = firestore.collection("users").document(user.getEmail());
        //


        getEmail = user.getEmail();

        toolbar = findViewById(R.id.toolbar);
        editName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editBio = findViewById(R.id.editBio);
        editTwitterName = findViewById(R.id.editTwitterUsername);
        editInstagramName = findViewById(R.id.editInstagramUsername);
        editSchool = findViewById(R.id.editSchoolName);
        usernameChangeClick = findViewById(R.id.changeUsername);
        profilePic = findViewById(R.id.changeImage);
        saveButton = findViewById(R.id.saveButton);
        errorFeedback = findViewById(R.id.errorFeedback);
        loading = findViewById(R.id.loading);
        page = findViewById(R.id.page);
        ccp = findViewById(R.id.ccp);




        statusLoadBar = findViewById(R.id.statusLoadBar);
        statusProgressText = findViewById(R.id.statusProgressText);

        

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileActivity.super.onBackPressed();
            }
        });

        getProfileImage();
        retrieveDetailsFromFirestore();
        


        editName.addTextChangedListener(textChangeListener);
        editSchool.addTextChangedListener(textChangeListener);
        editPhone.addTextChangedListener(textChangeListener);
        editTwitterName.addTextChangedListener(textChangeListener);
        editInstagramName.addTextChangedListener(textChangeListener);
        editBio.addTextChangedListener(textChangeListener);






        //handle profile image change
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1).setFixAspectRatio(true).start(EditProfileActivity.this);
            }
        });
        
        usernameChangeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditProfileActivity.this, SetUsername.class);
                startActivity(i);
            }
        });

        ccp.registerPhoneNumberTextView(editPhone);
        ccp.enablePhoneAutoFormatter(true);
        ccp.enableSetCountryByTimeZone(true);


    }

    private TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!editName.getText().toString().trim().equals(getFullname)||
                    !editSchool.getText().toString().trim().equals(getSchool)||
                    !editPhone.getText().toString().trim().equals(getPhone)||
                    !editTwitterName.getText().toString().trim().equals(getTwitterName)||
                    !editInstagramName.getText().toString().trim().equals(getInstagramName)||
                    !editBio.getText().toString().trim().equals(getBio)){

                saveButton.setBackground(getResources().getDrawable(R.drawable.blue_button));
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkInputs();
                    }
                });

            }
            else {
                saveButton.setBackground(getResources().getDrawable(R.drawable.disabled_button));
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private void checkInputs() {


        //get values from input
        setName = editName.getText().toString().trim();
        setSchool = editSchool.getText().toString().trim();
        setBio = editBio.getText().toString().trim();
        setInstagram = editInstagramName.getText().toString().trim();
        setTwitter = editTwitterName.getText().toString().trim();
        String checkPhone = editPhone.getText().toString().trim();
        String countryCode =  ccp.getSelectedCountryCode();



        if (!checkPhone.isEmpty()){

            if (!checkPhone.startsWith(countryCode)){
                setPhone = countryCode + checkPhone;
            } else{
                setPhone = checkPhone;
            }

        } else {
            setPhone = "";
        }



        //check setBio
        if (setBio.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Description cannot be empty");
        }

        //check setName
        else if (setName.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Name cannot be empty");
        }
        else if (!setName.matches("[a-zA-Z0-9 ]*")){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Name can only contain alphabets");
        }

       // //check setPhone
       // else if (!Patterns.PHONE.matcher(setPhone).matches()){
       //     errorFeedback.setVisibility(View.VISIBLE);
       //     errorFeedback.setText("Invalid Phone number");
       // }

        //check setSchool
        else if (setSchool.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Location cannot be empty");
        }
        else {
            errorFeedback.setVisibility(View.GONE);
            if (networkListener.connected){
                updateInfo();
                saveButton.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
            }
        }


    }

    private void updateInfo() {



        Map<String, Object> details = new HashMap<>();
        details.put("name", setName);
        details.put("bio", setBio);
        details.put("school", setSchool);
        details.put("phone", setPhone);
        details.put("newAccount", false);
        details.put("instagram", setInstagram);
        details.put("twitter", setTwitter);


        reference = firestore.collection("users").document(user.getEmail());
        reference.update(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MySnackBar snackBar = new MySnackBar(EditProfileActivity.this, page, "Saved!", R.color.color_success_green_300, Snackbar.LENGTH_LONG);
                loading.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Intent i = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                        //finish();
                        //startActivity(i);


                    }
                }, 1500);
            }
        });


    }

    private void getProfileImage(){

        Uri userProfilePic = user.getPhotoUrl();


        Glide.with(this)  //2
                .load(userProfilePic) //3
                .centerCrop() //4
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fallback(R.drawable.user_image_placeholder) //7
                .into(profilePic); //8

    }

    private void retrieveDetailsFromFirestore() {

        getEmail = user.getEmail();


        //retrieve from firestore
        registerQuery = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                getFullname = documentSnapshot.getString("name");
                getPhone = documentSnapshot.getString("phone");
                getUsername = documentSnapshot.getString("username");
                getVerified = documentSnapshot.getBoolean("verified");
                getBio = documentSnapshot.getString("bio");
                getSchool = documentSnapshot.getString("school");
                getUserProfilePic = documentSnapshot.getString("profileImageLink");
                getTwitterName = documentSnapshot.getString("twitter");
                getInstagramName = documentSnapshot.getString("instagram");



                Glide.with(getApplicationContext())  //2
                        .load(getUserProfilePic) //3
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.user_image_placeholder) //7
                        .fallback(R.drawable.user_image_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())//7
                        .into(profilePic); //8




                editName.setText(getFullname);
                editEmail.setText(getEmail);
                editBio.setText(getBio);
                editSchool.setText(getSchool);
                editPhone.setText(getPhone);
                editInstagramName.setText(getInstagramName);
                editTwitterName.setText(getTwitterName);

                

            }
        });
        //end of retrieve
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageType = "profile";
        saveTo = user.getDisplayName() + "_profileImg.jpg";

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK){
                imageUri = result.getUri();
                profilePic.setImageURI(imageUri);

                statusLoadBar.setVisibility(View.VISIBLE);


                Animation fadeUp = AnimationUtils.loadAnimation(this, R.anim.fade_up_animation);
                statusLoadBar.setAnimation(fadeUp);

                if (imageType.equals("profile")){

                    profilePic.setImageURI(imageUri);
                    canGoBack = false;
                    statusProgressText.setText("Updating Profile Picture, Please wait");

                }

                //save image to firebase passing the type of image to be profile image (this is my idea, so that i can only use one method everytime)
                uploadImageToFirestore(imageUri, saveTo, imageType);
            }
        }

    }

    private  void uploadImageToFirestore(Uri imageUri, String saveTo, final String imageType){



        final StorageReference fileRef = storageReference.child("profile-images").child(saveTo);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Animation fadeDown = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.fade_down_animation);
                statusLoadBar.setAnimation(fadeDown);

                statusLoadBar.setVisibility(View.INVISIBLE);
                canGoBack = true;

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //upload to profile image database if its profile image were trying to change
                        if (imageType.equals("profile")){
                            Log.e("here", "the thing reach here ooo");

                            Glide.with(EditProfileActivity.this)  //2

                                    .load(uri) //3
                                    .centerCrop() //4
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fallback(R.drawable.user_image_placeholder) //7
                                    .into(profilePic); //8

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri).build();

                            String userEmail = auth.getCurrentUser().getEmail();

                            DocumentReference reference = firestore.collection("users").document(userEmail);

                            reference.update("profileImageLink", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    MySnackBar snackBar = new MySnackBar(EditProfileActivity.this, getWindow().getDecorView().findViewById(R.id.page), "Profile Image updated!", R.color.color_theme_blue, Snackbar.LENGTH_LONG);
                                }
                            });

                            Log.e("here", "it even stored it");


                            user.updateProfile(profileUpdates);
                            Log.e("here", "it even stored it");




                        }
                        else {
                            //nothing
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //nothing
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                canGoBack = true;
                MySnackBar snackBar = new MySnackBar(EditProfileActivity.this, getWindow().getDecorView(), "Updating profile image failed, please try again.", R.color.color_error_red_200, Snackbar.LENGTH_LONG);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter networkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkListener.networkListenerReceiver, networkIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        registerQuery.remove();
        unregisterReceiver(networkListener.networkListenerReceiver);
    }

    @Override
    public void onBackPressed() {
        if (canGoBack){
            super.onBackPressed();
        }
        else {
            Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();
        }
    }
}
