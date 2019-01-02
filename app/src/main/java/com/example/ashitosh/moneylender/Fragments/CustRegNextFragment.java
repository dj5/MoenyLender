package com.example.ashitosh.moneylender.Fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustRegNextFragment extends Fragment  {


    private Spinner spinner,agentSpinner;
    private EditText reqamount,filedamount,interest;
    private ArrayAdapter<String> adapter,agentAdapter;
    private List<String> type,agentList,agentEmailList;
    private Button regbtn,dor;
    private FirebaseFirestore fs;

    private String typeStr,agentEmail,reqamountStr,expInstallStr,interestStr,doiStr,dorStr,agentnameStr,filedamountStr;

    private String AccountNo,Address,custName,phone,custemail,AmountToReturn,PendingAmount;

    private ProgressDialog pd;
    private int loanno=0;

    public Date sdate,edate;
    private String totalLoan;
    private String btnId,addLoanAccNo;

    private int mYear, mMonth, mDay;
    private int issueMonth,returnMonth,issueYear,returnYear;
    public CustRegNextFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_cust_reg_next, container, false);

        spinner=v.findViewById(R.id.CustInstallType);
        agentSpinner=v.findViewById(R.id.CustAgentName);



        reqamount=v.findViewById(R.id.CustReqAmount);
        filedamount=v.findViewById(R.id.CustFiledAmount);
        interest=v.findViewById(R.id.CustRegInterest);
      //  doi=v.findViewById(R.id.CustDOI);
        dor=v.findViewById(R.id.CustDOR);
        regbtn=v.findViewById(R.id.CustRegBtn);

        fs=FirebaseFirestore.getInstance();

        pd=new ProgressDialog(this.getActivity());

        agentList=new ArrayList<String>();
        agentEmailList=new ArrayList<>();
        agentEmailList.add("select Agent");

        type=new ArrayList<String>();
        type.add("Daily");
        type.add("Monthly");

        adapter=new ArrayAdapter<>(this.getActivity(),android.R.layout.simple_spinner_item,type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        totalLoan="0";

        //Fetching Custoer Details from previous fragment
        final Bundle data=getArguments();

        custName= Objects.requireNonNull(data).getString("CustName");
        custemail=data.getString("CustEmail");
        phone=data.getString("CustPhone");
        Address=data.getString("CustAddr");

        btnId=data.getString("BtnId");
        addLoanAccNo=data.getString("AccountNo");

        totalLoan=data.getString("TotalLoan");

        Toast.makeText(getActivity().getApplicationContext(), "BtnId: "+btnId, Toast.LENGTH_SHORT).show();

        //Selects date of Issue


        JodaTimeAndroid.init(this.getActivity());

        DateTimeZone zone=DateTimeZone.forID("Asia/Kolkata");
        DateTime dateTime=new DateTime(zone);
        doiStr=dateTime.toLocalDate().toString();

        try {

            sdate=new SimpleDateFormat("dd/MM/yyyy").parse(doiStr);
            doiStr=sdate.toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }


//Selects date Of Return

        dor.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @SuppressLint("SimpleDateFormat")
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                returnMonth=month;

                                dorStr=year+"-"+(month+1)+"-"+dayOfMonth;

                                try {
                                    edate=new SimpleDateFormat("yyyy-MM-dd").parse(dorStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                dor.setText("DOR="+dorStr);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }

        });


        //Fetch Agent Names
        fs.collection("MoneyLender").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e!=null)
                {
                    Log.e("Error: "+e.getMessage(),"Error");
                }
                else {

                    for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {

                        if (doc.getType().equals(DocumentChange.Type.ADDED)) {
                            agentList.add(doc.getDocument().getString("Name"));
                            agentEmailList.add(doc.getDocument().getString("Email"));
                        }
                    }

                    agentAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,agentList);
                    agentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    agentSpinner.setAdapter(agentAdapter);

                }
            }
        });





        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeStr=parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(),"type selected: "+typeStr,Toast.LENGTH_LONG).show();

                if(typeStr.equals("Daily"))
                {
                    interest.setVisibility(View.GONE);
                }
                if(typeStr.equals("Monthly"))
                {
                    interest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        agentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                agentnameStr=parent.getItemAtPosition(position).toString();

                agentEmail=agentEmailList.get(position+1);
                Toast.makeText(getActivity(),"agent Email: "+agentEmail+"agent name:"+agentnameStr,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        //Uploading customer Insformation


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pd.setCanceledOnTouchOutside(false);
                pd.setMessage("Please wait until registering customer");
                pd.show();

                if (btnId.equals("FirstLoan")) {
                    fs.collection("AccNo").document("CurrentAcc").get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        pd.setMessage("Account no. fetched");

                                        AccountNo = task.getResult().getString("Accno");

                                        if (!Objects.requireNonNull(AccountNo).isEmpty()) {

                                            Toast.makeText(getActivity(), "Account no.fetched", Toast.LENGTH_SHORT).show();

                                            Map<String, Object> Custdata = new HashMap<>();

                                            Custdata.put("AccountNo", AccountNo);
                                            Custdata.put("CustEmail", custemail);
                                            Custdata.put("CustAddr", Address);
                                            //   Custdata.put("agent_name",agentnameStr);
                                            Custdata.put("CustName", custName);
                                            Custdata.put("CustPhone", phone);
                                            Custdata.put("CustTotalLoan","1");

                                            //Registering Clint
                                            fs.collection("clients").document("client_" + AccountNo).set(Custdata, SetOptions.merge())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {


                                                                pd.setMessage("customer registered");

                                                                Toast.makeText(getActivity(), "Customer Registed Successfully", Toast.LENGTH_LONG).show();

                                                            }

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    pd.setMessage("failed to register customer");
                                                    pd.hide();
                                                    pd.dismiss();

                                                    Toast.makeText(getActivity(), "Failed to register Customer: " + e.getMessage(), Toast.LENGTH_LONG).show();

                                                }


                                            });


                                            //Cust Loan Registration
                                            reqamountStr = reqamount.getText().toString();
                                            interestStr = interest.getText().toString();
                                            filedamountStr = filedamount.getText().toString();

                                            if (typeStr.equals("Daily")) {
                                                interestStr = "0";
                                            }

                                            if (isValid()) {
                                                Custdata.put("Status", "1");
                                                fs.collection("Agents").document("Agent_" + agentEmail).collection(typeStr).document("cust_" + String.valueOf(AccountNo)).set(Custdata, SetOptions.merge())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {


                                                                    Map<String,Object> data=new HashMap<>();
                                                                    data.put("Status","1");



                                                                     fs.collection("Agents").document("Agent_"+agentEmail).collection(typeStr).document("cust_"+ String.valueOf(Integer.parseInt(AccountNo)-1))
                                                                            .collection("Loans").document("loan_1")
                                                                            .set(data,SetOptions.merge())
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        Toast.makeText(getActivity().getApplicationContext(), "Successfully loan added", Toast.LENGTH_SHORT).show();
                                                                                        pd.dismiss();
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        Toast.makeText(getActivity().getApplicationContext(), "failed to add loan", Toast.LENGTH_SHORT).show();
                                                                                        pd.dismiss();
                                                                                    }
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getActivity().getApplicationContext(), "failed to upload loan "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            pd.dismiss();
                                                                        }
                                                                    });


                                                                    pd.setMessage("agent assigned");
                                                                    Toast.makeText(getActivity(), "Agent Customer Registed Successfully", Toast.LENGTH_LONG).show();

                                                                }

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pd.setMessage("failed to assign agent");
                                                        pd.hide();
                                                        pd.dismiss();

                                                        Toast.makeText(getActivity(), "Failed to register Agent Customer: " + e.getMessage(), Toast.LENGTH_LONG).show();

                                                    }
                                                });


                                                //uploading loan details

                                                Map<String, Object> loanData = new HashMap<>();


                                                if (typeStr.equals("Daily")) {
                                                    AmountToReturn = filedamountStr;
                                                    DateTime start = new DateTime(sdate);
                                                    DateTime end = new DateTime(edate);

                                                    int DaysDuration = Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
                                                    Double Amount = Double.parseDouble(AmountToReturn) / DaysDuration;

                                                    DecimalFormat dec = new DecimalFormat("#0.00");

                                                    expInstallStr = String.valueOf(dec.format(Amount));
                                                } else if (typeStr.equals("Monthly")) {

                                                    DateTime start = new DateTime(sdate);
                                                    DateTime end = new DateTime(edate);


                                                    int months=Months.monthsBetween(start.toLocalDate(),end.toLocalDate()).getMonths();

                                                    Double rate = Double.parseDouble(interestStr);

                                                    Double SI = (Double.parseDouble(filedamountStr) * rate * months) / (100);

                                                    Double Amount = Double.parseDouble(filedamountStr) + SI;

                                                    DecimalFormat dec = new DecimalFormat("#0.00");

                                                    AmountToReturn = String.valueOf(dec.format(Amount));

                                                    //calculating expected Amount

                                                    Double expectAmount = Amount / months;

                                                    expInstallStr = String.valueOf(dec.format(expectAmount));

                                                }

                                                PendingAmount = "0";

                                                loanData.put("LoanId","1");
                                                loanData.put("AgentName", agentnameStr);
                                                loanData.put("DOI", doiStr);
                                                loanData.put("DOR", dorStr);

                                                loanData.put("ExpectedInstallment", expInstallStr);

                                                loanData.put("FiledAmount", filedamountStr);
                                                loanData.put("Interest", interestStr);
                                                loanData.put("LoanType", typeStr);
                                                loanData.put("ReqAmount", reqamountStr);
                                                loanData.put("AmountToReturn", AmountToReturn);
                                                loanData.put("PendingAmount", PendingAmount);

                                                if (Double.parseDouble(AmountToReturn) > 0) {
                                                    loanData.put("Status", "1");
                                                } else {
                                                    loanData.put("Status", "0");
                                                }


                                                fs.collection("clients").document("client_" + AccountNo).collection("loans").document("loan_1" )
                                                        .set(loanData, SetOptions.merge())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(getActivity(), "Customer Loan added Successfully", Toast.LENGTH_LONG).show();
                                                                    loanno++;

                                                                }


                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        pd.setMessage("Failed to register customer");
                                                        pd.hide();
                                                        pd.dismiss();

                                                        Toast.makeText(getActivity(), "Failed to register Customer: " + e.getMessage(), Toast.LENGTH_LONG).show();

                                                    }


                                                });


