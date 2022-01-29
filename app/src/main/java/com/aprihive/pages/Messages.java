package com.aprihive.pages;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aprihive.MessagingActivity;
import com.aprihive.R;
import com.aprihive.adapters.MessagedUsersRecyclerAdapter;
import com.aprihive.models.FindModel;
import com.aprihive.models.MessagedUsersModel;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class Messages extends Fragment   implements MessagedUsersRecyclerAdapter.MyClickListener {

    
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;


    private String getEmail;
    private HashMap<String, Object> getMessageDetails;

    private RecyclerView recyclerView;
    private List<MessagedUsersModel> usersList;
    private SwipeRefreshLayout swipeRefresh;
    private ConstraintLayout nothingImage;
    private MessagedUsersRecyclerAdapter adapter;
    private String getLastMessage;
    private ListenerRegistration registerQuery;

    public Messages() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        //firebase
        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        usersList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.messagedRecyclerView);
        swipeRefresh = view.findViewById(R.id.messaged_swipeRefresh);
        nothingImage = view.findViewById(R.id.notFoundImage);

        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.bg_color));
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.color_theme_green_100), getResources().getColor(R.color.color_theme_green_300));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMessagedUsersFromFirebase();
            }
        });

        adapter = new MessagedUsersRecyclerAdapter(getContext(), usersList, this);
        swipeRefresh.setRefreshing(true);
        getMessagedUsersFromFirebase();

        return view;
    }


    private void setupRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        //set items to arrange from bottom
        // linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }


    private void getMessagedUsersFromFirebase() {

        assert user != null;
        Query messagesQuery = db.collection("users").document(user.getEmail()).collection("messages").orderBy("time", Query.Direction.DESCENDING);

        registerQuery = messagesQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot result, @Nullable FirebaseFirestoreException error) {

                usersList.clear();
                for (DocumentSnapshot value : result){

                    getEmail = value.getId();


                    MessagedUsersModel messagedUsersModel = new MessagedUsersModel();
                    messagedUsersModel.setReceiverEmail(getEmail);


                    usersList.add(messagedUsersModel);

                }


                setupRecyclerView();
                swipeRefresh.setRefreshing(false);

            }
        });

    }



    @Override
    public void onOpen(int position, String getEmail) {
        Intent intent = new Intent(getContext(), MessagingActivity.class);
        intent.putExtra("getEmail", getEmail);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            registerQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            registerQuery.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessagedUsersFromFirebase();
    }
}
