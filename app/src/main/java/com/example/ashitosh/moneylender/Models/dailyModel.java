package com.example.ashitosh.moneylender.Models;

public class dailyModel {

   private String Date,TotalCollection;

    public dailyModel() {
    }

    public dailyModel(String date, String totalCollection) {
        Date = date;
        TotalCollection = totalCollection;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTotalCollection() {
        return TotalCollection;
    }

    public void setTotalCollection(String totalCollection) {
        TotalCollection = totalCollection;
    }
}
