package com.ghostrun.model;

import java.util.Random;

import com.google.android.maps.GeoPoint;

public class Robot {
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

    public void moveTowardPlayer() {
        // FIXME: Race condition between hasLocation and getLocationAsGeoPoint
        if (!player.hasLocation()) return;
        GeoPoint oldLocation = getLocation();
        GeoPoint playerLocation = player.getLocationAsGeoPoint();
        int lat = (oldLocation.getLatitudeE6()
                + playerLocation.getLatitudeE6()) / 2;
        int lon = (oldLocation.getLongitudeE6()
                + playerLocation.getLongitudeE6()) / 2;
        setLocation(new GeoPoint(lat, lon));
    }

}
