package com.erlite.aprihive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.erlite.aprihive.methods.SetBarsColor;

public class EnterDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_details);

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());

    }
}
