package com.ghostrun.model;

import java.util.Random;

import com.google.android.maps.GeoPoint;

public class Robot {
    final int ROBOT_SPEED = 200;
    private GeoPoint location;
    Player player;
    
    public Robot(GeoPoint location, Player following) {
        this.setLocation(location);
        this.player = following;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }
    
    public void updateLocation() {
        if (player.hasLocation()) {
            moveTowardPoint(player.getLocationAsGeoPoint());
        } else {
            moveRandomly(ROBOT_SPEED);
        }
    }

    /** Move robot randomly from existing position by a distance that is
     *  normally distributed with standard deviation given in millions of
     *  a degree of latitude/longitude.
     *  @param stddev Standard deviation of distance to move, given in units
     *    of millions of a degree of arc.
     */
    public void moveRandomly(int stddev) {
        Random rand = new Random();
        GeoPoint oldLocation = getLocation();
        int lat = (int) (oldLocation.getLatitudeE6()
                + stddev * rand.nextGaussian());
        int lon = (int) (oldLocation.getLongitudeE6()
                + stddev * rand.nextGaussian());
        setLocation(new GeoPoint(lat, lon));
    }

    /** Move robot toward the given point by a distance up to ROBOT_SPEED
     *  (or to the point if it is closer than ROBOT_SPEED.
     *  @param point The desired destination point.
     */
    public void moveTowardPoint(GeoPoint point) {
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
