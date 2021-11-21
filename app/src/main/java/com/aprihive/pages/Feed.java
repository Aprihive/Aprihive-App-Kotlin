package com.aprihive.pages;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aprihive.ImageViewActivity;
import com.aprihive.R;
import com.aprihive.adapters.DiscoverRecyclerAdapter;
import com.aprihive.fragments.PostOptionsModal;
import com.aprihive.fragments.SendRequestModal;
import com.aprihive.models.DiscoverPostsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Feed extends Fragment implements DiscoverRecyclerAdapter.MyClickListener {
    
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private List<DiscoverPostsModel> postList;

    private DiscoverRecyclerAdapter adapter;
    private Context context;

    private String  getPostId, getLocation, getPostText, getPostImageLink; //fetch from firebase into
    private Boolean getVerified; //fetch from firebase into
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fab;
    private ListenerRegistration registerQuery;
    private DiscoverPostsModel discoverModel;
    private String getPostUserEmail;
    private List<DiscoverPostsModel> postAuthorList;

    private String TAG = "debug";
    public static Runnable refreshPostsRunnable;

    private TextView nothingText;
    private String profileEmail;
    private String getTags;
    private Timestamp getTime;

    public Feed() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.findRecyclerView);
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh);
        context = getActivity().getApplicationContext();
        nothingText = view.findViewById(R.id.nothingText);




        //to refresh posts after posting or deleting
        refreshPostsRunnable = new Runnable() {
            @Override
            public void run() {
                getPostsFromBackend();
            }
        };




        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.bg_color));
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.color_theme_green_100),  getResources().getColor(R.color.color_theme_green_300));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPostsFromBackend();
            }
        });

        postList = new ArrayList<>();
        adapter = new DiscoverRecyclerAdapter(getContext(), postList, this);
        Log.d(TAG, "attached adapter");



        profileEmail = container.getTag().toString();


        swipeRefresh.setRefreshing(true);
        getPostsFromBackend();



        return view;
    }

    private void setupRecyclerView() {

        assert getActivity() != null;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        //set items to arrange from bottom
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    public void getPostsFromBackend(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("checking posts", "ok talk");


        Query discoverQuery = db.collection("posts").orderBy("created on");

        discoverQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){


                    postList.clear();
                    for (DocumentSnapshot value : task.getResult()){


                        getLocation = value.getString("location");
                        getTime = value.getTimestamp("created on");
                        getPostText = value.getString("postText");
                        getPostImageLink = value.getString("imageLink");
                        getPostUserEmail = value.getString("userEmail");
                        getPostId = value.getString("postId");


                        discoverModel = new DiscoverPostsModel();
                        discoverModel.setAuthorEmail(getPostUserEmail);
                        discoverModel.setLocation(getLocation);
                        try {
                            discoverModel.setTimePosted(postTime(getTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        discoverModel.setPostText(getPostText);
                        discoverModel.setPostImageLink(getPostImageLink);
                        discoverModel.setPostId(getPostId);
                        try {
                            getTags = value.getString("tags");
                            discoverModel.setPostTags(getTags);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        postList.add(discoverModel);



                    }

                    setupRecyclerView();
                    swipeRefresh.setRefreshing(false);


                }

                filter(profileEmail);

            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        getPostsFromBackend();

    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            adapter.registerQuery.remove();
            adapter.likeRegisterQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            adapter.registerQuery.remove();
            adapter.likeRegisterQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVote(int position, String postId) {
        final Boolean[] processLike = {true};
        final DocumentReference fileRef = db.collection("upvotes").document(postId);
        fileRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                try {
                    if (processLike[0]){

                        if (value.contains(auth.getCurrentUser().getUid())){
                            fileRef.update(auth.getCurrentUser().getUid(), FieldValue.delete());
                            processLike[0] =false;
                        }
                        else {
                            fileRef.update(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail());
                            processLike[0] =false;
                        }


                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public void onSendRequest(int position, String postId, String postAuthorEmail, String postText, String postImage, String postAuthor){
        SendRequestModal bottomSheet = new SendRequestModal();
        Bundle bundle = new Bundle();
        bundle.putString("postAuthorEmail", postAuthorEmail);
        bundle.putString("postAuthor", postAuthor);
        bundle.putString("postText", postText);
        bundle.putString("postImage", postImage);
        bundle.putString("postId", postId);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onPostMenuClick(int position, String postId, String postAuthorEmail, String postText, String postImage){
        PostOptionsModal bottomSheet = new PostOptionsModal(refreshPostsRunnable);
        Bundle bundle = new Bundle();
        bundle.putString("postAuthorEmail", postAuthorEmail);
        bundle.putString("postText", postText);
        bundle.putString("postId", postId);
        bundle.putString("postImage", postImage);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onTextExpandClick(int position, TextView textView){

        if (textView.getMaxLines() < 4){
            textView.setMaxLines(Integer.MAX_VALUE);
        } else {
            textView.setMaxLines(3);
        }
    }

    @Override
    public void onProfileOpen(int position, String postAuthor){
        //nothing
    }

    @Override
    public void onImageClick(int position, String postImage){
        Intent i = new Intent(context, ImageViewActivity.class);
        i.putExtra("imageUri", postImage);
        startActivity(i);
    }

    private void filter(String text){
        // creating a new array list to filter our data.
        List<DiscoverPostsModel> filteredList = new ArrayList<>();

        String query = text.toLowerCase();


        // running a for loop to compare elements.
        for (DiscoverPostsModel item : postList) {



            // checking if the entered string matched with any item of our recycler view.
           if (item.getAuthorEmail().toLowerCase().contains(query)){
               filteredList.add(item);
           }

        }
        if (filteredList.isEmpty()) {
            adapter.filterList((ArrayList<DiscoverPostsModel>) filteredList);
            nothingText.setVisibility(View.VISIBLE);
        }
        else {

            nothingText.setVisibility(View.GONE);
            //at last we are passing that filtered list to our adapter class.
            adapter.filterList((ArrayList<DiscoverPostsModel>) filteredList);

        }

    }

    private String postTime(Timestamp timestamp) throws ParseException {

        String time = "";

        Date date = timestamp.toDate();
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(date);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");


        if (ago.contains("1 day ago")){
            time = "yesterday";
        }
        else if (ago.contains("month")){
            //time = String.valueOf(sdf.parse(String.valueOf(date)));
            time = sdf.format(date);
        }
        else {
            time = ago;
        }
        return time;

    }
}


