package com.example.android.apis.model;

import java.util.Random;

import com.google.android.maps.GeoPoint;

public class Robot {
    private GeoPoint location;
    
    public Robot(GeoPoint location) {
        this.setLocation(location);
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

    /** Move robot randomly from exisitng position by a distance that is
     *  normally distributed with standard deviation given in millions of
     *  a degree of lattitude/longitude.
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

}