//Updating account no.

                                                Map<String, String> acc = new HashMap<>();

                                                AccountNo = String.valueOf(Integer.parseInt(AccountNo) + 1);

                                                acc.put("Accno", AccountNo);

                                                fs.collection("AccNo").document("CurrentAcc").set(acc, SetOptions.merge())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getActivity(), "acc no. updated", Toast.LENGTH_SHORT).show();

                                                                    pd.hide();
                                                                    pd.dismiss();
                                                                    sendTomain();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        pd.setMessage("Failed to register customer");
                                                        pd.hide();
                                                        pd.dismiss();

                                                        Toast.makeText(getActivity(), "Failed to update Account no.: " + e.getMessage(), Toast.LENGTH_LONG).show();

                                                    }


                                                });


                                            } else {
                                                Toast.makeText(getActivity(), "invalid data", Toast.LENGTH_LONG).show();

                                            }

                                        } else {
                                            Toast.makeText(getActivity(), "Account no. could not fetched", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });     //end of Acc no fetch
                }
  //Add Loan
                else if(btnId.equals("AddLoan")) {

                    fs.collection("AccNo").document("CurrentAcc").get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {


                                            //Cust Loan Registration
                                            reqamountStr = reqamount.getText().toString();
                                            interestStr = interest.getText().toString();
                                            filedamountStr = filedamount.getText().toString();

                                            if (typeStr.equals("Daily")) {
                                                interestStr = "0";
                                            }

                                            if (isValid()) {

 //Adds Multiple loans
                                                Map<String,Object> data=new HashMap<>();
                                                data.put("Status","1");

                                                fs.collection("Agents").document("Agent_"+agentEmail).collection(typeStr).document("cust_"+addLoanAccNo)
                                                        .collection("Loans").document("loan_"+String.valueOf(Integer.parseInt(totalLoan)+1))
                                                        .set(data,SetOptions.merge())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(getActivity().getApplicationContext(), "Successfully loan added", Toast.LENGTH_SHORT).show();
                                                                    pd.dismiss();
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(getActivity().getApplicationContext(), "failed to add loan", Toast.LENGTH_SHORT).show();
                                                                    pd.dismiss();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity().getApplicationContext(), "failed to upload loan "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                    }
                                                });


                                                //uploading loan details

                                                Map<String, Object> loanData = new HashMap<>();


                                                if (typeStr.equals("Daily")) {
                                                    AmountToReturn = filedamountStr;
                                                    DateTime start = new DateTime(sdate);
                                                    DateTime end = new DateTime(edate);

                                                    int DaysDuration = Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
                                                    Double Amount = Double.parseDouble(AmountToReturn) / DaysDuration;

                                                    DecimalFormat dec = new DecimalFormat("#0.00");

                                                    expInstallStr = String.valueOf(dec.format(Amount));
                                                } else if (typeStr.equals("Monthly")) {



                                                    DateTime start = new DateTime(sdate);
                                                    DateTime end = new DateTime(edate);


                                                    int months=Months.monthsBetween(start.toLocalDate(),end.toLocalDate()).getMonths();

                                               //     int Monthduration = monthsBetweenDates(sdate, edate);   //duration in months

                                                    Double rate = Double.parseDouble(interestStr);

                                                    Double SI = (Double.parseDouble(filedamountStr) * rate * months) / (100);

                                                    Double Amount = Double.parseDouble(filedamountStr) + SI;

                                                    DecimalFormat dec = new DecimalFormat("#0.00");

                                                    AmountToReturn = String.valueOf(dec.format(Amount));

                                                    //calculating expected Amount

                                                    Double expectAmount = Amount /  months;

                                                    expInstallStr = String.valueOf(dec.format(expectAmount));

                                                }

                                                PendingAmount = "0";

                                                loanData.put("LoanId",String.valueOf(Integer.parseInt(totalLoan)+1));
                                                loanData.put("AgentName", agentnameStr);
                                                loanData.put("DOI", doiStr);
                                                loanData.put("DOR", dorStr);

                                                loanData.put("ExpectedInstallment", expInstallStr);

                                                loanData.put("FiledAmount", filedamountStr);
                                                loanData.put("Interest", interestStr);
                                                loanData.put("LoanType", typeStr);
                                                loanData.put("ReqAmount", reqamountStr);
                                                loanData.put("AmountToReturn", AmountToReturn);
                                                loanData.put("PendingAmount", PendingAmount);

                                                if (Double.parseDouble(AmountToReturn) > 0) {
                                                    loanData.put("Status", "1");
                                                } else {
                                                    loanData.put("Status", "0");
                                                }


                                                fs.collection("clients").document("client_" + addLoanAccNo).collection("loans").document("loan_"+String.valueOf(Integer.parseInt(totalLoan)+1))
                                                        .set(loanData, SetOptions.merge())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(getActivity(), "Customer Loan added Successfully", Toast.LENGTH_LONG).show();
                                                                    //loanno++;

                                                                    fs.collection("clients").document("client_"+addLoanAccNo).update("CustTotalLoan",String.valueOf(Integer.parseInt(totalLoan)+1))
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        sendTomain();
                                                                                    }
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getActivity().getApplicationContext(), "Error in updating Total Loans "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }


                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        pd.setMessage("Failed to register customer");
                                                        pd.hide();
                                                        pd.dismiss();
                                                        sendTomain();

                                                        Toast.makeText(getActivity(), "Failed to register Customer: " + e.getMessage(), Toast.LENGTH_LONG).show();

                                                    }


                                                });


                                            } else {
                                                Toast.makeText(getActivity(), "invalid data", Toast.LENGTH_LONG).show();

                                            }

                                        } else {
                                            Toast.makeText(getActivity(), "Account no. could not fetched", Toast.LENGTH_SHORT).show();

                                        }

                                    }


                            });

                }
            }


        }); //end of onclicklistner



        return v;
    }


    private void sendTomain() {

        SearchFragment fragment=new SearchFragment();

        android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(fragment,"Custhead").addToBackStack("head");

        fragmentTransaction.replace(R.id.mainFrame,fragment);

        fragmentTransaction.commit();

    }



    private boolean isValid() {

        if(reqamountStr.isEmpty())
        {
            reqamount.setError("Enter Requested Amount");
            reqamount.requestFocus();
            return false;
        }
     /*  else if(doiStr.isEmpty())
        {
            doi.setError("Enter Date OF Issue");
           doi.requestFocus();
            return false;
        }
        */
        else if(dorStr.isEmpty())
        {
            dor.setError("Enter Date Of Return");
            dor.requestFocus();
            return false;
        }
        else if(interestStr.isEmpty())
        {
            interest.setError("Enter Interest Rate");
            interest.requestFocus();
            return false;
        }
        else if(filedamountStr.isEmpty())
        {
            filedamount.setError("Enter Filed Amount");
            filedamount.requestFocus();
            return false;
        }
        else if(typeStr.isEmpty())
        {
            spinner.requestFocus();
            Toast.makeText(getActivity(),"Select Installment Type",Toast.LENGTH_LONG).show();
            return false;
        }

        else if(agentEmail.isEmpty())
        {

            agentSpinner.requestFocus();
            Toast.makeText(getActivity(),"Select Agent Name",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
