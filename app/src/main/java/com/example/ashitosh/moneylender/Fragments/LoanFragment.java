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

import com.example.ashitosh.moneylender.Adapters.CustAdapter;
import com.example.ashitosh.moneylender.Adapters.LoanAdapter;
import com.example.ashitosh.moneylender.Models.LoanModel;
import com.example.ashitosh.moneylender.Models.custModel;
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
public class LoanFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore fs;
    private LoanAdapter adapter;
    private List<LoanModel> userList;

    private String Accno,agentEmail;
    public LoanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_loan, container, false);

        fs= FirebaseFirestore.getInstance();

        userList=new ArrayList<>();

        adapter=new LoanAdapter(userList);

        Accno= Objects.requireNonNull(getArguments()).getString("AccountNo");
        agentEmail=getArguments().getString("AgentEmail");

        recyclerView=v.findViewById(R.id.LoanRecyle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        fs.collection("clients").document("client_"+Accno).collection("loans").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                            LoanModel model=doc.getDocument().toObject(LoanModel.class);

                            Toast.makeText(getActivity(), "Loan Email: "+agentEmail, Toast.LENGTH_SHORT).show();

                            try {
                                if (agentEmail!=null) {


                                    if (model.getAgentEmail().equals(agentEmail)) {

                                        userList.add(model);
                                        String name = doc.getDocument().getString("Name");
                                        Log.d("name", "name: " + name);
                                    }
                                }
                                else
                                {
                                    userList.add(model);
                                    String name = doc.getDocument().getString("Name");
                                    Log.d("name", "name: " + name);
                                }
                                adapter.notifyDataSetChanged();
                            }
                            catch (java.lang.NullPointerException e1)
                            {
                                Toast.makeText(getActivity(), "Exception: "+e1.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            }
        });

        return v;
    }

}
