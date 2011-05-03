package com.ghostrun.model;

import java.util.LinkedList;
import java.util.List;

import com.ghostrun.model.ai.DirectStrategy;
import com.ghostrun.model.ai.HorizontalStrategy;
import com.ghostrun.model.ai.RandomStrategy;
import com.ghostrun.model.ai.RobotStrategy;
import com.ghostrun.model.ai.VerticalStrategy;
import com.google.android.maps.GeoPoint;

public class Robot {
    public enum RobotType {
        BLINKY,
        PINKY,
        INKY,
        CLYDE
    }

    final int ROBOT_SPEED = 20;
    private GeoPoint location;
    private MazeGraphPoint destination;
    private Player player;
    private RobotStrategy ai;
    private final RobotType type;

    ////////////////////////////////////////////////////////////////////////
    //                          constructors/factories
    ////////////////////////////////////////////////////////////////////////
    public static List<Robot> createRobots(List<MazeGraphPoint> startingPoints,
            Player following) {
        List<Robot> result = new LinkedList<Robot>();
        int i = 0;
        for (MazeGraphPoint point : startingPoints) {
            RobotStrategy s;
            RobotType t;
            switch (i) {
            case 0: s = new RandomStrategy(); t = RobotType.BLINKY; break;
            case 1: s = new HorizontalStrategy(); t = RobotType.PINKY; break;
            case 2: s = new VerticalStrategy(); t = RobotType.INKY; break;
            case 3: s = new DirectStrategy(); t = RobotType.CLYDE; break;
            default: throw new RuntimeException("Unknown RobotType:" + i);
            }
            i = (i + 1) % 4;
            result.add(new Robot(point, following, s, t));
        }
        return result;
    }

    public static List<Robot> createRandomRobots(int numRobots, MazeGraph maze,
            Player player) {
        List<MazeGraphPoint> startingPoints = new LinkedList<MazeGraphPoint>();
        for (int i = 0; i < numRobots; ++i) {
            startingPoints.add(maze.getRandomPoint());
        }
        return Robot.createRobots(startingPoints, player);
    }

    public Robot(MazeGraphPoint startingPoint, Player following,
            RobotStrategy strategy, RobotType robotType) {
        this.setDestination(startingPoint);
        this.setLocation(startingPoint.getLocation());
        this.player = following;
        this.ai = strategy;
        this.type = robotType;
    }

    public Robot(MazeGraphPoint startingPoint, Player following) {
        this.setDestination(startingPoint);
        this.setLocation(startingPoint.getLocation());
        this.player = following;
        this.ai = new RandomStrategy();
        this.type = RobotType.BLINKY;
    }

    ////////////////////////////////////////////////////////////////////////
    //                          public methods
    ////////////////////////////////////////////////////////////////////////
    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public MazeGraphPoint getDestination() {
        return destination;
    }
    
    public RobotType getRobotType() {
        return type;
    }

    public void setDestination(MazeGraphPoint destination) {
        this.destination = destination;
    }

    public void updateLocation() {
        if (destination == null) {
            return;
        }
        moveTowardPoint(destination.getLocation());
        if (destination.getLocation().equals(location)) {
            GeoPoint playerLoc = player.getLocationAsGeoPoint();
            if (playerLoc == null) return;
            setDestination(ai.getNextWaypoint(destination, playerLoc));
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //                          private methods
    ////////////////////////////////////////////////////////////////////////

    /** Move robot toward the given point by a distance up to ROBOT_SPEED
     *  (or to the point if it is closer than ROBOT_SPEED.
     *  @param point The desired destination point.
     */
    private void moveTowardPoint(GeoPoint point) {
        if (point == null) {
            return;
        }
        GeoPointOffset direction = new GeoPointOffset(location, point);
        double totalDistance = direction.getLength();
        if (ROBOT_SPEED > totalDistance) {
            location = point;
        } else {
            direction.scaleBy(ROBOT_SPEED / totalDistance);
            location = direction.addTo(location);
        }
    }

    private class GeoPointOffset {
        private int deltaLat;

        private int deltaLon;

        public GeoPointOffset(GeoPoint start, GeoPoint end) {
            this.deltaLat = end.getLatitudeE6() - start.getLatitudeE6();
            this.deltaLon = end.getLongitudeE6() - start.getLongitudeE6();
        }

        public GeoPoint addTo(GeoPoint p) {
            int lat = p.getLatitudeE6() + this.deltaLat;
            int lon = p.getLongitudeE6() + this.deltaLon;
            return new GeoPoint(lat, lon);
        }

        public void scaleBy(double factor) {
            deltaLat = (int) (deltaLat * factor);
            deltaLon = (int) (deltaLon * factor);
        }

        public double getLength() {
            return Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon);
        }

    }

}
