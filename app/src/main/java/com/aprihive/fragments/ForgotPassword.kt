package com.aprihive.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aprihive.R;
import com.aprihive.methods.NetworkListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class ForgotPassword extends BottomSheetDialogFragment {

    private EditText emailInput;
    private Button submit, closeBtn;
    private TextView info, title;
    private ImageView image;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;
    private NetworkListener networkListener;
    private ConstraintLayout loading, closeScreen, forgotPasswordScreen;


    private String email;

    public ForgotPassword() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_forgot_password, container, false);


        auth = FirebaseAuth.getInstance();


        emailInput = view.findViewById(R.id.email);
        submit = view.findViewById(R.id.submitBtn);
        info = view.findViewById(R.id.textView4);
        image = view.findViewById(R.id.imageView4);
        title = view.findViewById(R.id.textView);
        loading = view.findViewById(R.id.loading);
        closeScreen = view.findViewById(R.id.closeScreen);
        forgotPasswordScreen = view.findViewById(R.id.forgotScreen);
        closeBtn = view.findViewById(R.id.closeBtn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });


        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        return view;
    }

    private void checkInputs(){

        email = emailInput.getText().toString().trim();

        //check email
        if (email.isEmpty()) {
            info.setText("Please enter an email address to continue");
            info.setTextColor(getResources().getColor(R.color.color_error_red_200));
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            info.setText("Please enter a correct email address");
            info.setTextColor(getResources().getColor(R.color.color_error_red_200));
        }

        else{
            resetPassword();
            disableButton();
        }

    }

    private void disableButton() {
        submit.setBackground(getResources().getDrawable(R.drawable.disabled_button));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nothing
            }
        });
        submit.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    private void resetPassword() {

        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                forgotPasswordScreen.setVisibility(View.INVISIBLE);
                closeScreen.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableButton();
                info.setText("Somehow, somewhere, something went wrong.");
                info.setTextColor(getResources().getColor(R.color.grey_color_200));
            }
        });


    }

    private void enableButton() {
        submit.setBackground(getResources().getDrawable(R.drawable.blue_button));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });
        submit.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }


}
