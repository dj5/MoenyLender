package com.example.ashitosh.moneylender;

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
public class AgentDetail extends Fragment {

    private TextView name,email,phone,address;
    private FirebaseFirestore fs;
    private FirebaseAuth f_auth;
    private String namestr,emailstr,phonestr,addrstr;
    private ProgressDialog pd;

    public AgentDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_agent_detail2, container, false);

        name=v.findViewById(R.id.AgentDetailName2);
        email=v.findViewById(R.id.AgentDetailEmail2);
        phone=v.findViewById(R.id.AgentDetailPhone2);
        address=v.findViewById(R.id.AgentDetailAddr2);

        pd=new ProgressDialog(getActivity());

        fs=FirebaseFirestore.getInstance();
        f_auth=FirebaseAuth.getInstance();

        Bundle data=getArguments();

        emailstr= Objects.requireNonNull(data).getString("agentEmail");
        phonestr=data.getString("agentPhone");
        addrstr=data.getString("agentAddress");
        namestr=data.getString("agentName");

            if(!Objects.requireNonNull(namestr).isEmpty() && ! emailstr.isEmpty() && !phonestr.isEmpty() && !Objects.requireNonNull(addrstr).isEmpty())
            {
                name.setText(namestr);
                email.setText(emailstr);
                phone.setText(phonestr);
                address.setText(addrstr);

            }


        return v;
    }



}
