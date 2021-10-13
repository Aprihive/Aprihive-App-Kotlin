package com.erlite.aprihive.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.erlite.aprihive.EditProfileActivity;
import com.erlite.aprihive.Home;
import com.erlite.aprihive.PersonalProfileActivity;
import com.erlite.aprihive.R;
import com.erlite.aprihive.methods.MySnackBar;
import com.erlite.aprihive.methods.SetBarsColor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SetUsername extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView feedback;
    private Button submitBtn;
    private EditText usernameInput;
    private String username, userId, userEmail;

    private TextView errorFeedback;
    private ConstraintLayout loading, page;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;
    Boolean newlyCreated = false;


    private static final String TAG = "checks" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_username);


        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        userEmail = user.getEmail();

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());


        toolbar = findViewById(R.id.toolbar);
        submitBtn = findViewById(R.id.saveButton);
        usernameInput = findViewById(R.id.username);
        errorFeedback = findViewById(R.id.errorFeedback);
        loading = findViewById(R.id.loading);
        page = findViewById(R.id.page);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetUsername.super.onBackPressed();
            }
        });





        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });

        Intent intent = getIntent();
        newlyCreated = intent.getBooleanExtra("newlyCreated", false);

        if (newlyCreated){
            usernameInput.setText(user.getUid());
        } else {
            usernameInput.setText(user.getDisplayName());
        }



    }

    private void checkInputs() {
        username = usernameInput.getText().toString().trim();

        if (username.isEmpty()){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("Username cannot be empty");
        }
        else if (username.length() < 5 || username.length() > 15){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("username should be between 5 to 10 characters");
        }
        else if (!username.matches("[a-zA-Z0-9._]*")){
            errorFeedback.setVisibility(View.VISIBLE);
            errorFeedback.setText("username should not contain spaces and special characters except \"_\" and \".\". ");
        }
        else{
            errorFeedback.setVisibility(View.GONE);
            checkUsername();
            loading.setVisibility(View.VISIBLE);
            disableButton();
        }

    }


    private void checkUsername() {

        Log.d(TAG, "checking username");

        CollectionReference collectionReference = db.collection("users");
        Query query = collectionReference.whereEqualTo("username".toLowerCase(), username.toLowerCase());




        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot : task.getResult()){

                        String existingUsernames = documentSnapshot.getString("username");

                        if (username.equalsIgnoreCase(existingUsernames)){
                            //username does exist
                            MySnackBar snackBar = new MySnackBar(SetUsername.this, page, "This username is not available", R.color.color_error_red_200, Snackbar.LENGTH_LONG);
                            loading.setVisibility(View.GONE);
                            enableButton();
                            Log.d(TAG, "username exists");
                        }
                    }
                }

                if (task.getResult().size() == 0) {
                    Log.d(TAG, "User does not exist");


                    changeUsername();

                }
            }
        });

    }


    private void changeUsername() {

        Log.d(TAG, "changing username");

        profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
        user.updateProfile(profileUpdates);



        reference = db.collection("users").document(userEmail);
        reference.update("username", username).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MySnackBar snackBar = new MySnackBar(SetUsername.this, page, "Username updated!", R.color.color_success_green_300, Snackbar.LENGTH_INDEFINITE);
                loading.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (newlyCreated){
                            Intent i = new Intent(SetUsername.this, VerifyEmail.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(SetUsername.this, EditProfileActivity.class);
                            startActivity(i);
                            finish();
                        }


                    }
                }, 1500);
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
