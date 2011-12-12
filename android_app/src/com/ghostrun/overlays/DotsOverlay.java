package com.ghostrun.overlays;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.Dots;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class DotsOverlay extends ItemizedOverlay<OverlayItem> {

    private Dots dots;

    public DotsOverlay(Drawable arg0, Dots dots) {
        super(boundCenterBottom(arg0));
        this.dots = dots;
        this.dots.setOverlay(this);
        this.refresh();
    }
    
    public void updateDots(List<Object> dotChange) {
    	dots.handleChange(dotChange);
    	refresh();
    }

    public void refresh() {
        this.setLastFocusedIndex(-1);
        this.populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
    	try {
    		GeoPoint pt = dots.get(i);
    		return new OverlayItem(pt, "", "");
    	} catch(Exception e) {
    		return new OverlayItem(dots.get(0), "", "");
    	}
    }

    @Override
    public int size() {
        return dots.remaining();
    }
}