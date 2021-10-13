package com.erlite.aprihive.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.erlite.aprihive.pages.Discover;
import com.erlite.aprihive.pages.Find;
import com.erlite.aprihive.pages.Messages;
import com.erlite.aprihive.pages.Requests;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {


    int tabNumber;
    BottomNavigationView tabLayout;


    public HomeViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new Discover();
            case 1:
                return new Find();

            case 2:
                return new Requests();

            case 3:
                return new Messages();

        }

        return new Discover();

    }

    @Override
    public int getCount() {
        return 4;
    }
}
