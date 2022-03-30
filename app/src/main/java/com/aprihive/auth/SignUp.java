package com.aprihive.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.aprihive.R;
import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.NetworkListener;
import com.aprihive.methods.SetBarsColor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView errorFeedback;
    private Button submitBtn;
    private EditText emailInput, nameInput, passwordInput;
    private ConstraintLayout loading, page;

    private String email, password, name;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;

    private static final String TAG = "ok" ;

    private NetworkListener networkListener;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_signup);


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
        nameInput = findViewById(R.id.fullName);
        passwordInput = findViewById(R.id.password);
        errorFeedback = findViewById(R.id.errorFeedback);
        loading = findViewById(R.id.loading);
        page = findViewById(R.id.page);



        //
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp.super.onBackPressed();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
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

    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter networkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkListener.networkListenerReceiver, networkIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkListener.networkListenerReceiver);
    }

    private void checkInputs() {

        //get values from input
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        name = nameInput.getText().toString().trim();

        //check email
        if (email.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Email field cannot be empty");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Please input a valid email address");
        }

        //check name
        else if (name.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Name cannot be empty");
        }
        else if (!name.matches("[a-zA-Z0-9 ]*")){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Name can only contain alphabets");
        }

        //check password
        else if (password.isEmpty()) {
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Password cannot be empty");
        }
        else if (password.length() < 8 || password.matches("[\\d]*")){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Your password must be at least 8 characters and must contain at least one number ");
        }

        else {
            errorFeedback.setVisibility(View.GONE);
            if (networkListener.connected){
                createUser();
                disableButton();
                loading.setVisibility(View.VISIBLE);
            }
        }


    }

    private void createUser(){

        //get values from input
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        name = nameInput.getText().toString().trim();


        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                user = authResult.getUser();

                getFCMToken();
                assert user != null;

                //set display name
                profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                user.updateProfile(profileUpdates);
                ////

                Map <String, Object> details = new HashMap<>();
                details.put("name", name);
                details.put("email", email);
                details.put("uid", user.getUid());
                details.put("admin-level", 0);
                details.put("username", user.getUid().substring(0, 7));
                details.put("username-lower", user.getUid().toLowerCase().substring(0, 7));
                details.put("bio", "This is what I do!");
                details.put("school", "");
                details.put("isAdmin", false);
                details.put("newAccount", true);
                details.put("phone", "");
                details.put("profileImageLink", "-");
                details.put("verified", false);
                details.put("threat", false);
                details.put("fcm-token", token);
                details.put("registered on", new Timestamp(new Date()));




                reference = db.collection("users").document(email);
                reference.set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user details stored");

                        createUserListsDb();
                        //waiting to send email b4 redirect
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "Failed");

                        MySnackBar snackBar = new MySnackBar(SignUp.this, page, "An error occurred while saving profile.", R.color.color_error_red_200, Snackbar.LENGTH_LONG);
                        enableButton();
                        Log.e(TAG, "Saving user details failed: " + e.getLocalizedMessage());
                        loading.setVisibility(View.GONE);
                    }
                });


                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "email link sent");

                        //redirect to set username
                        Intent i = new Intent(SignUp.this, SetUsername.class);
                        i.putExtra("newlyCreated", true);
                        startActivity(i);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "email link not sent");
                        MySnackBar snackBar = new MySnackBar(SignUp.this, page, "Email verification failed", R.color.color_error_red_200, Snackbar.LENGTH_LONG);

                    }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "Creating account failed: " + e.getLocalizedMessage() );

                String errorMessage = "We could not sign you up at this time.\nPlease try again later.";

                if (e.getLocalizedMessage().contains("email address is already in use")){
                    errorMessage = "The email address entered is not available.\nPlease use another.";
                }

                MySnackBar snackBar = new MySnackBar(SignUp.this, page, errorMessage, R.color.color_error_red_200, Snackbar.LENGTH_LONG);

                enableButton();
                loading.setVisibility(View.GONE);

            }
        });

    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                    }
                });
    }


    private void createUserListsDb() {
        DocumentReference listsRef = db.collection("users").document(email).collection("lists").document("following");
        Map<String, Object> setDefault = new HashMap<>();
        listsRef.set(setDefault).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d("Debug", "success creating lists db for user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.d("Debug", "failed" + e);
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
