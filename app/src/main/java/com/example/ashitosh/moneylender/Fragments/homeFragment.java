package com.example.ashitosh.moneylender.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
public class homeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore fs;
    private AgentAdapter adapter;
    private ArrayList<AgentModel> userList;

    private EditText search;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v=inflater.inflate(R.layout.fragment_home, container, false);



        search=v.findViewById(R.id.AgentSearch);

        fs= FirebaseFirestore.getInstance();

        userList=new ArrayList<>(); 


        adapter= new AgentAdapter(userList, Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"HomeFrag");

        recyclerView=v.findViewById(R.id.AgentRecycleList);
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
            if(item.getName().toLowerCase().contains(s.toLowerCase()))
            {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }


}