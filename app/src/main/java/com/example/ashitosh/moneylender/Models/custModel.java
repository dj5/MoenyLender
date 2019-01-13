package com.example.ashitosh.moneylender.Models;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ServerTimestamp;


public class custModel {

    private String AccountNo, CustEmail,CustAddr,CustName,CustPhone,CustTotalLoan,CustAdharId,CustDob,GuarantorName,GuarantorMob,GuarantorAddr;


    public custModel() {
        //constructor for firestore
    }

    public custModel(String accountNo, String custEmail, String custAddr, String custName, String custPhone, String custTotalLoan, String custAdharId, String custDob, String guarantorName, String guarantorMob, String guarantorAddr) {
        AccountNo = accountNo;
        CustEmail = custEmail;
        CustAddr = custAddr;
        CustName = custName;
        CustPhone = custPhone;
        CustTotalLoan = custTotalLoan;
        CustAdharId = custAdharId;
        CustDob = custDob;
        GuarantorName = guarantorName;
        GuarantorMob = guarantorMob;
        GuarantorAddr = guarantorAddr;
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

    public String getCustAdharId() {
        return CustAdharId;
    }

    public void setCustAdharId(String custAdharId) {
        CustAdharId = custAdharId;
    }

    public String getCustDob() {
        return CustDob;
    }

    public void setCustDob(String custDob) {
        CustDob = custDob;
    }

    public String getGuarantorName() {
        return GuarantorName;
    }

    public void setGuarantorName(String guarantorName) {
        GuarantorName = guarantorName;
    }

    public String getGuarantorMob() {
        return GuarantorMob;
    }

    public void setGuarantorMob(String guarantorMob) {
        GuarantorMob = guarantorMob;
    }

    public String getGuarantorAddr() {
        return GuarantorAddr;
    }

    public void setGuarantorAddr(String guarantorAddr) {
        GuarantorAddr = guarantorAddr;
    }
}