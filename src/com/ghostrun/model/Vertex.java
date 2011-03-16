package com.ghostrun.model;

import java.util.Vector;

import com.google.android.maps.GeoPoint;

public class Vertex {
    private int label;
    private GeoPoint location;
    private Vector<Vertex> neighbors;

    public Vertex(int label, GeoPoint location) {
        this.label = label;
        this.location = location;
        neighbors = new Vector<Vertex>();
    }

    public GeoPoint getLocation() {
        return location;
    }
    
    public int getLabel() {
        return label;
    }
    
    public void addNeighbor(Vertex v) {
        neighbors.add(v);
    }
    
    public String toString() {
        return "Vertex "+label;
    }

}
