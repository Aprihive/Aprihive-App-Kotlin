// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprihive.R;
import com.aprihive.methods.SetBarsColor;
import com.aprihive.methods.SharedPrefs;

public class AboutApp extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPrefs sharedPrefs;
    private ImageView instagram, twitter, linkedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        toolbar = findViewById(R.id.toolbar);
        instagram = findViewById(R.id.instagram);
        twitter = findViewById(R.id.twitter);
        linkedin = findViewById(R.id.linkedIn);



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        TextView eula = findViewById(R.id.eula);

        eula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/eula"));
                startActivity(intent);
            }
        });

        TextView privacy = findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/privacy"));
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutApp.super.onBackPressed();
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/aprihive"));
                    intent.setPackage("com.instagram.android");
                    startActivity(intent);
                }
                catch (Exception e){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/aprihive"));
                    startActivity(intent);
                }

            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=aprihiveapp"));
                    startActivity(intent);
                }
                catch(Exception e){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/aprihiveapp"));
                    startActivity(intent);
                }
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/aprihive/"));
                    startActivity(intent);

            }
        });




    }
}
