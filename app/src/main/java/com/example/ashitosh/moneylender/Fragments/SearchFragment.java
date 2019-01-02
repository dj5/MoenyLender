package com.example.ashitosh.moneylender.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Adapters.CustAdapter;
import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.Models.custModel;
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
public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore fs;
    private CustAdapter adapter;
    private ArrayList<custModel> userList;
    private EditText search;
    private FloatingActionButton addcust,delcust;

   // private CollectionReference collectionReference;


    public SearchFragment() {
        // Required empty public constructor


    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_search, container, false);

        addcust=v.findViewById(R.id.CustAdd);
   //     delcust=v.findViewById(R.id.CustDel);
        search=v.findViewById(R.id.ManageClientSearch);

        fs= FirebaseFirestore.getInstance();

        userList=new ArrayList<>();

        adapter=new CustAdapter(userList, Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"Owner");

        recyclerView=v.findViewById(R.id.cust_List);
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
                    for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                    {
                        if (doc.getType().equals(DocumentChange.Type.ADDED))
                        {
                            custModel model=doc.getDocument().toObject(custModel.class);
                            userList.add(model);

                            String name=doc.getDocument().getString("Name");
                            Log.d("name","name: "+name);

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


        addcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddCustFragment fragment=new AddCustFragment();


                android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction().add(fragment,"AddCust").addToBackStack("AddCust");

                fragmentTransaction.replace(R.id.mainFrame,fragment);

                fragmentTransaction.commit();
            }
        });

        /*
        delcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteCustFragment fragment=new   DeleteCustFragment();

                android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction().add(fragment,"DelCust").addToBackStack("DelCust");

                fragmentTransaction.replace(R.id.mainFrame,fragment);

                fragmentTransaction.commit();
            }
        });
*/

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
            if(item.getCustName().toLowerCase().contains(s.toLowerCase()) || item.getAccountNo().toLowerCase().contains(s.toLowerCase()))
            {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

}


