package com.ghostrun.controllers;

import java.util.List;

import android.os.Handler;

import com.ghostrun.activity.GameMapView;
import com.ghostrun.driving.Node;
import com.ghostrun.model.Dots;
import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.Player;
import com.ghostrun.model.Robot;
import com.ghostrun.overlays.DotsOverlay;
import com.ghostrun.overlays.RobotsOverlay;
import com.ghostrun.util.GeoPointUtils;

public class GameLoop implements Runnable {
    public final int DEATH_DISTANCE = 500;
    public final int ROBOT_UPDATE_RATE_MS = 500;
    public final int ROBOT_START_SPACING = 10000;
    
    Handler h = new Handler();
    private MazeGraph maze;
    private Player player = new Player();
    private List<Robot> robots;
    private RobotsOverlay robotOverlay;
    private GameMapView activity;
    private Dots dots;
    private DotsOverlay dotsOverlay;
    
    private int currentPoints;

    public Player getPlayer() {
        return player;
    }
    public List<Robot> getRobots() {
        return robots;
    }
    public void setRobotOverlay(RobotsOverlay robotOverlay) {
        this.robotOverlay = robotOverlay;
    }
    
    public void setDotsOverlay(DotsOverlay dotsOverlay) {
    	this.dotsOverlay = dotsOverlay;
    }
    
    public GameLoop(List<Node> nodes, GameMapView a) {
        activity = a;
    	maze = new MazeGraph(nodes);
    	dots = new Dots(maze);
    	robots = Robot.createRandomRobots(4, maze, player);
    	this.currentPoints = 0;
    }
    
    public Dots getDots() {
        return this.dots;
    }

    @Override
    public void run() {
        updateRobotPositions();
        if (isGameOver()) {
            activity.handlePlayerDeath();
            return;
        }
        if (player.hasLocation()) {
            int points = dots.eatDotsAt(player.getLocationAsGeoPoint());
            if (points > 0) {
                this.currentPoints += points;			
                this.activity.updateScore(this.currentPoints);
            }
        }
        dotsOverlay.refresh();
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
}
