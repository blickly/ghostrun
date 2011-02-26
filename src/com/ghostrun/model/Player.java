package com.ghostrun.model;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class Player {
    private Location location;

    public boolean hasLocation() {
        return location != null;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public GeoPoint getLocationAsGeoPoint() {
        if (hasLocation()) {
            return new GeoPoint((int)(location.getLatitude() * 1e6),
                    (int)(location.getLongitude() * 1e6));
        } else {
            return null;
        }
    }

}
