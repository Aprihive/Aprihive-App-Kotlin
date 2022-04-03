package com.aprihive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_payment);

        Window window = getWindow();
        Context context = PaymentActivity.this;

        window.setStatusBarColor(context.getResources().getColor(R.color.color_theme_blue_500));
        window.setNavigationBarColor(context.getResources().getColor(R.color.color_theme_blue_500));

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

        Button back = findViewById(R.id.button2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
