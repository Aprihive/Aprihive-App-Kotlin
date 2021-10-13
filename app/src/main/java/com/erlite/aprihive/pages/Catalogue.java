package com.erlite.aprihive.pages;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.erlite.aprihive.Home;
import com.erlite.aprihive.ImageViewActivity;
import com.erlite.aprihive.R;
import com.erlite.aprihive.UserProfileActivity;
import com.erlite.aprihive.adapters.CatalogueRecyclerViewAdapter;
import com.erlite.aprihive.fragments.AddPostModal;
import com.erlite.aprihive.fragments.PostOptionsModal;
import com.erlite.aprihive.fragments.SendRequestModal;
import com.erlite.aprihive.models.CatalogueModel;
import com.erlite.aprihive.models.FindModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;


public class Catalogue extends Fragment implements CatalogueRecyclerViewAdapter.MyClickListener {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private List<CatalogueModel> itemList;

    private CatalogueRecyclerViewAdapter adapter;
    private Context context;

    private String  getItemId, getItemName, getItemDescription, getItemImageLink, getItemPrice, getItemUrl; //fetch from firebase into
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fab;
    private ListenerRegistration registerQuery;
    private CatalogueModel catalogueModel;
    private String getItemAuthorEmail;
    private List<CatalogueModel> postAuthorList;

    private String TAG = "debug";
    public static Runnable refreshItemsRunnable;

    private TextView nothingText;
    private String profileEmail;


    public Catalogue() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.findRecyclerView);
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh);
        context = getActivity().getApplicationContext();
        nothingText = view.findViewById(R.id.nothingText);


        profileEmail = container.getTag().toString();


        //to refresh posts after posting or deleting
        refreshItemsRunnable = new Runnable() {
            @Override
            public void run() {
                getItemsFromBackend();
            }
        };




        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.bg_color));
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.color_theme_green_100),  getResources().getColor(R.color.color_theme_green_300));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getItemsFromBackend();
            }
        });

        itemList = new ArrayList<>();
        adapter = new CatalogueRecyclerViewAdapter(getContext(), itemList);
        Log.d(TAG, "attached adapter");

        


        swipeRefresh.setRefreshing(true);
        getItemsFromBackend();



        return view;
    }

    private void setupRecyclerView() {

        if (itemList.isEmpty()){
            nothingText.setVisibility(View.VISIBLE);
        } else {
            nothingText.setVisibility(View.GONE);

        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        //set items to arrange from bottom
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    public void getItemsFromBackend(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("checking posts", "ok talk");



        Query itemQuery = db.collection("users").document(profileEmail).collection("catalogue").orderBy("created on", Query.Direction.DESCENDING);

        itemQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){


                    itemList.clear();
                    for (DocumentSnapshot value : task.getResult()){


                        getItemName = value.getString("name");
                        getItemDescription = value.getString("description");
                        getItemImageLink = value.getString("imageLink");
                        getItemAuthorEmail = value.getString("userEmail");
                        getItemId = value.getString("itemId");
                        getItemPrice = value.getString("itemPrice");
                        getItemUrl = value.getString("itemUrl");




                        catalogueModel = new CatalogueModel();
                        catalogueModel.setItemAuthor(getItemAuthorEmail);
                        catalogueModel.setItemName(getItemName);
                        catalogueModel.setItemDescription(getItemDescription);
                        catalogueModel.setItemImageLink(getItemImageLink);
                        catalogueModel.setItemId(getItemId);
                        catalogueModel.setItemPrice(getItemPrice);
                        catalogueModel.setItemUrl(getItemUrl);



                        itemList.add(catalogueModel);


                    }




                    setupRecyclerView();
                    swipeRefresh.setRefreshing(false);


                }

            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        getItemsFromBackend();

    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            adapter.registerQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            adapter.registerQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onPostMenuClick(int position, String postId, String postAuthorEmail, String postText, String postImage){
        PostOptionsModal bottomSheet = new PostOptionsModal(refreshItemsRunnable);
        Bundle bundle = new Bundle();
        bundle.putString("postAuthorEmail", postAuthorEmail);
        bundle.putString("postText", postText);
        bundle.putString("postId", postId);
        bundle.putString("postImage", postImage);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "TAG");
    }


    
}


