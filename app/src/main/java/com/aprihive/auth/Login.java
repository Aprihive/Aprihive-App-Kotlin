package com.aprihive.auth;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aprihive.Home;
import com.aprihive.R;
import com.aprihive.fragments.ForgotPassword;
import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.NetworkListener;
import com.aprihive.methods.SetBarsColor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView forgotPassword;
    private TextView errorFeedback;

    private Button submitBtn;
    private EditText emailInput, passwordInput;
    private ConstraintLayout loading, page;

    private String email, password;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;
    private NetworkListener networkListener;

    private static final String TAG = "ok" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        networkListener = new NetworkListener(this, page, getWindow());



        ///declare
        toolbar = findViewById(R.id.toolbar);
        submitBtn = findViewById(R.id.submitBtn);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        errorFeedback = findViewById(R.id.errorFeedback);
        loading = findViewById(R.id.loading);
        page = findViewById(R.id.page);
        toolbar = findViewById(R.id.toolbar);
        forgotPassword = findViewById(R.id.forgotPassword);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Login.super.onBackPressed();

            }
        });

        TextView privacy = findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aprihive.jesulonimii.me/privacy"));
                startActivity(intent);
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPassword bottomSheet = new ForgotPassword();
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });


        IntentFilter networkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkListener.networkListenerReceiver, networkIntentFilter);


    }

   /* @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(net, intentFilter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver();
    }*/

    private void checkInputs() {
        //get values from input
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();

        //check email
        if (email.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Email field cannot be empty");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Please input a valid email address");
        }

        //check password
        else if (password.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Password cannot be empty");
        }

        else {
            errorFeedback.setVisibility(View.GONE);

            if (networkListener.connected){
                checkUser();
                disableButton();
                loading.setVisibility(View.VISIBLE);
            }

        }

    }

    private void checkUser() {


            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        user = auth.getCurrentUser();

                        assert user != null;
                        if (user.isEmailVerified()){
                            //redirect to home
                            Intent i = new Intent(Login.this, Home.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            //redirect to email verification
                            Intent i = new Intent(Login.this, VerifyEmail.class);
                            startActivity(i);
                            finish();
                        }

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "could not login");
                    enableButton();
                    loading.setVisibility(View.GONE);
                    MySnackBar snackBar = new MySnackBar(Login.this, page, "Login Failed\nCheck the details provided", R.color.color_error_red_200, Snackbar.LENGTH_LONG);
                }
            });




    }

    private void enableButton(){
        submitBtn.setVisibility(View.VISIBLE);
        submitBtn.setBackground(getResources().getDrawable(R.drawable.blue_button));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });
    }

    private void disableButton(){
        submitBtn.setVisibility(View.INVISIBLE);
        submitBtn.setBackground(getResources().getDrawable(R.drawable.disabled_button));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nothing;
            }
        });
    }


}
