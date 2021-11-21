package com.aprihive.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aprihive.R;
import com.aprihive.models.DiscoverPostsModel;
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
import java.util.Map;

public class DiscoverRecyclerAdapter extends RecyclerView.Adapter<DiscoverRecyclerAdapter.ViewHolder> {

    private static final String TAG = "debug" ;
    Context context;
    private List<DiscoverPostsModel> postList;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference reference;


    private String getFullname;
    private String getUsername;
    private String getPostImageLink;
    private String getPostText;
    private int getUpvotes;
    private String getAuthorEmail;
    private String getProfileImageUrl;
    private Boolean getVerified;
    public ListenerRegistration registerQuery, likeRegisterQuery;
    private String getPostId;

    private Boolean processLike = false;
    private DocumentReference likeRef;
    private MyClickListener listener;
    private String getPostTags;
    private String getPostTime;
    private String getLocation;


    public DiscoverRecyclerAdapter(Context context, List<DiscoverPostsModel> postList, MyClickListener listener) {
        this.context = context;
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout(find_users_item)
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.discover_post_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view, listener);


        viewHolder.upvoteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onVote(viewHolder.getAbsoluteAdapterPosition(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostId() );
            }
        });

        viewHolder.postText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTextExpandClick(viewHolder.getAbsoluteAdapterPosition(), viewHolder.postText);
            }
        });

        viewHolder.optionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPostMenuClick(viewHolder.getAbsoluteAdapterPosition(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostId(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getAuthorEmail(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostText(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostImageLink());
            }
        });

        viewHolder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClick(viewHolder.getAbsoluteAdapterPosition(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostImageLink());
            }
        });


        viewHolder.postFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProfileOpen(viewHolder.getAbsoluteAdapterPosition(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getAuthorEmail());
            }
        });

        viewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProfileOpen(viewHolder.getAbsoluteAdapterPosition(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getAuthorEmail());
            }
        });

        viewHolder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSendRequest(viewHolder.getAbsoluteAdapterPosition(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostId(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getAuthorEmail(), postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostText(),
                        postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostImageLink(),
                        postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostId().substring(0, postList.get(viewHolder.getAbsoluteAdapterPosition()).getPostId().length()-7));
            }
        });





        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        getAuthorEmail = postList.get(position).getAuthorEmail();
        getPostText = postList.get(position).getPostText();
        getLocation = postList.get(position).getLocation();
        getPostTime = postList.get(position).getTimePosted();
        getPostId = postList.get(position).getPostId();
        getPostImageLink = postList.get(position).getPostImageLink();
        getPostTags = postList.get(position).getPostTags();

        reference = db.collection("users").document(getAuthorEmail);
        registerQuery = reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                //get user details to post
                try {

                    getFullname = value.getString("name");
                    getUsername = value.getString("username");
                    getVerified = value.getBoolean("verified");
                    getProfileImageUrl = value.getString("profileImageLink");

                    holder.postFullName.setText(getFullname);
                    holder.postUsername.setText("@" + getUsername);

                    if (!getUsername.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                        holder.requestButton.setVisibility(View.VISIBLE);
                    }

                    //profile dp
                    Glide.with(context)
                            .load(getProfileImageUrl)
                            .centerCrop()
                            .error(R.drawable.user_image_placeholder)
                            .fallback(R.drawable.user_image_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.profileImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //verification icon
                try {
                    if (getVerified){
                        holder.verifiedIcon.setVisibility(View.VISIBLE);
                    } else {
                        holder.verifiedIcon.setVisibility(View.GONE);
                    }
                } catch (Exception e){
                    //nothing
                }

            }
        });



        //additions



        likeRef = db.collection("upvotes").document(getPostId);
        likeRegisterQuery =  likeRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        holder.upvoteIcon.setColorFilter(context.getResources().getColor(R.color.color_success_green_200));
                    }
                    else {
                        holder.upvoteIcon.setColorFilter(context.getResources().getColor(R.color.grey_color_100));
                    }




                    try {
                        Map<String, Object> map = value.getData();
                        getUpvotes = map.size();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }


                    if (getUpvotes == 1){
                        holder.trustedByText.setText("  Upvoted by " + getUpvotes + " person");
                    }
                    else {
                        holder.trustedByText.setText("  Upvoted by " + getUpvotes + " people");
                    }

                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }


            }
        });






        //end




        holder.postText.setText(getPostText);
        holder.postTime.setText(getPostTime + " ");

        if (getPostImageLink == "" || getPostImageLink == null) {
            holder.postImage.setVisibility(View.GONE);
        } else {
            //post image
            holder.postImage.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(getPostImageLink)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.postImage);
        }



    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView profileImage, verifiedIcon, postImage, upvoteIcon;
        private CardView optionsIcon;
        private TextView postFullName, postUsername, postText,trustedByText, requestButton, postTime;
        private ConstraintLayout postItem;

        public ViewHolder(@NonNull View itemView, MyClickListener listener) {
            super(itemView);


            profileImage = itemView.findViewById(R.id.post_profileImage);
            verifiedIcon = itemView.findViewById(R.id.post_verifiedIcon);
            postFullName = itemView.findViewById(R.id.post_fullName);
            postUsername = itemView.findViewById(R.id.post_username);
            postText = itemView.findViewById(R.id.post_text);
            postItem = itemView.findViewById(R.id.postItem);
            postTime = itemView.findViewById(R.id.postTime);
            postImage = itemView.findViewById(R.id.postImage);
            optionsIcon = itemView.findViewById(R.id.optionsIcon);
            trustedByText = itemView.findViewById(R.id.trustedBy);
            requestButton = itemView.findViewById(R.id.requestButton);
            upvoteIcon = itemView.findViewById(R.id.upvoteIcon);



        }


    }

    public interface MyClickListener {
        void onVote(int position, String postId);
        void onSendRequest(int position, String postId, String postAuthorEmail, String postText, String postImage, String postAuthor);
        void onPostMenuClick(int position, String postId, String postAuthorEmail, String postText, String postImage);
        void onTextExpandClick(int position, TextView textView);
        void onProfileOpen(int position, String postAuthor);
        void onImageClick(int position, String postimage);

    }


    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        likeRegisterQuery.remove();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        try {
            likeRegisterQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<DiscoverPostsModel> filterList) {

        // below line is to add our filtered list in our course array list.
       postList = filterList;

        // below line is to notify our adapter of change in recycler view data.
        notifyDataSetChanged();
    }



}
