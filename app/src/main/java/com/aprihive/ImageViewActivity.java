// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aprihive.methods.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.R;
import com.aprihive.methods.SetBarsColor;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.Timestamp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;

public class ImageViewActivity extends AppCompatActivity {

    PhotoView image;
    Toolbar toolbar;
    private boolean fullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        SharedPrefs sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

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
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    fullScreen = false;
                } else {
                    setBarsColor.enableFullscreen();
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
                    fullScreen =  true;
                }

            }
        });





    }

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.image_view_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == R.id.action_download) {
            Glide.with(ImageViewActivity.this)
                    .asBitmap()
                    .load(getIntent().getStringExtra("imageUri"))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            saveImage(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void saveImage(Bitmap resource) {
        String root = Environment.getExternalStorageDirectory().toString();

        Timestamp timestamp = new Timestamp(new Date());
        String time = timestamp.toString();

        String fileName = "Aprihive-"+ time +".jpg";
        File myDirectory = new File(root + "/Aprihive");
        myDirectory.mkdirs();

        File file = new File (myDirectory, fileName);

        if (file.exists()){
            file.delete ();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            resource.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Toast.makeText(ImageViewActivity.this, "Image saved to storage.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
