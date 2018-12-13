package com.example.ashitosh.moneylender.Models;

public class CustColModel {

    String AmountRecieved,DateOfCollection,AccountNo;

    public CustColModel() {
    }

    public CustColModel(String amountRecieved, String dateOfCollection, String accountNo) {
        AmountRecieved = amountRecieved;
        DateOfCollection = dateOfCollection;
        AccountNo = accountNo;
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

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }
}
