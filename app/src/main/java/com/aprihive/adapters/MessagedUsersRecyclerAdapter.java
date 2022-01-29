// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved

package com.aprihive.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aprihive.MessagingActivity;
import com.aprihive.R;
import com.aprihive.UserProfileActivity;
import com.aprihive.models.MessagedUsersModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MessagedUsersRecyclerAdapter extends RecyclerView.Adapter<MessagedUsersRecyclerAdapter.ViewHolder> {

    Context context;
    List<MessagedUsersModel> userList;
    MyClickListener listener;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String usersName;
    private String usersImageLink;
    private String usersBio;
    private boolean threat, verified;
    private ListenerRegistration registerQuery;
    private ListenerRegistration lastMessageRegQuery;

    public MessagedUsersRecyclerAdapter(Context context, List<MessagedUsersModel> userList, MyClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //inflate layout(find_users_item)
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.messaged_users_item, viewGroup, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        final MessagedUsersRecyclerAdapter.ViewHolder viewHolder = new MessagedUsersRecyclerAdapter.ViewHolder(view);

        viewHolder.userItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onOpen(viewHolder.getAbsoluteAdapterPosition(), userList.get(viewHolder.getAbsoluteAdapterPosition()).getReceiverEmail());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        DocumentReference documentReference = db.collection("users").document(userList.get(i).getReceiverEmail());
        registerQuery = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                usersName = value.getString("name");
                usersImageLink = value.getString("profileImageLink");
                usersBio = value.getString("bio");
                threat = value.getBoolean("threat");
                verified = value.getBoolean("verified");

                viewHolder.fullName.setText(usersName);

                //profile dp
                Glide.with(context)
                        .load(usersImageLink)
                        .centerCrop()
                        .error(R.drawable.user_image_placeholder)
                        .fallback(R.drawable.user_image_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.profileImage);

                //verification icon
                try {
                    if (verified){
                        viewHolder.verifiedIcon.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.verifiedIcon.setVisibility(View.GONE);
                    }
                } catch (Exception e){
                    //nothing
                }

                //threat icon
                try {
                    if (threat){
                        viewHolder.threatIcon.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.threatIcon.setVisibility(View.GONE);
                    }
                } catch (Exception e){
                    //nothing
                }

            }
        });

        Query lastMessageQuery = db.collection("users").document(user.getEmail()).collection("messages").document(userList.get(i).getReceiverEmail()).collection("messageBox").orderBy("time", Query.Direction.DESCENDING).limit(1);

        lastMessageRegQuery = lastMessageQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                try {
                    for (DocumentSnapshot details : value.getDocuments()){
                        viewHolder.lastMessage.setText(details.getString("messageText"));
                        try {
                            viewHolder.lastMessageTime.setText(lastMessageTime(details.getTimestamp("time")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });





    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private String lastMessageTime(Timestamp timestamp) throws ParseException {

        String time = "";

        Date date = timestamp.toDate();
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(date);

        SimpleDateFormat sdfToday = new SimpleDateFormat("hh:mm aaa");
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yy");


        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date); // your date


        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)){
            time = "yesterday";
        }

        else if(ago.contains("day")){
            time = sdfDate.format(date);
        }

        else if (ago.contains("month") || ago.contains("week")){
            //time = String.valueOf(sdf.parse(String.valueOf(date)));
            time = sdfDate.format(date);
        }

        else {
            time = sdfToday.format(date);
        }
        return time;

    }


    public class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView profileImage, verifiedIcon, threatIcon;
        TextView fullName, lastMessage, lastMessageTime;
        ConstraintLayout userItem;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.messaged_fullName);
            profileImage = itemView.findViewById(R.id.messaged_profileImage);
            verifiedIcon = itemView.findViewById(R.id.messaged_verifiedIcon);
            threatIcon = itemView.findViewById(R.id.messaged_warningIcon);
            lastMessage = itemView.findViewById(R.id.messaged_recent);
            lastMessageTime = itemView.findViewById(R.id.messaged_recentTime);
            userItem = itemView.findViewById(R.id.userItem);

        }
    }

    public interface MyClickListener {
        void onOpen(int position, String getEmail);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        try {
            registerQuery.remove();
            lastMessageRegQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
