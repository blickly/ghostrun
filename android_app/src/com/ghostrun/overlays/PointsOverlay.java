package com.ghostrun.overlays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;

import com.ghostrun.activity.MapEditor;
import com.ghostrun.driving.DrivingDirections;
import com.ghostrun.driving.DrivingDirections.Mode;
import com.ghostrun.driving.DrivingDirectionsFactory;
import com.ghostrun.driving.Node;
import com.ghostrun.driving.NodeFactory;
import com.ghostrun.driving.NodePair;
import com.ghostrun.driving.Route;
import com.ghostrun.driving.impl.RouteImpl;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.google.gson.Gson;

public class PointsOverlay extends ItemizedOverlay<OverlayItem> {
	private int node_id = 0, selected = -1, connect_selected = -1, mode = 0;	
	//private HashMap<NodePair, Route> routesMap;
	private List<Node> nodes;
	private GestureDetector gestureDetector;
	private MapView mapView;
	private Paint mPaint;
	
	private Drawable marker, selectedMarker;
	private MapEditor editor;
	
	public PointsOverlay(Drawable marker, Drawable selectedMarker, 
						 MapEditor editor, List<Node> nodes) {
		this(marker, selectedMarker, editor);
		
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		for (Node n : nodes) {
			this.nodes.add(n);
			System.out.println("adding node: " + n.latlng);
			nodeMap.put(n.id, n);
		}
		
		/*
		int processedNodes = 0;
		for (Node n1 : nodes) {
			final Node f1 = n1; 
			for (Node n2 : n1.neighbors) {
				final Node f2 = n2;
				if (processedNodes % 20 == 0) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				processedNodes ++;
				DrivingDirections directions = DrivingDirectionsFactory.createDrivingDirections();
				directions.driveTo(n1.latlng, n2.latlng, 
						DrivingDirections.Mode.DRIVING, 
						new DrivingDirections.IDirectionsListener() {
							public void onDirectionsAvailable (Route route, Mode mode) {
								routesMap.put(new NodePair(f1.id, f2.id), route);
							}
					
							public void onDirectionsNotAvailable () {
					
							}
					});
			}
		}
		*/
		this.setLastFocusedIndex(-1);
		this.selected = this.nodes.size() -1;
		this.mapView.invalidate();
		this.populate();
	}
	
	public PointsOverlay(Drawable marker, Drawable selectedMarker, MapEditor editor) {
		super(boundCenterBottom(marker));
		
		this.editor = editor;
		this.marker = marker;
		this.selectedMarker = selectedMarker;
		this.mapView = editor.mapView;
		this.nodes = new ArrayList<Node>();
		//this.routesMap = new HashMap<NodePair, Route>();
		
        this.mPaint = new Paint();
        this.mPaint.setDither(true);
        this.mPaint.setColor(Color.RED);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(2);
                
		this.populate();
	}
	
	public String getJson() {
		return NodeFactory.getJsonFromNodes(nodes);
	}

	protected void assertTrue(boolean t, String s) {
		if (!t)
			throw new RuntimeException (s);
	}
	
