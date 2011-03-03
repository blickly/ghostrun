package com.ghostrun.model;

import android.os.Handler;

import com.ghostrun.overlays.RobotsItemizedOverlay;
import com.google.android.maps.GeoPoint;

public class GameLoop implements Runnable {
    Handler h = new Handler();

    private Player player;
    private RobotsItemizedOverlay robots;
    
    public GameLoop(Player player, RobotsItemizedOverlay robots) {
        this.player = player;
        this.robots = robots;
    }

    @Override
    public void run() {
        robots.updateRobots();
        if (player.hasLocation()) {
            for (Robot r : robots.iter()) {
                if (getDistance(player.getLocationAsGeoPoint(), r.getLocation())
                        < 200) {
                    System.exit(0);
                    return;
                }
            }
        }
        h.postDelayed(this, 500);
    }

    private static int getDistance(GeoPoint loc1, GeoPoint loc2) {
        if (loc1 == null || loc2 == null) {
            return Integer.MAX_VALUE;
        }
        int deltaLat = loc1.getLatitudeE6() - loc2.getLatitudeE6();
        int deltaLon = loc1.getLongitudeE6() - loc2.getLongitudeE6();
        return (int) Math.sqrt(deltaLon * deltaLon + deltaLat * deltaLat);
    }
}
