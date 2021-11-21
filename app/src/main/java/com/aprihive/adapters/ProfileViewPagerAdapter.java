package com.aprihive.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aprihive.pages.Feed;
import com.aprihive.pages.Catalogue;

public class ProfileViewPagerAdapter extends FragmentPagerAdapter {
    public ProfileViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new Feed();
            case 1:
                return new Catalogue();
            default:
                return null;
        }

    }


    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Feed";
            case 1:
                return "Catalogue";
            default:
                return null;
        }
    }



}
