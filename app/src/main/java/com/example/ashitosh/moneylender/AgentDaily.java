package com.example.ashitosh.moneylender;

public class AgentDaily {

    private String Date,TotalCollection;

    public AgentDaily() {
    }

    public AgentDaily(String Date, String TotalCollection) {
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
