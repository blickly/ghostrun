package com.ghostrun.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ghostrun.R;
import com.ghostrun.controllers.GameLoop;
import com.ghostrun.driving.Node;
import com.ghostrun.driving.NodeFactory;
import com.ghostrun.overlays.PlayerOverlay;
import com.ghostrun.overlays.PointsOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/** Map Activity for showing the status of a game in progress.
 */
public class MapEditor extends MapActivity {
    public MapView mapView;
    PointsOverlay pointsOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapeditor);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapView.getController().setZoom(17);

        List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.clear();
        
        
        // Add points
        newPointsOverlay();
        
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
    	if (this.pointsOverlay != null)
    		this.mapView.getOverlays().remove(this.pointsOverlay);
    	
    	Drawable marker = this.getResources().getDrawable(R.drawable.blue);
        Drawable selectedMarker = this.getResources().getDrawable(R.drawable.blue_dot);
        this.pointsOverlay = new PointsOverlay(marker, selectedMarker, this);
        this.mapView.getOverlays().add(pointsOverlay);
    }
    
    public void newPointsOverlay(List<Node> nodes) {
    	if (this.pointsOverlay != null)
    		this.mapView.getOverlays().remove(this.pointsOverlay);
    	
    	Drawable marker = this.getResources().getDrawable(R.drawable.blue);
        Drawable selectedMarker = this.getResources().getDrawable(R.drawable.blue_dot);
        this.pointsOverlay = new PointsOverlay(marker, selectedMarker, this, nodes);
        this.mapView.getOverlays().add(pointsOverlay);
    }

    @Override
    public void onPause() {
        super.onPause();
        //locationOverlay.disableMyLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        //locationOverlay.enableMyLocation();
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

    	alert.setTitle("Save Map");
    	alert.setMessage("Map Name");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	alert.setView(input);
    	
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    	  String value = input.getText().toString()+".pac";
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
        
        menu.add("Select On");
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        	@Override
    	    public boolean onMenuItemClick(MenuItem item) {	
    	    	if (item.getTitle().toString().equals("Select On")) {
    	    		item.setTitle("Remove On");
    	    		pointsOverlay.remove();
    	    	} else {
    	    		item.setTitle("Select On");
    	    		pointsOverlay.select();
    	    	}
    	    	return true;
    	    }
    	});
        
        menu.add("Random Map");
        menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	String filename = "data1.json";
        		InputStreamReader input = null;
    			try {
					input = new InputStreamReader(
							getAssets().open(filename));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		BufferedReader bufRead = new BufferedReader(input);
            	List<Node> randNodes = null;
				try {
					randNodes = NodeFactory.generateRandomMap(bufRead.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	System.out.println("size of random nodes: " + randNodes.size());
            	newPointsOverlay(randNodes);
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