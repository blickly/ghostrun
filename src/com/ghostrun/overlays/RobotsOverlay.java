package com.ghostrun.overlays;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.Robot;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RobotsOverlay extends ItemizedOverlay<OverlayItem> {
    private List<Robot> robots;
    private Drawable redMarker, orangeMarker, pinkMarker, blueMarker;

    public RobotsOverlay(Drawable redMarker, Drawable orangeMarker
            , Drawable pinkMarker, Drawable blueMarker, List<Robot> robots) {
        super(boundCenterBottom(redMarker));
        this.redMarker = boundCenterBottom(redMarker);
        this.orangeMarker = boundCenterBottom(orangeMarker);
        this.pinkMarker = boundCenterBottom(pinkMarker);
        this.blueMarker = boundCenterBottom(blueMarker);
        this.robots = robots;

        populate();
    }
    
    public void refresh() {
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        Robot r = robots.get(i);
        OverlayItem ghost = new OverlayItem(r.getLocation(), "", "");
        switch (r.getRobotType()) {
            case BLINKY:
                ghost.setMarker(redMarker);
                break;
            case INKY:
                ghost.setMarker(blueMarker);
                break;
            case PINKY:
                ghost.setMarker(pinkMarker);
                break;
            case CLYDE:
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
