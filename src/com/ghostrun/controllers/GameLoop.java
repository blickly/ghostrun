package com.ghostrun.controllers;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.MazeGraphPoint;
import com.ghostrun.model.Player;
import com.ghostrun.model.Robot;
import com.ghostrun.overlays.RobotsItemizedOverlay;
import com.google.android.maps.GeoPoint;

public class GameLoop implements Runnable {
    public final int DEATH_DISTANCE = 500;
    public final int ROBOT_UPDATE_RATE_MS = 500;
    public final int ROBOT_START_SPACING = 10000;
    
    Handler h = new Handler();
    private MazeGraph maze = new MazeGraph();
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
        maze.createSimpleMap();
        createRandomRobots(2);
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
            r.updateLocation();
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

    private void createRandomRobots(int numRobots) {
        for (int i = 0; i < numRobots; ++i) {
            MazeGraphPoint randomPoint = maze.getRandomPoint();
            Robot newRobot = new Robot(randomPoint.getLocation(), player);
            newRobot.setDestination(randomPoint);
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
