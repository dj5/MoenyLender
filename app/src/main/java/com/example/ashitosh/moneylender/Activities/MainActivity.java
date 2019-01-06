package com.example.ashitosh.moneylender.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Agent;
import com.example.ashitosh.moneylender.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private android.support.v7.widget.Toolbar toolbar;

    private FirebaseAuth f_auth;
    private GoogleApiClient gac;
    private GoogleSignInOptions gso;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.Main_Toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("MoneyLender");


        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gac=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

    }


    @Override
    protected void onStart() {

        super.onStart();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null)
        {
           String email=user.getEmail();

           if (Objects.requireNonNull(email).equals("ashitosh.bhade@gmail.com") || Objects.requireNonNull(email).equals("dj5@gmail.com"))
           {
               Intent intent=new Intent(this.getApplicationContext(),Owner.class);
               startActivity(intent);
               finish();
           }
           else
           {
               Intent intent=new Intent(this.getApplicationContext(),Agent.class);
               startActivity(intent);
               finish();
           }
        }
        else if(googleSignInAccount ==null)
        {
            Intent intent=new Intent(this.getApplicationContext(),loginActivity.class);
            startActivity(intent);
            finish();

        }
    }


    private void sendToStart() {

        Intent intent=new Intent(this.getApplicationContext(),loginActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menus,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        item.getItemId();
        {
            revokeAccess();             //reviking apps accesss to firebase
            signOut();
        }

        return true;
    }

   //logging out google account user
    private void signOut() {

        Auth.GoogleSignInApi.signOut(gac).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                Toast.makeText(MainActivity.this,"signed out",Toast.LENGTH_LONG).show();
                sendToStart();
            }
        });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(gac).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(MainActivity.this,"Access Revoked",Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(MainActivity.this,"google connction failed ",Toast.LENGTH_LONG).show();

    }
}
