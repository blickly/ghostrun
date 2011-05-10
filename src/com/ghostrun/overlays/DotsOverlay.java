package com.ghostrun.overlays;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.Dots;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class DotsOverlay extends ItemizedOverlay<OverlayItem>{

    private Dots dots;

    public DotsOverlay(Drawable arg0, Dots dots) {
        super(boundCenterBottom(arg0));
        this.dots = dots;
    }

    public void refresh() {
        this.setLastFocusedIndex(-1);
        this.populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return new OverlayItem(dots.get(i), "", "");
    }

    @Override
    public int size() {
        return dots.remaining();
    }
}
