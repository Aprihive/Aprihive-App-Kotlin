// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.aprihive.MainActivity;
import com.aprihive.R;
import com.aprihive.Splash;
import com.aprihive.models.OnboardingModel;
import com.aprihive.pages.Catalogue;
import com.aprihive.pages.Feed;

import java.util.List;

public class OnboardingViewAdapter extends PagerAdapter {

    Context context;
    List<OnboardingModel> list;

    public OnboardingViewAdapter(Context context, List<OnboardingModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.onboarding_item, null);

        ImageView imgSlide = layout.findViewById(R.id.imageView);
        TextView title = layout.findViewById(R.id.title);
        TextView description = layout.findViewById(R.id.description);
        TextView button = layout.findViewById(R.id.continueBtn);


        title.setText(list.get(position).getTitle());
        description.setText(list.get(position).getDescription());
        imgSlide.setImageResource(list.get(position).getImageUri());
        button.setVisibility(list.get(position).getButtonDisplay());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });

        container.addView(layout);

        return layout;
        
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
