package com.ghostrun.model;

import java.util.ArrayList;
import java.util.Collection;

import com.ghostrun.util.RandUtils;
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
    	if (!neighbors.contains(other))
    		neighbors.add(other);
    }

    public MazeGraphPoint getRandomNeighbor() {
        if (neighbors.isEmpty()) {
            return null;
        }
        int index = RandUtils.nextInt(neighbors.size());
        return neighbors.get(index);
    }

    public Collection<MazeGraphPoint> getNeighbors() {
        return neighbors;
    }
    
    public boolean equals(Object o) {
    	if (o instanceof MazeGraphPoint) {
    		MazeGraphPoint p = (MazeGraphPoint)o;
    		return p.getLocation().equals(this.getLocation());
    	}
    	return false;
    }
    public int hashCode() {
        return location.hashCode();
    }

    private GeoPoint location;
    private ArrayList<MazeGraphPoint> neighbors;
}
