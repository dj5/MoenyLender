package com.example.ashitosh.moneylender.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ashitosh.moneylender.Fragments.ActiveClientFragment;
import com.example.ashitosh.moneylender.Fragments.AgentClient;
import com.example.ashitosh.moneylender.Fragments.OwnerAccount;
import com.example.ashitosh.moneylender.Fragments.SearchFragment;
import com.example.ashitosh.moneylender.Fragments.ManageAgent;
import com.example.ashitosh.moneylender.Fragments.csvFragment;
import com.example.ashitosh.moneylender.Fragments.homeFragment;
import com.example.ashitosh.moneylender.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Owner extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private BottomNavigationView nav;
    private RelativeLayout mainframe;

    private android.support.v7.widget.Toolbar mtoolbar;

    private GoogleApiClient gac;

    private GoogleSignInOptions gso;

    private FirebaseAuth f_auth;

    private homeFragment homeFrag;
    private SearchFragment searchFrag;
    private ManageAgent manageAgent;
    private OwnerAccount ownerFrag;
    private DrawerLayout mdrawerLayout;
    private NavigationView navigationView;
    private csvFragment csvFragment;
    private AgentClient agentClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owner);

        nav=findViewById(R.id.bottom_navigation);

        mainframe=findViewById(R.id.mainFrame);


        //firebase
        f_auth= FirebaseAuth.getInstance();

        //toolbar
        mtoolbar=findViewById(R.id.Owner_Toolbar);
        setSupportActionBar(mtoolbar);

        homeFrag= new homeFragment();

        searchFrag=new SearchFragment();

        manageAgent=new ManageAgent();
        ownerFrag=new OwnerAccount();
        csvFragment=new csvFragment();
        agentClient=new AgentClient();
        //******************************************************

        mdrawerLayout=findViewById(R.id.drawer_layout);
        mdrawerLayout.setDrawerElevation(10);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        navigationView=findViewById(R.id.nav_view);
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(Owner.this,DividerItemDecoration.VERTICAL));

        //*********************************************************

        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        gac=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


        //Set Default Fragment
        setFragment(homeFrag);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mdrawerLayout.closeDrawers();

                switch (item.getItemId())
                {
                    case R.id.csvfile:

                        csvFragment fragment=new csvFragment();
                        Bundle data=new Bundle();
                        data.putString("frag","Owner");
                        fragment.setArguments(data);

                        android.support.v4.app.FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction()
                                .add(fragment,"ActiveCust").addToBackStack("ActiveCust");

                        fragmentTransaction.replace(R.id.mainFrame,fragment);
                        fragmentTransaction.commit();
                        return true;


                    case R.id.ActiveClient:

                        ActiveClientFragment frag1=new ActiveClientFragment();
                        android.support.v4.app.FragmentTransaction ft1= getSupportFragmentManager().beginTransaction()
                                .add(frag1,"ActiveCust").addToBackStack("ActiveCust");
                        ft1.replace(R.id.mainFrame,frag1);
                        ft1.commit();
                        return true;


                    case R.id.ManageClient:

                        SearchFragment frag2=new SearchFragment();
                        android.support.v4.app.FragmentTransaction ft2= getSupportFragmentManager().beginTransaction()
                                .add(frag2,"manageCust").addToBackStack("ManageCust");
                        ft2.replace(R.id.mainFrame,frag2);
                        ft2.commit();
                        return true;

                    case R.id.ManageAgent:

                        ManageAgent frag3=new ManageAgent();
                        android.support.v4.app.FragmentTransaction ft3= getSupportFragmentManager().beginTransaction()
                                .add(frag3,"manageCust").addToBackStack("ManageCust");
                        ft3.replace(R.id.mainFrame,frag3);
                        ft3.commit();
                        return true;

                    case R.id.AgentClients:

                        setFragment(agentClient);
                        return true;

                    case R.id.logoutnav:
                        askToExit();
                        return true;

                }

                return true;
            }
        });

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.HomeNav:

                      //  CollectionFragment fragment=new CollectionFragment();
                        setFragment(homeFrag);
                        return true;

                    case R.id.AccountNav:
                        setFragment(ownerFrag);
                        return true;

                    case R.id.CsvNav:

                        Bundle data=new Bundle();
                        data.putString("frag","Owner");
                        csvFragment.setArguments(data);
                        setFragment(csvFragment);
                        return true;

                    default:    return false;
                }

            }
        });


 //***************************************************************************************
        mdrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

  //*************************************************************************************

    private void setFragment(Fragment fragment) {

        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame,fragment);
        fragmentTransaction.commit();

    }


    public void askToExit()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to Log out?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        revokeAccess();
                        signOut();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    /*

    @Override

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.owner_menu,menu);
        return true;
    }

*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case android.R.id.home:
                mdrawerLayout.openDrawer(GravityCompat.START);
                return true;



        }
        return super.onOptionsItemSelected(item);
    }




    private void signOut() {

        Auth.GoogleSignInApi.signOut(gac).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                Toast.makeText(Owner.this,"signed out",Toast.LENGTH_LONG).show();
                sendToStart();
            }
        });

        FirebaseAuth.getInstance().signOut();
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(gac).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(Owner.this,"Access Revoked",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendToStart() {

        Intent intent=new Intent(Owner.this,loginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Owner.this,"Connection failed",Toast.LENGTH_LONG).show();
    }



}
