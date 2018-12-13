package com.example.ashitosh.moneylender.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ashitosh.moneylender.AgentDetail;
import com.example.ashitosh.moneylender.Fragments.AgentDetailFragment;
import com.example.ashitosh.moneylender.Models.AgentModel;
import com.example.ashitosh.moneylender.Fragments.CollectionFragment;
import com.example.ashitosh.moneylender.R;
import com.google.common.collect.ConcurrentHashMultiset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.ViewHolder> implements Filterable
{

    List<AgentModel> list;
    FragmentManager manager;
    private AgentFilter agentFilter;
    String fragment;
    public List<AgentModel> filteredUserList;


    public AgentAdapter(List<AgentModel> list, FragmentManager supportFragmentManager,String fragment) {

        this.list = list;
        manager=supportFragmentManager;
        this.fragment=fragment;
        filteredUserList=new ArrayList<>();
    }



    @NonNull
    @Override
    public AgentAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_layout,parent,false);

        ViewHolder holder=new ViewHolder(v);

        return holder;

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        holder.name.setText(list.get(position).getName());

       // holder.email.setText(list.get(position).getEmail());

       // holder.address.setText(list.get(position).getAddress());

       // holder.phone.setText(list.get(position).getPhone());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data=new Bundle();

                data.putString("agentName",list.get(position).getName());
                data.putString("agentEmail",list.get(position).getEmail());

               if(fragment.equals("AgentManage"))
               {
                   AgentDetail fragment = new AgentDetail();

                   data.putString("agentPhone",list.get(position).getPhone());
                   data.putString("agentAddress",list.get(position).getAddress());


                   fragment.setArguments(data);
                   android.support.v4.app.FragmentTransaction fragmentTransaction = manager.beginTransaction().add(fragment, "Agent").addToBackStack("Agents");

                   fragmentTransaction.replace(R.id.mainFrame, fragment);

                   fragmentTransaction.commit();

               }
               else {

                   CollectionFragment fragment = new CollectionFragment();

                   fragment.setArguments(data);
                   android.support.v4.app.FragmentTransaction fragmentTransaction = manager.beginTransaction().add(fragment, "Agent").addToBackStack("Agents");

                   fragmentTransaction.replace(R.id.mainFrame, fragment);

                   fragmentTransaction.commit();
               }
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public Filter getFilter() {
        if(agentFilter == null)
            agentFilter = new AgentFilter(this, list);
        return agentFilter;

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,email,address,phone;

        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.Agent_Name);
            card=itemView.findViewById(R.id.AgentCard);
           // address=itemView.findViewById(R.id.Agent_Addr);
          //  email=itemView.findViewById(R.id.Agent_Email);
         //   phone=itemView.findViewById(R.id.Agent_Phone);
        }
    }



//Filter Class

   private class AgentFilter extends Filter {

        private final AgentAdapter adapter;

        private final List<AgentModel> originalList;

        private final List<AgentModel> filteredList;

        private    List<AgentModel> filteredUserList;

        public AgentFilter(AgentAdapter adapter, List<AgentModel> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
            this.filteredUserList=new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().trim();

                for (final AgentModel user : originalList) {
                    if (user.getName().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredUserList.clear();
            adapter.filteredUserList.addAll((ArrayList<AgentModel>) results.values);
           // adapter.list=filteredUserList;
            adapter.notifyDataSetChanged();
        }
    }
}
