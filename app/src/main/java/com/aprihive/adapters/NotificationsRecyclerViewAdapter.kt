// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved

package com.aprihive.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aprihive.R;
import com.aprihive.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.MyViewHolder> {

    private final MyClickListener listener;
    Context context;
    private List<NotificationModel> notificationList;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference reference;

    private String getType;
    private String getSenderEmail;
    private String getDeadline;
    private String getPostId;
    private String getPostImageLink;
    private String getPostText;
    private String getNotificationDate;

    private String getFullname;
    private String getUsername;
    private String getProfileImageUrl;
    private ListenerRegistration registerQuery;
    private String senderProfileImage;
    private String senderFullname;
    private String senderUsername;
    private String getRequestText;
    private String getAuthorUsername;
    private String receiverUsername;


    public NotificationsRecyclerViewAdapter(Context context, List<NotificationModel> notificationList, MyClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout(find_users_item)
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.notification_item, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);



        viewHolder.notificationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOpenRequestDetails(viewHolder.getAbsoluteAdapterPosition(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getType(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getSenderUsername(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getReceiverUsername(),
                        notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getDeadline(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getPostId(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getPostImageLink(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getPostText(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getRequestText(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getRequestedOn(),
                        notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getSenderEmail(), notificationList.get(viewHolder.getAbsoluteAdapterPosition()).getAuthorEmail());
            }
        });



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getType = notificationList.get(position).getType();
        getSenderEmail = notificationList.get(position).getSenderEmail();
        senderUsername = notificationList.get(position).getSenderUsername();
        receiverUsername = notificationList.get(position).getReceiverUsername();
        getDeadline = notificationList.get(position).getDeadline();
        getPostId = notificationList.get(position).getPostId();
        getPostImageLink = notificationList.get(position).getPostImageLink();
        getRequestText = notificationList.get(position).getRequestText();
        getNotificationDate = notificationList.get(position).getRequestedOn();






        if (getType.equals("from")){
            holder.typeIcon.setImageResource(R.drawable.ic_arrow_down);
            holder.typeIcon.setColorFilter(context.getResources().getColor(R.color.color_success_green_500));
            holder.typeIconBg.setCardBackgroundColor(context.getResources().getColor(R.color.color_success_green_050));
            holder.notificationTitle.setText(senderUsername.substring(0, 1).toUpperCase() + senderUsername.substring(1).toLowerCase() + " sent you a request!");
        } else if (getType.equals("to")){
            holder.typeIcon.setImageResource(R.drawable.ic_arrow_up);
            holder.notificationTitle.setText("You sent " + receiverUsername + " a request.");

        }

        holder.notificationDate.setText(getNotificationDate);
        holder.notificationSubTitle.setText(getRequestText);




    }

    @Override
    public int getItemCount() {
            return notificationList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView typeIcon;
        CardView typeIconBg;
        TextView notificationTitle, notificationSubTitle, notificationDate;
        ConstraintLayout notificationItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            
            typeIcon = itemView.findViewById(R.id.notification_typeIcon);
            typeIconBg = itemView.findViewById(R.id.notification_typeIconBg);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationSubTitle = itemView.findViewById(R.id.notificationSubTitle);
            notificationDate = itemView.findViewById(R.id.notificationDate);
            notificationItem = itemView.findViewById(R.id.notificationItem);

        }
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<NotificationModel> filterList) {

        // below line is to add our filtered list in our course array list.
        notificationList = filterList;

        // below line is to notify our adapter of change in recycler view data.
        notifyDataSetChanged();
    }


    public interface MyClickListener {
        void onOpenRequestDetails(int position, String getType, String getSenderUsername, String getReceiverUsername, String getDeadline, String getPostId, String getPostImageLink, String getPostText, String getRequestText, String getRequestedOn, String getSenderEmail, String getReceiverEmail);
    }


}
