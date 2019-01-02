package com.example.ashitosh.moneylender.Fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
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
import com.opencsv.CSVWriter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
    private Map<String,Object> custNames;
    private ArrayList<Map<String,Object>> monthlyTotalCol,dailyTotalCol;
    private TextView agentHead;
    private String requestor,month;
    private  ArrayList<String> tempCust,tempTotalDaily,tempTotalMonthly,tempDaily,tempMonthly;
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

        Bundle data=getArguments();

        requestor= Objects.requireNonNull(data).getString("frag");

        userList=new ArrayList<>();
        custList= new ArrayList<>();

        agentEmailList=new ArrayList();
        agentList=new ArrayList();

        dailyColList=new ArrayList<>();
        monthlyColList=new ArrayList<>();

        monthlyTotalCol=new ArrayList<>();
        dailyTotalCol=new ArrayList<>();

        custDetail=new ArrayList<>();
        custLoans=new HashMap<>();
        custNames=new HashMap<>();

        tempCust =new ArrayList<>();
        tempTotalDaily=new ArrayList<>();
        tempMonthly=new ArrayList<>();
        tempTotalMonthly=new ArrayList<>();
        tempDaily=new ArrayList<>();

        dateText=null;

        adapter=new LoanAdapter(userList);
        dateBtn=v.findViewById(R.id.csvDate);

        spinner=v.findViewById(R.id.CsvSpinner);
        agentSpinner=v.findViewById(R.id.AgentCsvSpinner);

        csvList=new ArrayList<>();


        csvList.add("Agent`s Daily Collection");
        csvList.add("Agent`s Monthly Collection");

        if (requestor.equals("Owner")) {

            csvList.add("Active Customer Accounts");
            csvList.add("Daily Total Collection");
            csvList.add("Monthly Total Collection");

        }

        csvadapter=new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()),android.R.layout.simple_spinner_item,csvList);
        csvadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(csvadapter);


        f_auth=FirebaseAuth.getInstance();

        email= Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();

        pd=new ProgressDialog(getActivity());

        final GenerateCsv g = new GenerateCsv();

        generateCsvBtn=v.findViewById(R.id.GenerateCsvBtn);
        readCsvBtn=v.findViewById(R.id.ReadCsvBtn);

        if (requestor.equals("Owner")) {
            fetchAgents();
            fetchCustomer();
        }
        else if(requestor.equals("Agent"))
        {
            fetchCustomer();
            agentSpinner.setVisibility(View.INVISIBLE);
            agentHead.setVisibility(View.INVISIBLE);
            agentName="agent";
        }

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
                    if (requestor.equals("Owner")) {
                        agentSpinner.setVisibility(View.VISIBLE);
                        agentHead.setVisibility(View.VISIBLE);
                    }
                }
                else if(csvType.equals("Agent`s Monthly Collection"))
                {

                    dateBtn.setVisibility(View.VISIBLE);
                    if (requestor.equals("Owner")) {
                        agentSpinner.setVisibility(View.VISIBLE);
                        agentHead.setVisibility(View.VISIBLE);
                    }
                }
                else if(csvType.equals("Daily Total Collection"))
                {

                    dateBtn.setVisibility(View.VISIBLE);
                    if (requestor.equals("Owner")) {
                        agentSpinner.setVisibility(View.VISIBLE);
                        agentHead.setVisibility(View.VISIBLE);
                    }
                }
                else if(csvType.equals("Monthly Total Collection"))
                {

                    dateBtn.setVisibility(View.VISIBLE);
                    if (requestor.equals("Owner")) {
                        agentSpinner.setVisibility(View.VISIBLE);
                        agentHead.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        agentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(requestor.equals("Owner")) {
                    agentName = agentList.get(position).toString();
                    email = agentEmailList.get(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        generateCsvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                if (agentList.contains("No Agents Found"))
                {
                    warn(v,"agent");
                }
                else if(csvType.equals("Active Customer Accounts"))
                {
                  //  custDetail.clear();
                  //  custLoans.clear();

                    GenerateCustomerCsv();

                }
                else if (dateText==null)
                {
                    warn(v,"date");
                }
                else if(csvType.equals("Agent`s Daily Collection"))
                {
                    if(dateText!=null && !agentName.isEmpty() && !custNames.isEmpty()) {

                    //    dailyColList.clear();
                        generateDailyCsv();
                    }
                    else
                    {
                        if (dateText.isEmpty()) {
                            dateBtn.setText("Please Select Date");
                            dateBtn.setError("Select Date");
                            v.refreshDrawableState();
                        }
                    }
                }
                else if(csvType.equals("Agent`s Monthly Collection"))
                {

                    if(dateText!=null && !agentName.isEmpty() && !localDate.toString().isEmpty() && !custNames.isEmpty()) {
                    //    monthlyColList.clear();
                        generateMonthlyCsv();
                    }
                    else
                    {
                        if (dateText.isEmpty()) {
                            dateBtn.setText("Please Select Date");
                            dateBtn.setError("Select Date");
                            v.refreshDrawableState();
                        }
                    }
                }
                else if(csvType.equals("Monthly Total Collection"))
                {
                        month= String.valueOf(localDate.getMonthOfYear())+String.valueOf(localDate.getYear());

                        if (!month.isEmpty() && !dateText.isEmpty() && !localDate.toString().isEmpty()) {

                      //      monthlyTotalCol.clear();
                            Toast.makeText(getActivity(), "Making call to daily", Toast.LENGTH_SHORT).show();

                            generateTotalMonthCsv();
                        }
                        else
                            {
                                if (dateText.isEmpty()) {
                                    dateBtn.setText("Please Select Date");
                                    dateBtn.setError("Select Date");
                                    v.refreshDrawableState();
                                }
                            }
                }

                else if(csvType.equals("Daily Total Collection"))
                {

                    if (!localDate.toString().isEmpty() && !dateText.isEmpty() ) {

                     //   dailyTotalCol.clear();
                        generateTotalDailyCsv();
                    }
                    else
                    {
                        if (dateText.isEmpty()) {
                            dateBtn.setText("Please Select Date");
                            dateBtn.setError("Select Date");
                            v.refreshDrawableState();
                        }
                    }
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


        readCsvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MimeTypeMap mtmap= MimeTypeMap.getSingleton();
                Intent openFile = new Intent(Intent.ACTION_VIEW);
                String mimetype= mtmap.getExtensionFromMimeType("csv");

                Uri uri = null;

                if (agentList.contains("No Agents Found"))
                {
                    warn(v,"agent");
                }
                else if(csvType.equals("Active Customer Accounts"))
                {
                    uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getPath()+"/ActiveCustomer.csv"));

                }
                else if (dateText==null)
                {
                    warn(v,"date");
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
                    uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getPath()+"/"+"MonthTotal_"+dateText+".csv"));

                }
                else if(csvType.equals("Daily Total Collection"))
                {
                    uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getPath()+"/"+"DailyTotal_"+dateText+".csv"));

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

    //****************************************************************

    private void warn(final View v,String str)
    {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        if (str.equals("agent")) {
            builder.setMessage("There is no record");
        }
        else
        {
            builder.setMessage("Please Select date");
        }
        builder.setCancelable(true);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                v.refreshDrawableState();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    //**************************************************************************************
    private void generateTotalDailyCsv() {

        pd.setMessage("Generating Daily Total csv");
        pd.setCanceledOnTouchOutside(false);
        pd.show();


        fs.collection("MoneyLender").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                {
                    if (doc.getType().equals(DocumentChange.Type.ADDED)) {

                        String AgentEmail = doc.getDocument().getData().get("Email").toString();
                        final String AgentName=doc.getDocument().getData().get("Name").toString();

                        if (!tempTotalDaily.contains(AgentEmail))
                        {
                            tempTotalDaily.add(AgentEmail);

                            if (!AgentEmail.isEmpty() && !dateText.isEmpty()) {
                            fs.collection("MoneyLender").document("Agent_" + AgentEmail)
                                    .collection("Daily")
                                    .document("Date_" + dateText)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                Map<String, Object> data = new HashMap<>();

                                                data.put(AgentName, task.getResult().getString("TotalCollection"));
                                                dailyTotalCol.add(data);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        }
                    }
                }

                if (!dailyTotalCol.isEmpty())
                {
                    File csv=new File("/sdcard/"+"DailyTotal_"+dateText+".csv");

                    if (csv.exists())
                    {
                        boolean c=csv.delete();
                        createDailyTotalCsv();
                    }
                    else {
                        createDailyTotalCsv();
                    }

                }
                pd.dismiss();
            }
        });
    }


    private void createDailyTotalCsv() {
        try
        {
            ita=dailyTotalCol.iterator();
            int i=0;


            writer = new CSVWriter(new FileWriter("/sdcard/"+"DailyTotal_"+dateText+".csv"), ',');
            writer.flushQuietly();

            String heading="Sn No." +","+"Agent Name"+","+"Total Collection";

            String[] head = heading.split(","); // array of your valumes
            writer.writeNext(head);
            double total=0;

            while(ita.hasNext()) {
                i++;

                Map<String,Object> agentData= (Map<String, Object>) ita.next();
                String val;

                if (agentData.values().iterator().next()!=null)
                {
                    val=agentData.values().iterator().next().toString();
                    total += Double.parseDouble(val);
                }


                String entry = i+ ","+agentData.keySet().iterator().next()+","+ agentData.values().iterator().next();
                String[] entries = entry.split(","); // array of your values
                writer.writeNext(entries);

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }

            String entry = ""+ ","+"Total:- "+","+ total;
            String[] entries = entry.split(","); // array of your values
            writer.writeNext(entries);

            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }


    //******************************************************************************************8


    private void generateTotalMonthCsv() {

        pd.setMessage("Generating Monthly Total csv");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        fs.collection("MoneyLender").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                {
                    if (doc.getType().equals(DocumentChange.Type.ADDED)) {

                        String AgentEmail = doc.getDocument().getData().get("Email").toString();
                        final String AgentName=doc.getDocument().getData().get("Name").toString();

                        if (tempTotalMonthly.contains(AgentEmail))
                        {
                            tempTotalMonthly.add(AgentEmail);

                            Toast.makeText(getActivity(), "adding: "+AgentEmail, Toast.LENGTH_SHORT).show();

                        if (!AgentEmail.isEmpty() && !month.isEmpty()) {
                            fs.collection("MoneyLender").document("Agent_" + AgentEmail)
                                    .collection("Monthly")
                                    .document("Month_" + month)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                Map<String, Object> data = new HashMap<>();

                                                data.put(AgentName, task.getResult().getString("TotalCollection"));
                                                monthlyTotalCol.add(data);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        }
                    }
                }

                if (!monthlyTotalCol.isEmpty())
                {
                    File csv=new File("/sdcard/"+"MonthTotal_"+dateText+".csv");

                    if (csv.exists())
                    {
                        boolean c=csv.delete();
                        createMonthlyTotalCsv();
                    }
                    else {
                        createMonthlyTotalCsv();
                    }


                }
                pd.dismiss();
            }
        });
    }

    private void createMonthlyTotalCsv() {
        try
        {
            ita=monthlyTotalCol.iterator();
            int i=0;


            writer = new CSVWriter(new FileWriter("/sdcard/"+"MonthTotal_"+dateText+".csv"), ',');
            writer.flushQuietly();

            String heading="Sn No." +","+"Agent Name"+","+"Total Collection";

            String[] head = heading.split(","); // array of your valumes
            writer.writeNext(head);
            double total=0;

            while(ita.hasNext()) {
                i++;

                Map<String,Object> agentData= (Map<String, Object>) ita.next();
                String val;

                if (agentData.values().iterator().next()!=null)
                {
                    val=agentData.values().iterator().next().toString();
                    total += Double.parseDouble(val);
                }


                String entry = i+ ","+agentData.keySet().iterator().next()+","+ agentData.values().iterator().next();
                String[] entries = entry.split(","); // array of your values
                writer.writeNext(entries);

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }

            String entry = ""+ ","+"Total:- "+","+ total;
            String[] entries = entry.split(","); // array of your values
            writer.writeNext(entries);

            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }


    //**************************************************************************************

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

                                if (!tempMonthly.contains(accno))
                                {

                                    tempMonthly.add(accno);

                                if(!accno.isEmpty()) {
                                    fs.collection("MoneyLender").document("Agent_" + email)
                                            .collection("Monthly")
                                            .document("Month_" + localDate.getMonthOfYear() + localDate.getYear())
                                            .collection("customers")
                                            .document("client_" + accno)
                                            .collection("loans")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                    for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {
                                                        if (doc.getType().equals(DocumentChange.Type.ADDED)) {
                                                            CustColModel model = doc.getDocument().toObject(CustColModel.class);
                                                            monthlyColList.add(model);

                                                        }
                                                    }
                                                }
                                            });
                                }
                                }
                            }
                        }
                        if (!monthlyColList.isEmpty())
                        {
                            File csv=new File("/sdcard/"+"Month_"+dateText+".csv");

                            if (csv.exists())
                            {
                                boolean c=csv.delete();
                                createMonthlyCsv();
                            }
                            else {
                                createMonthlyCsv();
                            }

                        }
                        pd.dismiss();
                    }
                });
    }


    private void createMonthlyCsv() {
        try
        {
            ita=monthlyColList.iterator();
            int i=0;


            writer = new CSVWriter(new FileWriter("/sdcard/"+"Month_"+dateText+".csv"), ',');
            writer.flushQuietly();

            String heading="Acc No." +","+"Customer Name"+","+"Loan Id"+","+"Month Of Collection"+","+"Amount Recieved";
            String[] head = heading.split(","); // array of your valumes
            writer.writeNext(head);

            double total=0;

            while(ita.hasNext()) {
                i++;
                CustColModel dailyColModel= (CustColModel) ita.next();

                String val= dailyColModel.getAmountRecieved();

                if (!val.isEmpty()) {
                    total += Double.parseDouble(val);
                }

                String entry = dailyColModel.getAccountNo() + ","+custNames.get(dailyColModel.getAccountNo()) +","+ dailyColModel.getLoanId() + "," + dailyColModel.getDateOfCollection() + "," +val ;
                String[] entries = entry.split(","); // array of your values
                writer.writeNext(entries);

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }

            String entry = "" + ","+""+","+"" + "," + "Total: " + "," +total;
            String[] entries = entry.split(","); // array of your values
            writer.writeNext(entries);

            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }

    //**************************************************************************************

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

                                    Toast.makeText(getActivity(), "adding: "+accno, Toast.LENGTH_SHORT).show();

                                    if (!tempDaily.contains(accno))
                                    {

                                        tempDaily.add(accno);

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

                                                        for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {
                                                            if (doc.getType().equals(DocumentChange.Type.ADDED)) {
                                                                CustColModel model = doc.getDocument().toObject(CustColModel.class);
                                                                dailyColList.add(model);

                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                    }
                                }
                            }

                            if (!dailyColList.isEmpty())
                            {

                                File csv=new File("/sdcard/" + "Date_" + dateText + ".csv");

                                if (csv.exists())
                                {
                                    boolean c=csv.delete();
                                    createDailyCsv();
                                }
                                else {
                                    createDailyCsv();
                                }
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "No record found", Toast.LENGTH_SHORT).show();
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

            writer = new CSVWriter(new FileWriter("/sdcard/" + "Date_" + dateText + ".csv"), ',');

            writer.flushQuietly();

            String heading="Acc No."+","+"Customer Name"+","+"Loan Id"+","+"Date Of Collection"+","+"Amount Recieved";
            String[] head = heading.split(","); // array of your valumes
            writer.writeNext(head);

            double total=0;

            while(ita.hasNext()) {
                i++;
                CustColModel dailyColModel= (CustColModel) ita.next();

                String val= dailyColModel.getAmountRecieved();

                if (!val.isEmpty()) {
                    total += Double.parseDouble(val);
                }
                String entry = dailyColModel.getAccountNo()+","+custNames.get(dailyColModel.getAccountNo()) + "," + dailyColModel.getLoanId() + "," + dailyColModel.getDateOfCollection() + "," +dailyColModel.getAmountRecieved() ;
                String[] entries = entry.split(","); // array of your values
                writer.writeNext(entries);

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }
            String entry =""+","+""+ "," + ""+ "," + "Total:" + "," +total;
            String[] entries = entry.split(","); // array of your values
            writer.writeNext(entries);

            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }


    //***************************************************************************************88
    private void fetchAgents()
    {
        //Fetch Agent Names
        pd.setMessage("Wait until fetching Details");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        agentList.clear();
        agentEmailList.clear();

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

                    if(agentList!=null) {
                        agentAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, agentList);
                        agentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        agentSpinner.setAdapter(agentAdapter);
                    }
                    else
                    {
                        agentList.add("No Agents Found");
                        agentEmailList.add("No Agents Found");
                        agentAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, agentList);
                        agentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        agentSpinner.setAdapter(agentAdapter);
                    }
                    pd.setMessage("Agents Name Fetched");
                    pd.dismiss();
                }

            }
        });

    }

    private void fetchCustomer()
    {
        pd.setMessage("Wait until fetching customers");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        fs.collection("clients").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                {
                    if (doc.getType().equals(DocumentChange.Type.ADDED))
                    {
                        custNames.put(doc.getDocument().getData().get("AccountNo").toString(),doc.getDocument().getData().get("CustName").toString());
                    }
                }

                pd.setMessage("Customer Names fetched");
                pd.dismiss();
            }
        });
    }

    //**************************************************************************************

    @SuppressLint("SdCardPath")
    private void GenerateCustomerCsv() {

        pd.setMessage("Wait until Generating csv");
        pd.setCanceledOnTouchOutside(false);
        pd.show();



        fs.collection("clients").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                for (DocumentChange doc: Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                {
                    if(doc.getType().equals(DocumentChange.Type.ADDED))
                    {
                        final custModel detailModel=doc.getDocument().toObject(custModel.class);

                            if (!tempCust.contains(detailModel.getAccountNo())) {

                                tempCust.add(detailModel.getAccountNo());
                                custDetail.add(detailModel);

                                fs.collection("clients").document("client_" + detailModel.getAccountNo())
                                        .collection("loans")
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {
                                                    if (doc.getType().equals(DocumentChange.Type.ADDED)) {
                                                        LoanModel loanModel = doc.getDocument().toObject(LoanModel.class);

                                                        if (loanModel.getStatus().equals("1")) {
                                                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), detailModel.getAccountNo() + loanModel.getLoanType(), Toast.LENGTH_SHORT).show();
                                                            custLoans.put(detailModel.getAccountNo(), loanModel);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                    }

                }
                if (!custLoans.isEmpty()) {


                    File csv=new File("/sdcard/ActiveCustomer.csv");

                    if (csv.exists())
                    {

                        if (csv.delete()) {

                            createCustomerCsv();

                        }
                    }
                    else {

                        createCustomerCsv();
                    }

                }
                pd.dismiss();
            }
        });



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

                LoanModel loanModel=new LoanModel();

                loanModel=custLoans.get(detailModel.getAccountNo());

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

             //   Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), entry, Toast.LENGTH_SHORT).show();
            }
            writer.close();
        }
        catch (IOException e)
        {
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }
    }


//**********************************************************************************************88

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