	public void addUnsafeMarker(GeoPoint p) {
		DrivingDirections directions = DrivingDirectionsFactory.createDrivingDirections();
		directions.driveTo(p, p, DrivingDirections.Mode.DRIVING, 
				new DrivingDirections.IDirectionsListener() {
					public void onDirectionsAvailable (Route route, Mode mode) {
						List<GeoPoint> points = route.getGeoPoints();
						try {
							GeoPoint pt = points.get(points.size()-1);
							addMarker(pt);
							
							/*
							GeoPoint pt2 = new ReverseGeocodeIntersection().
								getClosestIntersection(pt.getLatitudeE6()/1000000.0,
														pt.getLongitudeE6()/1000000.0);
							addMarker(pt2);
							*/
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			
					public void onDirectionsNotAvailable () {
			
					}
			});
	}
	
	public synchronized void select() {
		mode = 0;
		this.connect_selected = -1;
	}
	
	public synchronized void remove() {
		mode = 1;
		this.connect_selected = -1;
		this.selected = -1;
	}
	
	public synchronized void connect() {
		mode = 2;
		this.selected = -1;
	}
	
	public void addMarker(GeoPoint p) throws Exception {
		assertTrue(selected != -1 || nodes.size() == 0, "No points selected");
		
		final Node endPt = new Node(p, node_id++, true);

		if (nodes.size() > 0) {
			final Node startPt = nodes.get(selected);
			//startPt.addNeighbor(endPt);
			//endPt.addNeighbor(startPt);
			
			DrivingDirections directions = DrivingDirectionsFactory.createDrivingDirections();
			directions.driveTo(startPt.latlng, endPt.latlng, 
					DrivingDirections.Mode.WALKING, 
					new DrivingDirections.IDirectionsListener() {
						public void onDirectionsAvailable (Route route, Mode mode) {
							addRoute(startPt, endPt, route);
							//routesMap.put(new NodePair(startPt.id, endPt.id), route);
							//mapView.invalidate();
						}
				
						public void onDirectionsNotAvailable () {
				
						}
				});
		}
		this.setLastFocusedIndex(-1);
		this.nodes.add(endPt);
		this.selected = this.nodes.size() -1;
		this.mapView.invalidate();
		this.populate();
	}
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		
		OverlayItem item = new OverlayItem(this.nodes.get(i).latlng, "", "");
		
		if (this.selected == i || this.connect_selected == i)
			item.setMarker(boundCenterBottom(this.selectedMarker));
		
		return item;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		//System.out.println("Size: " + this.nodes.size());
		return this.nodes.size();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow){
        super.draw(canvas, mapv, shadow);
        
        Set<Integer> done = new HashSet<Integer>();
        
        Projection projection = mapv.getProjection();
        for (Node n1 : nodes) {
        	for (Node n2 : n1.neighbors) {
        		
        		if (done.contains(n2.id))
        			continue;
        		
        		Point p1 = new Point();
        		Point p2 = new Point();
     
        		projection.toPixels(n1.latlng, p1);
        		projection.toPixels(n2.latlng, p2);
        		
        		Path path = new Path();
        		path.moveTo(p2.x, p2.y);
        		path.lineTo(p1.x, p1.y);
        		canvas.drawPath(path, this.mPaint);		
        	}
        	done.add(n1.id);
        }
    }
	
	public boolean onTap(GeoPoint p, MapView mapView) { 
		try {
			boolean tapped = super.onTap(p, mapView);
			if (tapped){
				//do what you want to do when you hit an item           
		    }           
		    else{
		        //do what you want to do when you DONT hit an item
		    	addUnsafeMarker(p);
		    }      
		}
		catch (Exception e){
			e.printStackTrace();
		}
	    return true;
	}
	
	@Override
	public boolean onTap(int index) {
		// TODO: connect two nodes together
		switch (mode) {
		case 0:
			if (index == this.selected)
				this.selected = -1;
			else
				this.selected = index;
			break;
		case 1:
			this.selected = -1;
			Node n = this.nodes.get(index);
			for (Node neighbor : n.neighbors) {
				neighbor.removeNeighbor(n);
			}
			this.setLastFocusedIndex(-1);
			this.nodes.remove(index);
			break;
		case 2:
			if (connect_selected == -1)
				connect_selected = index;
			else {
				final Node n1 = this.nodes.get(connect_selected);
				final Node n2 = this.nodes.get(index);
				
				if (n1.neighbors.contains(n2))
					break;
	
				DrivingDirections directions = DrivingDirectionsFactory.createDrivingDirections();
				directions.driveTo(n1.latlng, n2.latlng, 
						DrivingDirections.Mode.WALKING, 
						new DrivingDirections.IDirectionsListener() {
							public void onDirectionsAvailable (Route route, Mode mode) {
								addRoute(n1, n2, route);
							}
					
							public void onDirectionsNotAvailable () {
					
							}
					});
				
				connect_selected = -1;
			}

		}

		this.populate();
		this.mapView.invalidate();
		return true;
	}
	
	public void addRoute(Node n1, Node n2, Route route) {

		Node lastNode = n1;
    	for (GeoPoint pt : route.getGeoPoints()) {
    		Node newNode = new Node(pt, node_id++, false);
    		nodes.add(newNode);
    		newNode.addNeighbor(lastNode);
    		lastNode.addNeighbor(newNode);
    		lastNode = newNode;
    		this.mapView.invalidate();
    		this.populate();
    	}
    	n2.addNeighbor(lastNode);
    	lastNode.addNeighbor(n2);
    	this.mapView.invalidate();
    	this.populate();
	}
}
