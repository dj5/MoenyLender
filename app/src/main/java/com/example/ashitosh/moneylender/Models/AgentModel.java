package com.example.ashitosh.moneylender.Models;

import org.w3c.dom.Text;

import java.util.List;

public class AgentModel {

    private String Name,Address,Email,Phone,Password;

    public AgentModel() {
        //fore firestore
    }

    public AgentModel(String name, String address, String email, String phone, String password) {
        Name = name;
        Address = address;
        Email = email;
        Phone = phone;
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
