package com.ghostrun.overlays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.MazeGraphPoint;
import com.ghostrun.util.RandUtils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DotsOverlay extends ItemizedOverlay<OverlayItem>{
	
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
			return 0;
		}
	}
	
	List<GeoPoint> items;
	MazeGraph maze;
	final double DENSITY = 1.0;
	final double INCREMENT = 200.0;
	final int EATING_DISTANCE = 150;
	final int DOT_POINTS = 10;
	int size = 0;
	MapView mapView;
	
	public DotsOverlay(Drawable arg0, MapView mapView) {
		super(boundCenterBottom(arg0));
		this.mapView = mapView;
	}
	
	public void setMazeGraph(MazeGraph maze) {
		this.maze = maze;
		this.items = new ArrayList<GeoPoint>();
		
		Set<PointPair> doneSet = new HashSet<PointPair>();
		for (MazeGraphPoint p : maze.getPoints()) {
			//this.items.add(p.getLocation());
			for (MazeGraphPoint n : p.getNeighbors()) {
				PointPair pair = new PointPair(p, n);
				if (!doneSet.contains(pair)) {
					//System.out.println("p: " + p.getLocation() + " n: " + n.getLocation());
					//this.items.add(n.getLocation());
					
					generateDotsAlongEdge(pair);
					doneSet.add(pair);
				}
			}
		}
		this.setLastFocusedIndex(-1);
		this.populate();
		this.mapView.invalidate();
		
		System.out.println("Done adding maze points: " + this.size());
	}
	
	public int refresh(GeoPoint playerLocation) {
		// TODO: eat dots!
		int points = 0; 
		Iterator<GeoPoint> iter = this.items.iterator();
		while (iter.hasNext()) {
			GeoPoint pt = iter.next();
			//System.out.println("Distance: " + distance(playerLocation, pt));
			if ((int) distance(playerLocation, pt) < EATING_DISTANCE) {
				points += DOT_POINTS;
				iter.remove();
			}
		}
		
		this.setLastFocusedIndex(-1);
		this.populate();
		this.mapView.invalidate();
		
		return points;
	}
	
	private int getDifference(double slope, double distance, int direction) {
		return (int)(direction * (Math.sqrt((distance * distance) / (slope * slope + 1))));
	}
	
	private void generateDotsAlongEdge(PointPair p) {
		
		//System.out.println("generating dots along the edge...");
		double slope = (double)(p.pt1.getLocation().getLatitudeE6() - p.pt2.getLocation().getLatitudeE6())/
						(double)(p.pt1.getLocation().getLongitudeE6() - p.pt2.getLocation().getLongitudeE6());
		
		GeoPoint pt1 = p.pt1.getLocation();
		GeoPoint pt2 = p.pt2.getLocation();
		
		GeoPoint curPoint = p.pt2.getLocation();
		int direction = (pt1.getLongitudeE6() > pt2.getLongitudeE6() ? 1 : -1);

		double totalDistance = distance(p.pt2.getLocation(), p.pt1.getLocation());
		int times = (int)(totalDistance / INCREMENT);
		int difference = getDifference(slope, INCREMENT, direction);
		
		while (times > 0) {
			curPoint = new GeoPoint((int)(curPoint.getLatitudeE6() + slope * difference),
									(int)(curPoint.getLongitudeE6() + difference));
			if (RandUtils.nextDouble() < DENSITY) {
				this.items.add(curPoint);
				this.populate();
			}
			times --;
		}
		
		System.out.println("added points: " + this.items.size());
		this.setLastFocusedIndex(-1);
		this.populate();
		this.mapView.invalidate();
	}
	
	private double distance(GeoPoint p1, GeoPoint p2) {
		double d1 = (double)(p1.getLatitudeE6() - p2.getLatitudeE6());
		double d2 = (double)(p1.getLongitudeE6() - p2.getLongitudeE6());
		return Math.sqrt(d1 * d1 + d2 * d2);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return new OverlayItem(this.items.get(i), "", "");
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.items.size();
	}
}
