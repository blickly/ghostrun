/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.activity;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.android.apis.overlays.RobotsItemizedOverlay;
import com.example.android.google.apis.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * Example of how to use an {@link com.google.android.maps.MapView}.
 * <h3>MapViewDemo</h3>

<p>This demonstrates creating a Map based Activity.</p>

<h4>Demo</h4>
Views/MapView

<h4>Source files</h4>
 * <table class="LinkTable">
 *         <tr>
 *             <td >src/com.example.android.apis/view/MapViewDemo.java</td>
 *             <td >The Alert Dialog Samples implementation</td>
 *         </tr>
 *         <tr>
 *             <td >/res/layout/mapview.xml</td>
 *             <td >Defines contents of the screen</td>
 *         </tr>
 * </table>
 */
public class MapViewDemo extends MapActivity {
    MapView mapView;
    List<Overlay> mapOverlays;
    MyLocationOverlay locationOverlay;
//    Drawable robotIcon;
//    RobotsItemizedOverlay robotsOverlay;
//    Drawable playerIcon;
//    PlayerItemizedOverlay playerOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();
        mapOverlays.clear();
        
        Drawable robotIcon = this.getResources().getDrawable(R.drawable.androidmarker);
        Overlay robotsOverlay = new RobotsItemizedOverlay(robotIcon);
        mapOverlays.add(robotsOverlay);
        
        // Add player overlay
        locationOverlay = new MyLocationOverlay(this, mapView);
        mapOverlays.add(locationOverlay);
        registerLocationUpdates(locationOverlay);

//        Player me = new Player(here);
//        playerIcon = this.getResources().getDrawable(R.drawable.ben_face_small);
//        playerOverlay = new PlayerItemizedOverlay(playerIcon, me);
//        mapOverlays.add(playerOverlay);
        
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
            return;
        }
        locationManager.requestLocationUpdates(bestLocationProvider, 0L, 0.0f, listener);
    }

}
