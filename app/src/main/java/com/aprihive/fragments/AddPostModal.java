// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.aprihive.backend.RetrofitInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.R;
import com.aprihive.methods.MySnackBar;
import com.aprihive.methods.NetworkListener;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddPostModal extends BottomSheetDialogFragment {



    private final Runnable postsRefresh;
    private EditText emailInput;
    private Button submit, closeBtn;
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
    private MultiAutoCompleteTextView tags;
    private ImageView imagePreview;
    private  String saveTo, imageType;
    private Uri imageUri;
    private StorageReference storageReference;
    private String postImageLink;
    private Random random;
    private String addPostText;
    private String postId;
    private Context context;
    private Map<String, Object>postDetails;
    private LayoutInflater myInflater;
    private ViewGroup myContainer;
    private Bundle mySavedInstance;
    private DocumentReference likeRef;
    private Map<String, Object> setLikes;
    String[] tagsList={"Catering,","Graphics Design,","Development,","App development,","Animation,","Fashion Designing,","Trading,","Bitcoin,","Repairs,","Clothing,","Shoes,","Games,","Confectioneries,","Shoe making,","Tutorials,"};
    private String tagsText;
    private TextCrawler textCrawler;
    private String fetchLink;
    private HashMap<String, String> linkPreviewData;



    public AddPostModal(Runnable postsRefresh) {
        this.postsRefresh = postsRefresh;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_add_post_modal, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();



        myInflater = inflater;
        myContainer = container;
        mySavedInstance = savedInstanceState;

        context = getActivity().getApplicationContext();



        addText = view.findViewById(R.id.addText);
        tags = view.findViewById(R.id.tags);
        imagePreview = view.findViewById(R.id.imagePreview);
        submit = view.findViewById(R.id.submitBtn);
        loading = view.findViewById(R.id.loading);
        info = view.findViewById(R.id.errorFeedback);

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, tagsList);
        tags.setAdapter(adapter);
        tags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        random = new Random();

        linkPreviewData = new HashMap<String, String>();
        linkPreviewData.put("title", "");
        linkPreviewData.put("description", "");
        linkPreviewData.put("image", "");
        linkPreviewData.put("url", "");


        postId = String.valueOf(random.nextInt(1000000));


        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(3,2).setFixAspectRatio(true).start(getContext(), AddPostModal.this);
            }
        });

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
        tagsText = tags.getText().toString().trim();

        //check email
        if (addPostText.isEmpty()) {
            info.setText("Please add a description");
            info.setVisibility(View.VISIBLE);
        }

        else{
            uploadPost();
            submit.setVisibility(View.INVISIBLE);
            info.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

    }

    private void uploadPost() {


        postDetails = new HashMap<>();

        if (!(imageUri == null)){

            final StorageReference fileRef = storageReference.child("posts-images").child(saveTo);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            postDetails.put("imageLink", uri.toString());
                            proceed();

                        }
                    });

                }
            });

        }
        else {
            postDetails.put("imageLink", "");
            fetchLinkData();
        }


    }

    private void fetchLinkData(){
        //find link in post
        List<String> postLinks = new ArrayList<String>();
        Matcher m = Patterns.WEB_URL.matcher(addPostText);

        while (m.find()) {
            String url = m.group();
            postLinks.add(url);
        }


        if (!postLinks.isEmpty()){
            fetchLink = postLinks.get(0);
            textCrawler = new TextCrawler();
            LinkPreviewCallback lpCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {

                }

                @Override
                public void onPos(SourceContent metaData, boolean isNull) {
                    linkPreviewData.put("title", metaData.getTitle());
                    linkPreviewData.put("description", metaData.getDescription());
                    linkPreviewData.put("image", metaData.getImages().get(0));
                    linkPreviewData.put("url", metaData.getUrl());

                    postDetails.put("linkPreviewData", linkPreviewData);
                    proceed();
                }
            };
            textCrawler.makePreview(lpCallback, fetchLink);
        }
        else {
            proceed();
        }


    }

    private void proceed() {

        assert getArguments() != null;



        postDetails.put("name", getArguments().getString("fullname"));
        postDetails.put("tags", tagsText);
        postDetails.put("location", getArguments().getString("location"));
        postDetails.put("username", user.getDisplayName());
        postDetails.put("verified", getArguments().getBoolean("verified"));
        postDetails.put("postId", user.getDisplayName() + "_"  + postId);
        postDetails.put("userEmail", user.getEmail());
        postDetails.put("upvotes", "0");
        postDetails.put("postText", addPostText);
        postDetails.put("linkPreviewData", linkPreviewData);
        postDetails.put("created on", new Timestamp(new Date()));


        reference = db.collection("posts").document(user.getDisplayName() + "_"  + postId);
        reference.set(postDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                createLikeDb();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MySnackBar snackBar = new MySnackBar(getActivity().getApplicationContext(), getActivity().getWindow().getDecorView(), "Something went wrong. Please Try again", R.color.color_error_red_200, Snackbar.LENGTH_LONG);
                dismiss();
            }
        });

    }

    private void createLikeDb() {
        likeRef = db.collection("upvotes").document(user.getDisplayName() + "_"  + postId);
        setLikes = new HashMap<>();
        setLikes.put(user.getUid(), user.getEmail());

        likeRef.set(setLikes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismiss();
                postsRefresh.run();
                Log.d("Debug", "success stuff");
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
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        saveTo = user.getDisplayName() + "_"  + postId + "_postImage.jpg";


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK){
                imageUri = result.getUri();
                imagePreview.setPadding(0,0,0,0);



                Glide.with(this)  //2

                        .load(imageUri) //3
                        .centerCrop() //4
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fallback(R.drawable.user_image_placeholder) //7
                        .into(imagePreview); //8

                Log.d("checking stuffs", "the thing 66 reach here ooo");

                //save image to firebase passing the type of image to be profile image (this is my idea, so that i can only use one method everytime)
                //uploadImageToFirestore(imageUri, saveTo);
            }
        }

    }




}
