package com.ghostrun.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import com.ghostrun.overlays.RobotsItemizedOverlay;
import com.google.android.maps.GeoPoint;

public class GameLoop implements Runnable {
    public final int DEATH_DISTANCE = 500;
    public final int ROBOT_MOVE_DISTANCE = 200;
    public final int ROBOT_UPDATE_RATE_MS = 500;
    public final int ROBOT_START_SPACING = 10000;
    
    Handler h = new Handler();
    private Player player = new Player();
    private List<Robot> robots = new ArrayList<Robot>();
    private RobotsItemizedOverlay robotOverlay;

    public Player getPlayer() {
        return player;
    }
    public List<Robot> getRobots() {
        return robots;
    }
    public void setRobotOverlay(RobotsItemizedOverlay robotOverlay) {
        this.robotOverlay = robotOverlay;
    }

    public GameLoop() {
        // Put robot on Campanile
        GeoPoint campanilePoint = new GeoPoint(37871944, -122257778);
        Robot robot = new Robot(campanilePoint, player);
        robots.add(robot);
        // Put robot at NE corner of campus
        GeoPoint neCornerPoint = new GeoPoint(37875522,-122256825);
        Robot robot2 = new Robot(neCornerPoint, player);
        robots.add(robot2);
        // Generate robots in random places
        createRandomRobots(campanilePoint, 7); 
    }

    @Override
    public void run() {
        updateRobotPositions();
        if (isGameOver()) {
            System.exit(0);
        }
        robotOverlay.refresh();
        h.postDelayed(this, ROBOT_UPDATE_RATE_MS);
    }


    /////////////////////////////////////////////////////////////////
    //                       private methods
    /////////////////////////////////////////////////////////////////

    private void updateRobotPositions() {
        for (Robot r : robots) {
            r.moveRandomly(ROBOT_MOVE_DISTANCE);
        }
    }

    private boolean isGameOver() {
        if (player.hasLocation()) {
            for (Robot r : robots) {
                if (getDistance(player.getLocationAsGeoPoint(), r.getLocation())
                        < DEATH_DISTANCE) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createRandomRobots(GeoPoint center, int numRobots) {
        for (int i = 0; i < numRobots; ++i) {
            Robot newRobot = new Robot(center, player);
            newRobot.moveRandomly(ROBOT_START_SPACING);
            robots.add(newRobot);
        }
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
