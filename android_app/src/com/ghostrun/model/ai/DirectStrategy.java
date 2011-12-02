package com.ghostrun.model.ai;

import com.ghostrun.model.MazeGraphPoint;
import com.ghostrun.util.GeoPointUtils;
import com.google.android.maps.GeoPoint;

public class DirectStrategy implements RobotStrategy {

    @Override
    public MazeGraphPoint getNextWaypoint(MazeGraphPoint location,
            GeoPoint destination) {
        MazeGraphPoint closestPoint = null;
        int closestDistance = Integer.MAX_VALUE;
        for (MazeGraphPoint n : location.getNeighbors()) {
            int currentDistance = GeoPointUtils.getDistance(n.getLocation(), destination);
            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestPoint = n;
            }
        }
        if (closestPoint != null) {
            return closestPoint;
        } else {
            return location.getRandomNeighbor();
        }
    }

}
