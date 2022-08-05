// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.aprihive.adapters.OnboardingViewAdapter;
import com.aprihive.methods.SetBarsColor;
import com.aprihive.methods.SharedPrefs;
import com.aprihive.models.OnboardingModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private TextView nextBtn, skipBtn;
    private TabLayout tabIndicator;
    private OnboardingViewAdapter adapter;
    private ViewPager pager;
    private int position;
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        tabIndicator = findViewById(R.id.tabLayout);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        sharedPrefs =  new SharedPrefs(this);

        String title1 = "Welcome to Aprihive!";
        String title2 = "Make Connections";
        String title3 = "Buying and Selling made easy";
        String title4 = "Own your Mini Shop!";


        String text1 = "The open social marketing network for everyone";
        String text2 = "Get connected to vendors or customers around you for the products or services you need or provide.";
        String text3 = "Send a request for a product or service you are interested in getting or providing, agree on pricing, payment method and delivery method.";
        String text4 = "Get started by clicking the continue button!\nJoin other users in building the Aprihive community.";
        String text5 = "Create your personalised mini-shops by adding items and services you provide to your catalogue.";

        //fill list screen
        final List<OnboardingModel> list = new ArrayList<>();
        list.add(new OnboardingModel(title1, text1, R.drawable.brand_logo, View.INVISIBLE));
        list.add(new OnboardingModel(title2, text2, R.drawable.vg_shopping, View.INVISIBLE));
        list.add(new OnboardingModel(title3, text3, R.drawable.vg_agreement, View.INVISIBLE));
        list.add(new OnboardingModel(title4, text5, R.drawable.vg_boy_skating, View.VISIBLE));


        //setup viewpager
        pager = findViewById(R.id.viewPager);
        adapter = new OnboardingViewAdapter(this, list);
        pager.setAdapter(adapter);

        //setup tablayout with viewpager
        tabIndicator.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0 || position == 1 || position == 2 ) {
                    nextBtn.setVisibility(View.VISIBLE);
                    skipBtn.setVisibility(View.VISIBLE);
                    tabIndicator.setVisibility(View.VISIBLE);



                } else {
                    Animation fadeAnim = AnimationUtils.loadAnimation(OnboardingActivity.this, R.anim.fade_down_animation);
                    nextBtn.setAnimation(fadeAnim);
                    nextBtn.setVisibility(View.INVISIBLE);

                    skipBtn.setAnimation(fadeAnim);
                    skipBtn.setVisibility(View.INVISIBLE);

                    tabIndicator.setAnimation(fadeAnim);
                    tabIndicator.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //set btn listener
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //set position
                position = pager.getCurrentItem();

                if (position < list.size()){

                    position ++;
                    pager.setCurrentItem(position);

                }

                //hide nxt btn
                if (position == list.size()-1){

                    nextBtn.setVisibility(View.INVISIBLE);
                    position ++;
                    pager.setCurrentItem(position);

                }



            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });


        setShowIntro();
    }

    public void setShowIntro(){

        sharedPrefs.shownOnboard = true;
        sharedPrefs.savePrefs(this);

    }

}
