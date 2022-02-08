// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aprihive.methods.SetBarsColor;
import com.aprihive.methods.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FetchDetails extends AppCompatActivity {

    private String getUserEmail;
    private String getUsernameData;
    FirebaseFirestore db;
    private String getUsername, getPhone, getBio, getSchool, getProfileImageUrl, getTwitter, getInstagram, getFullname;
    private Boolean getVerified;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_fetch_details);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        SharedPrefs sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        db =  FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Uri data = getIntent().getData();
        assert data != null;
        getUsernameData = data.getLastPathSegment().toLowerCase();
        Log.e("debug", "used username");

        if (user.getDisplayName().toLowerCase().equals(getUsernameData)){
            Intent i = new Intent(FetchDetails.this, PersonalProfileActivity.class);
            startActivity(i);
            finish();
        }
        else {
            getUserInfoFromDbByUsername();
        }


    }

    private void getUserInfoFromDbByUsername() {

        //retrieve from firestore
        Log.e("debug", "checking username");

        CollectionReference collectionReference = db.collection("users");
        Query query = collectionReference.whereEqualTo("username-lower", getUsernameData.toLowerCase());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                Log.e("debug", getUsernameData.toLowerCase() + " checking ongoing");

                if (task.isSuccessful()){
                    for (DocumentSnapshot value : task.getResult()){

                        Log.e("debug", "check success");


                        getFullname = value.getString("name");
                        getPhone = value.getString("phone");
                        getUsername = value.getString("username");
                        getUserEmail = value.getString("email");
                        getVerified = value.getBoolean("verified");
                        getBio = value.getString("bio");
                        getSchool = value.getString("school");
                        getProfileImageUrl = value.getString("profileImageLink");
                        getTwitter = value.getString("twitter");
                        getInstagram = value.getString("instagram");

                        Log.e("fetch", "fetched finished");

                        openProfile();


                    }


                }
                if (task.getResult().size() == 0) {
                    Log.d("debug", "User does not exist");
                    Toast.makeText(FetchDetails.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FetchDetails.this, Home.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    private void openProfile() {
        Intent intent = new Intent(FetchDetails.this, UserProfileActivity.class);
        intent.putExtra("getEmail", getUserEmail);
        intent.putExtra("getVerified", getVerified);
        intent.putExtra("getPhone", getPhone);
        intent.putExtra("getTwitter", getTwitter);
        intent.putExtra("getInstagram", getInstagram);
        intent.putExtra("getUsername", getUsername);
        intent.putExtra("getFullName", getFullname);
        intent.putExtra("getProfileImageUrl", getProfileImageUrl);
        intent.putExtra("getBio", getBio);
        intent.putExtra("getSchoolName", getSchool);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
}
