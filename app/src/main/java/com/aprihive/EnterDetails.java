// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aprihive.R;
import com.aprihive.methods.SetBarsColor;

public class EnterDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_details);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());

    }
}
