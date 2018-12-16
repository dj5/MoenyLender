package com.example.ashitosh.moneylender.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashitosh.moneylender.Adapters.CustAdapter;
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
public class ActiveClientFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore fs;
    private CustAdapter adapter;
    private List<custModel> userList;
    private String status;
    private ProgressDialog pd;
    public ActiveClientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_active_client, container, false);

        fs= FirebaseFirestore.getInstance();


        userList=new ArrayList<>();

        adapter=new CustAdapter(userList, Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"Owner");

        recyclerView=v.findViewById(R.id.Activecust_List);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        fs.collection("clients").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e!=null)
                {
                    Log.e("Error: "+e.getMessage(),"Error");
                }
                else
                {
                    for (final DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                    {
                        if (doc.getType().equals(DocumentChange.Type.ADDED))
                        {
                              fs.collection("clients").document("client_"+doc.getDocument().getData().get("AccountNo"))
                                      .collection("loans")
                                      .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                          @Override
                                          public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                              for (DocumentChange doc1: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                                              {
                                                  if (doc1.getType().equals(DocumentChange.Type.ADDED))
                                                  {
                                                      status=doc1.getDocument().getData().get("Status").toString();


                                                      if(status.equals("1"))
                                                      {
                                                          custModel model = doc.getDocument().toObject(custModel.class);
                                                          userList.add(model);
                                                          String name = doc.getDocument().getString("Name");
                                                          Log.d("name", "name: " + name);
                                                          adapter.notifyDataSetChanged();
                                                      }
                                                  }
                                              }
                                          }
                                      });


                        }
                    }
                }
            }
        });
        return v;
    }
}


