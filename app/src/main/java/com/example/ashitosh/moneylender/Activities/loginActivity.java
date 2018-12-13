package com.example.ashitosh.moneylender.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Agent;
import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.forgetpassword;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.android.gms.common.SignInButton.SIZE_STANDARD;

public class loginActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    private static int RC_SIGN_IN = 9001;
    private static final String TAG ="" ;

    private String uid;
    private android.support.v7.widget.Toolbar toolbar;
    private EditText email,pass;
    private Button signinbtn;


    private ProgressDialog pd;
    //firebase
    private FirebaseAuth f_auth;
    private FirebaseUser f_user;
    private FirebaseFirestore firestore;

    private GoogleApiClient gac;
    private GoogleSignInOptions gso;
    private SignInButton gsignin;
    //private GoogleApiClient gac;

    private TextView forgetPass;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        pd=new ProgressDialog(this);
        toolbar=findViewById(R.id.Login_Toolbar);
        setSupportActionBar(toolbar);
      //  Objects.requireNonNull(getSupportActionBar()).setTitle("MoneyLender");




        email=findViewById(R.id.login_email);
        pass=findViewById(R.id.Login_Pass);
        signinbtn=findViewById(R.id.Login_btn);
        forgetPass=findViewById(R.id.forgetPass);
        //google signin button

        gsignin  = findViewById(R.id.googleSignIn);
        gsignin.setSize(SIZE_STANDARD);

        pd=new ProgressDialog(this);

        f_auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        f_user=f_auth.getCurrentUser();

        //**************************google sign in***********************8
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                 gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        gac=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        //*****************************google sign in button listener**************************

        gsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pd.setTitle("signing in");
                pd.setMessage("wait until signing in");
                pd.setCanceledOnTouchOutside(false);
                pd.closeOptionsMenu();
                pd.show();
                signIn();

            }
        });


        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String passstr,emailstr;

                passstr=pass.getText().toString();
                emailstr=email.getText().toString();

                if(!passstr.isEmpty() && !emailstr.isEmpty()) {

                    pd.setTitle("signing in");
                    pd.setMessage("wait until signing in");
                    pd.setCanceledOnTouchOutside(false);
                    pd.closeOptionsMenu();
                    pd.show();

                    normalSignIn(emailstr,passstr);
                }
                else
                {
                    if(passstr.isEmpty())
                    {
                        pass.setError("Enter the Passsword");
                        pass.requestFocus();
                    }

                    if(emailstr.isEmpty())
                    {
                        email.setError("Enter the Eamil");
                        email.requestFocus();
                    }
                }
            }
        });


        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(loginActivity.this,forgetpassword.class);
                startActivity(intent);
            }
        });
    }


    private void normalSignIn(final String emailstr, final String passstr) {

        f_auth.signInWithEmailAndPassword(emailstr,passstr)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            pd.hide();
                            pd.dismiss();

                            Toast.makeText(loginActivity.this,"successfully logged in",Toast.LENGTH_LONG).show();


                            uid= Objects.requireNonNull(f_auth.getCurrentUser()).getEmail();
                            Toast.makeText(loginActivity.this,"uid= "+uid,Toast.LENGTH_LONG).show();
                            sendTomain();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.hide();
                pd.dismiss();

                Toast.makeText(loginActivity.this,"failed to login"+emailstr+"="+passstr,Toast.LENGTH_LONG).show();

            }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(gac);

        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

/*
    @Override
    public void onStart() {

        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if(googleSignInAccount !=null)
        {
            Intent activity=new Intent(loginActivity.this,MainActivity.class);

            startActivity(activity);
            finish();
        }

    }

*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess())
            {
                GoogleSignInAccount account=result.getSignInAccount();

                firebaseAuthWithGoogle(Objects.requireNonNull(account));

                Map<String,Object> fdata=new HashMap<>();
                fdata.put("name",account.getDisplayName());
                fdata.put("id",account.getId());
                fdata.put("imgUri", Objects.requireNonNull(account.getPhotoUrl()).toString());


                //Storing  user profile image
                Uri file=Uri.parse(String.valueOf(account.getPhotoUrl()));
                StorageReference storage= FirebaseStorage.getInstance().getReference("Profile Pic").child(account.getId() +".jpg");

                storage.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(loginActivity.this,"image uplaoded successfully",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(loginActivity.this,"image could not upload",Toast.LENGTH_LONG).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(loginActivity.this,"failed to upload image ",Toast.LENGTH_LONG).show();

                    }
                });

                //uploading  user data firebase

                firestore.collection("customer").document("cust_"+account.getEmail()).set(fdata, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(loginActivity.this,"data uploaded successfully",Toast.LENGTH_LONG).show();


                                }
                                else
                                {
                                    Toast.makeText(loginActivity.this,"cannot upload data",Toast.LENGTH_LONG).show();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(loginActivity.this,"cannot upload data+"+e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });


                uid=account.getEmail();
                Toast.makeText(loginActivity.this,"firebase account created",Toast.LENGTH_LONG).show();

                sendTomain();


            }
            else
            {
                // Google Sign-In failed
                Log.e(TAG, "Google Sign-In failed.");

            }
            //   Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //   handleSignInResult(task);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);


        f_auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(loginActivity.this,"firebase account created",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(loginActivity.this,"firebase account failed",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);


            // Signed in successfully, show authenticated UI.

            String email = account.getEmail();
            String name = account.getDisplayName();
            String family = account.getFamilyName();
            final String id = account.getId();
            String reqId = account.getIdToken();
            Uri photo = account.getPhotoUrl();


            Toast.makeText(loginActivity.this, name + family + email + id + reqId, Toast.LENGTH_LONG).show();

            Map<String, Object> data = new HashMap<>();


            data.put("name", name);
            data.put("family", family);
            data.put("email", email);
            data.put("id", email);
            data.put("reqid", reqId);
            pd.setMessage("uploading data");



            firestore.collection("MoneyLender").document("agent_"+id).set(data,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(loginActivity.this, "customer data successfully stored", Toast.LENGTH_LONG).show();
                }

            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        pd.setMessage("data successfully uploaded");
                        pd.hide();
                        pd.dismiss();
                        Toast.makeText(loginActivity.this, "updation completed", Toast.LENGTH_LONG).show();

                        uid=account.getEmail();
                        Toast.makeText(loginActivity.this,"uid= "+id,Toast.LENGTH_LONG).show();

                        sendTomain();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    pd.setMessage("failed to upload data");
                    pd.hide();
                    pd.dismiss();

                    Toast.makeText(loginActivity.this, "Error in uploading customer data", Toast.LENGTH_LONG).show();

                }
            });



        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }


    public void sendTomain()
    {
        Toast.makeText(this.getApplicationContext(),"uid= "+uid,Toast.LENGTH_LONG).show();

        if(uid!=null) {
            if (uid.equals("ashitosh.bhade@gmail.com")||uid.equals("dj5@gmail.com")) {
                Intent activity = new Intent(loginActivity.this, Owner.class);
                startActivity(activity);

            } else {

                Toast.makeText(this.getApplicationContext(), "uid= " + uid, Toast.LENGTH_LONG).show();

                Intent activity = new Intent(loginActivity.this, Agent.class);
                startActivity(activity);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this.getApplicationContext(),"could not logged in",Toast.LENGTH_LONG).show();
    }


}
