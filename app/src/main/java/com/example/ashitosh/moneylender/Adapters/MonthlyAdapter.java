package com.example.ashitosh.moneylender.Adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashitosh.moneylender.Fragments.AddAgentFragment;
import com.example.ashitosh.moneylender.Fragments.CustCollectionFragment;
import com.example.ashitosh.moneylender.Models.MonthlyModel;
import com.example.ashitosh.moneylender.R;

import org.joda.time.LocalDate;

import java.util.List;

public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.ViewHolder>
{

  private   List<MonthlyModel> list;
  private   FragmentManager manager;
  private   String email,month,year;
  private   LocalDate date;
  public MonthlyAdapter(List<MonthlyModel> list, FragmentManager supportFragmentManager, String agentEmaill) {

        this.list = list;
        manager=supportFragmentManager;
        this.email=agentEmaill;
    }


    @NonNull
    @Override
    public MonthlyAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_layout,parent,false);



        ViewHolder holder=new ViewHolder(v);


        return holder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        date= LocalDate.parse(list.get(position).getDate());

        month= String.valueOf(date.getMonthOfYear());
        year= String.valueOf(date.getYear());

        holder.month.setText(month+"-"+year);
        holder.total_amount.setText(list.get(position).getTotalCollection()+" Rs");


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data=new Bundle();

                data.putString("InstallmentType","Monthly");
                data.putString("agentEmail",email);
                data.putString("docname","Month_"+month+year);


                CustCollectionFragment fragment=new CustCollectionFragment();

                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction=manager.beginTransaction().add(fragment,"monthly").addToBackStack("monthly");

                fragmentTransaction.replace(R.id.installment_layout,fragment);

                fragmentTransaction.commit();
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView month,total_amount;

        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);


            card=itemView.findViewById(R.id.MonthlyCard);
            month=itemView.findViewById(R.id.MonthlyYear);
            total_amount=itemView.findViewById(R.id.MonthlyAmount);


        }
    }
}
