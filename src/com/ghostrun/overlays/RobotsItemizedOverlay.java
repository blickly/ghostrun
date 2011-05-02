package com.ghostrun.overlays;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.ghostrun.R;
import com.ghostrun.model.Robot;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RobotsItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private List<Robot> robots;
    private Drawable redMarker, orangeMarker, pinkMarker, blueMarker;

    public RobotsItemizedOverlay(Drawable redMarker, Drawable orangeMarker
            , Drawable pinkMarker, Drawable blueMarker, List<Robot> robots) {
        super(boundCenterBottom(redMarker));
        this.redMarker = redMarker;
        this.orangeMarker = orangeMarker;
        this.pinkMarker = pinkMarker;
        this.blueMarker = blueMarker;
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
        OverlayItem ghost = new OverlayItem(r.getLocation(), "", "");
        switch (i) {
            case 0:
                ghost.setMarker(redMarker);
                break;
            case 1:
                ghost.setMarker(blueMarker);
                break;
            case 2:
                ghost.setMarker(pinkMarker);
                break;
            case 3:
                ghost.setMarker(orangeMarker);
                break;
        }
        return ghost;
    }

    @Override
    public int size() {
        return robots.size();
    }
}
