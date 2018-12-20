package com.example.ashitosh.moneylender.Fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Activities.GenerateCsv;
import com.example.ashitosh.moneylender.Adapters.LoanAdapter;
import com.example.ashitosh.moneylender.BuildConfig;
import com.example.ashitosh.moneylender.Models.CustColModel;
import com.example.ashitosh.moneylender.Models.LoanModel;
import com.example.ashitosh.moneylender.Models.custModel;
import com.example.ashitosh.moneylender.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class csvFragment extends Fragment {

    private FirebaseFirestore fs;
    private LoanAdapter adapter;
    private List<LoanModel> userList;
    private List<String> custList;
    private CSVWriter writer = null;
    private Iterator ita;
    private String email,csvType,dateText,agentName;
    private ProgressDialog pd;
    private FirebaseAuth f_auth;
    private Spinner spinner,agentSpinner;
    private ArrayList<String> csvList;
    private ArrayAdapter<String> csvadapter,agentAdapter;

    private Button generateCsvBtn,readCsvBtn,dateBtn;

    private Map<String,LoanModel> custLoans;
    private ArrayList<custModel> custDetail;

    private LocalDate localDate;

    private int mMonth,mYear,mDay;

    private ArrayList agentList,agentEmailList;
    private ArrayList<CustColModel> dailyColList,monthlyColList;

    private TextView agentHead;

    public csvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_csv, container, false);
        fs= FirebaseFirestore.getInstance();

        agentHead=v.findViewById(R.id.AgentCsvNameHead);

        userList=new ArrayList<>();
        custList= new ArrayList<>();

        agentEmailList=new ArrayList();
        agentList=new ArrayList();

        dailyColList=new ArrayList<>();
        monthlyColList=new ArrayList<>();

        custDetail=new ArrayList<>();
        custLoans=new HashMap<>();

        adapter=new LoanAdapter(userList);
        dateBtn=v.findViewById(R.id.csvDate);

        spinner=v.findViewById(R.id.CsvSpinner);
        agentSpinner=v.findViewById(R.id.AgentCsvSpinner);

        csvList=new ArrayList<>();

        csvList.add("Active Customer Accounts");
        csvList.add("Agent`s Daily Collection");
        csvList.add("Agent`s Monthly Collection");
        csvList.add("Monthly Total Collection");

        csvadapter=new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()),android.R.layout.simple_spinner_item,csvList);
        csvadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(csvadapter);


        f_auth=FirebaseAuth.getInstance();

        email= Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();

        pd=new ProgressDialog(getActivity());

        final GenerateCsv g = new GenerateCsv();

        generateCsvBtn=v.findViewById(R.id.GenerateCsvBtn);
        readCsvBtn=v.findViewById(R.id.ReadCsvBtn);

        fetchAgents();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                csvType=parent.getItemAtPosition(position).toString();
                if(csvType.equals("Active Customer Accounts"))
                {
                    dateBtn.setVisibility(View.INVISIBLE);
                    agentSpinner.setVisibility(View.INVISIBLE);
                    agentHead.setVisibility(View.INVISIBLE);
                }
                else if(csvType.equals("Agent`s Daily Collection"))
                {

                    dateBtn.setVisibility(View.VISIBLE);
                    agentSpinner.setVisibility(View.VISIBLE);
                    agentHead.setVisibility(View.VISIBLE);
                }
                else if(csvType.equals("Agent`s Monthly Collection"))
                {

                    dateBtn.setVisibility(View.VISIBLE);
                    agentSpinner.setVisibility(View.VISIBLE);
                    agentHead.setVisibility(View.VISIBLE);
                }
                else if(csvType.equals("Monthly Total Collection"))
                {

                    dateBtn.setVisibility(View.VISIBLE);
                    agentSpinner.setVisibility(View.VISIBLE);
                    agentHead.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        agentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                agentName=agentList.get(position).toString();
                email=agentEmailList.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        generateCsvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(csvType.equals("Active Customer Accounts"))
                {
                    GenerateCustomerCsv();
                }
                else if(csvType.equals("Agent`s Daily Collection"))
                {
                    if(!dateText.isEmpty() && !agentName.isEmpty()) {
                        generateDailyCsv();
                    }
                    else
                    {
                        dateBtn.setText("Please Select Date");
                        dateBtn.setError("Select Date");
                        v.refreshDrawableState();
                    }
                }
                else if(csvType.equals("Agent`s Monthly Collection"))
                {

                    if(!dateText.isEmpty() && !agentName.isEmpty() && !localDate.toString().isEmpty()) {
                        generateMonthlyCsv();
                    }
                    else
                    {
                        dateBtn.setText("Please Select Date");
                        dateBtn.setError("Select Date");
                        v.refreshDrawableState();
                    }
                }
                else if(csvType.equals("Monthly Total Collection"))
                {
                }
            }
        });


        dateBtn.setOnClickListener(new View.OnClickListener() {
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

                                dateText=year+"-"+(month+1)+"-"+dayOfMonth;
                                dateBtn.setText("Date: "+dateText);

                                localDate= LocalDate.parse(dateText);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

/*
        generateCsvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setMessage("Wait until Generating File");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                fs.collection("Agents").document("Agent"+email).collection("Daily").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e!=null)
                        {
                            Log.e("Error: "+e.getMessage(),"Error");
                        }
                        else
                        {
                            for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                            {
                                if (doc.getType().equals(DocumentChange.Type.ADDED))
                                {
//                                    custModel model=doc.getDocument().toObject(custModel.class);
//                                    userList.add(model);
//
//                                    String name=doc.getDocument().getString("CustName");
//                                    Log.d("name","name: "+name);
//
//                                    adapter.notifyDataSetChanged();
                                    String Accno=doc.getDocument().getString("AccountNo");
                                    custList.add(doc.getDocument().getString("CustName"));
                                       Toast.makeText(getActivity(),Accno,Toast.LENGTH_LONG).show();

                                    fs.collection("clients").document("client_"+Accno).collection("loans").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                            if(e!=null)
                                            {
                                                Log.e("Error: "+e.getMessage(),"Error");
                                            }
                                            else
                                            {
                                                for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                                                {
                                                    if (doc.getType().equals(DocumentChange.Type.ADDED))
                                                    {
                                                        LoanModel model=doc.getDocument().toObject(LoanModel.class);
                                                        userList.add(model);

                                                        String name=doc.getDocument().getString("AgentName");
                                                        Log.d("name","name: "+name);

                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });


                fs.collection("Agents").document("Agent_"+email).collection("Monthly").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e!=null)
                        {
                            Log.e("Error: "+e.getMessage(),"Error");
                        }
                        else
                        {
                            for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                            {
                                if (doc.getType().equals(DocumentChange.Type.ADDED))
                                {
//                                    custModel model=doc.getDocument().toObject(custModel.class);
//                                    userList.add(model);
//
//                                    String name=doc.getDocument().getString("CustName");
//                                    Log.d("name","name: "+name);
//
//                                    adapter.notifyDataSetChanged();
                                    custList.add(doc.getDocument().getString("CustName"));
                                    String Accno=doc.getDocument().getString("AccountNo");
                                    //       Toast.makeText(getActivity(),doc.getDocument().getString(""),Toast.LENGTH_LONG).show();

                                    fs.collection("clients").document("client_"+Accno).collection("loans").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                            if(e!=null)
                                            {
                                                Log.e("Error: "+e.getMessage(),"Error");
                                            }
                                            else
                                            {
                                                for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                                                {
                                                    if (doc.getType().equals(DocumentChange.Type.ADDED))
                                                    {


                                                        LoanModel model=doc.getDocument().toObject(LoanModel.class);
                                                        userList.add(model);

                                                        String name=doc.getDocument().getString("AgentName");
                                                        Log.d("name","name: "+name);

                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }

                                            }
                                        }
                                    });
                                }
                            }


                        }
                    }
                });
              //  g.customerCsv("Customer Name","Return Date","Loan Amount","Total");
              //  g.customerCsv("dj","10-9-2019","5000","10000");

               // createCsv();
                pd.dismiss();

            }
        });
*/

        readCsvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MimeTypeMap mtmap= MimeTypeMap.getSingleton();
                Intent openFile = new Intent(Intent.ACTION_VIEW);
                String mimetype= mtmap.getExtensionFromMimeType("csv");

                Uri uri = null;

                if(csvType.equals("Active Customer Accounts"))
                {
                    uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getPath()+"/ActiveCustomer.csv"));

                }
                else if(csvType.equals("Agent`s Daily Collection"))
                {
                    uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getPath()+"/"+"Date_"+dateText+".csv"));

                }
                else if(csvType.equals("Agent`s Monthly Collection"))
                {
                    uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getPath()+"/"+"Month_"+dateText+".csv"));

                }
                else if(csvType.equals("Monthly Total Collection"))
                {

                }

                if(uri!=null) {
                    openFile.setDataAndType(uri, mimetype);

                    openFile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    List<ResolveInfo> resInfoList = Objects.requireNonNull(getActivity()).getPackageManager().queryIntentActivities(openFile, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    try {
                        startActivity(openFile);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return v;

    }

    private void generateMonthlyCsv() {

        pd.setMessage("Generating Monthly Csv");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        fs.collection("MoneyLender").document("Agent_"+email)
                .collection("Monthly")
                .document("Month_"+localDate.getMonthOfYear()+localDate.getYear())
                .collection("customers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                        {

                            if (doc.getType().equals(DocumentChange.Type.ADDED))
                            {
                                String accno=doc.getDocument().getData().get("AccountNo").toString();

                                if(!accno.isEmpty()) {
                                    fs.collection("MoneyLender").document("Agent_" + email)
                                            .collection("Monthly")
                                            .document("Month_" + localDate.getMonthOfYear()+localDate.getYear())
                                            .collection("customers")
                                            .document("client_" + accno)
                                            .collection("loans")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                    for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                                                    {
                                                        if (doc.getType().equals(DocumentChange.Type.ADDED))
                                                        {
                                                            CustColModel model=doc.getDocument().toObject(CustColModel.class);
                                                            monthlyColList.add(model);

                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                        if (!dailyColList.isEmpty())
                        {
                            createMonthlyCsv();
                        }
                        pd.dismiss();
                    }
                });
    }

    private void createMonthlyCsv() {
        try
        {
            ita=dailyColList.iterator();
            int i=0;

            writer = new CSVWriter(new FileWriter("/sdcard/"+"Month_"+String.valueOf(localDate.getMonthOfYear())+String.valueOf(localDate.getYear())+".csv"), ',');
            writer.flushQuietly();

            String heading="Acc No." +","+"Loan Id"+","+"Month Of Collection"+","+"Amount Recieved";
            String[] head = heading.split(","); // array of your valumes
            writer.writeNext(head);

            while(ita.hasNext()) {
                i++;
                CustColModel dailyColModel= (CustColModel) ita.next();

                String entry = dailyColModel.getAccountNo() + "," + dailyColModel.getLoanId() + "," + dailyColModel.getDateOfCollection() + "," +dailyColModel.getAmountRecieved() ;
                String[] entries = entry.split(","); // array of your values
                writer.writeNext(entries);

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }
            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }

    private void generateDailyCsv() {

        pd.setMessage("Generating Daily Csv");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        fs.collection("MoneyLender").document("Agent_"+email)
                    .collection("Daily")
                    .document("Date_"+dateText)
                    .collection("customers")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                            {

                                if (doc.getType().equals(DocumentChange.Type.ADDED))
                                {
                                    String accno=doc.getDocument().getData().get("AccountNo").toString();

                                    if(!accno.isEmpty()) {
                                        fs.collection("MoneyLender").document("Agent_" + email)
                                                .collection("Daily")
                                                .document("Date_" + dateText)
                                                .collection("customers")
                                                .document("client_" + accno)
                                                .collection("loans")
                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                        for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges())
                                                        {
                                                            if (doc.getType().equals(DocumentChange.Type.ADDED))
                                                            {
                                                                CustColModel model=doc.getDocument().toObject(CustColModel.class);
                                                                dailyColList.add(model);

                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }

                            if (!dailyColList.isEmpty())
                            {
                                createDailyCsv();
                            }
                            pd.dismiss();
                        }
                    });
    }


    private void fetchAgents()
    {
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

    }

    @SuppressLint("SdCardPath")
    private void GenerateCustomerCsv() {

        pd.setMessage("Wait until Generating csv");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        ita=custDetail.iterator();
        int i=0;

        try {
            writer = new CSVWriter(new FileWriter("/sdcard/ActiveCustomer.csv"), ',');

        } catch (IOException e) {
            e.printStackTrace();
        }


        String heading="Acc No."+","+"Customer Name" +","+"Loan Id"+","+"Loan Type"+","+"Filed Amount"+","+"Interest Rate"+","+"Date Of Issue"+","+"Date Of Return"+","+"Total Loan";
        String[] head = heading.split(","); // array of your valumes
        writer.writeNext(head);


        fs.collection("clients").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                {
                    if(doc.getType().equals(DocumentChange.Type.ADDED))
                    {
                        final custModel detailModel=doc.getDocument().toObject(custModel.class);

                        custDetail.add(detailModel);

                        fs.collection("clients").document("client_"+detailModel.getAccountNo())
                                .collection("loans")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                        for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                                        {
                                            if (doc.getType().equals(DocumentChange.Type.ADDED))
                                            {
                                                LoanModel loanModel=doc.getDocument().toObject(LoanModel.class);

                                                if(loanModel.getStatus().equals("1")) {
                                                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), detailModel.getAccountNo()+loanModel.getLoanType(), Toast.LENGTH_SHORT).show();
                                                   custLoans.put(detailModel.getAccountNo(),loanModel);
                                                }
                                            }
                                        }
                                    }
                                });

                    }

                }
                if (!custLoans.isEmpty()) {
                    createCustomerCsv();
                }
                pd.dismiss();
            }
        });



    }


    private void createDailyCsv() {

        try
        {
            ita=dailyColList.iterator();
            int i=0;

            writer = new CSVWriter(new FileWriter("/sdcard/"+agentName+"_"+dateText+".csv"), ',');
            writer.flushQuietly();

            String heading="Acc No." +","+"Loan Id"+","+"Date Of Collection"+","+"Amount Recieved";
            String[] head = heading.split(","); // array of your valumes
            writer.writeNext(head);

            while(ita.hasNext()) {
                i++;
                CustColModel dailyColModel= (CustColModel) ita.next();

                String entry = dailyColModel.getAccountNo() + "," + dailyColModel.getLoanId() + "," + dailyColModel.getDateOfCollection() + "," +dailyColModel.getAmountRecieved() ;
                String[] entries = entry.split(","); // array of your values
                writer.writeNext(entries);

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }
            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }

    private void createCustomerCsv() {

        try
        {
            ita=custDetail.iterator();
            int i=0;

            writer = new CSVWriter(new FileWriter("/sdcard/ActiveCustomer.csv"), ',');
            writer.flushQuietly();

            String heading="Acc No."+","+"Customer Name" +","+"Loan Id"+","+"Loan Type"+","+"Filed Amount"+","+"Interest Rate"+","+"Date Of Issue"+","+"Date Of Return"+","+"Total Loan";
            String[] head = heading.split(","); // array of your valumes
            writer.writeNext(head);

            while(ita.hasNext()) {
                i++;
                custModel detailModel= (custModel) ita.next();

                LoanModel loanModel=custLoans.get(detailModel.getAccountNo());

                double TotalLoan;
                if (loanModel.getLoanType().equals("Daily")) {
                    TotalLoan = Double.parseDouble(loanModel.getFiledAmount());
                } else {

                    LocalDate sdate = LocalDate.parse(loanModel.getDOI());
                    LocalDate edate = LocalDate.parse(loanModel.getDOR());

                    TotalLoan = interest(sdate, edate, Double.parseDouble(loanModel.getFiledAmount()), Double.parseDouble(loanModel.getInterest()));
                }


                String entry = detailModel.getAccountNo() + "," + detailModel.getCustName() + "," + loanModel.getLoanId() + "," + loanModel.getLoanType() + "," + loanModel.getFiledAmount() + "," + loanModel.getInterest() + "," + loanModel.getDOI() + "," + loanModel.getDOR() + "," + String.valueOf(TotalLoan);
                String[] entries = entry.split(","); // array of your values
                writer.writeNext(entries);

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }
            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }



    private double interest(LocalDate sdate, LocalDate edate, double Amount, double interestRate)
    {

            int months= Months.monthsBetween(sdate,edate).getMonths();

            Double SI = (Amount * interestRate * months) / (100);

            double TotalAmount = Amount + SI;

            DecimalFormat dec = new DecimalFormat("#0.00");

            TotalAmount = Double.valueOf(dec.format(TotalAmount));

            //calculating expected Amount

            return TotalAmount;
        }

    }



