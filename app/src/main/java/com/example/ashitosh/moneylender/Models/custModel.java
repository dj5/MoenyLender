package com.example.ashitosh.moneylender.Models;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ServerTimestamp;


public class custModel {

    private String AccountNo, CustEmail,CustAddr,CustName,CustPhone,CustTotalLoan;


    public custModel() {
        //constructor for firestore
    }

    public custModel(String accountNo, String custEmail, String custAddr, String custName, String custPhone, String custTotalLoan) {
        AccountNo = accountNo;
        CustEmail = custEmail;
        CustAddr = custAddr;
        CustName = custName;
        CustPhone = custPhone;
        CustTotalLoan = custTotalLoan;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }

    public String getCustEmail() {
        return CustEmail;
    }

    public void setCustEmail(String custEmail) {
        CustEmail = custEmail;
    }

    public String getCustAddr() {
        return CustAddr;
    }

    public void setCustAddr(String custAddr) {
        CustAddr = custAddr;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getCustPhone() {
        return CustPhone;
    }

    public void setCustPhone(String custPhone) {
        CustPhone = custPhone;
    }

    public String getCustTotalLoan() {
        return CustTotalLoan;
    }

    public void setCustTotalLoan(String custTotalLoan) {
        CustTotalLoan = custTotalLoan;
    }
}