package com.ghostrun.model;

import java.util.ArrayList;
import java.util.Random;

import com.google.android.maps.GeoPoint;

public class MazeGraph {

    public MazeGraphPoint addPoint(GeoPoint point) {
        MazeGraphPoint mgPoint = new MazeGraphPoint(point);
        points.add(mgPoint);
        return mgPoint;
    }
    
    public void addEdge(MazeGraphPoint p1, MazeGraphPoint p2) {
        p1.addConncetionTo(p2);
        p2.addConncetionTo(p1);
    }

    public MazeGraphPoint getRandomPoint() {
        Random rand = new Random();
        return points.get(rand.nextInt(points.size()));
    }

    private ArrayList<MazeGraphPoint> points;

}
