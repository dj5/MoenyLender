package com.example.ashitosh.moneylender.Fragments;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAgentFragment extends Fragment {

    private EditText name,email,phone,address;
    private FirebaseFirestore fs;
    private CollectionReference ref;
    private Button reg;
    private String nameStr,emailStr,phoneStr,addrStr;
    private Map<String,Object> fdata;
    private  RelativeLayout layout;
    private ProgressDialog pd;

    private FirebaseAuth f_auth;
    public AddAgentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_add_agent, container, false);

        layout=v.findViewById(R.id.relativeLayout);
        name=v.findViewById(R.id.AgentName);
        email=v.findViewById(R.id.AgentEmail);
        address=v.findViewById(R.id.AgentAddr);
        phone=v.findViewById(R.id.AgentPhone);
        reg=v.findViewById(R.id.AgentRegBtn);

        pd=new ProgressDialog(getActivity());

        f_auth=FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAgent();

            }
        });


        return v;
    }

    private void registerAgent() {


        fs=FirebaseFirestore.getInstance();

        ref=fs.collection("MoneyLender");

        fdata=new HashMap<>();

        nameStr=name.getText().toString();
        emailStr=email.getText().toString();
        phoneStr=phone.getText().toString();
        addrStr=address.getText().toString();

        if(isDataFilled())
        {

            pd.setTitle("Registration");
            pd.setMessage("registering Agent");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            fdata.put("Name",nameStr);
            fdata.put("Email",emailStr);
            fdata.put("Phone",phoneStr);
            fdata.put("Address",addrStr);


            uploadData();


            f_auth.createUserWithEmailAndPassword(emailStr,"12345678")
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(getActivity(),"Agent Account Created",Toast.LENGTH_LONG).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                    Toast.makeText(getActivity(),"failed to register agent",Toast.LENGTH_LONG).show();

                }
            });


        }




    }

    private void uploadData() {

        ref.document("Agent_"+emailStr).set(fdata, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {


                            final Snackbar snackbar=Snackbar.make(layout,"data uploaded",Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                        }
                                    });

                            // Changing message text color
                            snackbar.setActionTextColor(Color.RED);

                            // Changing action button text color
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.GREEN);
                            snackbar.setDuration(4000).dismiss();
                            snackbar.show();


                            pd.setMessage("successfully uploaded");
                            pd.hide();
                            pd.dismiss();

                            Toast.makeText(getActivity(),"Agent data successfully uploaded",Toast.LENGTH_LONG).show();


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity(),"failed to upload Agent data",Toast.LENGTH_LONG).show();

                pd.setMessage("failed to upload");
                pd.hide();
                pd.dismiss();

            }
        });
    }

    private boolean isDataFilled() {

        if(nameStr.isEmpty())
        {
            name.requestFocus();
            name.setError("Enter Name");
            return false;
        }

        if(emailStr.isEmpty())
        {
            email.requestFocus();
            email.setError("Enter Email");
            return false;
        }
        else if(!isValidEmail(emailStr))
        {
            email.setError("Invalid Email Pattern");
            email.requestFocus();
            return false;
        }

        if (phoneStr.isEmpty())
        {
            phone.requestFocus();
            phone.setError("Enter Phone No.");
            return false;
        }

        if (addrStr.isEmpty())
        {
            address.requestFocus();
            address.setError("Enter The Address");
            return false;
        }

        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
