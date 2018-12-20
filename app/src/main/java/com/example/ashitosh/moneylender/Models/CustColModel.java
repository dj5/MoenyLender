package com.example.ashitosh.moneylender.Models;

public class CustColModel {

    String AmountRecieved,DateOfCollection,LoanId,AccountNo;


    public CustColModel(String amountRecieved, String dateOfCollection, String loanId, String accountNo) {
        AmountRecieved = amountRecieved;
        DateOfCollection = dateOfCollection;
        LoanId = loanId;
        AccountNo = accountNo;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }

    public CustColModel() {

    }

    public String getLoanId() {
        return LoanId;
    }

    public void setLoanId(String loanId) {
        LoanId = loanId;
    }


    public String getAmountRecieved() {
        return AmountRecieved;
    }

    public void setAmountRecieved(String amountRecieved) {
        AmountRecieved = amountRecieved;
    }

    public String getDateOfCollection() {
        return DateOfCollection;
    }

    public void setDateOfCollection(String dateOfCollection) {
        DateOfCollection = dateOfCollection;
    }

}
