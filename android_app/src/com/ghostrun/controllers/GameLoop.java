package com.ghostrun.controllers;

import java.util.List;

import android.os.Handler;
import android.os.SystemClock;

import com.ghostrun.activity.GameDisplay;
import com.ghostrun.config.Constants;
import com.ghostrun.model.Dots;
import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.Player;
import com.ghostrun.model.Robot;
import com.ghostrun.overlays.DotsOverlay;
import com.ghostrun.overlays.RobotsOverlay;
import com.ghostrun.util.GeoPointUtils;

public class GameLoop implements Runnable {
    // Activities
    private Handler h = new Handler();
    private GameDisplay activity;

    // Overlays
    private RobotsOverlay robotOverlay;
    private DotsOverlay dotsOverlay;

    // Model
    private MazeGraph maze;
    private Player player = new Player();
    private List<Robot> robots;
    private Dots dots;
    private int currentPoints;

    /////////////////////////////////////////////////////////////////
    //                       constructors
    /////////////////////////////////////////////////////////////////
    public GameLoop(MazeGraph graph, GameDisplay a) {
        activity = a;
        maze = graph;
        dots = new Dots(maze);
        robots = Robot.createRandomRobots(4, maze, player);
        this.currentPoints = 0;
    }
    
    public GameLoop(MazeGraph graph, Dots dots, GameDisplay a) {
        activity = a;
        maze = graph;
        this.dots = dots;
        robots = Robot.createRandomRobots(4, maze, player);
        this.currentPoints = 0;
    }
    /////////////////////////////////////////////////////////////////
    //                       public methods
    /////////////////////////////////////////////////////////////////

    // Getters/Setters
    public Player getPlayer() {
        return player;
    }
    public List<Robot> getRobots() {
        return robots;
    }
    public Dots getDots() {
        return this.dots;
    }
    public void setRobotOverlay(RobotsOverlay robotOverlay) {
        this.robotOverlay = robotOverlay;
        robotOverlay.refresh();
    }    
    public void setDotsOverlay(DotsOverlay dotsOverlay) {
    	this.dotsOverlay = dotsOverlay;
        dotsOverlay.refresh();
    }

    /** Run a single iteration of the GameLoop.
     *  This method should be called every {@link Constants#GAMELOOP_RATE_MS}
     *  milliseconds, and is responsible for calculating enemy movement,
     *  updating the score, and refreshing the views.
     *  
     *  @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        long startTime = SystemClock.uptimeMillis();
        updateRobotPositions();
        robotOverlay.refresh();
        /*
        if (isGameOver()) {
            activity.handlePlayerDeath();
            return;
        }
        */
        int pointIncrement = dots.eatDotsAt(player.getLocationAsGeoPoint());
        if (pointIncrement > 0) {
            this.currentPoints += pointIncrement;			
            this.activity.updateScore(this.currentPoints);
            dotsOverlay.refresh();
        }
        activity.refreshMap();
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
