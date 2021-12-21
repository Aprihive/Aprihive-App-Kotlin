// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.adapters.HomeViewPagerAdapter;
import com.aprihive.R;
import com.aprihive.auth.Login;
import com.aprihive.auth.SetUsername;
import com.aprihive.fragments.AddPostModal;
import com.aprihive.methods.MyActionDialog;
import com.aprihive.methods.NetworkListener;
import com.aprihive.methods.SetBarsColor;
import com.aprihive.methods.SharedPrefs;
import com.aprihive.pages.Discover;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public ViewPager viewPager;
    private BottomNavigationView tabLayout;
    private NetworkListener networkListener;
    private ConstraintLayout loading, page;
    private Toolbar toolbar;
    private NavigationView navigView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;

    private TextView navName, navUsername;
    private ImageView navImage;
    private ImageView verificationIcon;
    private ListenerRegistration registerQuery;
    private ImageView logo;
    private FloatingActionButton fab;
    private String getFullname, getUsername, getProfilePic;
    private boolean getVerified;
    public Bundle bundle;
    private Runnable postRefresh;
    private SharedPrefs sharedPrefs;
    private String CHANNEL_ID = "welcome_notification";
    private Boolean isUserNew;
    private Boolean isAdmin;
    private TextView signOut;
    private Runnable action;
    private String getLocation;
    private String getAction;
    private String getTitle;
    private String getText;
    private String getActionText;
    private Boolean getCloseable;
    private String getActionType;
    private Boolean getActive;
    private String getIconType;
    private String getIconColor;
    private String getVersion;
    private DocumentReference notifyReference;
    private ListenerRegistration notifyRegisterQuery;
    private boolean notificationGotten = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean notified = false;
    private HomeViewPagerAdapter adapter;
    private GoogleSignInClient mGoogleSignInClient;
    private Boolean getThreat;
    private ImageView threatIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefs = new SharedPrefs(this);
        int getTheme = sharedPrefs.themeSettings;
        AppCompatDelegate.setDefaultNightMode(getTheme);




        sharedPreferences = getSharedPreferences("aprihive", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

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

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    //nothing
                }
            }
        };

        user = auth.getCurrentUser();

        //reference = db.collection("users").document(user.getEmail());

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        adapter = new HomeViewPagerAdapter(getSupportFragmentManager());

        bundle = new Bundle();


        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.bar);
        page = findViewById(R.id.page);
        navigView = findViewById(R.id.nav_view);
        signOut = navigView.findViewById(R.id.logout);
        drawer = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fabAddPost);

        logo = findViewById(R.id.logoImageView);

        navName = navigView.getHeaderView(0).findViewById(R.id.nav_profile_name);
        navUsername = navigView.getHeaderView(0).findViewById(R.id.nav_profile_username);
        navImage = navigView.getHeaderView(0).findViewById(R.id.nav_profile_pic);
        verificationIcon = navigView.getHeaderView(0).findViewById(R.id.verifiedIcon);
        threatIcon = navigView.getHeaderView(0).findViewById(R.id.warningIcon);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, PersonalProfileActivity.class);
                startActivity(i);
            }
        });

        navName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, PersonalProfileActivity.class);
                startActivity(i);
            }
        });

        action = new Runnable() {
            @Override
            public void run() {
                auth.signOut();

                try {
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            db.clearPersistence();
                            Discover discover = new Discover();
                            discover.onStop();
                            registerQuery.remove();
                            notifyRegisterQuery.remove();
                            Intent intent = new Intent(Home.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                db.clearPersistence();
                Discover discover = new Discover();
                discover.onStop();
                registerQuery.remove();
                notifyRegisterQuery.remove();
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        };

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyActionDialog dialog = new MyActionDialog(Home.this, "Sign Out?", "Are you sure you want to sign out?", R.drawable.ic_exit, R.color.color_error_red_200, action, "Yes, Sign out", "No, Just kidding.");
                dialog.show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPostModal bottomSheet = new AddPostModal(Discover.refreshPostsRunnable);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
            }
        });

        //init action bar drawer toggle
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        navigView.setNavigationItemSelectedListener(this);

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (user == null){
                    auth.signOut();
                    db.clearPersistence();
                    finish();
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

        //setUserDetailsInNavbar();



        networkListener = new NetworkListener(this, page, getWindow());
        IntentFilter networkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkListener.networkListenerReceiver, networkIntentFilter);

        //viewPager.setAdapter(adapter);

        tabLayout.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.find:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.requests:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.messages:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        tabLayout.getMenu().findItem(R.id.home).setChecked(true);
                        if(viewPager.getCurrentItem() != 0){
                            showHamburger();
                            findViewById(R.id.logoImageView).setVisibility(View.VISIBLE);
                        }
                        fab.show();
                        break;
                    case 1:
                        tabLayout.getMenu().findItem(R.id.find).setChecked(true);
                        if(viewPager.getCurrentItem() != 1){
                            showHamburger();
                            findViewById(R.id.logoImageView).setVisibility(View.VISIBLE);
                        }
                        fab.hide();
                        break;
                    case 2:
                        tabLayout.getMenu().findItem(R.id.requests).setChecked(true);
                        showHamburger();
                        findViewById(R.id.logoImageView).setVisibility(View.VISIBLE);
                        fab.hide();
                        break;
                    case 3:
                        tabLayout.getMenu().findItem(R.id.messages).setChecked(true);
                        showHamburger();
                        findViewById(R.id.logoImageView).setVisibility(View.VISIBLE);
                        fab.hide();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        if(getIntent().getBooleanExtra("newly created", false)){

            sendNotification();

        }

        if (savedInstanceState != null) {
            notificationGotten = savedInstanceState.getBoolean("notified", false);
            if (!notificationGotten){
                getNotification();
            }
        } else {
            getNotification();
        }


    }

    private void sendNotification() {
        createNotificationChannel();
        NotificationCompat.Builder builder  = new NotificationCompat.Builder(Home.this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notification_icon).setColor(ContextCompat.getColor(this, R.color.color_theme_green_100));
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
        builder.setContentTitle("Welcome to Aprihive!");
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Welcome to the network for social marketing!\nPlease complete your profile then go ahead to create your first campaign to start networking.\nDon't forget to add your items to your catalogue!"));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, EditProfileActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Home.this);
        notificationManagerCompat.notify(1, builder.build());
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            
            CharSequence name = "Reminder Service";
            String description = "Basic information and app reminders.";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            
            
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private void setUserDetailsInNavbar() {

        reference = db.collection("users").document(user.getEmail());

        registerQuery = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                try {
                    getFullname = value.getString("name");
                    getUsername = value.getString("username");
                    getLocation = value.getString("school");
                    getProfilePic = value.getString("profileImageLink");
                    getVerified = value.getBoolean("verified");
                    getThreat = value.getBoolean("threat");

                    isAdmin = value.getBoolean("isAdmin");
                    isUserNew = value.getBoolean("newAccount");



                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Home.this, EditProfileActivity.class));
                        }
                    };

                    if (isUserNew){
                        sendNotification();
                        MyActionDialog confirm = new MyActionDialog(Home.this, "Welcome to Aprihive!", "Welcome to the social marketing network.\nPlease click continue to finish setting up your account", R.drawable.vg_boy_skating, action, "Continue", R.color.color_text_blue_500);
                        confirm.show();
                    }

                    bundle.putString("fullname", getFullname);
                    bundle.putString("username", getUsername);
                    bundle.putBoolean("verified", getVerified);
                    bundle.putString("location", getLocation);


                    navName.setText(getFullname);
                    navUsername.setText("@" + getUsername);
                    if (getVerified) {
                        verificationIcon.setVisibility(View.VISIBLE);
                    } else {
                        verificationIcon.setVisibility(View.GONE);
                    }

                    if (getThreat) {
                        threatIcon.setVisibility(View.VISIBLE);
                    } else {
                        threatIcon.setVisibility(View.GONE);
                    }

                    Glide.with(getApplicationContext())
                            .load(getProfilePic)
                            .centerCrop() //4
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.user_image_placeholder)
                            .fallback(R.drawable.user_image_placeholder) //7
                            .into(navImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });




    }

    private void getNotification() {

        notifyReference = db.collection("general").document("app notifications");
        notifyRegisterQuery = notifyReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                try {


                    getActive = value.getBoolean("active");
                    getTitle = value.getString("title");
                    getText = value.getString("text");
                    getIconType = value.getString("iconType");
                    getIconColor = value.getString("iconColor");
                    getCloseable = value.getBoolean("close");
                    getActionText = value.getString("actionText");
                    getActionType = value.getString("actionType");
                    getAction = value.getString("action");
                    getVersion = value.getString("version");


                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                            if (getActionType.equals("url")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getAction));
                                startActivity(intent);
                            } else if (getActionType.equals("okay")) {
                                //nothing
                            } else if (getActionType.equals("update")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getAction));
                                startActivity(intent);
                            } else if (getActionType.equals("closeApp")) {
                                finishAndRemoveTask();
                            } else if (getActionType.equals("intent")) {
                                try {
                                    Intent i = new Intent(Home.this, Class.forName(getAction));
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //nothing
                            }

                        }
                    };


                    MyActionDialog dialog = new MyActionDialog(Home.this, getTitle, getText, icon(getIconType), iconColor(getIconColor), action, getActionText, getCloseable);

                    try {
                        //clear existing dialog
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (getActionType.equals("update") && getActive) {
                        if (!getVersion.equals(getResources().getString(R.string.version))) {
                            dialog.show();
                        }
                    } else if (!getActionType.equals("update") && getActive) {
                        dialog.show();
                    }

                    if (getCloseable){
                        setUserDetailsInNavbar();
                        viewPager.setAdapter(adapter);
                    }


                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        notified = true;
    }

    private int iconColor(String getIconColor) {
        int i;
        switch (getIconColor){
            case "red":
                i = R.color.color_error_red_300;
                break;
            case "yellow":
                i = R.color.color_yellow;
                break;
            case "blue":
                i = R.color.color_theme_blue;
                break;
            case "green":
                i = R.color.color_green;
                break;
            default:
                i = R.color.color_theme_blue;
        }
        return i;
    }

    private int icon(String getIconType) {
        int i;
        switch (getIconType){
            case "warning":
                i = R.drawable.ic_warning;
                break;
            case "info":
                i = R.drawable.ic_info;
                break;
            case "alert":
                i = R.drawable.ic_alert;
                break;
            case "done":
                i = R.drawable.ic_done;
                break;
            case "cancel":
                i = R.drawable.ic_cancel;
                break;
            case "notify":
                i = R.drawable.ic_notifications_active;
                break;
            case "logo":
                i = R.drawable.icon;
                break;
            case "fire":
                i = R.drawable.ic_feed;
                break;
            default:
                i = R.drawable.ic_info;
        }
        return i;
    }

    public void showHamburger(){
        toggle.setDrawerIndicatorEnabled(true);
    }
    public void hideHamburger(){
        toggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        //permissions
        verifyPermissions();

        if (user != null) {
            //nothing

            if (user.getDisplayName().equals(user.getUid())) {

                Intent intent = new Intent(Home.this, SetUsername.class);
                startActivity(intent);


            }

        } else {
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
           // Discover.refreshPostsRunnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean verifyPermissions() {

        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {

            String[] PERMISSIONS = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            return false;
        }
        return true;

    }

    public Boolean isAdmin(){
        return isAdmin;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.myProfile){
            Intent i = new Intent(Home.this, PersonalProfileActivity.class);
            startActivity(i);
        }

        if (item.getItemId() ==  R.id.infoActions){
            Intent i = new Intent(Home.this, SettingsActivity.class);
            startActivity(i);
        }

        if (item.getItemId() ==  R.id.promotions){
            //TODO: create promotions activities
            Toast.makeText(this, "This action is temporarily disabled.", Toast.LENGTH_SHORT).show();
        }

       // if (item.getItemId() == R.id.signOut){
       //     auth.signOut();
       //     db.clearPersistence();
       //     Discover discover = new Discover();
       //     discover.onStop();
       //     registerQuery.remove();
       //     Intent intent = new Intent(Home.this, MainActivity.class);
       //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
       //     startActivity(intent);
       //     finish();
       // }

        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("notified", notified);
    }

}
