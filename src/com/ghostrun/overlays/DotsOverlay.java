package com.ghostrun.overlays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.drawable.Drawable;

import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.MazeGraphPoint;
import com.ghostrun.util.RandUtils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
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
	final double DENSITY = 0.5;
	
	public DotsOverlay(Drawable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
		
	}
	
	public void setMazeGraph(MazeGraph maze) {
		this.maze = maze;
		this.items = new ArrayList<GeoPoint>();
		List<MazeGraphPoint> points = new ArrayList<MazeGraphPoint>();
		Set<PointPair> doneSet = new HashSet<PointPair>();
		for (MazeGraphPoint p : points) {
			for (MazeGraphPoint n : p.getNeighbors()) {
				PointPair pair = new PointPair(p, n);
				if (!doneSet.contains(pair)) {
					generateDotsAlongEdge(pair);
				}
			}
		}
	}
	
	private void generateDotsAlongEdge(PointPair p) {
		
		double slope = (double)(p.pt1.getLocation().getLatitudeE6() - p.pt2.getLocation().getLatitudeE6())/
						(double)(p.pt1.getLocation().getLongitudeE6() - p.pt2.getLocation().getLongitudeE6());
		
		GeoPoint curPoint = p.pt2.getLocation();
		final double increment = p.pt1.getLocation().getLongitudeE6() > p.pt2.getLocation().getLongitudeE6() ? 10.0 : -10.0;
		double totalDistance = distance(p.pt2.getLocation(), p.pt1.getLocation());
		while (distance(curPoint, p.pt1.getLocation()) < totalDistance) {
			curPoint = new GeoPoint((int)(curPoint.getLatitudeE6() + slope * increment), 
									(int)(curPoint.getLongitudeE6() + increment));
			if (RandUtils.nextDouble() < DENSITY) {
				items.add(curPoint);
			}
		}
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
		return 0;
	}

}
