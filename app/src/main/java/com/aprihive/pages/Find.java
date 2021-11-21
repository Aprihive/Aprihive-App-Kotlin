package com.aprihive.pages;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.aprihive.Home;
import com.aprihive.R;
import com.aprihive.adapters.FindRecyclerViewAdapter;
import com.aprihive.models.FindModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.List;


public class Find extends Fragment implements FindRecyclerViewAdapter.MyClickListener {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final Object TAG = "ok";
    private TextView connectButton, userItem;
    private RecyclerView recyclerView;
    private List<FindModel> usersList;
    private FindRecyclerViewAdapter adapter;

    private String getEmail, getFullname, getUsername, getPhone, getBio, getSchoolName, getProfileLink; //fetch from firebase into
    private Boolean getVerified; //fetch from firebase into
    private SwipeRefreshLayout swipeRefresh;
    private ConstraintLayout nothingImage;
    private String getTwitter;
    private String getInstagram;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        final Context context = getActivity().getApplicationContext();



        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        usersList= new ArrayList<>();
        connectButton = view.findViewById(R.id.connectButton);
        userItem = view.findViewById(R.id.fullName);
        recyclerView = view.findViewById(R.id.findRecyclerView);
        swipeRefresh = view.findViewById(R.id.find_swipeRefresh);
        nothingImage = view.findViewById(R.id.notFoundImage);


        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.bg_color));
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.color_theme_green_100), getResources().getColor(R.color.color_theme_green_300));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsersFromFirebase();
            }
        });


        adapter = new FindRecyclerViewAdapter(getContext(), usersList, this);
        swipeRefresh.setRefreshing(true);
        getUsersFromFirebase();


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

    private void getUsersFromFirebase(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        assert user != null;

        Query findQuery = db.collection("users").orderBy("registered on");

        //.orderBy("registered on", "asc")

        findQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    usersList.clear();

                    for (DocumentSnapshot value : task.getResult()){

                        getEmail = value.getString("email");
                        getFullname = value.getString("name");
                        getPhone = value.getString("phone");
                        getTwitter = value.getString("twitter");
                        getInstagram = value.getString("instagram");
                        getVerified = value.getBoolean("verified");
                        getBio = value.getString("bio");
                        getUsername = value.getString("username");
                        getSchoolName = value.getString("school");
                        getProfileLink = value.getString("profileImageLink");


                        FindModel findModel = new FindModel();

                        findModel.setFullname(getFullname);
                        findModel.setEmail(getEmail);
                        findModel.setBio(getBio);
                        findModel.setUsername(getUsername);
                        findModel.setVerified(getVerified);
                        findModel.setSchoolName(getSchoolName);
                        findModel.setProfileImageUrl(getProfileLink);
                        findModel.setPhone(getPhone);
                        findModel.setTwitter(getTwitter);
                        findModel.setInstagram(getInstagram);




                        usersList.add(findModel);
                        usersList.removeIf((p -> p.getEmail().equals(user.getEmail())));

                    }


                    setupRecyclerView();
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

    private void filter(String text){
        // creating a new array list to filter our data.
        List<FindModel> filteredUsersList = new ArrayList<>();

        String query = text.toLowerCase();

        // running a for loop to compare elements.
        for (FindModel item : usersList) {

            // checking if the entered string matched with any item of our recycler view.
            if (item.getUsername().toLowerCase().contains(query)){
                filteredUsersList.add(item);
            }
            else if (item.getFullname().toLowerCase().contains(query)){
                filteredUsersList.add(item);
            }
            else if (item.getSchoolName().toLowerCase().contains(query)){
                filteredUsersList.add(item);
            }
            else if (item.getBio().toLowerCase().contains(query)){
                filteredUsersList.add(item);
            }
        }
        if (filteredUsersList.isEmpty()) {
            // if no item is added in filtered list we are displaying a toast message as no data found.
            adapter.filterList((ArrayList<FindModel>) filteredUsersList);
            nothingImage.setVisibility(View.VISIBLE);


        } else {

            // at last we are passing that filtered list to our adapter class.
            adapter.filterList((ArrayList<FindModel>) filteredUsersList);
            nothingImage.setVisibility(View.GONE);

        }

    }


    @Override
    public void onFollow(int position, String userEmail) {
        
        DocumentReference fileRef = db.collection("users").document(userEmail).collection("lists").document("following");
        final Boolean[] processFollow = {true};
        fileRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                try {
                    if (processFollow[0]){

                        if (value.contains(auth.getCurrentUser().getUid())){
                            fileRef.update(auth.getCurrentUser().getUid(), FieldValue.delete());
                            processFollow[0] =false;
                        }
                        else {
                            fileRef.update(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail());
                            processFollow[0] =false;
                        }


                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}

