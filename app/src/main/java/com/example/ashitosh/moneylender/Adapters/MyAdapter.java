package com.example.ashitosh.moneylender.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashitosh.moneylender.R;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private String[] magents;

    public MyAdapter(String[] agents) {
        this.magents=agents;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TextView v= (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_layout,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

        holder.textView.setText( magents[position]);
    }

    @Override
    public int getItemCount() {
        return magents.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public MyViewHolder(View v) {

            super(v);
            this.textView=v.findViewById(R.id.Agent_Name);
        }
    }
}
