// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.fragments;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aprihive.R;
import com.aprihive.backend.RetrofitInterface;
import com.aprihive.methods.MyActionDialog;
import com.aprihive.methods.MySnackBar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostOptionsModal extends BottomSheetDialogFragment {

    private final Runnable postsRefresh;
    private ConstraintLayout editPostClick, deletePostClick, copyPostClick, sharePostClick, removeImageClick;
    private TextView postIdDisplay;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String fetchPostId;
    private StorageReference storageReference;
    private Bundle bundle;
    private Context context;
    private String fetchPostImage;
    private Runnable action;
    private Runnable removeImageAction;
    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;

    public PostOptionsModal(Runnable postsRefresh) {
        this.postsRefresh = postsRefresh;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        
        View view = inflater.inflate(R.layout.bottom_sheet_post_options, container, false);



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);


        final Activity activity = getActivity();
        context = getActivity().getApplicationContext();


        //create runnable action to pass as argument to the custom dialog modal
        action = new Runnable() {
            @Override
            public void run() {
                confirmDelete();
            }
        };

        removeImageAction = new Runnable() {
            @Override
            public void run() {
                confirmImageRemove();
            }
        };


        editPostClick = view.findViewById(R.id.editPostClick);
        removeImageClick = view.findViewById(R.id.removeImageClick);
        deletePostClick = view.findViewById(R.id.deletePostClick);
        TextView deletePostText = view.findViewById(R.id.textView3);
        copyPostClick = view.findViewById(R.id.copyPostClick);
        sharePostClick = view.findViewById(R.id.sharePostClick);
        postIdDisplay = view.findViewById(R.id.postId);

        fetchPostId =  getArguments().getString("postId");
        fetchPostImage = getArguments().getString("postImage");

        postIdDisplay.setText(fetchPostId);

       if (getArguments().getString("postAuthorEmail").equals(user.getEmail())){
            deletePostClick.setVisibility(View.VISIBLE);
            //editPostClick.setVisibility(View.VISIBLE);
        }

        else if (getArguments().getBoolean("isAdmin")){
            deletePostText.setText("Delete (Admin)");
            deletePostClick.setVisibility(View.VISIBLE);
            if (!fetchPostImage.equals("")){
                removeImageClick.setVisibility(View.VISIBLE);
            }
        }




        copyPostClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("post", getArguments().getString("postText"));
                clipboard.setPrimaryClip(clip);

                MySnackBar snackBar = new MySnackBar(context, getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "Copied to clipboard!", R.color.color_theme_blue, Snackbar.LENGTH_SHORT);
                dismiss();
            }
        });

        deletePostClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assert getArguments() != null;
                if (getArguments().getString("postAuthorEmail").equals(user.getEmail())){
                    MyActionDialog dialog = new MyActionDialog(getContext(), "Delete Post?", "Are you sure you want to delete this post?", R.drawable.vg_delete, action);
                    dialog.show();
                }
                else if (getArguments().getBoolean("isAdmin")){
                    MyActionDialog dialog = new MyActionDialog(getContext(), "Delete this Post?", "Are you sure you want to delete this post for violation of guidelines?", R.drawable.vg_delete, action);
                    dialog.show();
                }


            }
        });

        removeImageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                    MyActionDialog dialog = new MyActionDialog(getContext(), "Remove Image?", "Are you sure you want to remove this post's image for containing graphic violence or explicit content? \n (This action cannot be undone!)", R.drawable.vg_delete, removeImageAction, "Yes, remove.", "No, cancel.");
                    dialog.show();

            }
        });






        return view;
    }

    private void confirmDelete() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //delete post itself
        DocumentReference postReference = db.collection("posts").document(fetchPostId);
        postReference.delete();

        //delete image if any
        if (!fetchPostImage.equals("")){
            StorageReference fileRef = storageReference.child("posts-images").child(fetchPostId+"_postImage.jpg");
            fileRef.delete();
        }

        if (getArguments().getBoolean("isAdmin") && !getArguments().getString("postAuthorEmail").equals(user.getEmail())){
           sendDeleteNotification();
        }


        //delete post upvotes document
        DocumentReference postLikesReference = db.collection("upvotes").document(fetchPostId);
        postLikesReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void aVoid) {
                MySnackBar snackBar = new MySnackBar(context, getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "Delete Successful", R.color.color_theme_blue, Snackbar.LENGTH_SHORT);
                postsRefresh.run();
                dismiss();

            }
        });

    }

    private void sendDeleteNotification() {
        HashMap<String, String> map = new HashMap<>();

        map.put("receiverEmail", getArguments().getString("postAuthorEmail"));
        map.put("postId", fetchPostId);
        map.put("postText", getArguments().getString("postText"));

        Call<Void> call = retrofitInterface.executePostRemovalNotification(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    Log.d("email-status", "email sent");
                }
                else if (response.code() == 400){
                    Log.d("email-status", "failure: email not sent");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();

                Log.e("error", t.getMessage());
                Log.e("error", t.getLocalizedMessage());

            }
        });
    }

    private void confirmImageRemove() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        Map<String, Object> details = new HashMap<>();
        details.put("imageLink", "");

        //delete image itself
        DocumentReference postReference = db.collection("posts").document(fetchPostId);
        postReference.update(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void aVoid) {
                StorageReference fileRef = storageReference.child("posts-images").child(fetchPostId+"_postImage.jpg");
                fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void aVoid) {
                        MySnackBar snackBar = new MySnackBar(context, getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "Image removed successfully", R.color.color_theme_blue, Snackbar.LENGTH_SHORT);
                        postsRefresh.run();
                        dismiss();
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MySnackBar snackBar = new MySnackBar(context, getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "Something went wrong, Please try again.", R.color.color_error_red_200, Snackbar.LENGTH_SHORT);
                postsRefresh.run();
                dismiss();
            }
        });



    }


}
