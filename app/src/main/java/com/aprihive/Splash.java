// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.aprihive.R;
import com.aprihive.methods.SharedPrefs;

public class Splash extends AppCompatActivity {

    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefs = new SharedPrefs(Splash.this);

        int getTheme = sharedPrefs.themeSettings;

        AppCompatDelegate.setDefaultNightMode(getTheme);

        Handler handler = new Handler();

        ImageView logo = findViewById(R.id.logo);

        logo.setVisibility(View.VISIBLE);
        Animation fadeUp = AnimationUtils.loadAnimation(this, R.anim.fade_up_animation);
        logo.setAnimation(fadeUp);


        getWindow().setNavigationBarColor(getResources().getColor(R.color.color_theme_blue_600));
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_theme_blue_600));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 3000);





    }
}
