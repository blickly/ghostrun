package com.ghostrun.model.ai;

import com.ghostrun.model.MazeGraphPoint;
import com.google.android.maps.GeoPoint;

public class RandomStrategy implements RobotStrategy {

    public MazeGraphPoint getNextWaypoint(MazeGraphPoint location, GeoPoint destination) {
        return location.getRandomNeighbor();
    }
}
