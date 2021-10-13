package com.erlite.aprihive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.erlite.aprihive.fragments.SetThemeModal;
import com.erlite.aprihive.methods.SetBarsColor;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ConstraintLayout aboutClick, themeSelect, shareApp, reportBug, termsPolicies, support;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());


        toolbar = findViewById(R.id.toolbar);
        aboutClick = findViewById(R.id.aboutClick);
        themeSelect = findViewById(R.id.themeSelect);
        shareApp = findViewById(R.id.shareApp);
        reportBug = findViewById(R.id.reportBug);
        termsPolicies = findViewById(R.id.termsPolicies);
        support = findViewById(R.id.supportClick);


        setSupportActionBar(toolbar);
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
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out Aprihive, the app I use for marketing my services and products easily at: https://aprihive.jesulonimii.me/download");
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/tc"));
                startActivity(intent);
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/support"));
                startActivity(intent);
            }
        });





    }



}
