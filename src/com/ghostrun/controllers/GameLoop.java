package com.ghostrun.controllers;

import java.util.List;

import android.os.Handler;
import android.os.SystemClock;

import com.ghostrun.activity.GameMapView;
import com.ghostrun.config.Constants;
import com.ghostrun.driving.Node;
import com.ghostrun.model.Dots;
import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.Player;
import com.ghostrun.model.Robot;
import com.ghostrun.overlays.DotsOverlay;
import com.ghostrun.overlays.RobotsOverlay;
import com.ghostrun.util.GeoPointUtils;

public class GameLoop implements Runnable {
    private Handler h = new Handler();
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
        robotOverlay.refresh();
    }
    
    public void setDotsOverlay(DotsOverlay dotsOverlay) {
    	this.dotsOverlay = dotsOverlay;
        dotsOverlay.refresh();
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
        long startTime = SystemClock.uptimeMillis();
        updateRobotPositions();
        robotOverlay.refresh();
        if (isGameOver()) {
            activity.handlePlayerDeath();
            return;
        }
        int points = dots.eatDotsAt(player.getLocationAsGeoPoint());
        if (points > 0) {
            this.currentPoints += points;			
            this.activity.updateScore(this.currentPoints);
            dotsOverlay.refresh();
        }
        h.postAtTime(this, startTime + Constants.GAMELOOP_RATE_MS);
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
                        r.getLocation()) < Constants.DEATH_DISTANCE) {
                    return true;
                }
            }
        }
        return false;
    }
}
