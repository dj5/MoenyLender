package com.example.ashitosh.moneylender;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Adapters.AgentAdapter;
import com.example.ashitosh.moneylender.Adapters.CustAdapter;
import com.example.ashitosh.moneylender.Fragments.csvFragment;
import com.example.ashitosh.moneylender.Models.AgentModel;
import com.example.ashitosh.moneylender.Models.custModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;



public class AgentHome extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore fs;
    private CustAdapter adapter;
    private List<custModel> userList;
    private Button DailyCsv,MonthlyCsv,CustCsv;
    private FirebaseAuth firebaseAuth;
    private String email;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_agent_home, container, false);

        fs= FirebaseFirestore.getInstance();

        firebaseAuth=FirebaseAuth.getInstance();

        userList=new ArrayList<>();

        DailyCsv=v.findViewById(R.id.DailyCSV);
        MonthlyCsv=v.findViewById(R.id.MonthlyCsv);
        CustCsv=v.findViewById(R.id.CustDetailCSV);

        adapter= new CustAdapter(userList, Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"Agent");

        recyclerView=v.findViewById(R.id.AgentRecycleList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        email= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

        DailyCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csvDaily fragment=new csvDaily();

                Bundle data=new Bundle();
                data.putString("frag","Agent");
                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(fragment,"csvDaily").addToBackStack("csvDaily");

                fragmentTransaction.replace(R.id.AgentmainFrame,fragment);
                fragmentTransaction.addToBackStack("DailyCsv");
                fragmentTransaction.commit();

            }
        });

        MonthlyCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csvMonthly fragment=new csvMonthly();

                Bundle data=new Bundle();
                data.putString("frag","Agent");
                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(fragment,"csvMonth").addToBackStack("csvMonth");

                fragmentTransaction.replace(R.id.AgentmainFrame,fragment);
                fragmentTransaction.addToBackStack("MonthCsv");
                fragmentTransaction.commit();

            }
        });

        CustCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csvFragment fragment=new csvFragment();

                android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(fragment,"csvCust").addToBackStack("csvCust");
                fragmentTransaction.replace(R.id.AgentmainFrame,fragment);
                fragmentTransaction.addToBackStack("CustCsv");
                fragmentTransaction.commit();

            }
        });


        fs.collection("Agents").document("Agent_"+email).collection("Daily").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e!=null)
                {
                    Log.e("Error: "+e.getMessage(),"Error");
                }
                else
                {
                    for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                    {
                        if (doc.getType().equals(DocumentChange.Type.ADDED))
                        {
                            custModel model=doc.getDocument().toObject(custModel.class);
                            userList.add(model);

                            String name=doc.getDocument().getString("CustName");
                            Log.d("name","name: "+name);

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


        fs.collection("Agents").document("Agent_"+email).collection("Monthly").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e!=null)
                {
                    Log.e("Error: "+e.getMessage(),"Error");
                }
                else
                {
                    for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                    {
                        if (doc.getType().equals(DocumentChange.Type.ADDED))
                        {
                            custModel model=doc.getDocument().toObject(custModel.class);
                            userList.add(model);

                            String name=doc.getDocument().getString("CustName");
                            Log.d("name","name: "+name);

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


        return v;
    }


}
