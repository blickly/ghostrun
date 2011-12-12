package com.ghostrun.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.ghostrun.activity.MultiplayerMapView;
import com.ghostrun.client.ServerRequest;
import com.ghostrun.config.Constants;
import com.ghostrun.overlays.DotsOverlay;
import com.ghostrun.util.GeoPointOffset;
import com.ghostrun.util.GeoPointUtils;
import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

public class Dots implements LocationListener {
    private Map<Integer, GeoPoint> dotMap;
    private List<GeoPoint> dotList;
    private String gid;

    public Dots(MazeGraph mazeGraph) {
        setMazeGraph(mazeGraph);
    }
    
    @SuppressWarnings("unchecked")
	public Dots(Map<String, Object> json, String gid) {
    	this.gid = gid;
    	dotMap = new HashMap<Integer, GeoPoint>();
    	dotList = new ArrayList<GeoPoint>();
    	    	
    	for (Map.Entry<String, Object> entry : ((Map<String, Object>)json.get("dots")).entrySet()) {
    		List<Object> intList = (List<Object>)entry.getValue();
    		GeoPoint pt = new GeoPoint(((Long)intList.get(0)).intValue(),
					 				   ((Long)intList.get(1)).intValue());
    		dotMap.put(new Integer(entry.getKey()), pt);
    		dotList.add(pt);
    	}
    }
    
    class PointPair {
        MazeGraphPoint pt1, pt2;
        PointPair(MazeGraphPoint pt1, MazeGraphPoint pt2) {
            this.pt1 = pt1;
            this.pt2 = pt2;
        }

        public boolean equals(Object o) {
            if (o instanceof PointPair) {
                PointPair p = (PointPair)o;
                return (p.pt1.equals(this.pt1) && p.pt2.equals(this.pt2)) ||
                (p.pt1.equals(this.pt2) && p.pt2.equals(this.pt1));
            }
            return false;
        }
        public int hashCode() {
            return pt1.hashCode() ^ pt2.hashCode();
        }
    }

    public void setMazeGraph(MazeGraph maze) {
    	this.dotMap = new HashMap<Integer, GeoPoint>();
        Set<PointPair> doneSet = new HashSet<PointPair>();
        int dot_id = 0;
        for (MazeGraphPoint p : maze.getPoints()) {
            for (MazeGraphPoint n : p.getNeighbors()) {
                PointPair pair = new PointPair(p, n);
                if (!doneSet.contains(pair)) {
                    dot_id = generateDotsAlongEdge(pair, dot_id);
                    doneSet.add(pair);
                }
            }
            dotMap.put(dot_id, p.getLocation());            
        }
    }

    public int eatDotsAt(GeoPoint playerLocation) {
        if (playerLocation == null) { return 0; }
        int pointIncrement = 0; 
        
        for (Map.Entry<Integer, GeoPoint> entry : dotMap.entrySet()) {
        	int distance = (int) GeoPointUtils.getDistance(playerLocation, entry.getValue());
        	if (distance < Constants.EATING_DISTANCE) {
        		pointIncrement += Constants.DOT_POINTS;
        		dotMap.remove(entry.getKey());
        	}
        }	
        return pointIncrement;
    }

    private int generateDotsAlongEdge(PointPair p, int dotid) {
        GeoPointOffset slope = new GeoPointOffset(p.pt1.getLocation(),
                p.pt2.getLocation());
        int totalDistance = (int)slope.getLength();
        int times = totalDistance / Constants.DOT_SPACING;
        slope.scaleBy(1.0 / times);

        GeoPoint curPoint = p.pt1.getLocation();
        times--;
        while (times > 0) {
            curPoint = slope.addTo(curPoint);
            dotMap.put(dotid++, curPoint);
            times--;
        }
        return dotid;
    }

    public GeoPoint get(int i) {
    	return dotList.get(i);
    }

    public int remaining() {
        return dotList.size();
    }
    
    public synchronized void handleChange(List<Object> dotChange) {
    	for (Object d : dotChange) {
    		int dot = ((Long)d).intValue();
    		dotMap.remove(d);
   	}
    	
    	dotList = new ArrayList<GeoPoint>();
    	for (Map.Entry<Integer, GeoPoint> entry : dotMap.entrySet()) {
    		dotList.add(entry.getValue());
    	}
    }
    
    public DotsOverlay overlay;
    
    public void setOverlay(DotsOverlay overlay) {
    	this.overlay = overlay;
    }
    
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location.hasAccuracy() && location.getAccuracy() > 10000.0)
			return;
		
	    GeoPoint playerLocation = new GeoPoint((int)(location.getLatitude()*1000000), (int)(location.getLongitude()*1000000));
     
        List<Integer> dotAte = new ArrayList<Integer>(); 
        
        for (Map.Entry<Integer, GeoPoint> entry : dotMap.entrySet()) {
        	int distance = (int) GeoPointUtils.getDistance(playerLocation, entry.getValue());
        	if (distance < Constants.EATING_DISTANCE) {
        		dotAte.add(entry.getKey());
        	}
        }
        
        for (Integer d : dotAte) {
        	dotList.remove(dotMap.remove(d));
        }
        this.overlay.refresh();
        
        if (dotAte.size() == 0)
        	return;
        
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("gid", gid);
        fields.put("eat", new Gson().toJson(dotAte));
        ServerRequest request = new ServerRequest("eat_dot", fields);
        MultiplayerMapView.mmv.updateScore(new Integer(request.makeRequest(true)));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}