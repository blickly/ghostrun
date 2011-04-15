package com.ghostrun.overlays;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;

import com.ghostrun.activity.MapEditor;
import com.ghostrun.driving.Node;
import com.ghostrun.driving.NodeFactory.NodesAndRoutes;
import com.ghostrun.driving.NodePair;
import com.ghostrun.driving.Route;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class MazeOverlay extends ItemizedOverlay<OverlayItem> {
	Map<NodePair, Route> routesMap;
	List<Node> nodes;
	Paint mPaint;	
	Drawable defaultMarker;
	
	public MazeOverlay(Drawable defaultMarker, NodesAndRoutes nodesAndRoutes) {
		super(boundCenterBottom(defaultMarker));
		
		/*
		this.defaultMarker.setVisible(false, true);
		
		this.defaultMarker = defaultMarker;
		*/
		this.nodes = nodesAndRoutes.nodes;
		this.routesMap = nodesAndRoutes.routesMap;
		System.out.println("MazeOverlay: nodes size" + this.nodes.size());
		System.out.println("MazeOverlay: routemap size" + this.routesMap.size());
		
        this.mPaint = new Paint();
        this.mPaint.setDither(true);
        this.mPaint.setColor(Color.RED);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(2);
                
		this.populate();
	}
	
	protected void assertTrue(boolean t, String s) {
		if (!t)
			throw new RuntimeException (s);
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		
		OverlayItem item = new OverlayItem(this.nodes.get(i).latlng, "", "");
		
		/*
		Drawable d = boundCenterBottom(this.defaultMarker);
		d.setVisible(false, false);
		item.setMarker(d);
		*/
		
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
        
        Collection<Route> routes = routesMap.values();
        for (Route route : routes) {
        	List<GeoPoint> geoPoints = route.getGeoPoints();
        	GeoPoint lastPt = geoPoints.get(0);
        	for (int i = 1; i < geoPoints.size(); i++) {
        		GeoPoint curPt = geoPoints.get(i);
                Point p1 = new Point();
                Point p2 = new Point();

                Path path = new Path();
                Projection projection = mapv.getProjection();
                projection.toPixels(lastPt, p1);
                projection.toPixels(curPt, p2);

                path.moveTo(p2.x, p2.y);
                path.lineTo(p1.x,p1.y);
        		canvas.drawPath(path, this.mPaint);
        		
        		lastPt = curPt;
        	}
        }
        mapv.invalidate();
    }	
}
