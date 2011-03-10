package com.ghostrun.model;

import java.util.HashSet;

import com.google.android.maps.GeoPoint;

public class MazeGraphPoint {
    public MazeGraphPoint(GeoPoint location) {
        this.location = location;
        neighbors = new HashSet<MazeGraphPoint>();
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void addConncetionTo(MazeGraphPoint other) {
        neighbors.add(other);
    }

    private GeoPoint location;
    private HashSet<MazeGraphPoint> neighbors;
}
