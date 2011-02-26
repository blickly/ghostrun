package com.ghostrun.overlays;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.Player;
import com.ghostrun.model.Robot;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RobotsItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<Robot> robots = new ArrayList<Robot>();
    private Player followingPlayer;

    public RobotsItemizedOverlay(Drawable defaultMarker, Player following) {
        super(boundCenterBottom(defaultMarker));
        
        this.followingPlayer = following;

        // Put robot on Campanile
        GeoPoint campanilePoint = new GeoPoint(37871944, -122257778);
        Robot robot = new Robot(campanilePoint, followingPlayer);
        robots.add(robot);
        // Put robot at NE corner of campus
        GeoPoint neCornerPoint = new GeoPoint(37875522,-122256825);
        Robot robot2 = new Robot(neCornerPoint, followingPlayer);
        robots.add(robot2);

        // Generate robots in random places
        createRandomRobots(campanilePoint, 7);
        
        populate();
    }

    @Override
    protected boolean onTap(int index) {
        android.util.Log.d("RobotsItemizedOverlay", "Item tapped. Id: " + index
                + " Total items: " + size());
        Robot tapee = robots.get(index);
        if (followingPlayer.hasLocation()) {
            tapee.moveTowardPlayer();
        } else {
            tapee.moveRandomly(500);
        }
        populate();
        return true;
    }

    @Override
    protected OverlayItem createItem(int i) {
        Robot r = robots.get(i);
        return new OverlayItem(r.getLocation(), "", "");
    }

    @Override
    public int size() {
        return robots.size();
    }

    /////////////////////////////////////////////////////////////////
    //                       private methods
    ////////////////////////////////////////////////////////////////

    private void createRandomRobots(GeoPoint center, int numRobots) {
        for (int i = 0; i < numRobots; ++i) {
            Robot newRobot = new Robot(center, followingPlayer);
            newRobot.moveRandomly(10000);
            robots.add(newRobot);
        }
    }
}
