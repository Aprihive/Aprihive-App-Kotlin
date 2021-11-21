// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved

package com.aprihive.fragments;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.aprihive.R;




public class DialogModal extends DialogFragment {


    public ImageView actionImage;
    public TextView actionTitle;
    public TextView actionText;
    public TextView positiveBtn;
    public TextView negativeBtn;
    private Context context;
    private Void positiveAction;
    private Void negativeAction;

    public DialogModal(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.custom_dialog, container, false);


        TextView actionText = view.findViewById(R.id.dialogText);
        TextView actionTitle = view.findViewById(R.id.dialogTitle);
        TextView positiveBtn = view.findViewById(R.id.positiveButton);
        TextView negativeBtn = view.findViewById(R.id.negativeButton);


        actionText.setText("Are you sure you want to delete this post?");
        actionTitle.setText("Delete Post");

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmDelete();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return view;
    }

    private void confirmDelete() {
    }


}
