package com.example.android.apis.overlays;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.drawable.Drawable;

import com.example.android.apis.model.Player;
import com.example.android.apis.model.Robot;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PlayerItemizedOverlay extends ItemizedOverlay {
    private OverlayItem item;

    public PlayerItemizedOverlay(Drawable defaultMarker, Player player) {
        super(boundCenterBottom(defaultMarker));
        this.item = new OverlayItem(player.getLocation(), "", "");
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return item;
    }

    @Override
    public int size() {
        return 1;
    }

}
