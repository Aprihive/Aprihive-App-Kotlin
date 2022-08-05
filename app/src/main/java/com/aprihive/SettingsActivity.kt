// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.aprihive.R;
import com.aprihive.fragments.SetThemeModal;
import com.aprihive.methods.SetBarsColor;
import com.aprihive.methods.SharedPrefs;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "debug" ;
    private Toolbar toolbar;
    private ConstraintLayout aboutClick, themeSelect, shareApp, reportBug, termsPolicies, versionName;
    private SwitchCompat pushNotifySwitch;
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_settings);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());



        sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);


        toolbar = findViewById(R.id.toolbar);
        aboutClick = findViewById(R.id.aboutClick);
        themeSelect = findViewById(R.id.themeSelect);
        shareApp = findViewById(R.id.shareApp);
        reportBug = findViewById(R.id.reportBug);
        termsPolicies = findViewById(R.id.termsPolicies);
        pushNotifySwitch = findViewById(R.id.goIcon2);
        versionName = findViewById(R.id.versionName);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.super.onBackPressed();
            }
        });

        aboutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, AboutApp.class);
                startActivity(i);
            }
        });

        themeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetThemeModal bottomSheet = new SetThemeModal();
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, R.string.share_app_message);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        reportBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "aprihive@jesulonimii.me");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Report a bug");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        termsPolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.com/eula"));
                startActivity(intent);
            }
        });



        if (sharedPrefs.getPushSelection()){
            pushNotifySwitch.setChecked(true);
        }else{
            pushNotifySwitch.setChecked(false);

        }


        pushNotifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    pushNotifySwitch.setChecked(true);

                    //PackageManager pm  = getApplicationContext().getPackageManager();
                    //ComponentName componentName = new ComponentName(SettingsActivity.this, PushNotificationService.class);
                    //pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);

                } else{
                    pushNotifySwitch.setChecked(false);


                }

                sharedPrefs.savePushSelection(pushNotifySwitch.isChecked());



            }
        });

        versionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        });



    }



}
