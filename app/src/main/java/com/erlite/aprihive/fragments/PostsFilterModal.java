// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.erlite.aprihive.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;

import com.erlite.aprihive.R;
import com.erlite.aprihive.methods.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PostsFilterModal extends BottomSheetDialogFragment {

    private RadioGroup btnGroup;
    private RadioButton darkBtn, lightBtn, systemBtn, batteryBtn;
    SharedPrefs sharedPrefs;

    public PostsFilterModal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_posts_filter, container, false);

        final Activity activity = getActivity();
        final Context context = getActivity().getApplicationContext();




        btnGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (btnGroup.getCheckedRadioButtonId()){


                }
                dismiss();
            }
        });

        return view;
    }
}
