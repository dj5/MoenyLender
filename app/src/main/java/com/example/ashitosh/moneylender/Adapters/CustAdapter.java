package com.example.ashitosh.moneylender.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashitosh.moneylender.Fragments.CustCollectionFragment;
import com.example.ashitosh.moneylender.Fragments.CustDetailFragment;
import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.Models.custModel;

import java.util.List;

//********************************************************p***************
public class CustAdapter extends RecyclerView.Adapter<CustAdapter.ViewHolder>
{

    List<custModel> list;
    FragmentManager manager;
    String frag;
    public CustAdapter(List<custModel> list, FragmentManager supportFragmentManager, String agentHome) {
        this.list = list;
        manager=supportFragmentManager;
        this.frag=agentHome;
    }


    @NonNull
    @Override
    public CustAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_head_layout,parent,false);

        ViewHolder holder=new ViewHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull CustAdapter.ViewHolder holder, final int position) {

        holder.name.setText(list.get(position).getCustName());
        holder.address.setText(list.get(position).getCustAddr());
        holder.account.setText(list.get(position).getAccountNo());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data=new Bundle();

                data.putString("CustName",list.get(position).getCustName());
                data.putString("Address",list.get(position).getCustAddr());
                data.putString("Phone",list.get(position).getCustPhone());
                data.putString("CustEmail",list.get(position).getCustEmail());
                data.putString("Account",list.get(position).getAccountNo());
                data.putString("TotalLoans",list.get(position).getCustTotalLoan());

                if(frag.equals("Owner")) {
                    data.putString("fragment","Owner" );

                }
                else
                {
                    data.putString("fragment","Agent");
                }

                CustDetailFragment fragment=new CustDetailFragment();

                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction=manager.beginTransaction().add(fragment,"Custhead").addToBackStack("head");

                if(frag.equals("Owner")) {
                    fragmentTransaction.replace(R.id.mainFrame, fragment);

                }
                else
                {
                    fragmentTransaction.replace(R.id.AgentmainFrame, fragment);

                }
                fragmentTransaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

            TextView name,address,account;
            CardView card;


        public ViewHolder(View itemView) {
            super(itemView);

            card=itemView.findViewById(R.id.CustHeadCard);
            name=itemView.findViewById(R.id.CustHeadName);
            address=itemView.findViewById(R.id.CustHeadAddr);
            account=itemView.findViewById(R.id.CustHeadAccNo);
        }
    }
}
