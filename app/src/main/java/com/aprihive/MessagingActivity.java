// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved

package com.aprihive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprihive.adapters.MessagingRecyclerAdapter;
import com.aprihive.backend.RetrofitInterface;
import com.aprihive.fragments.OptionsDialogModal;
import com.aprihive.fragments.ReportModal;
import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.SetBarsColor;
import com.aprihive.models.MessageModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessagingActivity extends AppCompatActivity implements MessagingRecyclerAdapter.MyClickListener {

    private static final String TAG = "debug";
    private Toolbar toolbar;
    private ConstraintLayout backButton;
    private TextView receiverName, receiverBio, receiverEmail;
    private Boolean verified;
    private ImageView receiverProfileImage;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private List<MessageModel> messagesList;
    private RecyclerView recyclerView;
    private MessagingRecyclerAdapter adapter;

    private EditText addMessage;
    private String addPostText;
    private ImageView sendButton;
    private HashMap<Object, Object> messageDetails;
    private int lastMessageNumber = 0;
    private DocumentReference reference;
    private Random random;
    private String messageId;
    private ListenerRegistration registerQuery;
    private String getMessageText;
    private Timestamp getTime;
    private String getMessageImageLink;
    private String getMessageType;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String token;
    private String receiverUserName;
    private String getMessageId;
    private DocumentReference msgRef;
    private String receiverProfileImageLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_messaging);


        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        random = new Random();

        SetBarsColor setBarsColor = new SetBarsColor(this, getWindow());
        messagesList = new ArrayList<>();

        adapter = new MessagingRecyclerAdapter(this, messagesList, this);

        toolbar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.backButton);

        receiverName =  findViewById(R.id.receiverName);
        receiverBio =  findViewById(R.id.receiverBio);
        receiverProfileImage = findViewById(R.id.receiverImage);
        recyclerView = findViewById(R.id.recyclerView);
        addMessage = findViewById(R.id.editText);
        sendButton = findViewById(R.id.sendButton);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        getUserDetails();
        getMessages();



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagingActivity.super.onBackPressed();
            }
        });

    }

    private void checkInputs(){

        addPostText = addMessage.getText().toString().trim();

        //check email
        if (addPostText.isEmpty()) {

        }
        else{
            messageId = String.valueOf(random.nextInt(1000000));
            sendMessage();
            addMessage.setText("");

        }

    }

    private void getMessages() {

        assert user != null;
        try {
            Query messagesQuery = db.collection("users").document(user.getEmail()).collection("messages").document(getIntent().getStringExtra("getEmail")).collection("messageBox").orderBy("time");

            registerQuery = messagesQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error!=null){
                        Log.e(TAG,"Error: "+error.getMessage());
                    }else {
                        messagesList.clear();
                        for (DocumentSnapshot details : value.getDocuments()){

                            MessageModel messageModel = new MessageModel();


                            try {
                                getMessageText = details.getString("messageText");
                                getMessageId = details.getId();

                                getTime = details.getTimestamp("time");
                                getMessageImageLink = details.getString("messageImageLink");
                                getMessageType= details.getString("type");

                                messageModel.setMessageText(getMessageText);
                                messageModel.setMessageImageLink(getMessageImageLink);
                                messageModel.setMessageType(getMessageType);
                                messageModel.setTime(getTime);
                                messageModel.setMessageId(getMessageId);
                                messageModel.setOtherUserEmail(getIntent().getStringExtra("getEmail"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }





                            messagesList.add(messageModel);

                        }
                        setupRecyclerView();
                    }



                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void sendMessage() {


        messageDetails = new HashMap<>();

        messageDetails.put("type", "to");
        messageDetails.put("time", new Timestamp(new Date()));
        messageDetails.put("messageText", addPostText);
        messageDetails.put("messageImageLink", "");


        //Map<String, Object> setDetails = new HashMap<>();
        //setDetails.put("requestFrom:-" + user.getDisplayName() + "-for:-" + postId, requestDetails);

        Log.e("debug", "sending");

        reference = db.collection("users").document(user.getEmail()).collection("messages").document(getIntent().getStringExtra("getEmail")).collection("messageBox").document(messageId);

        reference.set(messageDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        createSendRequestInstance();
                        //sendPushNotification();
                        Log.d("debug", "test 2");

                        HashMap<String, Object> timeAndRead = new HashMap<>();
                        timeAndRead.put("time", new Timestamp(new Date()));
                        timeAndRead.put("read", true);


                        DocumentReference addTimeReference = db.collection("users").document(user.getEmail()).collection("messages").document(getIntent().getStringExtra("getEmail"));
                        addTimeReference.set(timeAndRead).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void aVoid) {

                            }
                        });




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MessagingActivity.this, "Failed: " + e, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: " + e );

                    }
                });


    }

    private void createSendRequestInstance() {

        messageDetails = new HashMap<>();

        messageDetails.put("type", "from");
        messageDetails.put("time", new Timestamp(new Date()));
        messageDetails.put("messageText", addPostText);
        messageDetails.put("messageImageLink", "");



        //Map<String, Object> setDetails = new HashMap<>();
        //setDetails.put("requestFrom:-" + user.getDisplayName() + "-for:-" + postId, requestDetails);

        Log.e("debug", "sending to receiver");

        reference = db.collection("users").document(getIntent().getStringExtra("getEmail")).collection("messages").document(user.getEmail()).collection("messageBox").document(messageId);;
        reference.set(messageDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        HashMap<String, Object> timeAndRead = new HashMap<>();
                        timeAndRead.put("time", new Timestamp(new Date()));
                        timeAndRead.put("read", false);

                        //sendPushNotification();
                        DocumentReference addTimeReference = reference = db.collection("users").document(getIntent().getStringExtra("getEmail")).collection("messages").document(user.getEmail());
                        addTimeReference.set(timeAndRead).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void aVoid) {
                                sendMessagePushNotification();
                            }
                        });
                        Log.e("debug", "sent to receiver");


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "onFailure: failed to send message" );

                    }
                });


    }

    private void getUserDetails() {

        DocumentReference documentReference = db.collection("users").document(getIntent().getStringExtra("getEmail"));
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                //threat = value.getBoolean("threat");
                //verified = value.getBoolean("verified");

                receiverUserName = value.getString("username");
                receiverProfileImageLink = value.getString("profileImageLink");


                receiverName.setText(value.getString("name"));
                token = value.getString("fcm-token");
                try {
                    receiverBio.setText(value.getString("bio").substring(0, 30) + "...");
                } catch (Exception e) {
                    receiverBio.setText(value.getString("bio"));
                }

                Glide.with(MessagingActivity.this)
                        .load(receiverProfileImageLink)
                        .centerCrop()
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(receiverProfileImage);



            }
        });
    }

    private void sendMessagePushNotification() {
        HashMap<String, String> map = new HashMap<>();

        map.put("senderName", user.getDisplayName());
        map.put("receiverName", receiverUserName);
        map.put("receiverToken", token);
        map.put("senderEmail", user.getEmail());
        map.put("senderProfilePicture", user.getPhotoUrl().toString());
        map.put("message", addPostText);
        try {
            map.put("time", messageTime(new Timestamp(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
            map.put("time", "Today");
        }


        Call<Void> call = retrofitInterface.executeMessagePushNotification(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    Log.e("message-push-status", "message push notification sent");
                }
                else if (response.code() == 400){
                    Log.e("message-push-status", "failure: msg push not sent");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MessagingActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();

                Log.e("error", t.getMessage());
                Log.e("error", t.getLocalizedMessage());

            }
        });

    }

    private void setupRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //set items to arrange from bottom
        // linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        try {
            linearLayoutManager.smoothScrollToPosition(recyclerView,null, messagesList.size()-1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteConvo:
                deleteConversation();
                return true;

            case R.id.report:
                ReportModal bottomSheet = new ReportModal();
                Bundle bundle = new Bundle();
                bundle.putString("postAuthorEmail", "ericx.group@gmail.com");
                bundle.putString("postAuthor", "Support");
                bundle.putString("postText", "Reporting conversation with " + receiverName.getText() + ".");
                bundle.putString("postImage", receiverProfileImageLink);
                bundle.putString("postId", receiverUserName);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "TAG");
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void deleteConversation() {




        msgRef = db.collection("users").document(user.getEmail()).collection("messages").document(getIntent().getStringExtra("getEmail"));

        msgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MessagingActivity.this, "Deleted conversation with " + receiverName.getText(), Toast.LENGTH_LONG).show();
                        onBackPressed();

                        int batchSize = 500;
                        Query query = db.collection("users").document(user.getEmail()).collection("messages").document(getIntent().getStringExtra("getEmail")).collection("messageBox").limit(batchSize);

                        try {
                            // retrieve a small batch of documents to avoid out-of-memory errors
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    int deleted = 0;

                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                    for (DocumentSnapshot document : documents) {
                                        document.getReference().delete();
                                        ++deleted;
                                    }
                                    if (deleted >= batchSize) {
                                        // retrieve and delete another batch
                                        deleteConversation();
                                    }

                                }
                            });
                        } catch (Exception e) {
                            System.err.println("Error deleting collection : " + e.getMessage());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessagingActivity.this, "Something went wrong\nPlease retry the action", Toast.LENGTH_SHORT).show();

                    }
                });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            registerQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String messageTime(Timestamp timestamp) throws ParseException {

        String time = "";

        Date date = timestamp.toDate();
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(date);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy, hh:mm aaa");
        SimpleDateFormat sdfYesterday = new SimpleDateFormat("hh:mm aaa");
        SimpleDateFormat sdfDays = new SimpleDateFormat("EEEE, hh:mm aaa");


        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday



        Calendar c2 = Calendar.getInstance();
        c2.setTime(date); // your date

        Calendar c3 = Calendar.getInstance(); // today
        c3.add(Calendar.DAY_OF_YEAR, 0); // today

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)){
            time = "Yesterday, " + sdfYesterday.format(date);
        }

        else if (ago.contains("month") || ago.contains("week")){
            //time = String.valueOf(sdf.parse(String.valueOf(date)));
            time = sdf.format(date);
        }

        else if(ago.contains("day")){
            time = sdfDays.format(date);
        }
        else if(ago.contains("hour")){

            if (c3.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c3.get(Calendar.DAY_OF_YEAR) == c3.get(Calendar.DAY_OF_YEAR)){
                time = sdfYesterday.format(date);
            }
            else {
                time = "Yesterday, " + sdfYesterday.format(date);
            }

        }
        else {
            time = ago;
        }
        return time;

    }


    @Override
    public void onMessageHold(int position, String messageId, String messageText, String messageEmail, String type) {


        Runnable copyMessageAction = new Runnable() {
            @Override
            public void run() {
                copyMessage(messageText);
            }
        };

        Runnable deleteForMeAction =  new Runnable() {
            @Override
            public void run() {
                deleteMessageForMe(messageEmail, messageId);
            }
        };

        Runnable unsendMessageAction =  new Runnable() {
            @Override
            public void run() {
                unsendMessage(messageEmail, messageId);
            }
        };

        Runnable shareAction =  new Runnable() {
            @Override
            public void run() {
                shareMessage(messageText);
            }
        };

        if (type.equals("to")){
            OptionsDialogModal optionsDialogModal = new OptionsDialogModal(this,"Share", shareAction, "Copy message", copyMessageAction, "Unsend message", unsendMessageAction, "Delete for me", deleteForMeAction);
        }
        else {
            OptionsDialogModal optionsDialogModal = new OptionsDialogModal(this,"Share", shareAction, "Copy message", copyMessageAction,  "Delete for me", deleteForMeAction);

        }


    }

    private void unsendMessage(String participant, String messageId) {

        DocumentReference myMsgRef = db.collection("users").document(user.getEmail()).collection("messages").document(participant).collection("messageBox").document(messageId);
        DocumentReference otherMsgRef = db.collection("users").document(participant).collection("messages").document(user.getEmail()).collection("messageBox").document(messageId);


        myMsgRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        otherMsgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MessagingActivity.this, "Message has been unsent.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessagingActivity.this, "Something went wrong\nPlease retry the action", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void copyMessage(String message) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", message);
        clipboard.setPrimaryClip(clip);

        MySnackBar snackBar = new MySnackBar(this, getWindow().getDecorView().findViewById(R.id.page), "Message copied to clipboard!", R.color.color_theme_blue, Snackbar.LENGTH_SHORT);

    }

    private void shareMessage(String message) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void deleteMessageForMe(String participant, String messageId){

        msgRef = db.collection("users").document(user.getEmail()).collection("messages").document(participant).collection("messageBox").document(messageId);

        msgRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MessagingActivity.this, "Message deleted.", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessagingActivity.this, "Something went wrong\nPlease retry the action", Toast.LENGTH_SHORT).show();

            }
        });



    }
}
