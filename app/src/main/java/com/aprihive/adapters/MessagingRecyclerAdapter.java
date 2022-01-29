// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved

package com.aprihive.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aprihive.R;
import com.aprihive.models.MessageModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessagingRecyclerAdapter extends RecyclerView.Adapter<MessagingRecyclerAdapter.Viewholder> {

    private Context context;
    private List<MessageModel> messagesList;
    MyClickListener listener;

    public static final int ITEM_TYPE_FROM = 0;
    public static final int ITEM_TYPE_TO = 1;
    private Viewholder viewHolder;


    public MessagingRecyclerAdapter(Context context, List<MessageModel> messagesList, MyClickListener listener) {
        this.context = context;
        this.messagesList = messagesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);


        if (viewType == ITEM_TYPE_FROM){
            View view = inflater.inflate(R.layout.message_container_from,viewGroup, false);
            viewHolder = new MessagingRecyclerAdapter.Viewholder(view);
        } else if (viewType == ITEM_TYPE_TO) {
            View view = inflater.inflate(R.layout.message_container_to, viewGroup, false);
            viewHolder = new MessagingRecyclerAdapter.Viewholder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {


        holder.messageText.setText(messagesList.get(position).getMessageText());

        if (!messagesList.get(position).getMessageImageLink().isEmpty()){
            viewHolder.imageHolder.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(messagesList.get(position).getMessageImageLink())
                    .centerCrop()
                    .error(R.drawable.user_image_placeholder)
                    .fallback(R.drawable.user_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.messageImage);

        } else {
            viewHolder.imageHolder.setVisibility(View.GONE);
        }


        try {
            holder.messageTime.setText(messageTime((Timestamp) messagesList.get(position).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemViewType(int position) {

        String type = messagesList.get(position).getMessageType();

        if (type.equals("from")) {
            return ITEM_TYPE_FROM;
        } else {
            return ITEM_TYPE_TO;
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ConstraintLayout messageItem;
        MaterialCardView imageHolder;
        TextView messageText, messageTime;
        ImageView messageImage;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.messageText);
            messageTime =  itemView.findViewById(R.id.messageTime);
            messageItem =  itemView.findViewById(R.id.messageItem);
            messageImage =  itemView.findViewById(R.id.messageImage);
            imageHolder =  itemView.findViewById(R.id.messageImage_bg);


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




        if (ago.contains("1 day ago")){
            time = "Yesterday, " + sdfYesterday.format(date);
        }

        else if (ago.contains("month") || ago.contains("week")){
            //time = String.valueOf(sdf.parse(String.valueOf(date)));
            time = sdf.format(date);
        }

        else if(ago.contains("day")){
            time = sdfDays.format(date);
        }
        else {
            time = ago;
        }
        return time;

    }

    public interface MyClickListener {

    }
}
