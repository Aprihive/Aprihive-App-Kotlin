package com.erlite.aprihive.methods;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.erlite.aprihive.R;
import com.google.android.material.snackbar.Snackbar;

public class MySnackBar {

    Snackbar snackbar;

    public MySnackBar(Context context, View display, String msg, int color, int time){

        snackbar = Snackbar.make(display, msg, time);
        snackbar.setBackgroundTint(context.getResources().getColor(color));

        View view = snackbar.getView();
        try {
            view.findViewById(R.id.snackbar_text).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        catch (Exception e1) {//nothing
        }
        snackbar.show();



    }


}
