// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprihive.methods.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.adapters.ProfileViewPagerAdapter;
import com.aprihive.R;
import com.aprihive.fragments.AddCatalogueItemModal;
import com.aprihive.fragments.AddPostModal;
import com.aprihive.methods.SetBarsColor;
import com.aprihive.pages.Catalogue;
import com.aprihive.pages.Feed;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import javax.annotation.Nullable;

public class PersonalProfileActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private DocumentReference reference;
    private String currentUserId;

    private StorageTask uploadTask;
    private StorageReference storageReference;
    public Uri imageUri;
    private String myUri;

    private String getEmail, getFullname, getUsername, getPhone, getBio, getSchool, getUserProfilePic; //fetch from firebase into
    private Boolean getVerified; //fetch from firebase into



    //firebase end

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ImageView verifiedIcon, profilePic;
    private TextView email, fullname, username, phone, bio, schoolName;
    private TextView editProfilebutton;
    private ListenerRegistration registerQuery;
    private FloatingActionButton addPostFab, addCatalogueItemFab;
    private Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);

        SharedPrefs sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getSupportFragmentManager());

        bundle = new Bundle();

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        reference = firestore.collection("users").document(currentUser.getEmail());
        //

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.profileViewPager);
        tabLayout = findViewById(R.id.tabLayout);

        verifiedIcon =  findViewById(R.id.verifiedIcon);
        profilePic = findViewById(R.id.profileImage);
        fullname = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.description);
        schoolName = findViewById(R.id.schoolName);

        editProfilebutton = findViewById(R.id.editProfileButton);
        addPostFab = findViewById(R.id.fabAddPost);
        addCatalogueItemFab = findViewById(R.id.fabAddItem);





        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        addPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPostModal bottomSheet = new AddPostModal(Feed.refreshPostsRunnable);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });

        addCatalogueItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCatalogueItemModal bottomSheet = new AddCatalogueItemModal(Catalogue.refreshItemsRunnable);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalProfileActivity.super.onBackPressed();
            }
        });


        viewPager.setAdapter(adapter);
        viewPager.setTag(mAuth.getCurrentUser().getEmail());
        tabLayout.setupWithViewPager(viewPager);

        // get tabs from adapter
        TabLayout.Tab feedTab = tabLayout.getTabAt(0);
        TabLayout.Tab catalogueTab = tabLayout.getTabAt(1);

        feedTab.setIcon(R.drawable.ic_feed).setText("My Feed");
        catalogueTab.setIcon(R.drawable.ic_shopping_cart).setText("My Catalogue");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        addCatalogueItemFab.hide();
                        addPostFab.show();
                        break;
                    case 1:
                        addPostFab.hide();
                        addCatalogueItemFab.show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        
        backendCodes();



        editProfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PersonalProfileActivity.this, EditProfileActivity.class);
                i.putExtra("name", getFullname);
                i.putExtra("bio", getBio);
                i.putExtra("school", getSchool);
                startActivity(i);
            }
        });

    }

    private void backendCodes() {
        if (!(mAuth.getCurrentUser() == null)) {
            retrieveDetailsFromFirestore();
        }
    }

    private void retrieveDetailsFromFirestore() {

        getEmail = currentUser.getEmail();


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


                bundle.putString("fullname", getFullname);
                bundle.putString("username", getUsername);
                bundle.putBoolean("verified", getVerified);

                Glide.with(getApplicationContext())  //2
                        .load(getUserProfilePic) //3
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.user_image_placeholder) //7
                        .fallback(R.drawable.user_image_placeholder) //7
                        .into(profilePic); //8

                assert getUserProfilePic != null;
                if (!getUserProfilePic.equals("")){
                    profilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(PersonalProfileActivity.this, ImageViewActivity.class);
                            i.putExtra("imageUri", getUserProfilePic);
                            startActivity(i);
                        }
                    });
                }


                if (getVerified){
                    verifiedIcon.setVisibility(View.VISIBLE);
                }
                else { verifiedIcon.setVisibility(View.INVISIBLE);
                }

                fullname.setText(getFullname);
                username.setText("@" + getUsername);
                bio.setText(getBio);
                schoolName.setText(getSchool);

                




            }
        });
        //end of retrieve
    }


    @Override
    protected void onPause() {
        super.onPause();
        registerQuery.remove();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        registerQuery.remove();
    }
}
