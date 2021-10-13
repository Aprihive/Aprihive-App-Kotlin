package com.erlite.aprihive;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlite.aprihive.adapters.ProfileViewPagerAdapter;
import com.erlite.aprihive.fragments.ContactMethodModal;
import com.erlite.aprihive.methods.SetBarsColor;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Map;

public class UserProfileActivity extends AppCompatActivity  {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getSupportFragmentManager());

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

        String getUsername = intent.getStringExtra("getUsername");
        Boolean getVerified = intent.getBooleanExtra("getVerified", false);
        getUserEmail = intent.getStringExtra("getEmail");
        String getFullName = intent.getStringExtra("getFullName");
        String getProfileImageUrl = intent.getStringExtra("getProfileImageUrl");
        String getBio = intent.getStringExtra("getBio");
        String getSchoolName= intent.getStringExtra("getSchoolName");
        String getPhone= intent.getStringExtra("getPhone");
        String getTwitter= intent.getStringExtra("getTwitter");
        String getInstagram= intent.getStringExtra("getInstagram");


        bundle.putString("phoneNumber", getPhone);
        bundle.putString("instagramName", getInstagram);
        bundle.putString("twitterName", getTwitter);


        fullname.setText(getFullName);
        username.setText("@" + getUsername);
        bio.setText(getBio);
        schoolName.setText(getSchoolName);

        if (getVerified){
            verifiedIcon.setVisibility(View.VISIBLE);
        } else {
            verifiedIcon.setVisibility(View.GONE);
        }



        Glide.with(this)
                .load(getProfileImageUrl)
                .centerCrop()
                .error(R.drawable.user_image_placeholder)
                .fallback(R.drawable.user_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profilePic);


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


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(getUsername);

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
        viewPager.setTag(getUserEmail);

        //set listener to viewpager from tabLayout
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setupWithViewPager(viewPager);

        // get tabs from adapter
        TabLayout.Tab feedTab = tabLayout.getTabAt(0);
        TabLayout.Tab catalogueTab = tabLayout.getTabAt(1);

        feedTab.setIcon(R.drawable.ic_feed);
        catalogueTab.setIcon(R.drawable.ic_shopping_cart);



        checkIfUserIsInFavourites();

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
