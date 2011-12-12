package com.ghostrun.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class LocationHelper {
    private LocationManager locationManager;
    private LocationResult locationResult = new LocationResult();
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;
    private List<LocationListener> locationListeners = new ArrayList<LocationListener>();
    private MapView mapView;
    
    public LocationHelper(Context context, MapView mapView) {
    	this.mapView = mapView;
    	locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

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
    
    public void stop() {
    	locationManager.removeUpdates(locationListenerGps);
    	locationManager.removeUpdates(locationListenerNetwork);
    }
    
    public void addLocationListener(LocationListener locationListener) {
    	locationListeners.add(locationListener);
    }
    
    public void removeLocationListener(LocationListener locationListener) {
    	locationListeners.remove(locationListener);
    }
    
    public GeoPoint getLastKnownLocation() {
    	return locationResult.getLocation();
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location)
        {
            locationResult.gotLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location)
        {	
            locationResult.gotLocation(location);
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
    		locationResult.gotLocation(gpsLocation);
    		locationResult.gotLocation(networkLocation);
    	}
    }

    public class LocationResult {
    	Location loc = null;
    	
        public void gotLocation(Location location) {
        	if (loc == null || location.getTime() - loc.getTime() > 500) {
        		loc = location;
        		for (LocationListener listener : locationListeners) {
        			listener.onLocationChanged(loc);
        		}
        	}
        }
      
        public GeoPoint getLocation() {
        	if (loc == null)
        		return null;
        	return new GeoPoint((int)(loc.getLatitude() * 1e6), 
								(int)(loc.getLongitude() * 1e6)); 
        }
    }
}