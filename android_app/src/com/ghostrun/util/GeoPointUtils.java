package com.ghostrun.util;

import com.google.android.maps.GeoPoint;

public class GeoPointUtils {
    public static int getDistance(GeoPoint loc1, GeoPoint loc2) {
        if (loc1 == null || loc2 == null) {
            return Integer.MAX_VALUE;
        }
        long deltaLat = (long)loc1.getLatitudeE6() - (long)loc2.getLatitudeE6();
        long deltaLon = (long)loc1.getLongitudeE6() - (long)loc2.getLongitudeE6();
        
        return (int) Math.sqrt(deltaLon * deltaLon + deltaLat * deltaLat);
    }
}
