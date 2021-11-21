// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aprihive.R;
import com.aprihive.auth.Login;
import com.aprihive.auth.SetUsername;
import com.aprihive.auth.SignUp;

import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.SetBarsColor;
import com.aprihive.methods.SharedPrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "ok";
    private TextView create, withGoogle;
    private TextView login;

    private FirebaseAuth auth;


    private FirebaseFirestore db;
    private DocumentReference reference;
    private UserProfileChangeRequest profileUpdates;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private SharedPrefs sharedPrefs;
    private FirebaseUser user;
    ConstraintLayout load;
    private View.OnClickListener emptyListener;
    private View.OnClickListener activeListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());

        create = findViewById(R.id.create);
        login = findViewById(R.id.loginBtn);
        withGoogle = findViewById(R.id.googleBtn);
        load = findViewById(R.id.progressTab);

        sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        emptyListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };

        activeListeners = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.create:
                        Intent i = new Intent(MainActivity.this, SignUp.class);
                        startActivity(i);
                        break;
                    case R.id.loginBtn:
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        break;
                    case R.id.googleBtn:
                        showOverlay();
                        signIn();
                        break;
                }
            }
        };

        create.setOnClickListener(activeListeners);
        login.setOnClickListener(activeListeners);
        withGoogle.setOnClickListener(activeListeners);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.notification_channel_id);
            String channelName = getString(R.string.notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            hideOverlay();
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                    }
                });

        //Intent intentBgService = new Intent(this, FirebaseMessagingServiceSetup.class);
        //startService(intentBgService);



    }

    private void showOverlay(){
        login.setOnClickListener(emptyListener);
        create.setOnClickListener(emptyListener);
        withGoogle.setOnClickListener(emptyListener);
        load.setVisibility(View.VISIBLE);
    }

    private void hideOverlay(){
        login.setOnClickListener(activeListeners);
        create.setOnClickListener(activeListeners);
        withGoogle.setOnClickListener(activeListeners);
        load.setVisibility(View.GONE);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                hideOverlay();
                Toast.makeText(MainActivity.this, "Login with Google Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = auth.getCurrentUser();
                            Log.e(TAG, "onComplete: ready to move");
                            checkPreviousLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            hideOverlay();

                            Toast.makeText(MainActivity.this, "Login with Google Failed", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //
                        }
                    }
                });
    }

    private void checkPreviousLogin() {
        user = auth.getCurrentUser();
        DocumentReference docReference = db.collection("users").document(user.getEmail());

        docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        Intent i = new Intent(MainActivity.this, Home.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        storeDetails();
                    }
                }
            }
        });
    }

    private void storeDetails() {
        user = auth.getCurrentUser();
        profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(user.getUid()).build();
        user.updateProfile(profileUpdates);
        ////

        Map<String, Object> details = new HashMap<>();
        details.put("name", user.getDisplayName());
        details.put("email", user.getEmail());
        details.put("username", user.getUid());
        details.put("username-lower", user.getUid().toLowerCase());
        details.put("bio", "This is what I do!");
        details.put("school", "");
        details.put("isAdmin", false);
        details.put("newAccount", true);
        details.put("phone", "");
        details.put("profileImageLink", user.getPhotoUrl().toString());
        details.put("verified", false);
        details.put("registered on", new Timestamp(new Date()));


        reference = db.collection("users").document(user.getEmail());
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

                Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createUserListsDb() {
        DocumentReference listsRef = db.collection("users").document(user.getEmail()).collection("lists").document("following");
        Map<String, Object> setDefault = new HashMap<>();
        listsRef.set(setDefault).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d("Debug", "success creating lists db for user");
                //redirect to set username
                Intent i = new Intent(MainActivity.this, SetUsername.class);
                i.putExtra("newlyCreated", true);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.d("Debug", "failed" + e);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        sharedPrefs = new SharedPrefs(MainActivity.this);

        int getTheme = sharedPrefs.themeSettings;

        AppCompatDelegate.setDefaultNightMode(getTheme);

        if(currentUser != null){

            if (auth.getCurrentUser().isEmailVerified()){
                Intent intent = new Intent(MainActivity.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        }
        else if(!(account == null)){
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        else{}

    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPrefs = new SharedPrefs(MainActivity.this);

        int getTheme = sharedPrefs.themeSettings;

        AppCompatDelegate.setDefaultNightMode(getTheme);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sharedPrefs = new SharedPrefs(MainActivity.this);

        int getTheme = sharedPrefs.themeSettings;

        AppCompatDelegate.setDefaultNightMode(getTheme);
    }



}


