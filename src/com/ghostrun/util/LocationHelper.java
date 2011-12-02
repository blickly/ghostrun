package com.ghostrun.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;

public class LocationHelper {
    private LocationManager locationManager;
    private LocationResult locationResult = new LocationResult();
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;
    
    public LocationHelper(Context context) {
    	locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        
        //exceptions thrown if provider not enabled
        try {
        	gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        if(gpsEnabled)
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        
        if(networkEnabled)
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        
        getLastLocation();
    }
    
    public GeoPoint getLastKnownLocation() {
    	return locationResult.getLocation();
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location)
        {
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location)
        {	
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}

    };

    private void getLastLocation() {
    	Location gpsLocation = null, networkLocation = null;
    	
    	if(gpsEnabled)
    		gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    	if(networkEnabled)
    		networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    	//if there are both values use the latest one
    	if(gpsLocation != null && networkLocation != null) {
    		if(gpsLocation.getTime() > networkLocation.getTime())
    			locationResult.gotLocation(gpsLocation);
    		else
    			locationResult.gotLocation(networkLocation);
    	}
    }

    public static class LocationResult {
    	Location loc;
        public void gotLocation(Location location) {
        	loc = location;
        }
      
        public GeoPoint getLocation() {
        	return new GeoPoint((int)(loc.getLatitude() * Math.pow(10, 6)), 
								(int)(loc.getLongitude() * Math.pow(10, 6))); 
        }
    }
}