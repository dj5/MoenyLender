package com.example.ashitosh.moneylender;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.widget.Button;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Activities.GenerateCsv;
import com.example.ashitosh.moneylender.Adapters.LoanAdapter;
import com.example.ashitosh.moneylender.Models.LoanModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;




public class csvDaily extends Fragment {
    private FirebaseFirestore fs;
    private LoanAdapter adapter;
    private List<AgentDaily> userList;
    private List<String> custList;
    private CSVWriter writer = null;
    private Iterator ita;
    private ProgressDialog pd;
    private String email;
    private FirebaseAuth f_auth;
    private Button generate,read;
    public csvDaily() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_csv, container, false);
        fs= FirebaseFirestore.getInstance();
        f_auth=FirebaseAuth.getInstance();

        generate=v.findViewById(R.id.generateDaily);
        read=v.findViewById(R.id.readDaily);

        Bundle data=getArguments();

        if(Objects.requireNonNull(Objects.requireNonNull(data).getString("frag")).equals("Owner"))
        {
            email=data.getString("agentemail");
        }
        else {
            email = Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();

        }

        userList=new ArrayList<>();
        custList= new ArrayList<>();
        //adapter=new LoanAdapter(userList);
        final GenerateCsv g = new GenerateCsv();
        Button btn = v.findViewById(R.id.button);

        pd=new ProgressDialog(getActivity());
        Toast.makeText(getActivity(),email,Toast.LENGTH_LONG).show();


        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               pd.setMessage("Wait until Generating File");
               pd.setCanceledOnTouchOutside(false);
               pd.show();


if(email!=null)

    fs.collection("MoneyLender").document("Agent_"+email).collection("Daily").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    AgentDaily model=doc.getDocument().toObject(AgentDaily.class);
                                    userList.add(model);
                             //       Toast.makeText(getActivity(),doc.getDocument().getString(""),Toast.LENGTH_LONG).show();

                                }


                            }

                            createCsv();

                            pd.dismiss();
                        }
                    }
                });

                // g.customerCsv("Customer Name","Return Date","Loan Amount","Total");
                // g.customerCsv("dj","10-9-2019","5000","10000");


            }
        });



        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MimeTypeMap mtmap= MimeTypeMap.getSingleton();
                Intent openFile = new Intent(Intent.ACTION_VIEW);
                String mimetype= mtmap.getExtensionFromMimeType("csv");

                Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getPath()+"/agentDaily.csv"));

                openFile.setDataAndType(uri,mimetype);

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
        });

        return v;

    }

    private void createCsv() {

        try
        {


            ita=userList.iterator();
            int i=0;
            writer = new CSVWriter(new FileWriter("/sdcard/agentDaily.csv"), ',');


            String heading="No."+","+"Day Of Collection" +","+"Total Collection";
            //   Toast.makeText(getActivity(),collection,Toast.LENGTH_LONG).show();
            String[] head = heading.split(","); // array of your values
            writer.writeNext(head);

            while(ita.hasNext()) {
                i++;
                AgentDaily lm= (AgentDaily) ita.next();
                String collection=i+","+ lm.getDate()+","+lm.getTotalCollection();
             //   Toast.makeText(getActivity(),collection,Toast.LENGTH_LONG).show();
                String[] entries = collection.split(","); // array of your values
                writer.writeNext(entries);
            }
            writer.close();



        }
        catch (IOException e)
        {
            pd.dismiss();
            Toast.makeText(getActivity(),"error: "+e,Toast.LENGTH_LONG).show();
        }

    }

}
