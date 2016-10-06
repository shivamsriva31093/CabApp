package com.example.cab.cabapp;

/**
 * Created by shashank on 2/23/2016.
 */
public class CabStatus {
    private boolean bookingStatus;
    public CabStatus(){

    }
    public CabStatus(boolean bookingStatus){
        this.bookingStatus = bookingStatus;
    }
    public boolean isBookingStatus(){
        return bookingStatus;
    }
}
