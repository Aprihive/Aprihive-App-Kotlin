// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.aprihive.R;
import com.aprihive.methods.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

public class Splash extends AppCompatActivity {

    private static final String TAG = "fcm-debug";
    SharedPrefs sharedPrefs;
    public FirebaseAnalytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        sharedPrefs = new SharedPrefs(Splash.this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        analytics = FirebaseAnalytics.getInstance(this);




        getWindow().setNavigationBarColor(getResources().getColor(R.color.color_theme_blue_600));
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_theme_blue_600));


        Intent intent;
        if (sharedPrefs.shownOnboard){
            intent = new Intent(Splash.this, MainActivity.class);
        }
        else {
            intent = new Intent(Splash.this, OnboardingActivity.class);
        }

        startActivity(intent);
        finish();




    }

}
