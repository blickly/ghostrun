package com.ghostrun.model.ai;

import com.ghostrun.model.MazeGraphPoint;
import com.google.android.maps.GeoPoint;

public class VerticalStrategy implements RobotStrategy {

    @Override
    public MazeGraphPoint getNextWaypoint(MazeGraphPoint location,
            GeoPoint destination) {
        MazeGraphPoint closestPoint = null;
        int closestDistance = Integer.MAX_VALUE;
        for (MazeGraphPoint n : location.getNeighbors()) {
            int currentDistance = Math.abs(n.getLocation().getLatitudeE6()
                    - destination.getLatitudeE6());
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
