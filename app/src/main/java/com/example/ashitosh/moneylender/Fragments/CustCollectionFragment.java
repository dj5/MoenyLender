package com.example.ashitosh.moneylender.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Adapters.CustColAdapter;
import com.example.ashitosh.moneylender.Adapters.DailyAdapter;
import com.example.ashitosh.moneylender.Models.CustColModel;
import com.example.ashitosh.moneylender.Models.dailyModel;
import com.example.ashitosh.moneylender.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustCollectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore fs;
    private CustColAdapter adapter;
    private List<CustColModel> userList;
    private String agentEmaill,type,docname;

    public CustCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_cust_collection, container, false);

        fs= FirebaseFirestore.getInstance();

        userList=new ArrayList<>();


        docname= Objects.requireNonNull(getArguments()).getString("docname");
        type= Objects.requireNonNull(getArguments()).getString("InstallmentType");
        agentEmaill= Objects.requireNonNull(getArguments()).getString("agentEmail");

        adapter= new CustColAdapter(userList, Objects.requireNonNull(getActivity()).getSupportFragmentManager());

        recyclerView=v.findViewById(R.id.CustColRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        Toast.makeText(this.getActivity(),"recycer, oncreate:"+agentEmaill,Toast.LENGTH_LONG).show();

        fs.collection("MoneyLender").document("Agent_"+agentEmaill)
                .collection(type).document(docname).collection("customers").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                            Toast.makeText(getActivity(),"doc added:"+agentEmaill,Toast.LENGTH_LONG).show();

                            CustColModel model=doc.getDocument().toObject(CustColModel.class);
                            userList.add(model);

                            String name=doc.getDocument().getString("Name");
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
