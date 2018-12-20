package com.example.ashitosh.moneylender.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCustFragment extends Fragment {


    private EditText name,email,phone,address;
    private Button next;
    private String nameStr,emailStr,phoneStr,addressStr;
    private FirebaseFirestore fs;

    public AddCustFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_add_cust, container, false);

        name=v.findViewById(R.id.CustName);
        email=v.findViewById(R.id.CustEmail);
        phone=v.findViewById(R.id.CustPhone);
        address=v.findViewById(R.id.CustAddr);

        next=v.findViewById(R.id.Next);

        fs=FirebaseFirestore.getInstance();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                nameStr=name.getText().toString();
                emailStr=email.getText().toString();
                phoneStr=phone.getText().toString();
                addressStr=address.getText().toString();


                if(isValid())
                {

                    Bundle data=new Bundle();

                    data.putString("CustName",nameStr);
                    data.putString("CustEmail",emailStr);
                    data.putString("CustPhone",phoneStr);
                    data.putString("CustAddr",addressStr);
                    data.putString("BtnId","FirstLoan");



                    CustRegNextFragment fragment=new CustRegNextFragment();

                    fragment.setArguments(data);

                    android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction().add(fragment,"RegCust").addToBackStack("RegCust");

                    fragmentTransaction.replace(R.id.mainFrame,fragment);

                    fragmentTransaction.commit();

                }
            }
        });


        return v;

    }

    private boolean isValid() {

        if(nameStr.isEmpty())
        {
            name.setError("Enter Name");
            name.requestFocus();
            return false;
        }
        else if(emailStr.isEmpty())
        {
            email.setError("Enter Email");
            email.requestFocus();
            return false;
        }
        else if(!isValidEmail(emailStr))
        {
            email.setError("Invalid Email");
            email.requestFocus();
            return false;
        }
        else if(phoneStr.isEmpty())
        {
            phone.setError("Enter Phone no");
            phone.requestFocus();
            return false;
        }
        else if(phoneStr.length()!=10)
        {
            phone.setError("Invalid Mobile No.");
            phone.requestFocus();
            return false;
        }
        else if(addressStr.isEmpty())
        {
            address.setError("Enter Address");
            address.requestFocus();
            return false;
        }


        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
