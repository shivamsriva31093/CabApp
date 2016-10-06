package com.example.cab.cabapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by shashank on 2/21/2016.
 */
public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;

    boolean isGPSEnabled= false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_FOR_UPDATES= 10;
    private static final long MIN_TIME_BETWEEN_UPDATES= 1000*60*1;

    protected LocationManager locationManager;

    public GPSTracker(Context context){
        this.mContext = context;
        getLocation();
    }

    public Location getLocation(){
        try{
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled&&!isNetworkEnabled){

            }
            else {

                this.canGetLocation = true;
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BETWEEN_UPDATES,MIN_DISTANCE_FOR_UPDATES,this);
                    if(locationManager!= null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location!=null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();



                        }
                    }
                }
                if(isGPSEnabled){
                    if(location==null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BETWEEN_UPDATES,MIN_DISTANCE_FOR_UPDATES,this);
                        if(locationManager!=null){
                            location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        if(location!=null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }
            }


        }
        catch (SecurityException se){
            se.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return location;
    }
    public void stopUsingGPS(){
        if (locationManager!=null){
            try {
                locationManager.removeUpdates(GPSTracker.this);
            }
            catch (SecurityException se){
                se.printStackTrace();
            }
        }
    }
    public double getLatitude(){
        if(location!=null){

            latitude = location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if(location!=null){
            longitude = location.getLongitude();
        }
        return longitude;
    }
    public boolean CanGetLocation(){
        return this.canGetLocation;
    }




    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
