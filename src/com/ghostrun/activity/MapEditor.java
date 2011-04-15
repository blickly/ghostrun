package com.ghostrun.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ghostrun.R;
import com.ghostrun.controllers.GameLoop;
import com.ghostrun.overlays.PlayerOverlay;
import com.ghostrun.overlays.PointsOverlay;
import com.ghostrun.overlays.RobotsItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/** Map Activity for showing the status of a game in progress.
 */
public class MapEditor extends MapActivity {
    public MapView mapView;
    MyLocationOverlay locationOverlay;
    PointsOverlay pointsOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapeditor);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.clear();
        
        GameLoop gameLoop = new GameLoop();

        // Add player overlay
        locationOverlay = new PlayerOverlay(this, mapView,
                gameLoop.getPlayer());
        registerLocationUpdates(locationOverlay);
        mapOverlays.add(locationOverlay);

        // Add robot overlay
        Drawable robotIcon = this.getResources().getDrawable(
                R.drawable.androidmarker);
        RobotsItemizedOverlay robotOverlay = new RobotsItemizedOverlay(
                    robotIcon, gameLoop.getRobots());
        mapOverlays.add(robotOverlay);
        
        // Add points
        newPointsOverlay();
        
        // Start game loop
        Handler handler = new Handler();
        gameLoop.setRobotOverlay(robotOverlay);
        handler.post(gameLoop);
        
        // Stop the current activity and return to the previous view.
        Button logobutton=(Button)findViewById(R.id.mapview_paclogo);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    public void newPointsOverlay() {
    	Drawable marker = this.getResources().getDrawable(R.drawable.blue);
        Drawable selectedMarker = this.getResources().getDrawable(R.drawable.blue_dot);
        final PointsOverlay pointsOverlay = new PointsOverlay(marker, selectedMarker, this);
        this.mapView.getOverlays().add(pointsOverlay);
        this.pointsOverlay = pointsOverlay;
        
        // Select/remove button
        final Button selectButton = (Button)findViewById(R.id.select);
        selectButton.setOnClickListener(new OnClickListener(){      
    	    public void onClick(View v) {	
    	    	if (selectButton.getText().toString().equals("Select On")) {
    	    		selectButton.setText("Remove On", TextView.BufferType.NORMAL);
    	    		pointsOverlay.remove();
    	    	} else {
    	    		selectButton.setText("Select On", TextView.BufferType.NORMAL);
    	    		pointsOverlay.select();
    	    	}
    	    }
    	});
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
    
    // Menu will hold "Sound" button and "Map Selection" button.
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Title");
    	alert.setMessage("Message");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	alert.setView(input);
    	
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    	  String value = input.getText().toString();
	    	  // Do something with value!
	    	  writeToFile(value);
	    	}});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});
    	final AlertDialog dialog = alert.create();
    	
        menu.add("Save Map");
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	dialog.show();
                return true;
            }
        });
        return true;
    }
    
    private void writeToFile(String fileName) {
    	String json = pointsOverlay.getJson();
        try {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()){
                File outputfile = new File(root, fileName);
                FileWriter filewriter = new FileWriter(outputfile);
                BufferedWriter out = new BufferedWriter(filewriter);
                out.write(json);
                out.close();
            }
        } catch (IOException e) {
            
        }
    }
}