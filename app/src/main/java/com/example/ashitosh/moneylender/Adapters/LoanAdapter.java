package com.example.ashitosh.moneylender.Adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashitosh.moneylender.Models.LoanModel;
import com.example.ashitosh.moneylender.R;

import java.util.List;

//********************************************************p***************
public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder>
{

    List<LoanModel> list;


    public LoanAdapter(List<LoanModel> list) {
        this.list = list;

    }


    @NonNull
    @Override
    public LoanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.loanhead_layout,parent,false);

        ViewHolder holder=new ViewHolder(v);

        return holder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LoanAdapter.ViewHolder holder, final int position) {


        holder.agent_name.setText(list.get(position).getAgentName());
        holder.doi.setText(list.get(position).getDOI());
        holder.dor.setText(list.get(position).getDOR());
        holder.loan_type.setText(list.get(position).getLoanType());
        holder.expected_installment.setText(list.get(position).getExpectedInstallment()+"/- Rs");
        holder.filed_amount.setText(list.get(position).getFiledAmount()+"/- Rs");
        holder.requested_amount.setText(list.get(position).getReqAmount()+"/- Rs");
        holder.intrest.setText(list.get(position).getInterest()+" %");
        holder.AmountToreturn.setText(list.get(position).getAmountToReturn()+"/- Rs");
        holder.PendingAmount.setText(list.get(position).getPendingAmount()+"/- Rs");
        holder.LoanId.setText(list.get(position).getLoanId());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView agent_name,doi,dor,expected_installment,filed_amount,intrest,loan_type,requested_amount;
        TextView AmountToreturn,PendingAmount,LoanId;


        public ViewHolder(View itemView) {
            super(itemView);

            agent_name=itemView.findViewById(R.id.LoanAgentName);
            doi=itemView.findViewById(R.id.Doi);
            dor=itemView.findViewById(R.id.Dor);
            expected_installment=itemView.findViewById(R.id.LoanInstallment);
            filed_amount=itemView.findViewById(R.id.LoanFiledAmount);
            intrest=itemView.findViewById(R.id.LoanInterest);
            loan_type=itemView.findViewById(R.id.LoanType);
            requested_amount=itemView.findViewById(R.id.LoanReqAmount);
            AmountToreturn=itemView.findViewById(R.id.Return);
            PendingAmount=itemView.findViewById(R.id.Pending);
            LoanId=itemView.findViewById(R.id.LoanIdVal);
        }
    }
}
