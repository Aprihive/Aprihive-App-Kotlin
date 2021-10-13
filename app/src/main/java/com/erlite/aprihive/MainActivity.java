package com.erlite.aprihive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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

import com.erlite.aprihive.auth.Login;
import com.erlite.aprihive.auth.SetUsername;
import com.erlite.aprihive.auth.SignUp;

import com.erlite.aprihive.methods.MySnackBar;
import com.erlite.aprihive.methods.SetBarsColor;
import com.erlite.aprihive.methods.SharedPrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "ok";
    private Button create, withGoogle;
    private TextView login;

    private FirebaseAuth auth;


    private FirebaseFirestore db;
    private DocumentReference reference;
    private UserProfileChangeRequest profileUpdates;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();


        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());

        create = findViewById(R.id.create);
        login = findViewById(R.id.loginBtn);
        withGoogle = findViewById(R.id.googleBtn);

        sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SignUp.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);
            }
        });

        withGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MySnackBar snackBar = new MySnackBar(MainActivity.this, getWindow().getDecorView(), "Google sign-in not available now\nPlease continue with email.", R.color.color_error_red_200, Snackbar.LENGTH_LONG);
               // Intent signInIntent = mGoogleSignInClient.getSignInIntent();
               // startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

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



    /*
    private void createRequest() {

        //initialize sign in options
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("615414296417-43qt0kg4b96ubc8jm9mkar5f4oecpbea.apps.googleusercontent.com")
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso above .
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            //handle result from google sign in
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if(account != null){
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {

                            auth.fetchSignInMethodsForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    DocumentReference check = db.collection("users").document(account.getEmail());

                                    check.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()){
                                                DocumentSnapshot userDoc = task.getResult();

                                                if (userDoc != null){
                                                    saveDetails(account);
                                                } else {
                                                    Intent i = new Intent(MainActivity.this, Home.class);
                                                    Log.e("check storage", "no storage needed");
                                                    startActivity(i);
                                                    finish();
                                                }

                                            }

                                        }
                                    });


                                }
                            });

                        }

                    }
                });

            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            MySnackBar snackBar = new MySnackBar(MainActivity.this, getWindow().getDecorView(), "Authentication Failed", R.color.color_error_red_200, Snackbar.LENGTH_LONG);

        }
    }

    private void saveDetails(GoogleSignInAccount account) {


        Log.e("check storage", "start storage");

        FirebaseUser user = auth.getCurrentUser();


        //set display name
        profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("user"+account.getId())
                .setPhotoUri(account.getPhotoUrl())
                .build();
        user.updateProfile(profileUpdates);
        ////


        Map<String, Object> details = new HashMap<>();
        details.put("name", account.getDisplayName());
        details.put("email", account.getEmail());
        details.put("username", account.getId());
        details.put("bio", "This is what I do!");
        details.put("school", "Obafemi Awolowo University");
        details.put("phone", "-");
        details.put("verified", false);
        details.put("registered on", new Timestamp(new Date()));




        reference = db.collection("users").document(account.getEmail());
        reference.set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: user details stored");

                //waiting to send email b4 redirect
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "Failed");

                MySnackBar snackBar = new MySnackBar(MainActivity.this, getWindow().getDecorView(), "An error occurred while saving profile.", R.color.color_error_red_200, Snackbar.LENGTH_LONG);
            }
        });


        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d(TAG, "email link sent");

                //redirect to set username
                Intent i = new Intent(MainActivity.this, SetUsername.class);
                i.putExtra("newlyCreated", true);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "email link not sent");
                MySnackBar snackBar = new MySnackBar(MainActivity.this, getWindow().getDecorView(), "Email verification failed", R.color.color_error_red_200, Snackbar.LENGTH_LONG);

            }
        });


    }

     */

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


