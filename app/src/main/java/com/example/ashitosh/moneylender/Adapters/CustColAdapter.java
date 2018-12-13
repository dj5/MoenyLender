package com.example.ashitosh.moneylender.Adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashitosh.moneylender.Models.CustColModel;
import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.Models.custModel;

import java.util.List;

//********************************************************p***************
public class CustColAdapter extends RecyclerView.Adapter<CustColAdapter.ViewHolder>
{

    List<CustColModel> list;
    FragmentManager manager;


    public CustColAdapter(List<CustColModel> list, FragmentManager supportFragmentManager)
    {
        this.list = list;
        manager=supportFragmentManager;
    }


    @NonNull
    @Override
    public CustColAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_colmodel,parent,false);

        ViewHolder holder=new ViewHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull CustColAdapter.ViewHolder holder, int position) {

        holder.AccNo.setText(list.get(position).getAccountNo());
        holder.Aamount_recieved.setText(list.get(position).getAmountRecieved());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView Acchead,AccNo,Aamount_recieved;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);

            Acchead=itemView.findViewById(R.id.CustAcc);
            Aamount_recieved=itemView.findViewById(R.id.CustColAmount);
            AccNo=itemView.findViewById(R.id.CustAccNo);
            card=itemView.findViewById(R.id.CustColCard);

        }
    }
}
