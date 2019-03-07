package com.example.ashitosh.moneylender.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentDetailFragment extends Fragment {

    private TextView name,email,phone,address;
    private FirebaseFirestore fs;
    private FirebaseAuth f_auth;
    private String namestr,emailstr,phonestr,addrstr;
    private ProgressDialog pd;
    private Button changePass;
    private Dialog myDialog,changePassDiag;
    private String changeemailstr,NewPassStr,ConfirmPassStr;
    private String passtr;

    private EditText Custemail,pass,NewPass,ConfirmPass;

    public AgentDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_agent_detail, container, false);

        name=v.findViewById(R.id.AgentDetailName);
        email=v.findViewById(R.id.AgentDetailEmail);
        phone=v.findViewById(R.id.AgentDetailPhone);
        address=v.findViewById(R.id.AgentDetailAddr);

        changePass=v.findViewById(R.id.ChangePass);

        pd=new ProgressDialog(getActivity());

        fs=FirebaseFirestore.getInstance();
        f_auth=FirebaseAuth.getInstance();

        emailstr= Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();

            pd.setMessage("Wait until Loading");
            pd.setCanceledOnTouchOutside(false);
            pd.show();


            fs.collection("MoneyLender").document("Agent_"+emailstr)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        namestr=task.getResult().getString("Name");
                        phonestr=task.getResult().getString("Phone");
                        addrstr=task.getResult().getString("Address");
                        emailstr=task.getResult().getString("Email");

                        if(!namestr.isEmpty() && ! Objects.requireNonNull(emailstr).isEmpty() && !phonestr.isEmpty() && !Objects.requireNonNull(addrstr).isEmpty())
                        {
                            name.setText(namestr);
                            email.setText(emailstr);
                            phone.setText(phonestr);
                            address.setText(addrstr);

                        }

                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), "Failed To Fetch Agent Details: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });



            changePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    changePass();

                }
            });



        return v;
    }


    public void changePass()
    {
        myDialog = new Dialog(Objects.requireNonNull(this.getActivity()));
        myDialog.setContentView(R.layout.chane_pass);
        myDialog.setCancelable(true);
        Button login = (Button) myDialog.findViewById(R.id.ChangePassSignin);


        email = (EditText) myDialog.findViewById(R.id.ChangeEmail);
        pass = (EditText) myDialog.findViewById(R.id.ChangePass);
        myDialog.show();

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pd.setMessage("Please Wait until Reauthenticating");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                changeemailstr =email.getText().toString();
                passtr =pass.getText().toString();

                //your login calculation goes here
                FirebaseAuth auth=FirebaseAuth.getInstance();

                if (valid()) {

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(changeemailstr, passtr);

// Prompt the user to re-provide their sign-in credentials
                    Objects.requireNonNull(user).reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        myDialog.dismiss();
                                        pd.dismiss();

                                        changePassDiag = new Dialog(Objects.requireNonNull(getActivity()));
                                        changePassDiag.setContentView(R.layout.update);
                                        changePassDiag.setCancelable(true);
                                        Button login2 = (Button) changePassDiag.findViewById(R.id.ChangePassSignin2);


                                         NewPass= (EditText) changePassDiag.findViewById(R.id.ChangePass2);
                                         ConfirmPass = (EditText) changePassDiag.findViewById(R.id.ConfirmPass2);
                                         changePassDiag.show();


                                        login2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                pd.setMessage("Wait until Updating password");
                                                pd.setCanceledOnTouchOutside(false);
                                                pd.show();

                                                NewPassStr=NewPass.getText().toString();
                                                ConfirmPassStr=ConfirmPass.getText().toString();

                                                if (ValidPass())
                                                {
                                                    user.updatePassword(NewPassStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                pd.setMessage("Updating Password");
                                                                Map<String,Object> data=new HashMap<>();

                                                                data.put("Password",NewPassStr);

                                                                fs.collection("MoneyLender").document("Agent_"+emailstr).update(data)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    pd.setMessage("Successfully updated");
                                                                                    pd.dismiss();

                                                                                    Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();

                                                                                    changePassDiag.dismiss();
                                                                                }
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                        pd.setMessage("Failed to Update");
                                                                        pd.dismiss();

                                                                        Toast.makeText(getActivity(), "Failed to update password: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                                                                        changePassDiag.dismiss();
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pd.dismiss();
                                                            changePassDiag.dismiss();
                                                            Toast.makeText(getActivity(), "Failed To update password: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                pd.dismiss();
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            pd.dismiss();
                            myDialog.dismiss();
                            Toast.makeText(getActivity(), "Authentication Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                pd.dismiss();
            }
        });

    }



    private boolean valid() {

        if (changeemailstr.isEmpty())
        {
            email.setError("Enter Email");
            email.setFocusable(true);
            return false;
        }
        else if (passtr.isEmpty())
        {
            pass.setError("Enter Email");
            pass.setFocusable(true);
            return false;
        }
        return true;
    }

    private boolean ValidPass() {

        if (NewPassStr.isEmpty())
        {
            NewPass.setError("Enter New Password");
            NewPass.setFocusable(true);
            return false;
        }
        else if (ConfirmPassStr.isEmpty())
        {
            ConfirmPass.setError("Enter Confirm Password");
            ConfirmPass.setFocusable(true);
            return false;
        }
        else if (!NewPassStr.equals(ConfirmPassStr))
        {
            ConfirmPass.setError("Password Doesn`t Match");
            ConfirmPass.setFocusable(true);
            return false;
        }
        return true;
    }


}
