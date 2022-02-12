package com.aprihive.methods;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;


import com.aprihive.R;
import com.google.android.material.snackbar.Snackbar;

public class NetworkListener {

    public Boolean connected;
    public View view;
    private MySnackBar customSnackBar;
    private int time;
    private Snackbar snackbar;


    public NetworkListener(Context context, View mview, Window window){

        view = window.getDecorView().findViewById(android.R.id.content);


        showNetworkStatus(context, view, "No Connection!", R.color.color_error_red_200, Snackbar.LENGTH_INDEFINITE);

    }

    public BroadcastReceiver networkListenerReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState().equals(NetworkInfo.State.CONNECTED) ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState().equals(NetworkInfo.State.CONNECTED);

            if (!connected){
                snackbar.show();
            } else {
                snackbar.dismiss();
            }

        }
    };


   private void showNetworkStatus(Context context, View display, String msg, int color, int time){
       snackbar = Snackbar.make(display, msg, time);
       snackbar.setBackgroundTint(context.getResources().getColor(color));

       View view = snackbar.getView();
       try {
           view.findViewById(R.id.snackbar_text).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
       }
       catch (Exception e1) {//nothing
       }

    }




}
