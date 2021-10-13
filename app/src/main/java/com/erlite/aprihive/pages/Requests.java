package com.erlite.aprihive.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.Log;
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

import com.erlite.aprihive.R;
import com.erlite.aprihive.RequestDetails;
import com.erlite.aprihive.adapters.FindRecyclerViewAdapter;
import com.erlite.aprihive.adapters.NotificationsRecyclerViewAdapter;
import com.erlite.aprihive.models.FindModel;
import com.erlite.aprihive.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class Requests extends Fragment implements NotificationsRecyclerViewAdapter.MyClickListener{

    private static final Object TAG = "ok";
    private RecyclerView recyclerView;
    public List<NotificationModel> notificationList;
    private NotificationsRecyclerViewAdapter adapter;

    private String getType;
    private String getAuthorEmail;
    private String getDeadline;
    private String getPostId;
    private String getPostImageLink;
    private String getPostText;
    private String getSenderEmail;
    private String getRequestText;
    private Timestamp getRequestedOn;
    
    private SwipeRefreshLayout swipeRefresh;
    private String getTitle;
    private String getAuthorUsername;
    private String getReceiverUsername;
    private String getSenderUsername;
    public Runnable refreshRequestsRunnable;
    private Context context;
    private TextView nothingText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        context = getActivity().getApplicationContext();


        notificationList= new ArrayList<>();
        recyclerView = view.findViewById(R.id.notificationRecyclerView);
        swipeRefresh = view.findViewById(R.id.notification_swipeRefresh);
        nothingText = view.findViewById(R.id.nothingText);

      // //to refresh posts after posting or deleting
      // refreshRequestsRunnable = new Runnable() {
      //     @Override
      //     public void run() {
      //         getRequestsNotifications();
      //     }
      // };

        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.bg_color));
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.color_theme_green_100), getResources().getColor(R.color.color_theme_green_300));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequestsNotifications();
            }
        });


        adapter = new NotificationsRecyclerViewAdapter(getContext(), notificationList, this);
        swipeRefresh.setRefreshing(true);
        getRequestsNotifications();


        return view;
    }

    private void setupRecyclerView(List<NotificationModel> notificationList) {

        if (notificationList.isEmpty()){
            nothingText.setVisibility(View.VISIBLE);
        } else {
            nothingText.setVisibility(View.GONE);

        }

        try {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            //set items to arrange from bottom
            // linearLayoutManager.setReverseLayout(true);
            //linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getRequestsNotifications(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("debug", "re1 " + notificationList);


        assert user != null;

        Query notificationsQuery = db.collection("users").document(user.getEmail()).collection("requests").orderBy("requested on", Query.Direction.DESCENDING);

        //.orderBy("registered on", "asc")

        notificationsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    notificationList.clear();

                    Log.d("debug", "re2 " + notificationList);


                    for (DocumentSnapshot value : task.getResult()){

                        getType = value.getString("type");
                        getPostId = value.getString("postId");
                        getAuthorEmail = value.getString("authorEmail");
                        getReceiverUsername = value.getString("receiverUsername");
                        getSenderUsername = value.getString("senderUsername");
                        getSenderEmail = value.getString("senderEmail");
                        getRequestText = value.getString("requestText");
                        getPostText = value.getString("postText");
                        getPostImageLink = value.getString("postImageLink");
                        getRequestedOn = value.getTimestamp("requested on");
                        getDeadline = value.getString("deadLine");



                        NotificationModel notificationModel = new NotificationModel();

                        notificationModel.setType(getType);
                        notificationModel.setPostId(getPostId);
                        notificationModel.setAuthorEmail(getAuthorEmail);
                        notificationModel.setReceiverUsername(getReceiverUsername);
                        notificationModel.setSenderUsername(getSenderUsername);
                        notificationModel.setSenderEmail(getSenderEmail);
                        notificationModel.setRequestText(getRequestText);
                        notificationModel.setPostText(getPostText);
                        notificationModel.setPostImageLink(getPostImageLink);
                        notificationModel.setRequestedOn(requestedOn(getRequestedOn));
                        notificationModel.setDeadline(getDeadline);





                        notificationList.add(notificationModel);
                        Log.d("debug", "re3 " + notificationList);


                    }


                    setupRecyclerView(notificationList);
                    swipeRefresh.setRefreshing(false);


                }

            }
        });


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        // Inflate the layout for this menu
        inflater.inflate(R.menu.find_bar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search by username or description...");
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
                getActivity().findViewById(R.id.logoImageView).setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
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

    private void filter(String text){
        // creating a new array list to filter our data.
        List<NotificationModel> filteredNotificationList = new ArrayList<>();

        String query = text.toLowerCase();

        // running a for loop to compare elements.
        for (NotificationModel item : notificationList) {

            // checking if the entered string matched with any item of our recycler view.
            if (item.getAuthorEmail().toLowerCase().contains(query)){
                filteredNotificationList.add(item);
            }

        }
        if (filteredNotificationList.isEmpty()) {
            // if no item is added in filtered list we are displaying a toast message as no data found.

            //TODO: add image to show that no notification found

        } else {

            // at last we are passing that filtered list to our adapter class.
            adapter.filterList((ArrayList<NotificationModel>) filteredNotificationList);

        }

    }

    private String requestedOn(Timestamp timestamp){

        Date date = timestamp.toDate();
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(date);
        return ago;

    }

    @Override
    public void onOpenRequestDetails(int position, String getType, String getSenderUsername, String getReceiverUsername, String getDeadline, String getPostId, String getPostImageLink, String getPostText, String getRequestText, String getRequestedOn, String getSenderEmail, String getReceiverEmail){

        Intent intent = new Intent(context, RequestDetails.class);
        intent.putExtra("getType", getType);
        intent.putExtra("getSenderName", getSenderUsername);
        intent.putExtra("getReceiverName", getReceiverUsername);
        intent.putExtra("getDeadline", getDeadline);
        intent.putExtra("getPostId", getPostId);
        intent.putExtra("getPostImageLink", getPostImageLink);
        intent.putExtra("getPostText", getPostText);
        intent.putExtra("getRequestText", getRequestText);
        intent.putExtra("getRequestedOn", getRequestedOn);
        intent.putExtra("getSenderEmail", getSenderEmail);
        intent.putExtra("getReceiverEmail", getReceiverEmail);
       // intent.putExtra("refreshAction", (Serializable) refreshRequestsRunnable);



        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        
    }

    @Override
    public void onResume() {
        super.onResume();
        getRequestsNotifications();
    }
}

