package com.ghostrun.overlays;

import android.content.Context;

import com.ghostrun.model.Player;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class PlayerOverlay extends MyLocationOverlay {
    Player player;

    public PlayerOverlay(Context context, MapView mapView, Player player) {
        super(context, mapView);
        this.player = player;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        super.onLocationChanged(location);
        android.util.Log.d("PlayerOverlay", "Location Changed. "
                + "New Locaion: " + location);
        player.setLocation(location);
    }

}
