package com.example.ashitosh.moneylender.Models;

public class LoanModel {

   private String Status,AgentName,DOI,DOR,ExpectedInstallment,FiledAmount,Interest,LoanType,ReqAmount,AmountToReturn,PendingAmount,LoanId,LFNO,Discount;

    public LoanModel(String LFNO, String discount) {
        this.LFNO = LFNO;
        Discount = discount;
    }

    public LoanModel() {
    }

    public String getLFNO() {
        return LFNO;
    }

    public void setLFNO(String LFNO) {
        this.LFNO = LFNO;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public LoanModel(String status, String agentName, String DOI, String DOR, String expectedInstallment, String filedAmount, String interest, String loanType, String reqAmount, String amountToReturn, String pendingAmount, String loanId) {
        Status = status;
        AgentName = agentName;

        this.DOI = DOI;
        this.DOR = DOR;
        ExpectedInstallment = expectedInstallment;
        FiledAmount = filedAmount;
        Interest = interest;
        LoanType = loanType;
        ReqAmount = reqAmount;
        AmountToReturn = amountToReturn;
        PendingAmount = pendingAmount;
        LoanId = loanId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }

    public String getDOI() {
        return DOI;
    }

    public void setDOI(String DOI) {
        this.DOI = DOI;
    }

    public String getDOR() {
        return DOR;
    }

    public void setDOR(String DOR) {
        this.DOR = DOR;
    }

    public String getExpectedInstallment() {
        return ExpectedInstallment;
    }

    public void setExpectedInstallment(String expectedInstallment) {
        ExpectedInstallment = expectedInstallment;
    }

    public String getFiledAmount() {
        return FiledAmount;
    }

    public void setFiledAmount(String filedAmount) {
        FiledAmount = filedAmount;
    }

    public String getInterest() {
        return Interest;
    }

    public void setInterest(String interest) {
        Interest = interest;
    }

    public String getLoanType() {
        return LoanType;
    }

    public void setLoanType(String loanType) {
        LoanType = loanType;
    }

    public String getReqAmount() {
        return ReqAmount;
    }

    public void setReqAmount(String reqAmount) {
        ReqAmount = reqAmount;
    }

    public String getAmountToReturn() {
        return AmountToReturn;
    }

    public void setAmountToReturn(String amountToReturn) {
        AmountToReturn = amountToReturn;
    }

    public String getPendingAmount() {
        return PendingAmount;
    }

    public void setPendingAmount(String pendingAmount) {
        PendingAmount = pendingAmount;
    }

    public String getLoanId() {
        return LoanId;
    }

    public void setLoanId(String loanId) {
        LoanId = loanId;
    }
}
