package com.example.ashitosh.moneylender.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.ashitosh.moneylender.Activities.loginActivity;
import com.example.ashitosh.moneylender.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAgentFragment extends Fragment {

    private EditText name,email,phone,address;
    private FirebaseFirestore fs;
    private CollectionReference ref;
    private Button reg;
    private String nameStr,emailStr,phoneStr,addrStr,prevEmail,prevPass;
    private ArrayList<String> provider;
    private Map<String,Object> fdata;
    private  RelativeLayout layout;
    private ProgressDialog pd;

    private FirebaseAuth f_auth;
    public AddAgentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_add_agent, container, false);

        layout=v.findViewById(R.id.relativeLayout);
        name=v.findViewById(R.id.AgentName);
        email=v.findViewById(R.id.AgentEmail);
        address=v.findViewById(R.id.AgentAddr);
        phone=v.findViewById(R.id.AgentPhone);
        reg=v.findViewById(R.id.AgentRegBtn);

        pd=new ProgressDialog(getActivity());

        f_auth=FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fs=FirebaseFirestore.getInstance();

                ref=fs.collection("MoneyLender");

                fdata=new HashMap<>();

                nameStr=name.getText().toString();
                emailStr=email.getText().toString();
                phoneStr=phone.getText().toString();
                addrStr=address.getText().toString();

                if (!isCheckEmail(emailStr) && f_auth.getCurrentUser()!=null)
                {
                    registerAgent();
                }
                else
                {
                    email.setError("Email already registered");
                }
            }
        });
        provider=new ArrayList<>();


        return v;
    }

    public boolean isCheckEmail(final String email)
    {


        final boolean[] flag = {false};

        f_auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>()
        {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task)
            {
                boolean check = Objects.requireNonNull(task.getResult().getProviders()).isEmpty();

                if(check)
                {
                    flag[0] =true;
                }

                else
                {
                    Toast.makeText(getActivity(), "This email has been registered.", Toast.LENGTH_SHORT).show();

                }
            }
        });
        return flag[0];
    }


    private void registerAgent() {



        if(isDataFilled())
        {

            pd.setTitle("Registration");
            pd.setMessage("registering Agent");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            fdata.put("Name",nameStr);
            fdata.put("Email",emailStr);
            fdata.put("Phone",phoneStr);
            fdata.put("Address",addrStr);
            fdata.put("Password","12345678");

            uploadData();


            prevEmail= Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();

            if(prevEmail!=null) {
                f_auth.fetchProvidersForEmail(Objects.requireNonNull(prevEmail)).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if (task.isSuccessful()) {
                            int size = Objects.requireNonNull(task.getResult().getProviders()).size();


                          //  Toast.makeText(getActivity(), "Provideres: " + task.getResult().getProviders(), Toast.LENGTH_SHORT).show();

                            provider.addAll(task.getResult().getProviders());

                            if (provider.isEmpty()) {
                                Toast.makeText(getActivity(), "No provider found", Toast.LENGTH_SHORT).show();
                            } else {
                                create();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
            else
            {
                Intent intent=new Intent(getActivity(),loginActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Failed To find previous user", Toast.LENGTH_SHORT).show();
            }
        }




    }

    private void create()
    {

        f_auth.createUserWithEmailAndPassword(emailStr,"12345678")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            if (task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                Toast.makeText(getActivity(),"Agent Account Created",Toast.LENGTH_LONG).show();


                            }
                            else
                            {
                                FirebaseUser user=f_auth.getCurrentUser();
                                if (user!=null)
                                {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(getActivity(),"Agent with this email address is already exists",Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(),"Failed To delete Existing user: "+e.getMessage(),Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity(),"failed to register agent: "+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });


        if (provider.contains("google.com")) {


            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(this.getActivity()));
            AuthCredential credential = GoogleAuthProvider.getCredential(Objects.requireNonNull(account).getIdToken(), null);

            f_auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(getActivity(), "Resigned In", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {

            prevPass=loginActivity.passstr;

            if (validate()) {
                f_auth.signInWithEmailAndPassword(prevEmail, prevPass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Resigned In", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    private boolean validate() {

        if (prevEmail==null)
        {
            Toast.makeText(getActivity(), "prev email not found", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (prevPass==null)
        {
            Toast.makeText(getActivity(), "prev pass is empty", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }

    private void uploadData() {

        ref.document("Agent_"+emailStr).set(fdata, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {


                            final Snackbar snackbar=Snackbar.make(layout,"data uploaded",Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                        }
                                    });

                            // Changing message text color
                            snackbar.setActionTextColor(Color.RED);

                            // Changing action button text color
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.GREEN);
                            snackbar.setDuration(4000).dismiss();
                            snackbar.show();


                            pd.setMessage("successfully uploaded");
                            pd.hide();
                            pd.dismiss();


                            ManageAgent manageAgent=new ManageAgent();

                            android.support.v4.app.FragmentTransaction transaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                                    .add(manageAgent,"ManageAgent").addToBackStack("ManageAgent");

                            transaction.replace(R.id.mainFrame,manageAgent);
                            transaction.commit();

                            Toast.makeText(getActivity(),"Agent data successfully uploaded",Toast.LENGTH_LONG).show();


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity(),"failed to upload Agent data",Toast.LENGTH_LONG).show();

                pd.setMessage("failed to upload");
                pd.hide();
                pd.dismiss();

            }
        });
    }

    private boolean isDataFilled() {

        if(nameStr.isEmpty())
        {
            name.requestFocus();
            name.setError("Enter Name");
            return false;
        }

        if(emailStr.isEmpty())
        {
            email.requestFocus();
            email.setError("Enter Email");
            return false;
        }
        else if(!isValidEmail(emailStr))
        {
            email.setError("Invalid Email Pattern");
            email.requestFocus();
            return false;
        }

        if (phoneStr.isEmpty())
        {
            phone.requestFocus();
            phone.setError("Enter Phone No.");
            return false;
        }

        if (addrStr.isEmpty())
        {
            address.requestFocus();
            address.setError("Enter The Address");
            return false;
        }

        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
