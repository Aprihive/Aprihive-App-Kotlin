// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprihive.fragments.ReportModal;
import com.aprihive.fragments.SendRequestModal;
import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.adapters.ProfileViewPagerAdapter;
import com.aprihive.R;
import com.aprihive.fragments.ContactMethodModal;
import com.aprihive.methods.SetBarsColor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserProfileActivity extends AppCompatActivity  {

    private static final String TAG = "debug" ;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference reference;

    private Toolbar toolbar;
    private TextView connectButton;
    private TextView callButton;

    private ImageView verifiedIcon, profilePic;
    private TextView email, fullname, username, phone, bio, schoolName;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Bundle bundle;
    private ListenerRegistration likeRegisterQuery;
    private String getUserEmail;
    private ListenerRegistration registerQuery;
    private String getUsername;
    private String getPhone, getBio, getSchool, getProfileImageUrl, getTwitter, getInstagram, getFullname;
    private boolean getVerified;
    private String getUsernameData;
    private ProfileViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        SharedPrefs sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        adapter = new ProfileViewPagerAdapter(getSupportFragmentManager());

        bundle = new Bundle();

        viewPager = findViewById(R.id.profileViewPager);
        toolbar = findViewById(R.id.toolbar);
        connectButton = findViewById(R.id.connectButton);
        callButton = findViewById(R.id.callButton);
        tabLayout =  findViewById(R.id.tabLayout);

        verifiedIcon =  findViewById(R.id.verifiedIcon);
        profilePic = findViewById(R.id.user_profileImage);
        fullname = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.description);
        schoolName = findViewById(R.id.schoolName);

        Intent intent = getIntent();

        try{
            Uri data = getIntent().getData();
            assert data != null;
            getUserEmail = data.getLastPathSegment();
            Log.e("debug", "used email");
            getUserInfoFromDbByEmail();

        } catch (Exception e) {
            getUserEmail = intent.getStringExtra("getEmail");
            getUsername = intent.getStringExtra("getUsername");
            getUserInfoFromDbByEmail();

        }





        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileActivity.super.onBackPressed();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference fileRef = db.collection("users").document(getUserEmail).collection("lists").document("following");
                final Boolean[] processFollow = {true};
                fileRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        try {
                            if (processFollow[0]){

                                if (value.contains(auth.getCurrentUser().getUid())){
                                    fileRef.update(auth.getCurrentUser().getUid(), FieldValue.delete());
                                    processFollow[0] =false;
                                }
                                else {
                                    fileRef.update(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail());
                                    processFollow[0] =false;
                                }


                            }
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }


                    }
                });

            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactMethodModal bottomSheet = new ContactMethodModal();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });

        //set viewpager
        viewPager.setAdapter(adapter);
        viewPager.setTag(getUserEmail); //pass data to viewpager

        //set listener to viewpager from tabLayout
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setupWithViewPager(viewPager);

        // get tabs from adapter
        TabLayout.Tab feedTab = tabLayout.getTabAt(0);
        TabLayout.Tab catalogueTab = tabLayout.getTabAt(1);

        feedTab.setIcon(R.drawable.ic_feed);
        catalogueTab.setIcon(R.drawable.ic_shopping_cart);

        Log.e(TAG, "onCreate: " + getUsername);
        getSupportActionBar().setTitle(getUsername);



    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileLink:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("profile-link", "https://aprihive.jesulonimii.me/user/"+getUsername);
                clipboard.setPrimaryClip(clip);

                MySnackBar snackBar = new MySnackBar(this, getWindow().getDecorView().findViewById(R.id.page), "Copied to clipboard!", R.color.color_theme_blue, Snackbar.LENGTH_SHORT);
                return true;
            case R.id.report:
                ReportModal bottomSheet = new ReportModal();
                Bundle bundle = new Bundle();
                bundle.putString("postAuthorEmail", "ericx.group@gmail.com");
                bundle.putString("postAuthor", "Support");
                bundle.putString("postText", "Reporting " + getFullname + " ("+getUsername+")");
                bundle.putString("postImage", getProfileImageUrl);
                bundle.putString("postId", getUsername);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void useLocalUserData(Intent intent) {

        getUsername = intent.getStringExtra("getUsername");
        getVerified = intent.getBooleanExtra("getVerified", false);
        getUserEmail = intent.getStringExtra("getEmail");
        getFullname = intent.getStringExtra("getFullName");
        getProfileImageUrl = intent.getStringExtra("getProfileImageUrl");
        getBio = intent.getStringExtra("getBio");
        getSchool = intent.getStringExtra("getSchoolName");
        getPhone= intent.getStringExtra("getPhone");
        getTwitter= intent.getStringExtra("getTwitter");
        getInstagram= intent.getStringExtra("getInstagram");

        populateViews();

    }

    private void populateViews() {

        Glide.with(getApplicationContext())  //2
                .load(getProfileImageUrl) //3
                .centerCrop() //4
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.user_image_placeholder) //7
                .fallback(R.drawable.user_image_placeholder) //7
                .into(profilePic); //8




        fullname.setText(getFullname);
        username.setText("@" + getUsername);
        bio.setText(getBio);
        schoolName.setText(getSchool);

        if (getVerified){
            verifiedIcon.setVisibility(View.VISIBLE);
        } else {
            verifiedIcon.setVisibility(View.GONE);
        }

        assert getProfileImageUrl != null;
        if (!getProfileImageUrl.equals("")){
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(UserProfileActivity.this, ImageViewActivity.class);
                    i.putExtra("imageUri", getProfileImageUrl);
                    startActivity(i);
                }
            });
        }

        bundle.putString("phoneNumber", getPhone);
        bundle.putString("instagramName", getInstagram);
        bundle.putString("twitterName", getTwitter);

        checkIfUserIsInFavourites();

    }

    private void getUserInfoFromDbByEmail() {

        //retrieve from firestore
        DocumentReference fetchReference;
        fetchReference = db.collection("users").document(getUserEmail);
        registerQuery = fetchReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                getFullname = documentSnapshot.getString("name");
                getPhone = documentSnapshot.getString("phone");
                getUsername = documentSnapshot.getString("username");
                getVerified = documentSnapshot.getBoolean("verified");
                getBio = documentSnapshot.getString("bio");
                getSchool = documentSnapshot.getString("school");
                getProfileImageUrl = documentSnapshot.getString("profileImageLink");
                getTwitter = documentSnapshot.getString("twitter");
                getInstagram = documentSnapshot.getString("instagram");

                populateViews();

            }
        });
        //end of retrieve

    }

    private void checkIfUserIsInFavourites() {

        reference = db.collection("users").document(getUserEmail).collection("lists").document("following");
        likeRegisterQuery =  reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                try {
                    assert user != null;
                    String uid = user.getUid();

                    Boolean check = false;

                    try {
                        check = value.contains(uid);
                    }
                    catch (Exception e){

                    }


                    if (check){
                        connectButton.setBackground(getResources().getDrawable(R.drawable.connect_active_button));
                        connectButton.setText("Following");
                        connectButton.setTextColor(getResources().getColor(R.color.bg_color));
                    }
                    else {
                        connectButton.setBackground(getResources().getDrawable(R.drawable.connect_default_button));
                        connectButton.setText("Follow");
                        connectButton.setTextColor(getResources().getColor(R.color.color_theme_blue));
                    }

                    /*
                    try {
                        Map<String, Object> map = value.getData();
                        getUpvotes = map.size();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (getUpvotes == 1){
                        holder.trustedByText.setText("  Trusted by " + getUpvotes + " person");
                    }
                    else {
                        holder.trustedByText.setText("  Trusted by " + getUpvotes + " people");
                    }
                    */

                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

    }


}
