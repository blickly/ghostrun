package com.example.android.apis.model;

import com.google.android.maps.GeoPoint;

public class Player {
    private GeoPoint location;
    
    public Player(GeoPoint location) {
        this.setLocation(location);
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

}
