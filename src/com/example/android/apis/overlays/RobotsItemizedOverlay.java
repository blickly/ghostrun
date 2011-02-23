package com.example.android.apis.overlays;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.drawable.Drawable;

import com.example.android.apis.model.Robot;
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
        Random rand = new Random();
        android.util.Log.d("RobotsItemizedOverlay", "Item tapped. Id: " + index
                + " Total items: " + size());
        Robot oldRobot = robots.get(index);
        GeoPoint oldLocation = oldRobot.getLocation();
        int lat = (int) (oldLocation.getLatitudeE6() + 500 * rand.nextGaussian());
        int lon = (int) (oldLocation.getLongitudeE6() + 500 * rand.nextGaussian());
        robots.set(index, new Robot(new GeoPoint(lat, lon)));
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
        Random rand = new Random();
        for (int i = 0; i < numRobots; ++i) {
            int lat = (int) (center.getLatitudeE6()
                    + 10000 * rand.nextGaussian());
            int lon = (int) (center.getLongitudeE6()
                    + 10000 * rand.nextGaussian());
            robots.add(new Robot(new GeoPoint(lat, lon)));
        }
    }
}
