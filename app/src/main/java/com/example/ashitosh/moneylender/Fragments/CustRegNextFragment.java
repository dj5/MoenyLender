package com.example.ashitosh.moneylender.Fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
    private EditText reqamount,discount,LfNo,interest,FiledAmount,LoanMonths,FileClosingDate;
    private ArrayAdapter<String> adapter,agentAdapter;
    private List<String> type,agentList,agentEmailList;
    private Button regbtn,dor,doi,FileOpeningDate;
    private FirebaseFirestore fs;

    private String typeStr,agentEmail,reqamountStr,expInstallStr,interestStr,discountstr,doiStr,dorStr,agentnameStr,filedamountStr;

    private String AccountNo,AdharId,CustDob,GuarantorName,GuarantorMob,GuarantorAddr,Address,custName,phone,custemail,AmountToReturn,PendingAmount;


    private ProgressDialog pd;
    private int loanno=0;
    private RadioGroup group;
    private RadioButton hundred,twoHundred;
    public Date sdate,edate;
    public LocalDate StartingDate,ClosingDate;
    private String totalLoan,FileOpeningDateStr,FileClosingDateStr,MonthStr;
    private String btnId,addLoanAccNo,days;
    private int mYear, mMonth, mDay;
    private int issueMonth,returnMonth,issueYear,returnYear;
    private   DateTimeZone zone;
    private  DateTime dateTime;

    public CustRegNextFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_cust_reg_next, container, false);

        spinner=v.findViewById(R.id.CustInstallType);
        agentSpinner=v.findViewById(R.id.CustAgentName);



        reqamount=v.findViewById(R.id.CustReqAmount);
        discount=v.findViewById(R.id.CustDiscount);
        FiledAmount=v.findViewById(R.id.CustFiledAmount);
        LfNo=v.findViewById(R.id.LFNO);

        interest=v.findViewById(R.id.CustRegInterest);
        //  doi=v.findViewById(R.id.CustDOI);
        LoanMonths=v.findViewById(R.id.LoanMonth);
        regbtn=v.findViewById(R.id.CustRegBtn);

        FileOpeningDate=v.findViewById(R.id.FileOpeningDate);
        FileClosingDate=v.findViewById(R.id.FileClosingDate);

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

        group=v.findViewById(R.id.CustRadioGrp);
        hundred=v.findViewById(R.id.HundredDays);
        twoHundred=v.findViewById(R.id.TwoHundredDays);

        //Fetching Custoer Details from previous fragment
        final Bundle data=getArguments();

        custName= Objects.requireNonNull(data).getString("CustName");
        custemail=data.getString("CustEmail");
        phone=data.getString("CustPhone");
        Address=data.getString("CustAddr");

        btnId=data.getString("BtnId");
        addLoanAccNo=data.getString("AccountNo");

        totalLoan=data.getString("TotalLoan");

        AdharId=data.getString("CustAdhar");
        CustDob=data.getString("CustDob");
        GuarantorName=data.getString("GuarantorName");
        GuarantorMob=data.getString("GuarantorMob");
        GuarantorAddr=data.getString("GurantorAddr");

        Toast.makeText(getActivity().getApplicationContext(), "BtnId: "+btnId, Toast.LENGTH_SHORT).show();

        //Selects date of Issue


        JodaTimeAndroid.init(this.getActivity());

        zone=DateTimeZone.forID("Asia/Kolkata");
        dateTime=new DateTime(zone);

   /*
        doiStr=dateTime.toLocalDate().toString();

        try {

            //      sdate=new SimpleDateFormat("dd/MM/yyyy").parse(doiStr);
            //      doiStr=sdate.toString();

            if (!doiStr.isEmpty())
            {
                sdate=new SimpleDateFormat("dd/MM/yyyy").parse(doiStr);
                FileOpeningDate.setText(doiStr);

            }else
            {
                FileOpeningDate.setError("Could Not Found Date");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
*/

        FileOpeningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                issueMonth=month+1;

                                month=month+1;

                                if(month<10) {
                                    doiStr = year + "-0" + month + "-" + dayOfMonth;
                                }
                                else
                                {
                                    doiStr = year + "-0" + month + "-" + dayOfMonth;
                                }

                                if (!doiStr.isEmpty())
                                {
                                    try {
                                        sdate=new SimpleDateFormat("dd/MM/yyyy").parse(doiStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    FileOpeningDate.setText(doiStr);

                                }else
                                {
                                    FileOpeningDate.setError("Could Not Found Date");
                                }


                            }

                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });



        LoanMonths.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (LoanMonths.getText().toString().isEmpty()) {
                    LoanMonths.setError("Enter Months");
                    LoanMonths.setFocusable(true);
                } else if (Integer.parseInt(LoanMonths.getText().toString()) <= 0) {
                    LoanMonths.setError("Months Should be greater");
                    LoanMonths.setEnabled(true);
                } else
                {
                    if (doiStr!=null) {
                    //   FileOpeningDate.setText(doiStr);
                    //   FileOpeningDate.setEnabled(false);

                    LocalDate temp = new LocalDate(doiStr);

                    LocalDate end = temp.plusMonths(Integer.parseInt(LoanMonths.getText().toString()));
                    dorStr = end.toString();

                    if (!dorStr.isEmpty()) {
                        FileClosingDate.setText(dorStr);
                        FileClosingDate.setFocusable(false);

                        try {
                            edate = new SimpleDateFormat("yyyy-MM-dd").parse(dorStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        LoanMonths.setError("Could Not Found File Closing date");
                        LoanMonths.setFocusable(false);
                    }
                    }
                    else
                    {
                        LoanMonths.setError("First select Date of Issue");
                    }
                }
            }
        });


