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


    private EditText name,email,phone,address,adharID,Dob,GuarantorName,GuarantorMob,GuarantorAddr;
    private Button next;
    private String nameStr,emailStr,phoneStr,addressStr,adharStr,dobStr,gNameStr,gMobStr,gAddrStr;
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
        adharID=v.findViewById(R.id.CustAdharId);
        Dob=v.findViewById(R.id.CustDOB);

        GuarantorName=v.findViewById(R.id.GuarantorName);
        GuarantorMob=v.findViewById(R.id.GuarantorMob);
        GuarantorAddr=v.findViewById(R.id.GuarantorAddr);

        next=v.findViewById(R.id.Next);

        fs=FirebaseFirestore.getInstance();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                nameStr=name.getText().toString();
                emailStr=email.getText().toString();
                phoneStr=phone.getText().toString();
                addressStr=address.getText().toString();

                adharStr=adharID.getText().toString();
                dobStr=Dob.getText().toString();
                gNameStr=GuarantorName.getText().toString();
                gMobStr=GuarantorMob.getText().toString();
                gAddrStr=GuarantorAddr.getText().toString();


                if(isValid())
                {

                    Bundle data=new Bundle();

                    data.putString("CustName",nameStr);
                    data.putString("CustEmail",emailStr);
                    data.putString("CustPhone",phoneStr);
                    data.putString("CustAddr",addressStr);
                    data.putString("BtnId","FirstLoan");

                    data.putString("CustAdhar",adharStr);
                    data.putString("CustDob",dobStr);
                    data.putString("GuarantorName",gNameStr);
                    data.putString("GuarantorMob",gMobStr);
                    data.putString("GurantorAddr",gAddrStr);

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
        else if(adharStr.isEmpty())
        {
            adharID.setError("Enter Adhar Id");
            adharID.requestFocus();
            return false;
        }
        else if(!adharStr.isEmpty())
        {
            if (adharStr.length()!=12) {
                adharID.setError("Invalid Adhar Id");
                adharID.requestFocus();
                return false;
            }
        }
        else if(dobStr.isEmpty())
        {
            Dob.setError("Enter DOB");
            Dob.requestFocus();
            return false;
        }
        else if(gNameStr.isEmpty())
        {
            GuarantorName.setError("Enter Guarantor Name");
            GuarantorName.requestFocus();
            return false;
        }
        else if(gAddrStr.isEmpty())
        {
            GuarantorAddr.setError("Enter Guarantor Address");
            GuarantorAddr.requestFocus();
            return false;
        }
        else if(gMobStr.isEmpty())
        {
            GuarantorMob.setError("Enter Mobile No.");
            GuarantorMob.requestFocus();
            return false;
        }
        else if(gMobStr.length()!=10)
        {
            GuarantorMob.setError("Invalid Mobile No.");
            GuarantorMob.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
