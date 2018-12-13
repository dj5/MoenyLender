package com.example.ashitosh.moneylender.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentDetailFragment extends Fragment {

    private TextView name,email,phone,address;
    private FirebaseFirestore fs;
    private FirebaseAuth f_auth;
    private String namestr,emailstr,phonestr,addrstr;
    private ProgressDialog pd;
    public AgentDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_agent_detail, container, false);

        name=v.findViewById(R.id.AgentDetailName);
        email=v.findViewById(R.id.AgentDetailEmail);
        phone=v.findViewById(R.id.AgentDetailPhone);
        address=v.findViewById(R.id.AgentDetailAddr);

        pd=new ProgressDialog(getActivity());

        fs=FirebaseFirestore.getInstance();
        f_auth=FirebaseAuth.getInstance();

        emailstr= Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();

            pd.setMessage("Wait until Loading");
            pd.setCanceledOnTouchOutside(false);
            pd.show();


            fs.collection("MoneyLender").document("Agent_"+emailstr)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        namestr=task.getResult().getString("Name");
                        phonestr=task.getResult().getString("Phone");
                        addrstr=task.getResult().getString("Address");
                        emailstr=task.getResult().getString("Email");

                        if(!namestr.isEmpty() && ! Objects.requireNonNull(emailstr).isEmpty() && !phonestr.isEmpty() && !Objects.requireNonNull(addrstr).isEmpty())
                        {
                            name.setText(namestr);
                            email.setText(emailstr);
                            phone.setText(phonestr);
                            address.setText(addrstr);

                        }

                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), "Failed To Fetch Agent Details: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });


        return v;
    }



}
