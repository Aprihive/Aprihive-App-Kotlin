// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.CatalogueItemDetails;
import com.aprihive.R;
import com.aprihive.models.CatalogueModel;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CatalogueRecyclerViewAdapter extends RecyclerView.Adapter<CatalogueRecyclerViewAdapter.MyViewHolder> {

    Context context;
    private List<CatalogueModel> itemList;
    public ListenerRegistration registerQuery;


    private String getItemName, getItemImageLink, getItemDescription, getItemId, getItemPrice, getItemUrl, getItemAuthor;


    public CatalogueRecyclerViewAdapter(Context context, List<CatalogueModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout(find_users_item)
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.catalogue_item, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);





        viewHolder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CatalogueItemDetails.class);


                intent.putExtra("getSellerEmail", itemList.get(viewHolder.getAbsoluteAdapterPosition()).getItemAuthor());
                intent.putExtra("getItemUrl", itemList.get(viewHolder.getAbsoluteAdapterPosition()).getItemUrl());
                intent.putExtra("getItemImageLink", itemList.get(viewHolder.getAbsoluteAdapterPosition()).getItemImageLink());
                intent.putExtra("getItemName", itemList.get(viewHolder.getAbsoluteAdapterPosition()).getItemName());
                intent.putExtra("getItemPrice", itemList.get(viewHolder.getAbsoluteAdapterPosition()).getItemPrice());
                intent.putExtra("getItemDescription", itemList.get(viewHolder.getAbsoluteAdapterPosition()).getItemDescription());
                intent.putExtra("getItemId", itemList.get(viewHolder.getAbsoluteAdapterPosition()).getItemId());

                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        getItemName = itemList.get(position).getItemName();
        getItemId = itemList.get(position).getItemId();
        getItemImageLink = itemList.get(position).getItemImageLink();
        getItemPrice = itemList.get(position).getItemPrice();
        getItemDescription = itemList.get(position).getItemDescription();
        getItemUrl = itemList.get(position).getItemUrl();
        getItemAuthor = itemList.get(position).getItemAuthor();


        holder.catalogueItemName.setText(getItemName);
        holder.catalogueItemDescription.setText(getItemDescription);
        holder.catalogueItemPrice.setText(getItemPrice);

        /*if (getItemAuthor.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            holder.moreIcon.setVisibility(View.VISIBLE);
        }*/


        Glide.with(context)
                .load(getItemImageLink)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.itemImage);



    }

    @Override
    public int getItemCount() {
            return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        CardView moreIcon;
        TextView catalogueItemName, catalogueItemDescription, catalogueItemPrice;
        ConstraintLayout itemContainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            moreIcon = itemView.findViewById(R.id.optionsIcon);
            catalogueItemName = itemView.findViewById(R.id.itemName);
            catalogueItemPrice = itemView.findViewById(R.id.itemPrice);
            catalogueItemDescription = itemView.findViewById(R.id.itemDescription);
            itemContainer = itemView.findViewById(R.id.containerItem);

        }
    }

    public interface MyClickListener {
        void onPostMenuClick(int position, String postId, String postAuthorEmail, String postText, String postImage);
    }






}
