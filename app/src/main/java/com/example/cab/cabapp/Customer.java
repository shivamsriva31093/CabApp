package com.example.cab.cabapp;

/**
 * Created by shashank on 2/21/2016.
 */
public class Customer{
    private String Name;
    private String TelephoneNo;
    private String Email;

    public Customer(String name, String telephoneNo,String email ){
        this.Name = name;
        this.Email = email;
        this.TelephoneNo = telephoneNo;
    }
    public String getName(){
        return Name;
    }
    public String getTelephoneNo(){
        return TelephoneNo;

    }
    public String getEmail(){
        return Email;
    }
}
