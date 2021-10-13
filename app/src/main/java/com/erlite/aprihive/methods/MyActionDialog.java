package com.erlite.aprihive.methods;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erlite.aprihive.R;




public class MyActionDialog {

    private final Dialog dialog;


    public MyActionDialog(Context context, String title, String msg, int image, Runnable action){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView actionText =  dialog.findViewById(R.id.dialogText);
        TextView actionTitle = dialog.findViewById(R.id.dialogTitle);
        ImageView actionImage = dialog.findViewById(R.id.dialogImage);
        TextView positiveBtn = dialog.findViewById(R.id.positiveButton);
        TextView negativeBtn = dialog.findViewById(R.id.negativeButton);

        actionText.setText(msg);
        actionTitle.setText(title);
        actionImage.setImageResource(image);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                action.run();
                dialog.dismiss();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



    }

    public MyActionDialog(Context context, String title, String msg, int image, Runnable action, String btnText, int btnColor){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        TextView actionText =  dialog.findViewById(R.id.dialogText);
        TextView actionTitle = dialog.findViewById(R.id.dialogTitle);
        ImageView actionImage = dialog.findViewById(R.id.dialogImage);
        TextView positiveBtn = dialog.findViewById(R.id.positiveButton);
        TextView negativeBtn = dialog.findViewById(R.id.negativeButton);

        actionText.setText(msg);
        actionTitle.setText(title);
        actionImage.setImageResource(image);

        positiveBtn.setText(btnText);
        //positiveBtn.setBackgroundColor(btnColor);
        negativeBtn.setVisibility(View.GONE);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                action.run();
                dialog.dismiss();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



    }

    //get notification
    public MyActionDialog(Context context, String title, String msg, int image, int imageColor, Runnable action, String btnText, Boolean cancelable){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(cancelable);

        TextView actionText =  dialog.findViewById(R.id.dialogText);
        TextView actionTitle = dialog.findViewById(R.id.dialogTitle);
        ImageView actionImage = dialog.findViewById(R.id.dialogImage);
        TextView positiveBtn = dialog.findViewById(R.id.positiveButton);
        TextView negativeBtn = dialog.findViewById(R.id.negativeButton);

        actionText.setText(msg);
        actionTitle.setText(title);
        actionImage.setColorFilter(context.getResources().getColor(imageColor));
        actionImage.setImageResource(image);

        positiveBtn.setText(btnText);
        //positiveBtn.setBackgroundColor(btnColor);
        negativeBtn.setVisibility(View.GONE);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                action.run();
                dialog.dismiss();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



    }

    public MyActionDialog(Context context, String title, String msg, int image, Runnable action, String positiveBtnText, String negativeBtnText){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        TextView actionText =  dialog.findViewById(R.id.dialogText);
        TextView actionTitle = dialog.findViewById(R.id.dialogTitle);
        ImageView actionImage = dialog.findViewById(R.id.dialogImage);
        TextView positiveBtn = dialog.findViewById(R.id.positiveButton);
        TextView negativeBtn = dialog.findViewById(R.id.negativeButton);

        actionText.setText(msg);
        actionTitle.setText(title);
        actionImage.setImageResource(image);

        positiveBtn.setText(positiveBtnText);
        negativeBtn.setText(negativeBtnText);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                action.run();
                dialog.dismiss();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }


}
