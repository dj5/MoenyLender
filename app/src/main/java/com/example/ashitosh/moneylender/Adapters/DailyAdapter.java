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
import com.example.ashitosh.moneylender.R;
import com.example.ashitosh.moneylender.Models.dailyModel;

import java.util.List;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder>
{

    List<dailyModel> list;
    FragmentManager manager;
    String email;
    public DailyAdapter(List<dailyModel> list, FragmentManager supportFragmentManager,String email) {

        this.list = list;
        manager=supportFragmentManager;
        this.email=email;
    }


    @NonNull
    @Override
    public DailyAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_layout,parent,false);



        ViewHolder holder=new ViewHolder(v);


        return holder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.date.setText(list.get(position).getDate());

        holder.total_amount.setText(list.get(position).getTotalCollection()+" /- Rs");

//        holder.card.setLabelFor(Integer.parseInt(list.get(position).getDate()));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data=new Bundle();

                data.putString("InstallmentType","Daily");
                data.putString("agentEmail",email);
                data.putString("docname","Date_"+list.get(position).getDate());


                CustCollectionFragment fragment=new CustCollectionFragment();

                fragment.setArguments(data);

                android.support.v4.app.FragmentTransaction fragmentTransaction=manager.beginTransaction().add(fragment,"daily").addToBackStack("daily");

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

        TextView date,total_amount;

        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);

            total_amount=itemView.findViewById(R.id.DailyAmount);
            date=itemView.findViewById(R.id.DailyDate);
            card=itemView.findViewById(R.id.DailyCard);


        }
    }
}
