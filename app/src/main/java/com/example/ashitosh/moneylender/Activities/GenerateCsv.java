package com.example.ashitosh.moneylender.Activities;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class GenerateCsv
{
    String custname,retdate, amountcollected,total,agentname;
    String monthlycol,dailycol;

    public void customerCsv(String custname, String retdate, String amountcollected, String total)
    {

        this.custname=custname;
        this.retdate=retdate;
        this.amountcollected=amountcollected;
        this.total=total;

        String fileName = Environment.getExternalStorageDirectory().getPath().concat("/customer.csv");
        String []entries ;
        entries=(custname+","+retdate+","+amountcollected+","+total).split(",");
        try (FileWriter fsw = new FileWriter(fileName,true);
             CSVWriter writer = new CSVWriter(fsw)) {
            writer.writeNext(entries);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void agentCsv(String agentname, String dailycol, String monthlycol)
    {
        this.agentname=agentname;
        this.monthlycol=monthlycol;
        this.dailycol=dailycol;

        String fileName = Environment.getExternalStorageDirectory().getPath().concat("/agent.csv");
        String []entries ;

        entries=(agentname+","+monthlycol+","+dailycol).split(",");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            try (FileOutputStream fos = new FileOutputStream(fileName);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 CSVWriter writer = new CSVWriter(osw)) {

                writer.writeNext(entries);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }




}
