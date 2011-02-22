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
import java.util.Random;

import com.example.android.apis.model.Player;
import com.example.android.apis.model.Robot;
import com.example.android.apis.overlays.PlayerItemizedOverlay;
import com.example.android.apis.overlays.RobotsItemizedOverlay;
import com.example.android.google.apis.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.LinearLayout;

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
    LinearLayout linearLayout;
    MapView mapView;
    List<Overlay> mapOverlays;
    Drawable robotIcon;
    Drawable playerIcon;
    RobotsItemizedOverlay robotsOverlay;
    PlayerItemizedOverlay playerOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();
        
        robotIcon = this.getResources().getDrawable(R.drawable.androidmarker);
        robotsOverlay = new RobotsItemizedOverlay(robotIcon);
        // Campanile
        GeoPoint campanilePoint = new GeoPoint(37871944, -122257778);
        Robot robot = new Robot(campanilePoint);
        robotsOverlay.addRobot(robot);
        // NE Corner of Campus
        GeoPoint neCornerPoint = new GeoPoint(37875522,-122256825);
        Robot robot2 = new Robot(neCornerPoint);
        robotsOverlay.addRobot(robot2);
        
        Location location = getLastLocation();
        GeoPoint here = new GeoPoint(
                (int) Math.round(location.getLatitude() * 1E6),
                (int) Math.round(location.getLongitude() * 1E6));
        
        // Generate robots in random places
        createRandomRobots(robotsOverlay, here, 7);

        mapOverlays.add(robotsOverlay);
        
        // Add player overlay
        Player me = new Player(here);
        playerIcon = this.getResources().getDrawable(R.drawable.ben_face_small);
        playerOverlay = new PlayerItemizedOverlay(playerIcon, me);
        
        mapOverlays.add(playerOverlay);
        
        makeDialog();
        
    }
    
    private void makeDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    System.exit(0);
                    break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gratuitous Dialog").setMessage("Initializing Map...")
            .setPositiveButton("OK", dialogClickListener)
            .setNegativeButton("Get me outta here!", dialogClickListener).show();
    }

    private Location getLastLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(
                Activity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        String bestLocationProvider = locationManager.getBestProvider(
                criteria, false);
        if (bestLocationProvider == null
                || !locationManager.isProviderEnabled(bestLocationProvider)) {
          return null;
        }
        return locationManager.getLastKnownLocation(bestLocationProvider);
      }

    private RobotsItemizedOverlay createRandomRobots(RobotsItemizedOverlay overlay, GeoPoint center, int numRobots) {
        Random rand = new Random();
        for (int i = 0; i < numRobots; ++i) {
            int lat = (int) (center.getLatitudeE6() + 10000 * rand.nextGaussian());
            int lon = (int) (center.getLongitudeE6() + 10000 * rand.nextGaussian());
            overlay.addRobot(new Robot(new GeoPoint(lat, lon)));
        }
        return overlay;
    }

    @Override
    protected boolean isRouteDisplayed() { return false; }
}
