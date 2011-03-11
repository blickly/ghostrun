package com.ghostrun.model;

import java.util.ArrayList;
import java.util.Random;

import com.google.android.maps.GeoPoint;

public class MazeGraphPoint {
    public MazeGraphPoint(GeoPoint location) {
        this.location = location;
        neighbors = new ArrayList<MazeGraphPoint>();
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void addConncetionTo(MazeGraphPoint other) {
        neighbors.add(other);
    }

    public MazeGraphPoint getRandomNeighbor() {
        if (neighbors.isEmpty()) {
            return null;
        }
        int index = (new Random()).nextInt(neighbors.size());
        return neighbors.get(index);
    }

    private GeoPoint location;
    private ArrayList<MazeGraphPoint> neighbors;
}
