package com.example.shubhampc.englishdictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shubhampc.englishdictionary.R;
import com.example.shubhampc.englishdictionary.WordMeaningActivity;

public class FragmentDefination extends Fragment {
    public FragmentDefination() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_defination,container,false); // inflate layout

        Context context = getActivity();
        TextView text = view.findViewById(R.id.textViewD);

        String en_defination = ((WordMeaningActivity)context).enDefination;


        if(en_defination == null || en_defination.equals("NA")){
            text.setText("No defination found!");
        }
        else{
            text.setText(en_defination);
        }



        return view;
    }
}
