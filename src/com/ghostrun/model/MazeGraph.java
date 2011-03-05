package com.ghostrun.model;

import com.google.android.maps.GeoPoint;

public class MazeGraph {

    public MazeGraphPoint addPoint(GeoPoint point) {
        return new MazeGraphPoint(point);
    }
    
    public void addEdge(MazeGraphPoint p1, MazeGraphPoint p2) {
        
    }

}
