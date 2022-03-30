// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprihive.R;
import com.aprihive.backend.RetrofitInterface;
import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.NetworkListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SendRequestModal extends BottomSheetDialogFragment {

    private EditText emailInput;
    private Button submit;
    private TextView info, title;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;
    private NetworkListener networkListener;
    private ConstraintLayout loading, closeScreen, forgotPasswordScreen;


    private String email;
    private EditText addText;
    private ImageView imagePreview;
    private  String saveTo, imageType;
    private Uri imageUri;
    private StorageReference storageReference;
    private String postImageLink;
    private Random random;
    private String addPostText;
    private String postId;
    private Context context;
    private Map<String, Object>requestDetails;
    private LayoutInflater myInflater;
    private ViewGroup myContainer;
    private Bundle mySavedInstance;
    private DocumentReference likeRef;
    private Map<String, Object> setLikes;
    private TextView previewText;
    private EditText deadline;
    private TextView selectDate;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TextView modalSubtitle;
    private String sendTo;
    private String sendToAuthorEmail;
    private String deadlineText;
    private View homeView;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private SharedPreferences sharedPreferences;





    public SendRequestModal() {
       //required constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_send_request_modal, container, false);

        context = getActivity().getApplicationContext();
        homeView = getActivity().getWindow().getDecorView().findViewById(R.id.page);

        sharedPreferences = context.getSharedPreferences("aprihive", Context.MODE_PRIVATE);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);






        addText = view.findViewById(R.id.addText);
        previewText = view.findViewById(R.id.previewText);
        submit = view.findViewById(R.id.submitBtn);
        loading = view.findViewById(R.id.loading);
        info = view.findViewById(R.id.errorFeedback);
        deadline = view.findViewById(R.id.deadline);
        selectDate = view.findViewById(R.id.selectDate);
        modalSubtitle = view.findViewById(R.id.modalSubtitle);
        sendTo = getArguments().getString("postAuthor");
        sendToAuthorEmail = getArguments().getString("postAuthorEmail");
        postId = getArguments().getString("postId");

        previewText.setText(getArguments().getString("postText"));
        modalSubtitle.setText("Send a request to "+ sendTo +" for this service");

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR); // Initial year selection
                int month =  calendar.get(Calendar.MONTH); // Initial month selection
                int day = calendar.get(Calendar.DAY_OF_MONTH); // Inital day selection
                


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), onDateSetListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                //datePickerDialog.getDatePicker().setMaxDate(maxDateConfig.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        onDateSetListener  = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker datePicker, int year, int getMonth , int day) {
                int month = getMonth + 1;
                String date = day + "/" + month + "/" + year;
                deadline.setText(date);

            }
        };

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });


        return view;
    }

    private void checkInputs(){

        addPostText = addText.getText().toString().trim();
        deadlineText = deadline.getText().toString().trim();

        //check email
        if (addPostText.isEmpty()) {
            info.setText("Please add a short brief");
            info.setVisibility(View.VISIBLE);
        }

        else{
            uploadRequest();
            submit.setVisibility(View.INVISIBLE);
            info.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            Log.d("debug", "test 0");

        }

    }

    private void uploadRequest() {



        requestDetails = new HashMap<>();

        requestDetails.put("type", "from");
        requestDetails.put("postId", postId);
        requestDetails.put("authorEmail", getArguments().getString("postAuthorEmail"));
        requestDetails.put("receiverUsername", sendTo);
        requestDetails.put("senderUsername", user.getDisplayName());
        requestDetails.put("senderEmail", user.getEmail());
        requestDetails.put("requestText", addPostText);
        requestDetails.put("postText", getArguments().getString("postText"));
        requestDetails.put("postImageLink", getArguments().getString("postImage"));
        requestDetails.put("requested on", new Timestamp(new Date()));
        requestDetails.put("deadLine", deadlineText);

        //Map<String, Object> setDetails = new HashMap<>();
        //setDetails.put("requestFrom:-" + user.getDisplayName() + "-for:-" + postId, requestDetails);

        Log.d("debug", "test 1");

        reference = db.collection("users").document(sendToAuthorEmail).collection("requests").document("requestFrom:-" + user.getDisplayName() + "-for:-" + postId);
        reference.set(requestDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                createPersonalRequestInstance();
                sendNotifications();
                Log.d("debug", "test 2");



            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MySnackBar snackBar = new MySnackBar(getActivity().getApplicationContext(), homeView, "failed", R.color.color_error_red_100, Snackbar.LENGTH_LONG);
                dismiss();
                Log.d("debug", "test 3" + e);

            }
        });



    }

    private void sendNotifications() {
        HashMap<String, String> map = new HashMap<>();

        map.put("token", getArguments().getString("token"));
        map.put("senderEmail", user.getEmail());
        map.put("receiverEmail", getArguments().getString("postAuthorEmail"));
        map.put("senderUsername", user.getDisplayName());
        map.put("receiverUsername", sendTo);
        map.put("postId", postId);
        map.put("postText", getArguments().getString("postText"));
        map.put("deadline", deadlineText);
        map.put("requestId", "requestTo:-" + sendTo + "-for:-" + postId);


        Call<Void> call = retrofitInterface.executeRequestNotification(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    Log.e("request-push-status", "sent");
                }
                else if (response.code() == 400){
                    Log.e("request-push-status", "failure: not sent");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();

                Log.e("error", t.getMessage());
                Log.e("error", t.getLocalizedMessage());

            }
        });

        Call<Void> callPushNotifications = retrofitInterface.executeRequestPushNotification(map);

        callPushNotifications.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    Log.e("push-notify-status", "sent");
                }
                else if (response.code() == 400){
                    Log.e("push-notify-status", "failure: not sent");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.e("error", t.getMessage());
                Log.e("error", t.getLocalizedMessage());

            }
        });

    }

    private void createPersonalRequestInstance() {

        requestDetails = new HashMap<>();

        requestDetails.put("type", "to");
        requestDetails.put("postId", postId);
        requestDetails.put("authorEmail", getArguments().getString("postAuthorEmail"));
        requestDetails.put("receiverUsername", sendTo);
        requestDetails.put("senderUsername", user.getDisplayName());
        requestDetails.put("senderEmail", user.getEmail());
        requestDetails.put("requestText", addPostText);
        requestDetails.put("postText", getArguments().getString("postText"));
        requestDetails.put("postImageLink", getArguments().getString("postImage"));
        requestDetails.put("requested on", new Timestamp(new Date()));
        requestDetails.put("deadLine", deadlineText);

       // Map<String, Object> setDetails = new HashMap<>();
       // setDetails.put("request-to:_" + sendTo + "_for:_" + postId, requestDetails);

        reference = db.collection("users").document(user.getEmail()).collection("requests").document("requestTo:-" + sendTo + "-for:-" + postId);
        reference.set(requestDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {



                MySnackBar snackBar = new MySnackBar(getActivity().getApplicationContext(), homeView, "Request sent to " + sendTo + " successfully", R.color.color_theme_blue, Snackbar.LENGTH_LONG);
                dismiss();

            }
        });

    }



}
