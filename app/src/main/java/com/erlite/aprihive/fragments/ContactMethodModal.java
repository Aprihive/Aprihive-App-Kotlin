// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.erlite.aprihive.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.erlite.aprihive.R;
import com.erlite.aprihive.methods.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ContactMethodModal extends BottomSheetDialogFragment {


    private ConstraintLayout whatsapp, call, sms, twitter, instagram;
    private String phoneNumber;
    private String requestInfo;
    private String instagramUsername;
    private String twitterUsername;
    private TextView nothingText;


    public ContactMethodModal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_contact_method, container, false);

        final Activity activity = getActivity();
        final Context context = getActivity().getApplicationContext();



        whatsapp = view.findViewById(R.id.viaWhatsapp);
        call = view.findViewById(R.id.viaPhoneCall);
        sms = view.findViewById(R.id.viaSms);
        twitter = view.findViewById(R.id.viaTwitter);
        instagram = view.findViewById(R.id.viaInstagram);
        nothingText = view.findViewById(R.id.nothingText);



        // get details.
        try {
            assert getArguments() != null;
            phoneNumber = getArguments().getString("phoneNumber");
            instagramUsername = getArguments().getString("instagramName");
            twitterUsername = getArguments().getString("twitterName");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //set visibility
        try {
            if (twitterUsername.isEmpty() || twitterUsername.equals("null")){
                twitter.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            twitter.setVisibility(View.GONE);
        }

        try {
            if (instagramUsername.isEmpty() || instagramUsername.equals("null")){
                instagram.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            instagram.setVisibility(View.GONE);
        }

        try {
            if (phoneNumber.isEmpty() || phoneNumber.equals("null")){
                whatsapp.setVisibility(View.GONE);
                call.setVisibility(View.GONE);
                sms.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            whatsapp.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            sms.setVisibility(View.GONE);
        }

        if (whatsapp.getVisibility() == View.GONE && twitter.getVisibility() == View.GONE && instagram.getVisibility() == View.GONE){
            nothingText.setVisibility(View.VISIBLE);
        } else {
            nothingText.setVisibility(View.GONE);
        }


        String sendText = "Hello, I got your request on Aprihive. \n (Your Text Here)";

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+sendText.replace("\n", " ").replace(" ", "%20")));
                startActivity(i);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+phoneNumber.trim()));
                startActivity(i);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.fromParts("sms", phoneNumber, null));
                i.putExtra("sms_body", sendText.replace("\n", " "));
                startActivity(i);
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/"+instagramUsername));
                    intent.setPackage("com.instagram.android");
                    startActivity(intent);
                }
                catch (Exception e){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/"+instagramUsername));
                    startActivity(intent);
                }

            }
        });
        
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" +twitterUsername));
                    startActivity(intent);
                }
                catch(Exception e){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+twitterUsername));
                    startActivity(intent);
                }
            }
        });

        return view;
    }


}
