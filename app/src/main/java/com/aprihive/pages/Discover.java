package com.aprihive.pages;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aprihive.Home;
import com.aprihive.ImageViewActivity;
import com.aprihive.PersonalProfileActivity;
import com.aprihive.R;
import com.aprihive.UserProfileActivity;
import com.aprihive.adapters.DiscoverRecyclerAdapter;
import com.aprihive.fragments.PostOptionsModal;
import com.aprihive.fragments.SendRequestModal;
import com.aprihive.models.DiscoverPostsModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;


public class Discover extends Fragment implements DiscoverRecyclerAdapter.MyClickListener {

    TextView connectButton, callButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private List<DiscoverPostsModel> postList;

    private DiscoverRecyclerAdapter adapter;
    private Context context;

    private String  getPostId, getUpvotes, getPostText, getPostImageLink; //fetch from firebase into
    private Boolean getVerified; //fetch from firebase into
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fab;
    private ListenerRegistration registerQuery;
    private DiscoverPostsModel discoverModel;
    private String getPostUserEmail;
    private List<DiscoverPostsModel> postAuthorList;

    private String TAG = "debug";
    public static Runnable refreshPostsRunnable;
    private String getTags;
    private ConstraintLayout nothingImage;
    private String getLocation;
    private Timestamp getTime;
    private String fetchLink;
    private HashMap<String, String> getLinkData;
    private ShimmerFrameLayout shimmer;


    public Discover() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        recyclerView = view.findViewById(R.id.findRecyclerView);
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh);
        context = getActivity().getApplicationContext();
        nothingImage = view.findViewById(R.id.notFoundImage);
        shimmer = view.findViewById(R.id.shimmerLayout);

        shimmer.startShimmer();


        fab = getActivity().findViewById(R.id.fabAddPost);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0 && fab.isShown()){
                    fab.hide();
                }
                else if (dy < 0 && !fab.isShown()){
                    fab.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });



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




        getPostsFromBackend();


        return view;
    }

    private void setupRecyclerView() {


        if (!(getActivity() ==null)){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            //set items to arrange from bottom
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
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
                            getLinkData = (HashMap<String, String>) value.get("linkPreviewData");
                            discoverModel.setLinkData(getLinkData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            getTags = value.getString("tags");
                            discoverModel.setPostTags(getTags);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        postList.add(discoverModel);



                    }

                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    setupRecyclerView();
                    swipeRefresh.setRefreshing(false);


                }

            }
        });

        
        
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
    public void onSendRequest(int position, String postId, String postAuthorEmail, String postText, String postImage, String token, String postAuthor){
        SendRequestModal bottomSheet = new SendRequestModal();
        Bundle bundle = new Bundle();
        bundle.putString("postAuthorEmail", postAuthorEmail);
        bundle.putString("postAuthor", postAuthor);
        bundle.putString("postText", postText);
        bundle.putString("postImage", postImage);
        bundle.putString("postId", postId);
        bundle.putString("token", token);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onPostMenuClick(int position, String postId, String postAuthorEmail, String postText, String postImage){

        boolean admin = ((Home) getActivity()).isAdmin();

        PostOptionsModal bottomSheet = new PostOptionsModal(refreshPostsRunnable);
        Bundle bundle = new Bundle();
        bundle.putString("postAuthorEmail", postAuthorEmail);
        bundle.putString("postText", postText);
        bundle.putString("postId", postId);
        bundle.putString("postImage", postImage);
        bundle.putBoolean("isAdmin", admin);
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
        if (postAuthor.equals(user.getEmail())){
            Intent i = new Intent(context, PersonalProfileActivity.class);
            i.putExtra("getEmail", postAuthor);
            startActivity(i);
        }
        else {
            Intent i = new Intent(context, UserProfileActivity.class);
            i.putExtra("getEmail", postAuthor);
            startActivity(i);
        }

    }

    @Override
    public void onImageClick(int position, String postImage){
        Intent i = new Intent(context, ImageViewActivity.class);
        i.putExtra("imageUri", postImage);
        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        // Inflate the layout for this menu
        inflater.inflate(R.menu.discover_bar_menu, menu);

        //MenuItem filterItem = menu.findItem(R.id.action_filter);
//
        //PostsFilterModal bottomSheet = new PostsFilterModal();
        //bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search by keywords, tags, location etc.");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        EditText viewEditText =  searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        viewEditText.setHintTextColor(getResources().getColor(R.color.grey_color_100));
        viewEditText.setTextColor(getResources().getColor(R.color.color_text_blue_500));

        ImageView viewIcon =  searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        viewIcon.setColorFilter(getResources().getColor(R.color.color_text_blue_500));

        ImageView viewCloseIcon =  searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        viewCloseIcon.setColorFilter(getResources().getColor(R.color.color_text_blue_500));

        View viewBg =  searchView.findViewById(androidx.appcompat.R.id.search_plate);
        viewBg.setBackground(getResources().getDrawable(R.drawable.text_box));


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Home)getActivity()).hideHamburger();
                getActivity().findViewById(R.id.logoImageView).setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ((Home)getActivity()).showHamburger();
                getActivity().findViewById(R.id.logoImageView).setVisibility(View.VISIBLE);
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconified(true);
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter!=null){

                    filter(newText);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onLinkOpen(int position, String link){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

    private void filter(String text){
        // creating a new array list to filter our data.
        List<DiscoverPostsModel> filteredPostLists = new ArrayList<>();

        String query = text.toLowerCase();

        // running a for loop to compare elements.
        for (DiscoverPostsModel item : postList) {

            // checking if the entered string matched with any item of our recycler view.
            try {
                if (item.getPostTags().toLowerCase().contains(query)){
                    filteredPostLists.add(item);
                }
                else if (item.getPostId().toLowerCase().contains(query)){
                    filteredPostLists.add(item);
                }
                else if (item.getLocation().toLowerCase().contains(query)){
                    filteredPostLists.add(item);
                }
                else if (item.getPostText().toLowerCase().contains(query)){
                    filteredPostLists.add(item);
                }
                else if (item.getAuthorEmail().toLowerCase().contains(query)){
                    filteredPostLists.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (filteredPostLists.isEmpty()) {
            // if no item is added in filtered list we are displaying a toast message as no data found.
            adapter.filterList((ArrayList<DiscoverPostsModel>) filteredPostLists);
            nothingImage.setVisibility(View.VISIBLE);


        } else {

            // at last we are passing that filtered list to our adapter class.
            adapter.filterList((ArrayList<DiscoverPostsModel>) filteredPostLists);
            nothingImage.setVisibility(View.GONE);

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


