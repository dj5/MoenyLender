package com.example.ashitosh.moneylender.Fragments;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class DelAgentFragment extends Fragment {

    private Button delAgent;
    private EditText email;
    private String emalstr;
    private ProgressDialog pd;
    private FirebaseFirestore fs;
    private DocumentReference dref;
    private RelativeLayout layout;

    public DelAgentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_del_agent, container, false);

        email=v.findViewById(R.id.delAgentEmail);
        delAgent=v.findViewById(R.id.delAgentBtn);
        pd=new ProgressDialog(getActivity());

        layout=v.findViewById(R.id.relativeLayout);

        fs=FirebaseFirestore.getInstance();

        delAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                emalstr=email.getText().toString();

                if(emalstr.isEmpty())
                {
                    email.setError("Enter Agent Email");
                    email.requestFocus();
                }
                else if (isValidEmail(emalstr))
                {

                    pd.setTitle("Delete Agent");
                    pd.setMessage("Deleting Agent");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    dref=fs.collection("MoneyLender").document("Agent_"+emalstr);

                    dref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                pd.setMessage("Agent successfully deleted");
                                pd.hide();
                                pd.dismiss();

                             /*
                                Snackbar snackbar=Snackbar.make(layout,"Agent Successfully deleted",Snackbar.LENGTH_LONG)
                                        .setAction("CLOSE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        });
                                snackbar.setActionTextColor(Color.GREEN);

                                View v=snackbar.getView();
                                TextView textView=v.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.GREEN);
                                snackbar.setDuration(4000).dismiss();
                                snackbar.show();
*/

                                Toast.makeText(getActivity(),"Agent successfully deleted",Toast.LENGTH_LONG).show();


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(),"Failed to delete Agent",Toast.LENGTH_LONG).show();

                            pd.setMessage("Failed to delete");
                            pd.hide();
                            pd.dismiss();
                        }
                    });


                }
                else
                {
                    email.setError("Invalid Email Pattern");
                    email.requestFocus();
                }

            }
        });

        return v;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
