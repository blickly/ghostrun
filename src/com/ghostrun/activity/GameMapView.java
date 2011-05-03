package com.ghostrun.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.ghostrun.R;
import com.ghostrun.controllers.GameLoop;
import com.ghostrun.driving.Node;
import com.ghostrun.driving.NodeFactory;
import com.ghostrun.overlays.DotsOverlay;
import com.ghostrun.overlays.MazeOverlay;
import com.ghostrun.overlays.PlayerOverlay;
import com.ghostrun.overlays.RobotsItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/** Map Activity for showing the status of a game in progress.
 */
public class GameMapView extends MapActivity {
    MapView mapView;
    MyLocationOverlay locationOverlay;
    MazeOverlay mazeOverlay;
    DotsOverlay dotsOverlay;
    GameLoop gameLoop;
    MediaPlayer begin_game_mp;
    MediaPlayer pacman_death_mp;
    boolean soundOn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(17);
        
        // Keep screen on when game is visible.
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Stop the current activity and return to the previous view.
        Button logobutton=(Button)findViewById(R.id.mapview_paclogo);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        this.mazeOverlay = null;
        
        soundOn = true;
    }

    public void addGameLoop(List<Node> nodes) {
    	List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.clear();
    	this.gameLoop = new GameLoop(nodes, this);
    	
        // Add maze overlay
        mazeOverlay = new MazeOverlay(nodes);
        mapOverlays.add(mazeOverlay);
        this.mapView.getController().setCenter(nodes.get(0).latlng);
        
        // Add Food dots overlay
        Drawable marker = this.getResources().getDrawable(R.drawable.androidmarker);
        this.dotsOverlay = new DotsOverlay(marker);
        this.gameLoop.setDotsOverlay(dotsOverlay);
    	
    	// Add player overlay
        locationOverlay = new PlayerOverlay(this, mapView,
                gameLoop.getPlayer());
        registerLocationUpdates(locationOverlay);
        mapOverlays.add(locationOverlay);

        // Add robot overlay
        Drawable redIcon = this.getResources().getDrawable(
                R.drawable.game_redghost);
        Drawable orangeIcon = this.getResources().getDrawable(
                R.drawable.game_orangeghost);
        Drawable blueIcon = this.getResources().getDrawable(
                R.drawable.game_blueghost);
        Drawable pinkIcon = this.getResources().getDrawable(
                R.drawable.game_pinkghost);
        RobotsItemizedOverlay robotOverlay = new RobotsItemizedOverlay(
                    redIcon, orangeIcon, pinkIcon, blueIcon, gameLoop.getRobots());
        mapOverlays.add(robotOverlay);
        
        // Add beginning sound.
        if (soundOn) {
            begin_game_mp = MediaPlayer.create(GameMapView.this, R.raw.pacman_beginning);
            begin_game_mp.start();
        }
        
        // Start game loop
        Handler handler = new Handler();
        gameLoop.setRobotOverlay(robotOverlay);
        handler.post(gameLoop);
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (locationOverlay != null)
        	locationOverlay.disableMyLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationOverlay != null)
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
        menu.add("Select Map");
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i= new Intent(GameMapView.this, FileBrowserView.class);
                startActivityForResult(i, 0);
                return true;
            }
        });
       
        menu.add("Sound On");
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO Auto-generated method stub
                if (soundOn) {
                    soundOn = false;
                    item.setTitle("Sound Off");
                } else {
                    soundOn = true;
                    item.setTitle("Sound On");
                }
                return true;
            }
        });
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (data == null) {
    	    return;
    	}
    	String filename = data.getStringExtra("filename");
		try {
			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input);
			String json = bufRead.readLine();
			
			//System.out.println(json);
			
			NodeFactory factory = new NodeFactory();
			NodeFactory.NodesAndRoutes nodesAndRoutes = factory.fromMap(json);
			
			//System.out.println(nodesAndRoutes.nodes.size());
			//System.out.println(nodesAndRoutes.routesMap.size());
			
			List<Node> nodes = nodesAndRoutes.toNodes();
			this.addGameLoop(nodes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
    /** Handle the death of the player.
     * This includes things like displaying a message to the player,
     * saving high scores, etc.
     */
    public void handlePlayerDeath() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(900);
        if (soundOn) {
            pacman_death_mp = MediaPlayer.create(GameMapView.this, R.raw.pacman_death);
            pacman_death_mp.start();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You died!")
               .setCancelable(false)
               .setPositiveButton("Aww, shit...", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                   }
               });
        builder.create().show();
    }
}