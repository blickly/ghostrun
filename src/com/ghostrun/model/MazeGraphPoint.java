package com.ghostrun.model;

import com.google.android.maps.GeoPoint;

public class MazeGraphPoint {
    private GeoPoint location;

    public MazeGraphPoint(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

}
