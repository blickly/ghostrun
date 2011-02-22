package com.example.android.apis.model;

import com.google.android.maps.GeoPoint;

public class Robot {
    private GeoPoint location;
    
    public Robot(GeoPoint location) {
        this.setLocation(location);
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

}
