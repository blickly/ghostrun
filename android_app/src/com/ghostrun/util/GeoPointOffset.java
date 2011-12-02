package com.ghostrun.util;

import com.google.android.maps.GeoPoint;

public class GeoPointOffset {
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