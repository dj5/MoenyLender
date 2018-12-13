package com.example.ashitosh.moneylender;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Activities.loginActivity;
import com.example.ashitosh.moneylender.Fragments.AgentDetailFragment;
import com.example.ashitosh.moneylender.Fragments.OwnerAccount;
import com.example.ashitosh.moneylender.Fragments.SearchFragment;
import com.example.ashitosh.moneylender.Fragments.ManageAgent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

public class Agent extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private BottomNavigationView nav;
    private RelativeLayout mainframe;

    private android.support.v7.widget.Toolbar mtoolbar;

    private GoogleApiClient gac;

    private GoogleSignInOptions gso;

    private FirebaseAuth f_auth;

    private AgentHome homeFrag;
    private SearchFragment searchFrag;
    private ManageAgent settingsFrag;
    private OwnerAccount agentFrag;
    private CollectMoneyFragment collectMoneyFragment;
    private AgentDetailFragment agentdetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agent);

        nav=findViewById(R.id.Agent_bottom_navigation);

        mainframe=findViewById(R.id.AgentmainFrame);

        //firebase
        f_auth= FirebaseAuth.getInstance();

        //toolbar
        mtoolbar=findViewById(R.id.AgentToolbar);
        setSupportActionBar(mtoolbar);

        homeFrag= new AgentHome();

        searchFrag=new SearchFragment();
        settingsFrag=new ManageAgent();
        agentFrag=new OwnerAccount();
        collectMoneyFragment=new CollectMoneyFragment();
        agentdetail=new AgentDetailFragment();

        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        gac=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


        //Set Default Fragment
        setFragment(homeFrag);

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.AgentHomeNav:

                        //  CollectionFragment fragment=new CollectionFragment();
                        setFragment(homeFrag);
                        return true;

                    case R.id.AgentCollectNav:
                        setFragment(collectMoneyFragment);
                        return true;

                    case R.id.AgentAccountNav:
                         setFragment(agentdetail);
                         return true;

                    default:    return false;
                }

            }
        });

    }

    private void setFragment(Fragment fragment) {

        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.AgentmainFrame,fragment);
        fragmentTransaction.addToBackStack("fragment");
        fragmentTransaction.commit();

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.owner_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.owner_logout)
        {
            signOut();
        }
        return true;
    }


    private void signOut() {

        Auth.GoogleSignInApi.signOut(gac).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                Toast.makeText(Agent.this,"signed out",Toast.LENGTH_LONG).show();
                sendToStart();
            }
        });
    }

    private void sendToStart() {

        Intent intent=new Intent(Agent.this,loginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection failed",Toast.LENGTH_LONG).show();
    }
}