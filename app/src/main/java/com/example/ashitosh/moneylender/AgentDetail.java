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

    private TextView name,email,phone,address,pass;
    private FirebaseFirestore fs;
    private FirebaseAuth f_auth;
    private String namestr,emailstr,phonestr,addrstr,passtr;
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
        pass=v.findViewById(R.id.AgentPass);

        pd=new ProgressDialog(getActivity());

        fs=FirebaseFirestore.getInstance();
        f_auth=FirebaseAuth.getInstance();

        Bundle data=getArguments();

        emailstr= Objects.requireNonNull(data).getString("agentEmail");
        phonestr=data.getString("agentPhone");
        addrstr=data.getString("agentAddress");
        namestr=data.getString("agentName");
        passtr=data.getString("agentPass");

        if (verify())
        {
                name.setText(namestr);
                email.setText(emailstr);
                phone.setText(phonestr);
                address.setText(addrstr);
                pass.setText(passtr);
        }

        return v;
    }


    private boolean verify()
    {
        if(Objects.requireNonNull(namestr).isEmpty())
        {
            Toast.makeText(getActivity(), "Agent Name not identified", Toast.LENGTH_SHORT).show();
            return false;
        }else  if (emailstr.isEmpty())
        {
            Toast.makeText(getActivity(), "Agent Email not identified", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if (phonestr.isEmpty())
        {
            Toast.makeText(getActivity(), "Agent Phone not identified", Toast.LENGTH_SHORT).show();
            return false;
        }else  if (addrstr.isEmpty())
        {
            Toast.makeText(getActivity(), "Agent Address not identified", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if (passtr.isEmpty())
        {
            Toast.makeText(getActivity(), "Agent Password not identified", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
