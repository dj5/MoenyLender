package com.example.ashitosh.moneylender.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.csvDaily;
import com.example.ashitosh.moneylender.csvMonthly;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends Fragment {


    private RadioButton dailybtn,monthlybtn;
    private RadioGroup radioGroup;

    private FirebaseFirestore fs;
    private FirebaseAuth f_auth;

    String agentEmail;
    String agentName;
    TextView name,email;
    Button csvDaily,csvMonthly;
    List<Map<String,String>> dailyCollection;

    public CollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_collection, container, false);



        csvDaily=v.findViewById(R.id.AgentDailyCsv);
        csvMonthly=v.findViewById(R.id.AgentMonthCsv);

        agentName= Objects.requireNonNull(getArguments()).getString("agentName");
        agentEmail=getArguments().getString("agentEmail");
        radioGroup=v.findViewById(R.id.AgentRadioGrp);


        dailybtn=v.findViewById(R.id.radioDaily);
        monthlybtn=v.findViewById(R.id.radioMonthly);

        fs=FirebaseFirestore.getInstance();
        f_auth=FirebaseAuth.getInstance();

        name=v.findViewById(R.id.ColHeading);
        name.setText(agentName);

        email=v.findViewById(R.id.ColEamil);
        email.setText(agentEmail);



        dailybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data=new Bundle();
                data.putString("agentEmail",agentEmail);

                DailyFragment fragment=new DailyFragment();

                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction()
                        .add(fragment,"dailyCol").addToBackStack("dailyCol");


                fragmentTransaction.replace(R.id.installment_layout,fragment);
                fragmentTransaction.commit();

            }
        });

        monthlybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data=new Bundle();
                data.putString("agentEmail",agentEmail);

                MonthlyFragment fragment=new MonthlyFragment();

                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(fragment,"monthlyCol").addToBackStack("monthlyCol");

                fragmentTransaction.replace(R.id.installment_layout,fragment);
                fragmentTransaction.commit();

            }
        });

        final Bundle data=new Bundle();
        data.putString("frag","Owner");
        data.putString("agentemail",agentEmail);

        csvDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                com.example.ashitosh.moneylender.csvDaily fragment=new csvDaily();

                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(fragment,"csvDaily").addToBackStack("csvDaily");

                fragmentTransaction.replace(R.id.mainFrame,fragment);
                fragmentTransaction.addToBackStack("DailyCsv");
                fragmentTransaction.commit();

            }
        });

        csvMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.example.ashitosh.moneylender.csvMonthly fragment=new csvMonthly();
                fragment.setArguments(data);
                android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(fragment,"csvMonth").addToBackStack("csvMonth");

                fragmentTransaction.replace(R.id.mainFrame,fragment);
                fragmentTransaction.addToBackStack("MonthCsv");
                fragmentTransaction.commit();

            }
        });

        //fs=FirebaseFirestore.getInstance();


        return v;
    }




}


