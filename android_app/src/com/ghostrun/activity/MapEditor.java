package com.ghostrun.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.ghostrun.R;
import com.ghostrun.client.ServerRequest;
import com.ghostrun.driving.Node;
import com.ghostrun.overlays.PlayerOverlay;
import com.ghostrun.overlays.PointsOverlay;
import com.ghostrun.util.LocationHelper;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/** Map Activity for showing the status of a game in progress.
 */
public class MapEditor extends MapActivity {
    public MapView mapView;
    private PointsOverlay pointsOverlay;
    private ImageButton button;
    private int mode = 0;
    private LocationHelper locationHelper;
    private MyLocationOverlay locationOverlay;
    
    public synchronized int getNextMode() {
    	this.mode ++;
    	return this.mode % 3; 
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapeditor);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);
        mapView.getController().setZoom(17);		
        //mapView.getOverlays().clear();
        
        locationHelper = new LocationHelper(this, mapView);
        
        locationOverlay = new PlayerOverlay(this, mapView, null);
        mapView.getOverlays().add(locationOverlay);
        locationHelper.addLocationListener(locationOverlay);
        
        final Drawable cone = this.getResources().getDrawable(R.drawable.cone);
        final Drawable cross = this.getResources().getDrawable(R.drawable.cross);
        final Drawable connect = this.getResources().getDrawable(R.drawable.connect);
        
        button = (ImageButton) findViewById(R.id.modebutton);
        button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ImageButton button = (ImageButton) arg0;
				switch (getNextMode()) {
				case 0:
					button.setImageDrawable(cone);
					pointsOverlay.select();
					break;
				case 1:
					button.setImageDrawable(cross);
					pointsOverlay.remove();
					break;
				case 2:
					button.setImageDrawable(connect);
					pointsOverlay.connect();
					break;
				}
			}
        });
        
        // Add points
        newPointsOverlay();
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
        if (locationOverlay != null)
        	locationOverlay.disableMyLocation();
        
        locationHelper.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected boolean isRouteDisplayed() { return false; }
    
    ///////////////////////////////////////////////////////////////////
    //                     private methods
    ///////////////////////////////////////////////////////////////////
    
    // Menu will hold "Sound" button and "Map Selection" button.
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
    	
        menu.add("Save");
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	saveMap(false);
                return true;
            }
        });
        
        menu.add("Play");
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	saveMap(true);
                Intent i= new Intent(MapEditor.this, GameMapView.class);
                i.putExtra("map", pointsOverlay.getJson());
                startActivity(i);
                return true;
            }
        });
        
        menu.add("Select On");
        menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        	@Override
    	    public boolean onMenuItemClick(MenuItem item) {	
    	    	if (item.getTitle().equals("Select On")) {
    	    		item.setTitle("Remove On");
    	    		pointsOverlay.remove();
    	    	} else {
    	    		item.setTitle("Select On");
    	    		pointsOverlay.select();
    	    	}
    	    	return true;
    	    }
    	});

        return true;
    }
    
    private void saveMap(boolean silent) {
    	String json = pointsOverlay.getJson();
    	Map<String, String> m = new HashMap<String, String>();
    	m.put("map", json);
    	
    	ServerRequest request = new ServerRequest("save_map", m);
    	
    	int map_index = 0;
    	
    	while (true) {
    		try {
    			map_index = new Integer(request.makeRequest(false));
    			break;
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
        
    	if (!silent) {
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);

    		alert.setTitle("Saved on the server with index: " + map_index);
    		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    			}});
    	
    		AlertDialog dialog = alert.create();
    		dialog.show();
    	}
    }
}