/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.support;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsUtility {
    LocationManager locationManager;
    Location lastKnownLocation;
    Context context;

    public GpsUtility(Context context) {
        this.context = context;
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            lastKnownLocation = location;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    public void setUpLocationService(){
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void pauseLocationService() {
        if(locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public String getString(Location location) {
        String retVal = "";

        double longitude = lastKnownLocation.getLongitude();
        double latitude = lastKnownLocation.getLatitude();
        retVal = String.format("geo:%d%d", latitude, longitude);
        return retVal;
    }

    public Location getLocation() {
        try{
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation == null){
                tryCourseLocation();
            }
        }
        catch(SecurityException e){
            tryCourseLocation();
        }
        catch(IllegalArgumentException e){
            tryCourseLocation();
        }
        return lastKnownLocation;
    }

    private Location tryCourseLocation(){
        try{
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch(SecurityException e){
            lastKnownLocation = null;
        }
        catch(IllegalArgumentException e){
            lastKnownLocation = null;
        }
        return lastKnownLocation;
    }
}