package com.example.ashitosh.moneylender.Fragments;


import android.os.Bundle;
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

import com.example.ashitosh.moneylender.Adapters.CustAdapter;
import com.example.ashitosh.moneylender.Models.custModel;
import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.csvDaily;
import com.example.ashitosh.moneylender.csvMonthly;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore fs;
    private CustAdapter adapter;
    private ArrayList<custModel> userList;
    private String email;
    private EditText search;
    public ClientsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_clients, container, false);

        fs= FirebaseFirestore.getInstance();


        userList=new ArrayList<>();


        search=v.findViewById(R.id.ClientSearch);

        adapter= new CustAdapter(userList, Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"ClientsFragment");

        recyclerView=v.findViewById(R.id.ClientRecycleList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Bundle data=getArguments();

        email= Objects.requireNonNull(data).getString("AgentEmail");


//gets Daily ACtive customer
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
                            Map<String,Object> data=doc.getDocument().getData();

                            String status=data.get("Status").toString();


                            if(status.equals("1")) {

                                custModel model = doc.getDocument().toObject(custModel.class);
                                userList.add(model);

                                String name = doc.getDocument().getString("CustName");
                                Log.d("name", "name: " + name);

                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });

        //Gets Monthly Active customer

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
                            //  String status=doc.getDocument().getData().get("Status").toString();
                            Map<String,Object> data=doc.getDocument().getData();

                            String status=data.get("Status").toString();

                            if(status.equals("1")) {

                                custModel model = doc.getDocument().toObject(custModel.class);
                                userList.add(model);

                                String name = doc.getDocument().getString("CustName");
                                Log.d("name", "name: " + name);
                                adapter.notifyDataSetChanged();

                            }
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

        ArrayList<custModel> filteredList=new ArrayList<>();

        for(custModel item:userList)
        {
            if(item.getCustName().toLowerCase().contains(s.toLowerCase()))
            {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

}
