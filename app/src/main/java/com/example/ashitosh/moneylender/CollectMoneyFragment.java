package com.example.ashitosh.moneylender;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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


    private EditText amount;
    private Button pay;
    private String acctext,amounttext,pendingamount,datetext;

    private double ExpectedInstallment;

    private double FiledAmount;

    private double AmountToReturn;

    private double TotalCollection;

    private String agentEmail,loantype,customerName;

    private TextView Accno,LoanType,ExpectedAmount,prevPending,prevDateOfCol,amountHead;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore fs;

    private Spinner LoanIdSpinner;

    private ArrayList<String> LoanId;

    private ArrayAdapter<String> adapter;

    private Map<String,String> typeText,expectText,pendings;

    private String loanIdtext;

    private ProgressDialog pd;
    private String mMonth,mYear,mDay;
    public int flag=0;
    private String status;
    private LocalDate lastDateOfCol,dateOfCol;

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

        fs=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

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


        JodaTimeAndroid.init(this.getActivity());

        DateTimeZone zone=DateTimeZone.forID("Asia/Kolkata");
        DateTime dateTime=new DateTime(zone);

        dateOfCol=dateTime.toLocalDate();

        datetext=dateTime.toLocalDate().toString();
        mMonth= String.valueOf(dateTime.getMonthOfYear());
        mDay= String.valueOf(dateTime.dayOfMonth());
        mYear= String.valueOf(dateTime.getYear());


        pd.setMessage("Wait until Loading Details");
        pd.setCanceledOnTouchOutside(false);

        pd.show();

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

                            if(Objects.requireNonNull(status).equals("1")) {
                                LoanId.add(id);
                                typeText.put(id, type);
                                expectText.put(id, expectAmount);
                                pendings.put(id,pending);                            }
                        }
                    }

                    adapter=new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_spinner_item,LoanId);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    LoanIdSpinner.setAdapter(adapter);

                    pd.dismiss();
                }
            }
        });


        LoanIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pd.setMessage("Fetchig Loan Details");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

               loanIdtext=parent.getItemAtPosition(position).toString();
               LoanType.setText(typeText.get(loanIdtext));
               loantype=typeText.get(loanIdtext);
               ExpectedAmount.setText(expectText.get(loanIdtext));
               ExpectedInstallment =Double.parseDouble(ExpectedAmount.getText().toString());
               prevPending.setText(pendings.get(loanIdtext));

               if(loantype.equals("Daily"))
               {
                   fs.collection("MoneyLender").document("Agent_" + agentEmail)
                           .collection(loantype).document("Date_" + datetext)
                           .collection("customers").document("client_" + acctext)
                           .collection("loans")
                           .document("loan_"+loanIdtext)
                           .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                       @SuppressLint("SetTextI18n")
                       @Override
                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                           if (task.isSuccessful())
                           {
                               if(task.getResult().contains("DateOfCollection")) {

                                   lastDateOfCol = LocalDate.parse(task.getResult().getString("DateOfCollection"));

                                   if (dateOfCol.equals(lastDateOfCol) && loantype.equals("Daily")) {

                                       amountHead.setVisibility(View.GONE);
                                       amount.setVisibility(View.GONE);
                                       pay.setVisibility(View.GONE);

                                       prevDateOfCol.setText("Amount for this Loan Id is Already paid today");
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
                                   amountHead.setVisibility(View.VISIBLE);
                                   amount.setVisibility(View.VISIBLE);
                                   pay.setVisibility(View.VISIBLE);
                                   prevDateOfCol.setText("First time Collection for thid loan Id");
                                   prevDateOfCol.setTextColor(Color.BLACK);
                                   v.refreshDrawableState();
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
               else if(loantype.equals("Monthly"))
               {
                   fs.collection("MoneyLender").document("Agent_" + agentEmail)
                           .collection(loantype).document("Month_" + mMonth+mYear)
                           .collection("customers").document("client_" + acctext)
                           .collection("loans")
                           .document("loan_"+loanIdtext)
                           .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                       @SuppressLint("SetTextI18n")
                       @Override
                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                           if (task.isSuccessful())
                           {
                               if(task.getResult().contains("DateOfCollection")) {

                                   lastDateOfCol = LocalDate.parse(task.getResult().getString("DateOfCollection"));
                                   Months months;
                                   months=Months.monthsBetween(lastDateOfCol, dateOfCol);

                                   if ((months.getMonths()==0) && loantype.equals("Monthly")) {

                                       amountHead.setVisibility(View.GONE);
                                       amount.setVisibility(View.GONE);
                                       pay.setVisibility(View.GONE);

                                       prevDateOfCol.setText("Amount for this Loan Id is Already paid in this month");
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
                                   amountHead.setVisibility(View.VISIBLE);
                                   amount.setVisibility(View.VISIBLE);
                                   pay.setVisibility(View.VISIBLE);
                                   prevDateOfCol.setText("First time Collection for thid loan Id");
                                   prevDateOfCol.setTextColor(Color.BLACK);

                                   v.refreshDrawableState();
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //*************************************************************

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                            Toast.makeText(getActivity(), "Client acc added Successfully", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getActivity(), "Client acc added Successfully", Toast.LENGTH_SHORT).show();
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

                    getAccountDetail();

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
                    Toast.makeText(getActivity(), "Failed to collect daily amount"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        }
        //****************************************************


    }


    private void UpdateClientAcc()
    {
        Map<String,Object> data=new HashMap<>();
        data.clear();


        if(Double.parseDouble(amounttext)<ExpectedInstallment)
        {
            String tempPend;
            tempPend= String.valueOf(ExpectedInstallment-Double.parseDouble(amounttext));
            pendingamount= String.valueOf(Double.parseDouble(pendingamount)+Double.parseDouble(tempPend));
        }
        else
        {
            if( (Double.parseDouble(amounttext) > ExpectedInstallment) && (Double.parseDouble(pendingamount)>0))
            {
                double temp=Double.parseDouble(amounttext) - ExpectedInstallment;

                if(Double.parseDouble(pendingamount) >= temp)
                {
                    pendingamount= String.valueOf(Double.parseDouble(pendingamount)-temp);
                }
                else if(temp>Double.parseDouble(pendingamount))
                {
                    pendingamount="0";
                    ExpectedInstallment=ExpectedInstallment-temp;


                    Toast.makeText(getActivity(), "Expected lessened", Toast.LENGTH_SHORT).show();
                }
            }
            else if((Double.parseDouble(amounttext) > ExpectedInstallment))
            {
                double temp=Double.parseDouble(amounttext) - ExpectedInstallment;

                    ExpectedInstallment = ExpectedInstallment - temp;


                Toast.makeText(getActivity(), "Expected lessened"+ ExpectedInstallment, Toast.LENGTH_SHORT).show();
            }
            else {
                pendingamount = String.valueOf(pendingamount);
            }
        }

        DecimalFormat dec=new DecimalFormat("#0.00");

        data.put("PendingAmount",String.valueOf(dec.format(Double.parseDouble(pendingamount))));
        data.put("ExpectedInstallment",String.valueOf(ExpectedInstallment));

        if((AmountToReturn>=0))
        {
            double tempamount=AmountToReturn;
            if(tempamount!=0 && (ExpectedInstallment>0)) {
                tempamount = tempamount - Double.parseDouble(amounttext);

                if(tempamount==0 && !(Double.parseDouble(pendingamount)>0) && !(ExpectedInstallment>0))
                {
                    status="0";
                    data.put("Status","0");
                }
                else if(tempamount==0 && ( (Double.parseDouble(pendingamount)>0) || (ExpectedInstallment>0)))
                {
                    ExpectedInstallment=Double.parseDouble(pendingamount);

                    data.put("ExpectedInstallment",String.valueOf(ExpectedInstallment));
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
                    ExpectedInstallment=Double.parseDouble(pendingamount);

                    if(ExpectedInstallment==0)
                    {
                        data.put("ExpectedInstallment",String.valueOf(ExpectedInstallment));
                        data.put("PendingAmount","0.0");
                        status="0";
                        data.put("Status","0");

                    }
                    else {
                        data.put("ExpectedInstallment", String.valueOf(ExpectedInstallment));
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
        else if(Integer.parseInt(pendingamount)>0)
        {
            Toast.makeText(getActivity(), "pending Amount: "+pendingamount, Toast.LENGTH_SHORT).show();
        }
        else
        {
            status="0";
            data.put("Status","0");
            Toast.makeText(getActivity(), "Account is null", Toast.LENGTH_SHORT).show();
        }

        if(status.equals("0"))
        {

            fs.collection("Agents").document( "Agent_"+Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                    .collection(loantype).document("cust_"+acctext)
                    .collection("Loans").document("loan_"+loanIdtext).update("Status",status)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),"Status Updated",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),"Status Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if(e!=null)
                    {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),"Status Updatation failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        fs.collection("clients").document("client_"+acctext)
                .collection("loans").document("loan_"+loanIdtext)
                .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "Successfully updated Account Details"+ExpectedInstallment+"AmountReturn: "+AmountToReturn, Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(getActivity(), "Successfully fetched Account Details"+ExpectedInstallment+"AmountReturn: "+AmountToReturn, Toast.LENGTH_SHORT).show();

                            updateTotal();
                            UpdateClientAcc();
                            clearText();

                            sendToHome();
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
