package com.example.ashitosh.moneylender.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteCustFragment extends Fragment {

    private EditText custAcc;
    private Button delCust;

    private FirebaseFirestore fs;

    private DocumentReference dr;

    private ProgressDialog pd;
    public DeleteCustFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_delete_cust, container, false);

        fs=FirebaseFirestore.getInstance();
        custAcc=v.findViewById(R.id.DelCustAcc);
        delCust=v.findViewById(R.id.DeleteCustBtn);

        pd=new ProgressDialog(this.getActivity());


        delCust.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                pd.setMessage("Deleting customer");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                if(!custAcc.getText().toString().isEmpty())
                {


                    fs.collection("clients").document("client_"+custAcc.getText().toString())                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                Toast.makeText(getActivity(), "customer deleted Successfully", Toast.LENGTH_LONG).show();

                                pd.setMessage("Customer Deleted Successfully");
                                pd.hide();
                                pd.dismiss();

                                sendTomain();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(), "Failed to delete Customer"+e.getMessage(), Toast.LENGTH_LONG).show();

                            pd.setMessage("failed to delete customer");
                            pd.hide();
                            pd.dismiss();
                        }
                    });


                }
                else
                {
                    custAcc.setError("Enter Acccount No.");
                    custAcc.requestFocus();
                }
            }
        });

        return v;
    }


    private void sendTomain() {

        SearchFragment fragment=new SearchFragment();

        android.support.v4.app.FragmentTransaction fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(fragment,"Custhead").addToBackStack("head");

        fragmentTransaction.replace(R.id.mainFrame,fragment);

        fragmentTransaction.commit();

    }

}
