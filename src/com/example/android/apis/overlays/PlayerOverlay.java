package com.example.android.apis.overlays;

import android.content.Context;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class PlayerOverlay extends MyLocationOverlay {

    public PlayerOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

}
