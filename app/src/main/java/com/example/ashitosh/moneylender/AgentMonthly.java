package com.example.ashitosh.moneylender;

public class AgentMonthly {

   private String Date,TotalCollection;

    public AgentMonthly() {
    }

    public AgentMonthly(String Date, String TotalCollection) {
        this.Date = Date;
        this.TotalCollection = TotalCollection;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getTotalCollection() {
        return TotalCollection;
    }

    public void setTotalCollection(String TotalCollection) {
        this.TotalCollection = TotalCollection;
    }
}
