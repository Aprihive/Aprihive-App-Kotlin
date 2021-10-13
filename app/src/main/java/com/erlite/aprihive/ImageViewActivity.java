// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.erlite.aprihive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlite.aprihive.methods.SetBarsColor;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

public class ImageViewActivity extends AppCompatActivity {

    PhotoView image;
    Toolbar toolbar;
    private boolean fullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());

        image = findViewById(R.id.image);
        toolbar = findViewById(R.id.toolbar);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(this)
                .load(getIntent().getStringExtra("imageUri"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageViewActivity.super.onBackPressed();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fullScreen){
                    setBarsColor.disableFullscreen();
                    toolbar.setBackgroundColor(getResources().getColor(R.color.bg_color));
                    fullScreen = false;
                } else {
                    setBarsColor.enableFullscreen();
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    fullScreen =  true;
                }

            }
        });



    }


}
