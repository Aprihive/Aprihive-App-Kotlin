package com.aprihive.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aprihive.Home;
import com.aprihive.R;
import com.aprihive.methods.SetBarsColor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyEmail extends AppCompatActivity {


    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;
    private String userId, userEmail;
    private Boolean emailVerified = false;

    private TextView errorFeedback;
    private ConstraintLayout loading, page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        userEmail = user.getEmail();

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());


        errorFeedback = findViewById(R.id.errorFeedback);
        loading = findViewById(R.id.loading);
        page = findViewById(R.id.page);



        errorFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVerification();
                user.sendEmailVerification();
                disableButton();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableButton();
                    }
                }, 100000);

            }
        });

    }

    private void checkVerification() {

        errorFeedback.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (user.isEmailVerified()){
                    errorFeedback.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    Intent i = new Intent(VerifyEmail.this, Home.class);
                    i.putExtra("newly created", true);
                    startActivity(i);

                }
                else {
                    errorFeedback.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    Toast.makeText(VerifyEmail.this, "Email not verified", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }


    private void enableButton(){
        errorFeedback.setTextColor(getResources().getColor(R.color.color_chip_blue_300));
        errorFeedback.setBackground(getResources().getDrawable(R.drawable.light_blue_bg_chip));
        errorFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification();
                disableButton();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableButton();
                    }
                }, 100000);

            }
        });
    }

    private void disableButton(){
        errorFeedback.setTextColor(getResources().getColor(R.color.white_text_color));
        errorFeedback.setBackground(getResources().getDrawable(R.drawable.disabled_button));
        errorFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nothing
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVerification();
    }


}