//Selects date Of Return
/*
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
*/
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
                    ClearForm();

                    interest.setVisibility(View.GONE);
                    group.setVisibility(View.VISIBLE);

                    LoanMonths.setEnabled(false);
                    LoanMonths.setVisibility(View.GONE);

                    discount.setEnabled(true);
                    discount.setVisibility(View.VISIBLE);

                }
                if(typeStr.equals("Monthly"))
                {
                    ClearForm();
                    discount.setEnabled(false);
                    discount.setVisibility(View.GONE);

                    LoanMonths.setEnabled(true);
                    LoanMonths.setVisibility(View.VISIBLE);
                    interest.setVisibility(View.VISIBLE);
                    group.setVisibility(View.GONE);



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        hundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DateTime dor=dateTime.plusDays(100);
                dorStr=dor.getYear()+"-"+dor.getMonthOfYear()+"-"+dor.getDayOfMonth();

                try {
                    edate=new SimpleDateFormat("yyyy-MM-dd").parse(dorStr);

                    FileOpeningDate.setText(doiStr);
                  //  FileOpeningDate.setEnabled(false);

                    FileClosingDate.setText(dorStr);
                    FileClosingDate.setEnabled(false);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        twoHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime dor=dateTime.plusDays(200);
                dorStr=dor.getYear()+"-"+dor.getMonthOfYear()+"-"+dor.getDayOfMonth();

                try {
                    edate=new SimpleDateFormat("yyyy-MM-dd").parse(dorStr);

                    FileOpeningDate.setText(doiStr);
                //    FileOpeningDate.setEnabled(false);

                    FileClosingDate.setText(dorStr);
                    FileClosingDate.setEnabled(false);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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


        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {

                if (typeStr.equals("Daily")) {
                    discount.setError(null);

                    if (reqamount.getText().toString().isEmpty()) {
                        reqamount.setError("Enter Requested Amount");
                        reqamount.setFocusable(true);
                    } else if (discount.getText().toString().isEmpty()) {
                        discount.setError("Enter Discount Amount");
                        discount.setFocusable(true);
                    } else if (Double.parseDouble(discount.getText().toString()) <= 0) {
                        discount.setError("Discount shound be Greater");
                        discount.setFocusable(true);
                    } else if (Double.parseDouble(reqamount.getText().toString()) < Double.parseDouble(discount.getText().toString())) {
                        discount.setError("Discount Amount is Greater Than Requested Amount");
                        discount.setFocusable(true);
                    } else if (Double.parseDouble(reqamount.getText().toString()) == Double.parseDouble(discount.getText().toString())) {
                        discount.setError("Discount Amount is same as Requested Amount");
                        discount.setFocusable(true);
                    } else {

                        double filedAmount = Double.parseDouble(reqamount.getText().toString()) - Double.parseDouble(s.toString());
                        FiledAmount.setText(String.valueOf(filedAmount));
                        filedamountStr = FiledAmount.getText().toString();
                        FiledAmount.setEnabled(false);
                    }


                }
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
                                            Custdata.put("CustTotalLoan", "1");
                                            Custdata.put("CustAdharId",AdharId);
                                            Custdata.put("CustDob",CustDob);
                                            Custdata.put("GuarantorName",GuarantorName);
                                            Custdata.put("GuarantorMob",GuarantorMob);
                                            Custdata.put("GuarantorAddr",GuarantorAddr);

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

                                            if (typeStr.equals("Daily")) {
                                                interestStr = "0";
                                                discountstr=discount.getText().toString();
                                            }
                                            else
                                            {
                                                filedamountStr=FiledAmount.getText().toString();
                                                discountstr="0";
                                            }

                                            if (isValid()) {
                                                Custdata.put("Status", "1");
                                                fs.collection("Agents").document("Agent_" + agentEmail).collection(typeStr).document("cust_" + String.valueOf(AccountNo)).set(Custdata, SetOptions.merge())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {


                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("Status", "1");


                                                                    fs.collection("Agents").document("Agent_" + agentEmail).collection(typeStr).document("cust_" + String.valueOf(Integer.parseInt(AccountNo) - 1))
                                                                            .collection("Loans").document("loan_1")
                                                                            .set(data, SetOptions.merge())
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(getActivity().getApplicationContext(), "Successfully loan added", Toast.LENGTH_SHORT).show();
                                                                                        pd.dismiss();
                                                                                    } else {
                                                                                        Toast.makeText(getActivity().getApplicationContext(), "failed to add loan", Toast.LENGTH_SHORT).show();
                                                                                        pd.dismiss();
                                                                                    }
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getActivity().getApplicationContext(), "failed to upload loan " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


                                                    int months = Months.monthsBetween(start.toLocalDate(), end.toLocalDate()).getMonths();

                                                    Double rate = Double.parseDouble(interestStr);

                                                    Double SI = (Double.parseDouble(filedamountStr) * (rate/100));

                                                    Double Amount = Double.parseDouble(filedamountStr);

                                                    DecimalFormat dec = new DecimalFormat("#0.00");

                                                    AmountToReturn = String.valueOf(dec.format(Amount));

                                                    //calculating expected Amount



                                                    expInstallStr = String.valueOf(dec.format(SI));

                                                }

                                                PendingAmount = "0";

                                                loanData.put("LoanId", "1");
                                                loanData.put("AgentName", agentnameStr);
                                                loanData.put("AgentEmail",agentEmail);
                                                loanData.put("DOI", doiStr);
                                                loanData.put("DOR", dorStr);

                                                loanData.put("ExpectedInstallment", expInstallStr);

                                                loanData.put("FiledAmount", filedamountStr);
                                                loanData.put("Interest", interestStr);
                                                loanData.put("LoanType", typeStr);
                                                loanData.put("ReqAmount", reqamountStr);
                                                loanData.put("AmountToReturn", AmountToReturn);
                                                loanData.put("PendingAmount", PendingAmount);
                                                loanData.put("LFNO", LfNo.getText().toString());
                                                loanData.put("Discount",discountstr);
                                                loanData.put("FileOpeningDate", FileOpeningDate.getText().toString());
                                                loanData.put("FileClosingDate", FileClosingDate.getText().toString());



                                                if (Double.parseDouble(AmountToReturn) > 0) {
                                                    loanData.put("Status", "1");
                                                } else {
                                                    loanData.put("Status", "0");
                                                }


                                                fs.collection("clients").document("client_" + AccountNo).collection("loans").document("loan_1")
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
                else if (btnId.equals("AddLoan")) {





                    fs.collection("AccNo").document("CurrentAcc").get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {


                                        //Cust Loan Registration
                                        reqamountStr = reqamount.getText().toString();
                                        interestStr = interest.getText().toString();

                                        if (typeStr.equals("Daily")) {
                                            interestStr = "0";
                                            discountstr = discount.getText().toString();

                                        }
                                        else if (typeStr.equals("Monthly"))
                                        {

                                            filedamountStr=FiledAmount.getText().toString();
                                            discountstr="0";
                                        }


                                        if (isValid()) {


                                            Map<String, Object> Custdata = new HashMap<>();

                                            Custdata.put("AccountNo", addLoanAccNo);
                                            Custdata.put("CustEmail", custemail);
                                            Custdata.put("CustAddr", Address);
                                            //   Custdata.put("agent_name",agentnameStr);
                                            Custdata.put("CustName", custName);
                                            Custdata.put("CustPhone", phone);
                                            Custdata.put("CustTotalLoan", "1");
                                            Custdata.put("CustAdharId",AdharId);
                                            Custdata.put("CustDob",CustDob);
                                            Custdata.put("GuarantorName",GuarantorName);
                                            Custdata.put("GuarantorMob",GuarantorMob);
                                            Custdata.put("GuarantorAddr",GuarantorAddr);

                                                Custdata.put("Status", "1");
                                                fs.collection("Agents").document("Agent_" + agentEmail).collection(typeStr).document("cust_" + String.valueOf(addLoanAccNo)).set(Custdata, SetOptions.merge())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    //Adds Multiple loans
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("Status", "1");

                                                                    fs.collection("Agents").document("Agent_" + agentEmail).collection(typeStr).document("cust_" + addLoanAccNo)
                                                                            .collection("Loans").document("loan_" + String.valueOf(Integer.parseInt(totalLoan) + 1))
                                                                            .set(data, SetOptions.merge())
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Successfully loan added", Toast.LENGTH_SHORT).show();
                                                                                        pd.dismiss();
                                                                                    } else {
                                                                                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "failed to add loan", Toast.LENGTH_SHORT).show();
                                                                                        pd.dismiss();
                                                                                    }
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "failed to upload loan " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


                                                int months = Months.monthsBetween(start.toLocalDate(), end.toLocalDate()).getMonths();

                                                //     int Monthduration = monthsBetweenDates(sdate, edate);   //duration in months

                                                Double rate = Double.parseDouble(interestStr);

                                                Double SI = (Double.parseDouble(filedamountStr) * (rate/100));

                                                Double Amount = Double.parseDouble(filedamountStr);

                                                DecimalFormat dec = new DecimalFormat("#0.00");

                                                AmountToReturn = String.valueOf(dec.format(Amount));

                                                //calculating expected Amount



                                                expInstallStr = String.valueOf(dec.format(SI));

                                            }

                                            PendingAmount = "0";

                                            loanData.put("LoanId", String.valueOf(Integer.parseInt(totalLoan) + 1));
                                            loanData.put("AgentName", agentnameStr);
                                            loanData.put("AgentEmail",agentEmail);
                                            loanData.put("DOI", doiStr);
                                            loanData.put("DOR", dorStr);

                                            loanData.put("ExpectedInstallment", expInstallStr);

                                            loanData.put("FiledAmount", filedamountStr);
                                            loanData.put("Interest", interestStr);
                                            loanData.put("LoanType", typeStr);
                                            loanData.put("ReqAmount", reqamountStr);
                                            loanData.put("AmountToReturn", AmountToReturn);
                                            loanData.put("PendingAmount", PendingAmount);
                                            loanData.put("LFNO", LfNo.getText().toString());
                                            loanData.put("Discount",discountstr);
                                            loanData.put("TotalMonths",LoanMonths.getText().toString());

                                            if (Double.parseDouble(AmountToReturn) > 0) {
                                                loanData.put("Status", "1");
                                            } else {
                                                loanData.put("Status", "0");
                                            }


                                            fs.collection("clients").document("client_" + addLoanAccNo).collection("loans").document("loan_" + String.valueOf(Integer.parseInt(totalLoan) + 1))
                                                    .set(loanData, SetOptions.merge())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                Toast.makeText(getActivity(), "Customer Loan added Successfully", Toast.LENGTH_LONG).show();
                                                                //loanno++;

                                                                fs.collection("clients").document("client_" + addLoanAccNo).update("CustTotalLoan", String.valueOf(Integer.parseInt(totalLoan) + 1))
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    sendTomain();
                                                                                }
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getActivity().getApplicationContext(), "Error in updating Total Loans " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "invalid data", Toast.LENGTH_LONG).show();

                                        }

                                    } else {

                                        pd.dismiss();
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



    private void ClearForm()
    {
        reqamount.setText(null);
        discount.setText(null);
        FiledAmount.setText(null);
        interest.setText(null);
        LoanMonths.setText(null);
        discount.setText(null);
        FileOpeningDate.setText(null);
        FileClosingDate.setText(null);
        group.clearCheck();

    }

    private boolean isValid() {

        if (LfNo.getText().toString().isEmpty())
        {
            LfNo.setError("Enter LF No.");
            LfNo.setFocusable(true);
        }
        else if(reqamountStr.isEmpty())
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

        else if (typeStr.equals("Daily"))
        {
            if(discount.getText().toString().isEmpty()) {

                discount.setError("Enter Discount Amount");
                discount.setFocusable(true);
                return false;
            }
            else if (Double.parseDouble(reqamount.getText().toString())<Double.parseDouble(discount.getText().toString()))
            {
                discount.setError("Discount Amount is Greater Than Requested Amount");
                discount.setFocusable(true);
                return false;
            }

            else if (Double.parseDouble(reqamount.getText().toString())==Double.parseDouble(discount.getText().toString()))
            {
                discount.setError("Discount Amount is same as Requested Amount");
                discount.setFocusable(true);
                return false;
            }
        }

        else if(dorStr==null)
        {

            if (LoanMonths.isEnabled()) {
                Toast.makeText(getActivity(), "Select Months", Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(getActivity(), "Select No. of Days", Toast.LENGTH_SHORT).show();
            }

            return false;
        }
        else if (typeStr.equals("Monthly")) {

            if (Integer.parseInt(LoanMonths.getText().toString()) <= 0) {
                LoanMonths.setError("Months Should be greater");
                LoanMonths.setEnabled(true);
            }
        }
        else if(interestStr.isEmpty())
        {
            interest.setError("Enter Interest Rate");
            interest.requestFocus();
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