package com.ghostrun.overlays;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.Robot;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RobotsItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<Robot> robots = new ArrayList<Robot>();

    public RobotsItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));

        // Put robot on Campanile
        GeoPoint campanilePoint = new GeoPoint(37871944, -122257778);
        Robot robot = new Robot(campanilePoint);
        robots.add(robot);
        // Put robot at NE corner of campus
        GeoPoint neCornerPoint = new GeoPoint(37875522,-122256825);
        Robot robot2 = new Robot(neCornerPoint);
        robots.add(robot2);

        // Generate robots in random places
        createRandomRobots(campanilePoint, 7);
        
        populate();
    }

    @Override
    protected boolean onTap(int index) {
        android.util.Log.d("RobotsItemizedOverlay", "Item tapped. Id: " + index
                + " Total items: " + size());
        robots.get(index).moveRandomly(500);
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
            Robot newRobot = new Robot(center);
            newRobot.moveRandomly(10000);
            robots.add(newRobot);
        }
    }
}
