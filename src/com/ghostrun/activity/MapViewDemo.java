package com.ghostrun.activity;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.ghostrun.R;
import com.ghostrun.model.Player;
import com.ghostrun.overlays.PlayerOverlay;
import com.ghostrun.overlays.RobotsItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/** Map Activity for showing the status of a game in progress.
 */
public class MapViewDemo extends MapActivity {
    MapView mapView;
    MyLocationOverlay locationOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.clear();

        // Add player overlay
        Player player = new Player();
        locationOverlay = new PlayerOverlay(player, this, mapView);
        registerLocationUpdates(locationOverlay);
        mapOverlays.add(locationOverlay);

        // Add robot overlay
        Drawable robotIcon = this.getResources().getDrawable(R.drawable.androidmarker);
        Overlay robotsOverlay = new RobotsItemizedOverlay(robotIcon, player);
        mapOverlays.add(robotsOverlay);

    }

    @Override
    public void onPause() {
        super.onPause();
        locationOverlay.disableMyLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationOverlay.enableMyLocation();
    }

    @Override
    protected boolean isRouteDisplayed() { return false; }
    
    ///////////////////////////////////////////////////////////////////
    //                     private methods
    ///////////////////////////////////////////////////////////////////

    private void registerLocationUpdates(LocationListener listener) {
        LocationManager locationManager = (LocationManager) getSystemService(
                Activity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        String bestLocationProvider = locationManager.getBestProvider(
                criteria, false);
        if (bestLocationProvider == null
                || !locationManager.isProviderEnabled(bestLocationProvider)) {
            android.util.Log.d("registerLocationUpdates",
                    "Provider not available or not enabled");
            return;
        }
        locationManager.requestLocationUpdates(bestLocationProvider, 0, 0, listener);
    }

}
