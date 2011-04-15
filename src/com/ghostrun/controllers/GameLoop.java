package com.ghostrun.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Handler;

import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.MazeGraphPoint;
import com.ghostrun.model.Player;
import com.ghostrun.model.Robot;
import com.ghostrun.overlays.RobotsItemizedOverlay;
import com.ghostrun.util.GeoPointUtils;

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
                if (GeoPointUtils.getDistance(player.getLocationAsGeoPoint(),
                        r.getLocation()) < DEATH_DISTANCE) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createRandomRobots(int numRobots) {
        List<MazeGraphPoint> startingPoints = new LinkedList<MazeGraphPoint>();
        for (int i = 0; i < numRobots; ++i) {
            startingPoints.add(maze.getRandomPoint());
        }
        robots = Robot.createRobots(startingPoints, player);
    }
}
