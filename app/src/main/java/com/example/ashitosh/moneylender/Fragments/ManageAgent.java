package com.example.ashitosh.moneylender.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Adapters.AgentAdapter;
import com.example.ashitosh.moneylender.Models.AgentModel;
import com.example.ashitosh.moneylender.Models.custModel;
import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.Activities.firestoreRecycle;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageAgent extends Fragment {

    private FirebaseAuth f_auth;
    private FirebaseFirestore fs;

    private FloatingActionButton reg,del;
    private RecyclerView recyclerView;

    private AgentAdapter adapter;
    private ArrayList<AgentModel> userList;

    private EditText search;

    public ManageAgent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);


        reg=v.findViewById(R.id.AddAgentBtn);
        del=v.findViewById(R.id.DelAgentBtn);
        search=v.findViewById(R.id.ManageAgentSearch);


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddAgentFragment fragment=new AddAgentFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainFrame,fragment);
                fragmentTransaction.addToBackStack("regAgent");
                fragmentTransaction.commit();
            }
        });


        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DelAgentFragment fragment=new DelAgentFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainFrame,fragment);
                fragmentTransaction.addToBackStack("delAgent");
                fragmentTransaction.commit();
            }
        });




        fs= FirebaseFirestore.getInstance();

        userList=new ArrayList<>();

        adapter= new AgentAdapter(userList, Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"AgentManage");

        recyclerView=v.findViewById(R.id.AgentListRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        fs.collection("MoneyLender").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            AgentModel model=doc.getDocument().toObject(AgentModel.class);
                            userList.add(model);

                            String name=doc.getDocument().getString("Name");
                            Log.d("name","name: "+name);

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //    adapter.getFilter().filter(s);
                //    adapter.notifyDataSetChanged();
                if (s.length()>0) {
                    filter(s.toString());
                }
                else
                {
                    adapter.filterList(userList);
                }
            }
        });


        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus && search.getText().length()>0)
                {
                    filter(search.getText().toString());
                    v.refreshDrawableState();
                }
                else if(!hasFocus && search.getText().length()<=0)
                {
                    adapter.filterList(userList);
                    v.refreshDrawableState();
                }

            }
        });

        return v;
    }

    private void filter(String s) {

        ArrayList<AgentModel> filteredList=new ArrayList<>();

        for(AgentModel item:userList)
        {
            if(item.getName().toLowerCase().contains(s.toLowerCase()) || item.getEmail().toLowerCase().contains(s.toLowerCase()) || item.getAddress().toLowerCase().contains(s.toLowerCase()))
            {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
}
