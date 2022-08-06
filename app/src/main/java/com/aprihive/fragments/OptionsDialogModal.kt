package com.aprihive.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aprihive.R;

public class OptionsDialogModal {
    
    Dialog dialog;
    TextView optionView1, optionView2, optionView3, optionView4, optionView5;
    Runnable action1, action2, action3, action4, action5;

    public OptionsDialogModal(Context context) {
             
        
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_option_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        optionView1 = dialog.findViewById(R.id.option1);
        optionView2 = dialog.findViewById(R.id.option2);
        optionView3 =  dialog.findViewById(R.id.option3);
        optionView4 = dialog.findViewById(R.id.option4);
        optionView5 = dialog.findViewById(R.id.option5);
        
        
        
    }

    public OptionsDialogModal(Context context, String option1, Runnable action1, String option2, Runnable action2) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_option_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        optionView1 = dialog.findViewById(R.id.option1);
        optionView2 = dialog.findViewById(R.id.option2);
        optionView3 =  dialog.findViewById(R.id.option3);
        optionView4 = dialog.findViewById(R.id.option4);
        optionView5 = dialog.findViewById(R.id.option5);


        this.action1 = action1;
        this.action2 = action2;

        optionView1.setText(option1);
        optionView2.setText(option2);


        optionView1.setVisibility(View.VISIBLE);
        optionView2.setVisibility(View.VISIBLE);

        optionView1.setOnClickListener(listener);
        optionView2.setOnClickListener(listener);

        dialog.show();



    }

    public OptionsDialogModal(Context context, String option1, Runnable action1, String option2, Runnable action2, String option3, Runnable action3) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_option_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        optionView1 = dialog.findViewById(R.id.option1);
        optionView2 = dialog.findViewById(R.id.option2);
        optionView3 =  dialog.findViewById(R.id.option3);
        optionView4 = dialog.findViewById(R.id.option4);
        optionView5 = dialog.findViewById(R.id.option5);


        this.action1 = action1;
        this.action2 = action2;
        this.action3 = action3;

        optionView1.setText(option1);
        optionView2.setText(option2);
        optionView3.setText(option3);



        optionView1.setVisibility(View.VISIBLE);
        optionView2.setVisibility(View.VISIBLE);
        optionView3.setVisibility(View.VISIBLE);

        optionView1.setOnClickListener(listener);
        optionView2.setOnClickListener(listener);
        optionView3.setOnClickListener(listener);

        dialog.show();


    }

    public OptionsDialogModal(Context context, String option1, Runnable action1, String option2, Runnable action2, String option3, Runnable action3, String option4, Runnable action4) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_option_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        optionView1 = dialog.findViewById(R.id.option1);
        optionView2 = dialog.findViewById(R.id.option2);
        optionView3 =  dialog.findViewById(R.id.option3);
        optionView4 = dialog.findViewById(R.id.option4);
        optionView5 = dialog.findViewById(R.id.option5);


        this.action1 = action1;
        this.action2 = action2;
        this.action3 = action3;
        this.action4 = action4;

        optionView1.setText(option1);
        optionView2.setText(option2);
        optionView3.setText(option3);
        optionView4.setText(option4);




        optionView1.setVisibility(View.VISIBLE);
        optionView2.setVisibility(View.VISIBLE);
        optionView3.setVisibility(View.VISIBLE);
        optionView4.setVisibility(View.VISIBLE);

        optionView1.setOnClickListener(listener);
        optionView2.setOnClickListener(listener);
        optionView3.setOnClickListener(listener);
        optionView4.setOnClickListener(listener);

        dialog.show();

    }

    public OptionsDialogModal(Context context, String option1, Runnable action1, String option2, Runnable action2, String option3, Runnable action3, String option4, Runnable action4, String option5, Runnable action5) {


        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_option_dialog);

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        optionView1 = dialog.findViewById(R.id.option1);
        optionView2 = dialog.findViewById(R.id.option2);
        optionView3 =  dialog.findViewById(R.id.option3);
        optionView4 = dialog.findViewById(R.id.option4);
        optionView5 = dialog.findViewById(R.id.option5);


        this.action1 = action1;
        this.action2 = action2;
        this.action3 = action3;
        this.action4 = action4;
        this.action5 = action5;



        optionView1.setText(option1);
        optionView2.setText(option2);
        optionView3.setText(option3);
        optionView4.setText(option4);
        optionView5.setText(option5);





        optionView1.setVisibility(View.VISIBLE);
        optionView2.setVisibility(View.VISIBLE);
        optionView3.setVisibility(View.VISIBLE);
        optionView4.setVisibility(View.VISIBLE);
        optionView5.setVisibility(View.VISIBLE);

        optionView1.setOnClickListener(listener);
        optionView2.setOnClickListener(listener);
        optionView3.setOnClickListener(listener);
        optionView4.setOnClickListener(listener);
        optionView5.setOnClickListener(listener);

        dialog.show();



    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.option1 :
                    action1.run();
                    break;
                case R.id.option2 :
                    action2.run();
                    break;
                case R.id.option3 :
                    action3.run();
                    break;
                case R.id.option4 :
                    action4.run();
                    break;
                case R.id.option5 :
                    action5.run();
                    break;
                default:
                    //nothing
            }
            dialog.dismiss();


        }
    };

}
