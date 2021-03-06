package com.example.ashitosh.moneylender.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Models.custModel;
import com.example.ashitosh.moneylender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustDetailFragment extends Fragment {


    private Button loanviewBtn,addLoanBtn;
    private TextView Custname,address,phone,email,AccNo,Loan,CustAdhar,CustDob,GuarantorName,GuarantorMob,GuarantorAddr;
    private String activity,totalLoan,agentEmail;
    private FirebaseFirestore fs;
    private String CurrentEmail;
    private Bundle data;
    public CustDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_cust_detail, container, false);


        loanviewBtn=v.findViewById(R.id.LoanBtn);
        addLoanBtn=v.findViewById(R.id.AddLoanBtn);

        Custname=v.findViewById(R.id.CustDetailName);
        address=v.findViewById(R.id.CustDetailAddr);
        phone=v.findViewById(R.id.CustDetailPhone);
        email=v.findViewById(R.id.CustDetailEmail);
        Loan=v.findViewById(R.id.CustDetailLoans);
        AccNo=v.findViewById(R.id.CustDetailAcc);

        CustAdhar=v.findViewById(R.id.CustDetailAdhar);
        CustDob=v.findViewById(R.id.CustDetailDob);
        GuarantorName=v.findViewById(R.id.GuarantorDetailName);
        GuarantorMob=v.findViewById(R.id.GuarantorDetailMob);
        GuarantorAddr=v.findViewById(R.id.GuarantorDetailAddr);


        fs= FirebaseFirestore.getInstance();

        data=getArguments();
        activity= Objects.requireNonNull(data).getString("fragment");

        agentEmail=data.getString("AgentEmail");

        Custname.setText(Objects.requireNonNull(data).getString("CustName"));
        address.setText(data.getString("Address"));
        phone.setText(data.getString("Phone"));
        email.setText(data.getString("CustEmail"));
        Loan.setText(data.getString("TotalLoans"));
        AccNo.setText(data.getString("Account"));

        totalLoan=data.getString("TotalLoans");

        Toast.makeText(getActivity(), "Total Loans: "+totalLoan, Toast.LENGTH_SHORT).show();

        CustAdhar.setText(data.getString("CustAdharId"));
        CustDob.setText(data.getString("CustDob"));
        GuarantorName.setText(data.getString("GuarantorName"));
        GuarantorMob.setText(data.getString("GuarantorMob"));
        GuarantorAddr.setText(data.getString("GuarantorAddr"));


        CurrentEmail= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        loanviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data=new Bundle();
                data.putString("AccountNo", String.valueOf(AccNo.getText().toString()));

                data.putString("AgentEmail",agentEmail);

                LoanFragment fragment=new LoanFragment();

                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(fragment,"Custhead").addToBackStack("head");

                if(activity.equals("Owner")) {
                    fragmentTransaction.replace(R.id.mainFrame, fragment);
                }
                else
                {
                    fragmentTransaction.replace(R.id.AgentmainFrame, fragment);

                }
                fragmentTransaction.commit();
            }
        });


        if(CurrentEmail.equals("ashitosh.bhade@gmail.com") || CurrentEmail.equals("gavadedu.dhananjay@gmail.com") || CurrentEmail.equals("dj5@gmail.com") )
        {
              addLoanBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            addLoanBtn.setVisibility(View.GONE);
        }

        addLoanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!data.isEmpty()) {
                    Bundle data1 = new Bundle();
                    data1.putString("AccountNo", String.valueOf(AccNo.getText().toString()));

                    data1.putString("BtnId", "AddLoan");
                    data1.putString("TotalLoan", totalLoan);


                    Objects.requireNonNull(data1).putString("CustName", Objects.requireNonNull(data).getString("CustName"));
                    data1.putString("CustEmail", data.getString("CustEmail"));
                    data1.putString("CustPhone", data.getString("Phone"));
                    data1.putString("CustAddr", data.getString("Address"));
                    data1.putString("CustAdhar", data.getString("CustAdharId"));
                    data1.putString("CustDob", data.getString("CustDob"));
                    data1.putString("GuarantorName", data.getString("GuarantorName"));
                    data1.putString("GuarantorMob", data.getString("GuarantorMob"));
                    data1.putString("GurantorAddr", data.getString("GuarantorAddr"));

                    CustRegNextFragment fragment = new CustRegNextFragment();

                    fragment.setArguments(data1);

                    android.support.v4.app.FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(fragment, "Custhead").addToBackStack("head");

                    if (activity.equals("Owner")) {
                        fragmentTransaction.replace(R.id.mainFrame, fragment);
                    } else {
                        fragmentTransaction.replace(R.id.AgentmainFrame, fragment);

                    }
                    fragmentTransaction.commit();
                }
                else
                {
                    Toast.makeText(getActivity(), "Customer data is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

}
