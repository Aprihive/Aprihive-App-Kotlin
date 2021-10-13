package com.erlite.aprihive.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.erlite.aprihive.R;
import com.erlite.aprihive.UserProfileActivity;
import com.erlite.aprihive.models.FindModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FindRecyclerViewAdapter extends RecyclerView.Adapter<FindRecyclerViewAdapter.MyViewHolder> {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference reference;
    
    Context context;
    private List<FindModel> userList;

    private String getFullname, getUsername, getBio, getSchoolName, getProfileImageUrl;
    private Boolean getVerified;
    private ListenerRegistration likeRegisterQuery;
    private String getUserEmail;
    private MyClickListener listener;


    public FindRecyclerViewAdapter(Context context, List<FindModel> userList, MyClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout(find_users_item)
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.find_users_item, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);





        viewHolder.userItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!userList.get(viewHolder.getAbsoluteAdapterPosition()).getUsername().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {

                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("getEmail", userList.get(viewHolder.getAbsoluteAdapterPosition()).getEmail());
                    intent.putExtra("getVerified", userList.get(viewHolder.getAbsoluteAdapterPosition()).getVerified());
                    intent.putExtra("getPhone", userList.get(viewHolder.getAbsoluteAdapterPosition()).getPhone());
                    intent.putExtra("getTwitter", userList.get(viewHolder.getAbsoluteAdapterPosition()).getTwitter());
                    intent.putExtra("getInstagram", userList.get(viewHolder.getAbsoluteAdapterPosition()).getInstagram());
                    intent.putExtra("getUsername", userList.get(viewHolder.getAbsoluteAdapterPosition()).getUsername());
                    intent.putExtra("getFullName", userList.get(viewHolder.getAbsoluteAdapterPosition()).getFullname());
                    intent.putExtra("getProfileImageUrl", userList.get(viewHolder.getAbsoluteAdapterPosition()).getProfileImageUrl());
                    intent.putExtra("getBio", userList.get(viewHolder.getAbsoluteAdapterPosition()).getBio());
                    intent.putExtra("getSchoolName", userList.get(viewHolder.getAbsoluteAdapterPosition()).getSchoolName());
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFollow(viewHolder.getAbsoluteAdapterPosition(), userList.get(viewHolder.getAbsoluteAdapterPosition()).getEmail());
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getFullname = userList.get(position).getFullname();
        getUserEmail = userList.get(position).getEmail();
        getSchoolName = userList.get(position).getSchoolName();
        getUsername = userList.get(position).getUsername();
        getProfileImageUrl = userList.get(position).getProfileImageUrl();
        getBio = userList.get(position).getBio();
        getVerified = userList.get(position).getVerified();

        holder.findFullName.setText(getFullname);
        holder.findBio.setText(getBio);
        holder.findUsername.setText("@" + getUsername);

        if (getUsername.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
            holder.followButton.setVisibility(View.INVISIBLE);
        }


        checkIfUserIsInFavourites(holder);

        Glide.with(context)
                .load(getProfileImageUrl)
                .centerCrop()
                .error(R.drawable.user_image_placeholder)
                .fallback(R.drawable.user_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.profileImage);

        if (getVerified){
            holder.verifiedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.verifiedIcon.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
            return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImage, verifiedIcon;
        TextView findFullName, findUsername, findBio, followButton;
        ConstraintLayout userItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.find_profileImage);
            verifiedIcon = itemView.findViewById(R.id.find_verifiedIcon);
            followButton = itemView.findViewById(R.id.connectButton);
            findFullName = itemView.findViewById(R.id.find_fullName);
            findUsername = itemView.findViewById(R.id.find_username);
            findBio = itemView.findViewById(R.id.find_bio);
            userItem = itemView.findViewById(R.id.userItem);

        }
    }

    public interface MyClickListener {
        void onFollow(int position, String userEmail);

    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<FindModel> filterList) {

        // below line is to add our filtered list in our course array list.
        userList = filterList;

        // below line is to notify our adapter of change in recycler view data.
        notifyDataSetChanged();
    }

    private void checkIfUserIsInFavourites(MyViewHolder holder) {

        reference = db.collection("users").document(getUserEmail).collection("lists").document("following");
        likeRegisterQuery =  reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                try {
                    assert user != null;
                    String uid = user.getUid();

                    Boolean check = false;

                    try {
                        check = value.contains(uid);
                    }
                    catch (Exception e){

                    }


                    if (check){
                        holder.followButton.setBackground(context.getResources().getDrawable(R.drawable.connect_active_button));
                        holder.followButton.setText("Following");
                        holder.followButton.setTextColor(context.getResources().getColor(R.color.bg_color));
                    }
                    else {
                        holder.followButton.setBackground(context.getResources().getDrawable(R.drawable.connect_default_button));
                        holder.followButton.setText("Follow");
                        holder.followButton.setTextColor(context.getResources().getColor(R.color.color_theme_blue));
                    }

                    /*
                    try {
                        Map<String, Object> map = value.getData();
                        getUpvotes = map.size();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (getUpvotes == 1){
                        holder.trustedByText.setText("  Trusted by " + getUpvotes + " person");
                    }
                    else {
                        holder.trustedByText.setText("  Trusted by " + getUpvotes + " people");
                    }
                    */

                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

    }



}
