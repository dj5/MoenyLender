package com.example.ashitosh.moneylender;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Fragments.CustDetailFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class CollectMoneyFragment extends Fragment {


    private SwipeRefreshLayout refreshLayout;
    private EditText amount;
    private Button pay;
    private String acctext,amounttext,pendingamount,datetext;

    private double ExpectedInstallment;

    private double FiledAmount;

    private double AmountToReturn,pendingMoney,interest;

    private double TotalCollection;

    private String agentEmail,loantype,customerName;

    private TextView Accno,prevPendingHead,ExpectedAmountHead,LoanType,ReturnAmount,ExpectedAmount,prevPending,prevDateOfCol,amountHead;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore fs;

    private Spinner LoanIdSpinner;

    private ArrayList<String> LoanId;

    private ArrayAdapter<String> adapter;

    private Map<String,String> typeText,Interests,expectText,pendings,issueDate,endDate,RemainingAmount,lagData;

    private String loanIdtext;

    private ProgressDialog pd;
    private String mMonth,mYear,mDay;
    private String doi;
    public int flag=0;
    private String status;

    int pendingMonths,monthsLeft,daysLeft;
    private LocalDate lastDateOfCol,dateOfCol,dor;

    public CollectMoneyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_collect_money, container, false);

        Accno=v.findViewById(R.id.accValue);
        amount=v.findViewById(R.id.AmountCollect);
        pay=v.findViewById(R.id.pay);
        LoanIdSpinner=v.findViewById(R.id.ActiveLoanSpinner);
        ExpectedAmount=v.findViewById(R.id.ExpectAmount);
        LoanType=v.findViewById(R.id.LoanType);
        prevPending=v.findViewById(R.id.prevPending);
        prevDateOfCol=v.findViewById(R.id.lastDateOfCol);
        amountHead=v.findViewById(R.id.AmountText);
        ReturnAmount=v.findViewById(R.id.AmountToReturn);
        prevPendingHead=v.findViewById(R.id.PrevPendingHead);
        fs=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        ExpectedAmountHead=v.findViewById(R.id.ExpectAmountHead);

        final DecimalFormat dec=new DecimalFormat("#0.00");

        refreshLayout=v.findViewById(R.id.swipeRefreshCollection);

        agentEmail= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

        pd=new ProgressDialog(getActivity());

        Bundle Custdata=getArguments();

        Accno.setText(Objects.requireNonNull(Custdata).getString("Account"));
        acctext=Accno.getText().toString();
        //*************************************************************

        LoanId=new ArrayList<String>();
        typeText=new HashMap<>();
        expectText=new HashMap<>();
        pendings=new HashMap<>();
        issueDate=new HashMap<>();
        RemainingAmount=new HashMap<>();
        lagData=new HashMap<>();
        endDate=new HashMap<>();
        Interests=new HashMap<>();

        FirebaseFirestoreSettings settings=new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();

        fs.setFirestoreSettings(settings);

        JodaTimeAndroid.init(this.getActivity());

        DateTimeZone zone=DateTimeZone.forID("Asia/Kolkata");
        DateTime dateTime=new DateTime(zone);

        dateOfCol=dateTime.toLocalDate();

        datetext=dateTime.toLocalDate().toString();
        mMonth= String.valueOf(dateTime.getMonthOfYear());
        mDay= String.valueOf(dateTime.dayOfMonth());
        mYear= String.valueOf(dateTime.getYear());


        if(Integer.parseInt(mMonth)<10)
        {
            mMonth="0"+mMonth;
        }

        Toast.makeText(getActivity(), "Month: "+mMonth, Toast.LENGTH_SHORT).show();

        pd.setMessage("Wait until Loading Details");
        pd.setCanceledOnTouchOutside(false);

        pd.show();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prevPending.setText(String.valueOf(pendingMoney));
            }
        });

        //Fetch Agent Names
        fs.collection("clients").document("client_"+acctext).collection("loans")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e!=null)
                        {
                            Log.e("Error: "+e.getMessage(),"Error");
                        }
                        else {

                            for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {

                                if (doc.getType().equals(DocumentChange.Type.ADDED)) {
                                    String id=doc.getDocument().getString("LoanId");
                                    String type=doc.getDocument().getString("LoanType");
                                    String expectAmount=doc.getDocument().getString("ExpectedInstallment");
                                    String pending=doc.getDocument().getString("PendingAmount");
                                    String status=doc.getDocument().getString("Status");
                                    String doi=doc.getDocument().getString("DOI");
                                    String remaining=doc.getDocument().getString("AmountToReturn");
                                    String interest=doc.getDocument().getString("Interest");
                                    String Email=doc.getDocument().getString("AgentEmail");

                                    String dor=doc.getDocument().getString("DOR");

                                    if(Objects.requireNonNull(status).equals("1") && Objects.requireNonNull(agentEmail).equals(Email) ) {
                                        LoanId.add(id);
                                        typeText.put(id, type);
                                        expectText.put(id, expectAmount);
                                        pendings.put(id,pending);
                                        issueDate.put(id,doi);
                                        endDate.put(id,dor);
                                        RemainingAmount.put(id,remaining);
                                        Interests.put(id,interest);
                                    }

                                }
                            }

                            Activity activity=getActivity();
                            if (LoanId!=null && activity!=null ) {
                                adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, LoanId);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                LoanIdSpinner.setAdapter(adapter);
                            }
                            pd.dismiss();
                        }
                    }
                });


        LoanIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pd.setMessage("Fetchig Loan Details");
                pd.setCanceledOnTouchOutside(false);
                pd.show();


                loanIdtext=parent.getItemAtPosition(position).toString();
                LoanType.setText(typeText.get(loanIdtext));
                loantype=typeText.get(loanIdtext);
                ExpectedAmount.setText(expectText.get(loanIdtext));
                prevPending.setText(pendings.get(loanIdtext));
                ReturnAmount.setText(dec.format(Double.parseDouble(RemainingAmount.get(loanIdtext))));

             //   getAccountDetail();

                ExpectedInstallment = Double.parseDouble(expectText.get(loanIdtext));

                AmountToReturn= Double.parseDouble(RemainingAmount.get(loanIdtext));

                interest= Double.parseDouble(Interests.get(loanIdtext));

          //      pendingMoney=Double.parseDouble(pendings.get(loanIdtext));


                if(loantype.equals("Daily"))
                {


                    prevPending.setVisibility(View.VISIBLE);
                    prevPendingHead.setVisibility(View.VISIBLE);


                    ExpectedAmountHead.setText("Expected Installment");

                    fs.collection("CollectionDates").document("cust_" + acctext).collection(typeText.get(loanIdtext))
                            .document(loanIdtext)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful())
                            {


                                if(task.getResult().contains("LastDateOfCollection")) {


                                    lastDateOfCol = LocalDate.parse(task.getResult().getString("LastDateOfCollection"));

                                    Days days;
                                    days=Days.daysBetween(lastDateOfCol,dateOfCol);


                                    if(days.getDays()>1)
                                    {


                                        pendingMoney = Double.parseDouble(pendings.get(loanIdtext)) + ((AmountToReturn / (Days.daysBetween(lastDateOfCol,LocalDate.parse(endDate.get(loanIdtext))).getDays())) * days.getDays());
                                        prevPending.setText(String.valueOf(dec.format(pendingMoney)));


                                    }
                                    else
                                    {
                                        pendingMoney= Double.parseDouble(pendings.get(loanIdtext));
                                        prevPending.setText(String.valueOf(dec.format(pendingMoney)));

                                    }

                                    prevPending.refreshDrawableState();
                                    prevPending.invalidate();
                                    prevPending.requestLayout();
                                    v.refreshDrawableState();

                                    if ((days.getDays()==0 || (days.getDays()<0)) && loantype.equals("Daily")) {

                                        amountHead.setVisibility(View.GONE);
                                        amount.setVisibility(View.GONE);
                                        pay.setVisibility(View.GONE);

                                        if(days.getDays() <0 )
                                        {
                                            prevDateOfCol.setText("Amount for this Loan Id is Already paid on: "+datetext);

                                        }
                                        else {
                                            prevDateOfCol.setText("Amount for this Loan Id is Already paid today: " + datetext);
                                        }

                                        prevDateOfCol.setTextColor(Color.RED);
                                        v.refreshDrawableState();
                                    }
                                    else{

                                        amountHead.setVisibility(View.VISIBLE);
                                        amount.setVisibility(View.VISIBLE);
                                        pay.setVisibility(View.VISIBLE);
                                        prevDateOfCol.setText("Last amount Collected on day " + lastDateOfCol.toString());
                                        prevDateOfCol.setTextColor(Color.BLACK);
                                        v.refreshDrawableState();
                                    }
                                }
                                else
                                {

                                    Days days;
                                    LocalDate issue = new LocalDate(issueDate.get(loanIdtext));
                                    days = Days.daysBetween(issue, dateOfCol);



                                    if (days.getDays() == 0) {
                                        amountHead.setVisibility(View.GONE);
                                        amount.setVisibility(View.GONE);
                                        pay.setVisibility(View.GONE);

                                        prevDateOfCol.setText("Amount cant be Collected today: "+datetext);
                                        prevDateOfCol.setTextColor(Color.RED);

                                        pd.dismiss();
                                        v.refreshDrawableState();

                                    }
                                    else
                                    {
                                        if(days.getDays()>1) {
                                            pendingMoney = Double.parseDouble(pendings.get(loanIdtext)) + ((AmountToReturn / (Days.daysBetween(issue,LocalDate.parse(endDate.get(loanIdtext))).getDays())) * days.getDays());
                                            prevPending.setText(String.valueOf(dec.format(pendingMoney)));


                                        }
                                        else
                                        {
                                            prevPending.setText(String.valueOf(dec.format(pendingMoney)));

                                        }

                                        prevPending.refreshDrawableState();
                                        prevPending.invalidate();
                                        prevPending.requestLayout();
                                        v.refreshDrawableState();

                                        amountHead.setVisibility(View.VISIBLE);
                                        amount.setVisibility(View.VISIBLE);
                                        pay.setVisibility(View.VISIBLE);
                                        prevDateOfCol.setText("First time Collection for thid loan Id");
                                        prevDateOfCol.setTextColor(Color.BLACK);
                                        v.refreshDrawableState();
                                    }
                                }


                                pd.dismiss();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else if(loantype.equals("Monthly")) {


                    ExpectedAmountHead.setText("Reamining Interest");

                    prevPending.setVisibility(View.GONE);
                    prevPendingHead.setVisibility(View.GONE);

                 //   Toast.makeText(getActivity(), "Expected amount: "+ExpectedInstallment+" interest: "+interest, Toast.LENGTH_SHORT).show();

                    fs.collection("CollectionDates").document("cust_" + acctext).collection(typeText.get(loanIdtext))
                            .document(loanIdtext)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {


                                if (task.getResult().contains("LastDateOfCollection")) {


                                    lastDateOfCol = LocalDate.parse(task.getResult().getString("LastDateOfCollection"));
                                    Months months;
                                    months = Months.monthsBetween(lastDateOfCol, dateOfCol);

                               //     Toast.makeText(getActivity(), "months"+months.getMonths(), Toast.LENGTH_SHORT).show();
                                    pendingMonths=months.getMonths();


                                    Toast.makeText(getActivity(), "pending month: "+(months.getMonths()-1), Toast.LENGTH_SHORT).show();

                                     if (pendingMonths==0)
                                     {
                                         pendingMoney=Double.parseDouble(pendings.get(loanIdtext));

                                        prevPending.setText(String.valueOf(pendingMoney));
                                        v.refreshDrawableState();
                                     }
                                    else if (pendingMonths==1)
                                    {

                                        prevPending.setText(String.valueOf(ExpectedInstallment));
                                       v.refreshDrawableState();
                                    }
                                    else if (pendingMonths>1)
                                    {

                                        ExpectedInstallment=ExpectedInstallment+(AmountToReturn*(interest/100))*(pendingMonths-1);

                                        Toast.makeText(getActivity(), "Expected amount: "+ExpectedInstallment+" interest: "+interest, Toast.LENGTH_SHORT).show();

                                        prevPending.setText(String.valueOf(ExpectedInstallment));

                                    }

                                    prevPending.refreshDrawableState();
                                    prevPending.invalidate();
                                    prevPending.requestLayout();

                                    v.refreshDrawableState();

                                    if (((months.getMonths() == 0)||(months.getMonths()<0)) && loantype.equals("Monthly")) {

                                        amountHead.setVisibility(View.GONE);
                                        amount.setVisibility(View.GONE);
                                        pay.setVisibility(View.GONE);

                                        if(months.getMonths()<0)
                                        {
                                            prevDateOfCol.setText("Amount for this Loan Id is Already paid in month: "+datetext);

                                        }
                                        else
                                        {
                                            prevDateOfCol.setText("Amount for this Loan Id is Already paid in this month: "+datetext);

                                        }
                                        prevDateOfCol.setTextColor(Color.RED);

                                        v.refreshDrawableState();

                                    } else {

                                        amountHead.setVisibility(View.VISIBLE);
                                        amount.setVisibility(View.VISIBLE);
                                        pay.setVisibility(View.VISIBLE);

                                        prevDateOfCol.setText("Last amount Collected on Month " + lastDateOfCol.toString());
                                        prevDateOfCol.setTextColor(Color.BLACK);
                                        v.refreshDrawableState();
                                    }
                                }
                                else
                                {

                                    Months months;
                                    LocalDate issue = new LocalDate(issueDate.get(loanIdtext));
                                    months = Months.monthsBetween(issue, dateOfCol);

                                    pendingMonths=months.getMonths();

                                    if (pendingMonths == 0) {
                                        amountHead.setVisibility(View.GONE);
                                        amount.setVisibility(View.GONE);
                                        pay.setVisibility(View.GONE);

                                        prevPending.setText(String.valueOf(pendingMoney));

                                        prevDateOfCol.setText("Amount cant be Collected in This Month: "+datetext);
                                        prevDateOfCol.setTextColor(Color.RED);

                                        pd.dismiss();
                                        v.refreshDrawableState();

                                    }
                                    else
                                    {

                                        if (pendingMonths==1)
                                        {
                                            prevPending.setText(String.valueOf(ExpectedInstallment));

                                        }
                                        else if (pendingMonths>1)
                                        {

                                            ExpectedInstallment=ExpectedInstallment+(AmountToReturn*(interest/100))*(pendingMonths-1);

                                            Toast.makeText(getActivity(), "Expected Amount: "+ExpectedInstallment, Toast.LENGTH_SHORT).show();
                                           // pendingMoney=ExpectedInstallment;
                                             prevPending.setText(String.valueOf(ExpectedInstallment));
                                            v.refreshDrawableState();
                                        }

                                        amountHead.setVisibility(View.VISIBLE);
                                        amount.setVisibility(View.VISIBLE);
                                        pay.setVisibility(View.VISIBLE);
                                        prevDateOfCol.setText("First time Collection for thid loan Id");
                                        prevDateOfCol.setTextColor(Color.BLACK);

                                    }

                                    prevPending.refreshDrawableState();
                                    prevPending.invalidate();
                                    prevPending.requestLayout();

                                    v.refreshDrawableState();

                                }
                                pd.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //*************************************************************

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dor= LocalDate.parse(endDate.get(loanIdtext));

                monthsLeft=Months.monthsBetween(dateOfCol,dor).getMonths();

                daysLeft=Days.daysBetween(dateOfCol,dor).getDays();

                pd.setMessage("Wait until uploading data");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                amounttext=amount.getText().toString();


                Map<String, Object> data= new HashMap<>();
                Map<String, Object> accno= new HashMap<>();

                if (isValid()) {
                    data.put("AmountRecieved", amounttext);
                    data.put("DateOfCollection", datetext);
                    data.put("LoanId",loanIdtext);
                    data.put("AccountNo",acctext);

                    accno.put("AccountNo",acctext);
                    if(loantype.equals("Daily")) {


                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection(loantype).document("Date_" + datetext)
                                .collection("customers").document("client_" + acctext)
                                .collection("loans")
                                .document("loan_"+loanIdtext)
                                .set(data, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            pd.hide();
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Amount Collected Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.hide();
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Failed to collect daily amount"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        //uploads client account

                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection(loantype).document("Date_" + datetext)
                                .collection("customers").document("client_" + acctext)
                                .set(accno,SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            pd.hide();
                                            pd.dismiss();
                                        //    Toast.makeText(getActivity(), "Client acc added Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.hide();
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Failed to add client acc"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {


                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection(loantype).document("Month_" + mMonth+mYear)
                                .collection("customers").document("client_" + acctext)
                                .collection("loans")
                                .document("loan_"+loanIdtext)
                                .set(data, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            pd.hide();
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Amount Collected Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), "Failed to collect monthly amount", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.hide();
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Failed to collect monthly amount"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        //uploads client acc no

                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection(loantype).document("Month_" + mMonth+mYear)
                                .collection("customers").document("client_" + acctext)
                                .set(accno,SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            pd.hide();
                                            pd.dismiss();
                                         //   Toast.makeText(getActivity(), "Client acc added Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.hide();
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Failed to add client acc"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                    updateTotal();
                    UpdateClientAcc();
                    updateDate();

                    clearText();

                    sendToHome();



                }
                else
                {
                    pd.setMessage("failed to validate data");
                    pd.dismiss();
                }


            }
        });

        return v;

    }

    private void updateTotal()
    {
        final Map<String,Object> dat=new HashMap<>();
        dat.clear();
        pd.setMessage("Updating TotalCollection");
        pd.show();


        if(loantype.equals("Daily")) {

            fs.collection("MoneyLender").document("Agent_" + agentEmail)
                    .collection("Daily").document("Date_" + datetext)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(task.getResult().contains("TotalCollection"))
                        {
                            double a=Double.parseDouble(task.getResult().getString("TotalCollection"));
                            TotalCollection=a+Double.parseDouble(amounttext);
                        }
                        else
                        {
                            TotalCollection= Double.parseDouble(amounttext);

                        }

                        dat.put("TotalCollection",String.valueOf(TotalCollection));
                        dat.put("Date",datetext);

                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection(loantype).document("Date_"+datetext )
                                .set(dat,SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        pd.dismiss();

                                //        Toast.makeText(getActivity(), "Total Collection Updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to update Total Collection amount"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Failed to collect daily amount"+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        else
        {
            fs.collection("MoneyLender").document("Agent_" + agentEmail)
                    .collection("Monthly").document("Month_" + mMonth+mYear)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(task.getResult().contains("TotalCollection"))
                        {
                            double a=Double.parseDouble(task.getResult().getString("TotalCollection"));
                            TotalCollection=a+Double.parseDouble(amounttext);
                        }
                        else
                        {
                            TotalCollection=Double.parseDouble(amounttext);

                        }

                        dat.put("TotalCollection",String.valueOf(TotalCollection));
                        dat.put("Date",datetext);

                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection("Monthly").document("Month_" + mMonth+mYear)
                                .set(dat,SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Total Collection Updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to update Total Collection amount"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed to collect Monthly amount"+e.getMessage(), Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });

        }
        //****************************************************


    }


    private void UpdateClientAcc()
    {



        Map<String, Object> data = new HashMap<>();
        data.clear();
        DecimalFormat dec = new DecimalFormat("#0.00");


      //  Toast.makeText(getActivity(), "pending: "+pendingMoney+" expected: "+ExpectedInstallment+" dayslef: "+daysLeft, Toast.LENGTH_SHORT).show();
        if (typeText.get(loanIdtext).equals("Daily"))
        {

            if (Double.parseDouble(amounttext)>=ExpectedInstallment && pendingMoney==0)
            {
                AmountToReturn =AmountToReturn-Double.parseDouble(amounttext);
                ExpectedInstallment=AmountToReturn/daysLeft;

                data.put("ExpectedInstallment", String.valueOf((int)ExpectedInstallment));
                data.put("AmountToReturn", String.valueOf((int)AmountToReturn));
                data.put("PendingAmount", "0");


            }
            else if (Double.parseDouble(amounttext)>=ExpectedInstallment && pendingMoney>0)
            {
                if (Double.parseDouble(amounttext)>=pendingMoney) {
                    double temp = Double.parseDouble(amounttext) - pendingMoney;

                    pendingMoney = 0;


                    if (temp == 0) {

                        data.put("PendingAmount", "0");
                    } else if (temp > 0) {

                        AmountToReturn = AmountToReturn - temp;

                        if (daysLeft>0) {
                            ExpectedInstallment = (AmountToReturn / daysLeft);
                        }
                        else
                        {
                            ExpectedInstallment=(AmountToReturn / daysLeft);
                        }

                        data.put("ExpectedInstallment", String.valueOf((int)ExpectedInstallment));
                        data.put("AmountToReturn", String.valueOf((int)AmountToReturn));
                        data.put("PendingAmount", "0");
                    }
                }
                else  if (Double.parseDouble(amounttext)<pendingMoney)
                {
                    pendingMoney=pendingMoney-Double.parseDouble(amounttext);

                    data.put("PendingAmount", String.valueOf((int)pendingMoney));
                }
            }
            else if (Double.parseDouble(amounttext)<ExpectedInstallment && pendingMoney==0)
            {
                if (Double.parseDouble(amounttext)<ExpectedInstallment) {
                    double temp = ExpectedInstallment-Double.parseDouble(amounttext) ;

                    pendingMoney=temp;

                    AmountToReturn = AmountToReturn - Double.parseDouble(amounttext);

                    if (daysLeft>0) {
                        ExpectedInstallment = (AmountToReturn / daysLeft);
                    }
                    else
                    {
                        ExpectedInstallment=(AmountToReturn / daysLeft);
                    }
                    data.put("ExpectedInstallment", String.valueOf((int)ExpectedInstallment));
                    data.put("AmountToReturn", String.valueOf((int)AmountToReturn));
                    data.put("PendingAmount", String.valueOf((int)temp));
                }

            }
            else if (Double.parseDouble(amounttext)<ExpectedInstallment && pendingMoney>0)
            {
                if (Double.parseDouble(amounttext)<=pendingMoney) {
                    double temp =pendingMoney - Double.parseDouble(amounttext);


                    if (temp == 0) {
                        pendingMoney=0;

                        data.put("PendingAmount", "0");
                    } else if (temp > 0) {

                        pendingMoney=temp;
                        data.put("PendingAmount", String.valueOf((int)pendingMoney));
                    }
                }
                else if (Double.parseDouble(amounttext)>pendingMoney)
                {
                    double temp = Double.parseDouble(amounttext)-pendingMoney;
                    pendingMoney=0;

                    AmountToReturn=AmountToReturn-temp;

                    if (daysLeft>0) {
                        ExpectedInstallment = (AmountToReturn / daysLeft);
                    }
                    else
                    {
                        ExpectedInstallment=(AmountToReturn / daysLeft);
                    }

                    data.put("ExpectedInstallment", String.valueOf((int)ExpectedInstallment));
                    data.put("AmountToReturn", String.valueOf((int)AmountToReturn));
                    data.put("PendingAmount", "0");
                }

            }

        }

        else if (typeText.get(loanIdtext).equals("Monthly"))
            {

                Toast.makeText(getActivity(), "updating monthly client: "+typeText.get(loanIdtext), Toast.LENGTH_SHORT).show();

                Toast.makeText(getActivity(), "In Monthly expectedAmount: "+ExpectedInstallment, Toast.LENGTH_SHORT).show();

                if (Double.parseDouble(amounttext)<=ExpectedInstallment)
                    {

                        double temp=ExpectedInstallment-Double.parseDouble(amounttext);

                        pendingMoney=temp;
                        ExpectedInstallment=(AmountToReturn*(interest/100))+temp;
                        data.put("ExpectedInstallment",String.valueOf((int) ExpectedInstallment));
                        data.put("PendingAmount",String.valueOf((int) pendingMoney));

                    }
                    else if (Double.parseDouble(amounttext)>ExpectedInstallment)
                    {
                         double temp=Double.parseDouble(amounttext)-ExpectedInstallment;

                         if (AmountToReturn>0)
                         {
                             AmountToReturn=AmountToReturn-temp;
                             ExpectedInstallment=(AmountToReturn*(interest/100));

                             data.put("ExpectedInstallment",String.valueOf((int) ExpectedInstallment));
                             data.put("AmountToReturn",String.valueOf((int)AmountToReturn));
                         }
                    }

            }






        if((AmountToReturn>=0) && (typeText.get(loanIdtext).equals("Daily")))
        {
            double tempamount=AmountToReturn;
            if(tempamount!=0 && (ExpectedInstallment>0)) {

                if(tempamount==0 && !(pendingMoney>0) && !(ExpectedInstallment>0))
                {
                    status="0";
                    data.put("Status","0");
                }
                else if(tempamount==0 && ( (pendingMoney>0) || (ExpectedInstallment>0)))
                {
                    ExpectedInstallment=pendingMoney;

                    data.put("ExpectedInstallment",String.valueOf(dec.format(ExpectedInstallment)));
                    data.put("PendingAmount","0.0");
                    status="1";
                    data.put("Status","1");
                }
                else
                {
                    status="1";
                    data.put("Status","1");
                }
                data.put("AmountToReturn", String.valueOf(tempamount));
            }
            else
            {
                if(ExpectedInstallment>0)
                {
                    ExpectedInstallment=pendingMoney;

                    if(ExpectedInstallment==0)
                    {
                        data.put("ExpectedInstallment",String.valueOf(dec.format(ExpectedInstallment)));
                        data.put("PendingAmount","0.0");
                        status="0";
                        data.put("Status","0");
                    }
                    else {
                        data.put("ExpectedInstallment", String.valueOf(dec.format(ExpectedInstallment)));
                        data.put("PendingAmount", "0.0");
                        status = "1";
                        data.put("Status", "1");
                    }
                }
                else {
                    status = "0";
                    data.put("Status", "0");
                    Toast.makeText(getActivity(), "AmountToReturn is null", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(AmountToReturn<=0 && ExpectedInstallment<=0 && pendingMoney<=0)
        {
            status="0";

            data.put("Status","0");

            Toast.makeText(getActivity(), "Account Deactivated", Toast.LENGTH_SHORT).show();
        }


        if(status!=null)
        {

            if (status.equals("0")) {
                fs.collection("Agents").document("Agent_" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                        .collection(loantype).document("cust_" + acctext)
                        .collection("Loans").document("loan_" + loanIdtext).update("Status", status)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Status Updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Status Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e != null) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Status Updatation failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        fs.collection("clients").document("client_"+acctext)
                .collection("loans").document("loan_"+loanIdtext)
                .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "Successfully amount collected"+ExpectedInstallment+"AmountReturn: "+AmountToReturn, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getActivity(), "Failed to update Account Details"+ExpectedInstallment+"AmountReturn: "+AmountToReturn, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to update Account Details"+ExpectedInstallment+"AmountReturn: "+AmountToReturn, Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void getAccountDetail()
    {
        pd.setMessage("Fectching Account Details");
        pd.show();

        fs.collection("clients").document("client_"+acctext)
                .collection("loans").document("loan_"+loanIdtext).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            pd.dismiss();
                            AmountToReturn= Double.parseDouble(task.getResult().getString("AmountToReturn"));
                            pendingamount=task.getResult().getString("PendingAmount");
                            FiledAmount=Double.parseDouble(task.getResult().getString("FiledAmount"));
                            ExpectedInstallment=Double.parseDouble(task.getResult().getString("ExpectedInstallment"));
                            interest=Double.parseDouble(task.getResult().getString("Interest"));

                            Toast.makeText(getActivity(), "Successfully fetched Account Details"+ExpectedInstallment+"AmountReturn: "+AmountToReturn, Toast.LENGTH_SHORT).show();


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Failed to fetch Account Details", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateDate()
    {

        lagData.put("LastDateOfCollection", String.valueOf(dateOfCol));

        if ( acctext!=null && loanIdtext!=null) {
            fs.collection("CollectionDates").document("cust_" + acctext).collection(typeText.get(loanIdtext))
                    .document(loanIdtext)
                    .set(lagData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(getActivity(), "Successfully updated Collection Date "+ExpectedInstallment+"AmountReturn: "+AmountToReturn, Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(),"failed to update date: "+e.getMessage() , Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void sendToHome() {

        AgentHome fragment = new AgentHome();

        android.support.v4.app.FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(fragment, "gotoHome").addToBackStack("gotoHome");
        fragmentTransaction.replace(R.id.AgentmainFrame, fragment);
        fragmentTransaction.commit();
    }

    private void clearText()
    {

        amount.setText("");

    }

    @SuppressLint("SetTextI18n")
    private boolean isValid() {

        if(acctext.isEmpty())
        {
            return false;
        }
        else if(amounttext.isEmpty())
        {
            amount.setError("Enter Amount");
            amount.requestFocus();
            return false;
        }
        else if(datetext.isEmpty())
        {
            return false;
        }
        else if(loantype.isEmpty())
        {
            return false;
        }
        return true;
    }


}