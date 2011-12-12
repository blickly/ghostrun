package com.ghostrun.overlays;

import android.content.Context;

import com.ghostrun.model.Player;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class PlayerOverlay extends MyLocationOverlay {
    private Player player;
    private MapView mapView;

    public PlayerOverlay(Context context, MapView mapView, Player player) {
        super(context, mapView);
        this.mapView = mapView;
        this.player = player;
    }
    
    public PlayerOverlay(Context context, MapView mapView) {
        this(context, mapView, null);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        super.onLocationChanged(location);
        //mapView.getController().setCenter(new GeoPoint((int)(location.getLatitude() * 1000000), (int)(location.getLongitude() * 1000000)));
        if (player != null) {
        	android.util.Log.d("PlayerOverlay", "Location Changed. "
        			+ "New Locaion: " + location);
        	
        	player.setLocation(location);
        }
    }
}
