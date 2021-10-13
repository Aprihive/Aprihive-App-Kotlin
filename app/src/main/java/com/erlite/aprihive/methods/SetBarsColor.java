package com.erlite.aprihive.methods;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatDelegate;

import com.erlite.aprihive.R;

public class SetBarsColor {

    Window window;
    Context context;

   public SetBarsColor(Context context, Window window){

       this.window = window;
       this.context = context;

       setColors();

    }

    public void enableFullscreen(){

        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);


    }

    public void disableFullscreen(){
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setColors();
    }

    public void setColors(){
        window.setStatusBarColor(context.getResources().getColor(R.color.bg_color));
        window.setNavigationBarColor(context.getResources().getColor(R.color.bg_color));

        if (window.getStatusBarColor() == context.getResources().getColor(R.color.white_color)){

            //make nav bar and status bar icons dark
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                    //  window.getDecorView().setSystemUiVisibility(View.| View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
                else {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
            }

        }
    }


}
