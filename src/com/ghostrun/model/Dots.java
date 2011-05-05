package com.ghostrun.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ghostrun.util.GeoPointOffset;
import com.ghostrun.util.GeoPointUtils;
import com.google.android.maps.GeoPoint;

public class Dots {
    private List<GeoPoint> dotList;
    
    public final double INCREMENT = 200.0;
    public final int EATING_DISTANCE = 200;
    public final int DOT_POINTS = 10;

    public Dots(MazeGraph mazeGraph) {
        setMazeGraph(mazeGraph);
    }

    class PointPair {
        MazeGraphPoint pt1, pt2;
        PointPair(MazeGraphPoint pt1, MazeGraphPoint pt2) {
            this.pt1 = pt1;
            this.pt2 = pt2;
        }

        public boolean equals(Object o) {
            if (o instanceof PointPair) {
                PointPair p = (PointPair)o;
                return (p.pt1.equals(this.pt1) && p.pt2.equals(this.pt2)) ||
                (p.pt1.equals(this.pt2) && p.pt2.equals(this.pt1));
            }
            return false;
        }
        public int hashCode() {
            return pt1.hashCode() ^ pt2.hashCode();
        }
    }

    public void setMazeGraph(MazeGraph maze) {
        this.dotList = new ArrayList<GeoPoint>();
        Set<PointPair> doneSet = new HashSet<PointPair>();
        for (MazeGraphPoint p : maze.getPoints()) {
            for (MazeGraphPoint n : p.getNeighbors()) {
                PointPair pair = new PointPair(p, n);
                if (!doneSet.contains(pair)) {
                    generateDotsAlongEdge(pair);
                    doneSet.add(pair);
                }
            }
        }

        System.out.println("Done adding maze points: " + dotList.size());
    }

    public int eatDotsAt(GeoPoint playerLocation) {
        int pointIncrement = 0; 
        Iterator<GeoPoint> iter = dotList.iterator();
        while (iter.hasNext()) {
            GeoPoint pt = iter.next();
            if ((int) GeoPointUtils.getDistance(playerLocation, pt) < EATING_DISTANCE) {
                pointIncrement += DOT_POINTS;
                iter.remove();
            }
        }
        return pointIncrement;
    }

    private void generateDotsAlongEdge(PointPair p) {
        GeoPointOffset slope = new GeoPointOffset(p.pt1.getLocation(), p.pt2.getLocation());
        double totalDistance = slope.getLength();
        int times = (int)(totalDistance / INCREMENT);
        slope.scaleBy(1.0 / times);

        GeoPoint curPoint = p.pt1.getLocation();
        while (times > 0) {
            curPoint = slope.addTo(curPoint);
            dotList.add(curPoint);
            times--;
        }

        System.out.println("added points: " + dotList.size());
    }

    public GeoPoint get(int i) {
        return dotList.get(i);
    }

    public int remaining() {
        return dotList.size();
    }
}