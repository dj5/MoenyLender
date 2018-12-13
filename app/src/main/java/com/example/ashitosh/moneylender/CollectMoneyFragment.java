package com.example.ashitosh.moneylender;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CollectMoneyFragment extends Fragment {


    private EditText acc,amount;
    private Button pay,date;
    private String acctext;
    private String amounttext;
    private String pendingamount;
    private double ExpectedInstallment;
    private double FiledAmount;
    private double AmountToReturn;
    private String datetext;
    private double TotalCollection;
    private String agentEmail,loantype,customerName;
    private TextView dateHead,loanHead;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fs;
    private RadioButton daily,monthly;
    private RadioGroup grp;
    private ProgressDialog pd;
    private int mMonth,mYear,mDay;
    public int flag=0;
    public CollectMoneyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_collect_money, container, false);

        acc=v.findViewById(R.id.AccNo);
        amount=v.findViewById(R.id.AmountCollect);
        pay=v.findViewById(R.id.pay);
        date=v.findViewById(R.id.getDate);
        dateHead=v.findViewById(R.id.dateHead);
        daily=v.findViewById(R.id.dailyColRadio);
        monthly=v.findViewById(R.id.MonthlyColRadio);
        grp=v.findViewById(R.id.loanGrp);
        loanHead=v.findViewById(R.id.LoanTypeHead);
        fs=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        agentEmail= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();


        pd=new ProgressDialog(getActivity());

        date.setOnClickListener(new View.OnClickListener() {
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
                                datetext=dayOfMonth+"-"+month+"-"+year;
                                dateHead.setText("DOR="+datetext);
                            }

                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loantype="Daily";
            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loantype="Monthly";
            }
        });


        //*************************************************************

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setMessage("Wait until uploading data");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                acctext=acc.getText().toString();
                amounttext=amount.getText().toString();

                Map<String, Object> data = new HashMap<>();

                if (isValid()) {
                    data.put("AmountRecieved", amounttext);
                    data.put("DateOfCollection", datetext);
                    data.put("AccountNo",acctext);

                    if(loantype.equals("Daily")) {

                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection(loantype).document("Date_" + datetext)
                                .collection("customers").document("client_" + acctext)
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
                    }
                    else
                    {
                        fs.collection("MoneyLender").document("Agent_" + agentEmail)
                                .collection(loantype).document("Month_" + mMonth+mYear)
                                .collection("customers").document("client_" + acctext)
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
                    .collection(loantype).document("Date_" + datetext)
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
                            TotalCollection=0;

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
                    .collection(loantype).document("Month_" + mMonth+mYear)
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
                                .collection(loantype).document("Month_" + mMonth+mYear)
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
            }
            else {
                pendingamount = String.valueOf(pendingamount);
            }
        }

        data.put("PendingAmount",pendingamount);

        if((AmountToReturn>=0))
        {
            double tempamount=AmountToReturn;
            if(tempamount!=0) {
                tempamount = tempamount - Double.parseDouble(amounttext);
                data.put("AmountToReturn", String.valueOf(tempamount));
            }
            else
            {
                Toast.makeText(getActivity(), "AmountToReturn is null", Toast.LENGTH_SHORT).show();
            }
        }
        else if(Integer.parseInt(pendingamount)>0)
        {
            Toast.makeText(getActivity(), "pending Amount: "+pendingamount, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Account is null", Toast.LENGTH_SHORT).show();

        }

        fs.collection("clients").document("client_"+acctext)
                .collection("loans").document("loan0")
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
                .collection("loans").document("loan0").get()
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

    private void clearText()
    {
        acc.setText("");
        amount.setText("");
        grp.clearCheck();
    }

    @SuppressLint("SetTextI18n")
    private boolean isValid() {

        if(acctext.isEmpty())
        {
            acc.setError("Enter Account no.");
            acc.requestFocus();
            return false;
        }

        else if(isAccExists(acctext))
        {
            acc.setError("Account No. does not exists");
            acc.requestFocus();
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
            date.setError("Select Date");
            dateHead.setText("Select Date");
            return false;
        }
        else if(loantype.isEmpty())
        {
            daily.setError("Select Loan type");
        }
        return true;
    }

    private boolean isAccExists(final String accno) {
        flag= 0;

        if (!agentEmail.isEmpty()) {

            pd.setMessage("Verifying Account no.");

            fs.collection("clients").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                for(DocumentSnapshot doc:task.getResult().getDocuments())
                                {
                                    if(accno.equals(doc.getString("AccountNo")))
                                    {
                                        flag=1;
                                        Toast.makeText(getActivity(), "Account no. exists", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), "Could not found Account No", Toast.LENGTH_SHORT).show();

                }
            });


            if(flag==1)
            {
                return true;
            }
            else
            {
                pd.setMessage("Failed to verify Account no.");
                pd.dismiss();
                return false;
            }
        }
        else
        {
            Toast.makeText(getActivity(), "Agent Email not found", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
