package com.example.cab.cabapp;

/**
 * Created by shashank on 2/22/2016.
 */
public class CabLocation {
    private String latitude;
    private String longitude;
    public CabLocation(){

    }
    public CabLocation(String latitude,String longitude){
        this.latitude = latitude;
        this.longitude=longitude;
    }

    public String getLatitude(){
    return latitude;
    }

    public String getLongitude(){
        return longitude;
    }


}
