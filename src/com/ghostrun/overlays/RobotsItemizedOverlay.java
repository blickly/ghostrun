package com.ghostrun.overlays;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.Robot;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RobotsItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private List<Robot> robots;

    public RobotsItemizedOverlay(Drawable defaultMarker, List<Robot> robots) {
        super(boundCenterBottom(defaultMarker));

        this.robots = robots;

        populate();
    }

//    @Override
//    protected boolean onTap(int index) {
//        android.util.Log.d("RobotsItemizedOverlay", "Item tapped. Id: " + index
//                + " Total items: " + size());
//        robots.get(index).moveRandomly(500);
//        populate();
//        return true;
//    }
    
    public void refresh() {
        populate();
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
}
