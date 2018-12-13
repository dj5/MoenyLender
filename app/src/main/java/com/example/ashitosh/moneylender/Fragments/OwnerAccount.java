package com.example.ashitosh.moneylender.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class OwnerAccount extends Fragment {

    private FirebaseAuth f_auth;
    private FirebaseFirestore fs;
    private de.hdodenhof.circleimageview.CircleImageView imageView;
    private TextView name,email,phone,address,namehead;
    private ProgressDialog pd;
    private DocumentSnapshot ds;
    private Picasso picasso;
    private Uri uri;
    private String imgurl,namestr,phonestr,addstr;


    public OwnerAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_manage_agent, container, false);

        pd=new ProgressDialog(getActivity());
        name=v.findViewById(R.id.OwnerName);
        email=v.findViewById(R.id.OwnerEmail);
        address=v.findViewById(R.id.OwnerAddress);
        phone=v.findViewById(R.id.OwnerPhone);
        namehead=v.findViewById(R.id.OwnerNameHead);

        imageView=v.findViewById(R.id.OwnerPhoto);

        fs=FirebaseFirestore.getInstance();
        f_auth=FirebaseAuth.getInstance();

        final String user= Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();

        pd.setMessage("please Wait Until Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        DocumentReference docRef = fs.collection("customer").document("cust_"+user);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String suri;

                if (task.isSuccessful())
                {
                    namestr=task.getResult().getString("name");
                    suri=  task.getResult().getString("imgUri");

                    email.setText(user);
                    phone.setText("not provided");
                    address.setText("not provided");

                    if (namestr!=null) {
                        name.setText(namestr);
                        namehead.setText(namestr);
                    }

                    if(suri!=null) {
                        uri=Uri.parse(suri);

                        Picasso.get().load(uri).into(imageView);
                    }

                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Failed To fetch data: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return v;
    }

}
