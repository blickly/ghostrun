package com.ghostrun.driving.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ghostrun.driving.Placemark;
import com.ghostrun.driving.Route;
import com.google.android.maps.GeoPoint;

public class RouteImpl implements Route
{
	private String totalDistance;
	private List<GeoPoint> geoPoints;
	private List<Placemark> placemarks;

	public RouteImpl() {
		
	}
	
	public RouteImpl(Map<Object, Object> map) {
		super();
		
		this.setTotalDistance((String) map.get("distance"));
		@SuppressWarnings("unchecked")
		List<Object> tmpPoints = (List<Object>) map.get("geopoints");
		Object[] geopoints = (Object[]) tmpPoints.toArray();
		for (int i = 0; i < geopoints.length; i ++) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> pt = (Map<Object, Object>)geopoints[i];
			this.addGeoPoint(new GeoPoint(((Long)pt.get("lat")).intValue(), 
					((Long)pt.get("lng")).intValue()));
		}
	}
	
	public Map<Object, Object> toJson() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("distance", this.totalDistance);
		@SuppressWarnings("unchecked")
		Map<Object, Object>[] geopoints = 
			new HashMap[this.geoPoints.size()];
		int index = 0;
		for (GeoPoint pt : this.geoPoints) {
			Map<Object, Object> m = new HashMap<Object, Object>();
			m.put("lat", new Integer(pt.getLatitudeE6()));
			m.put("lng", new Integer(pt.getLongitudeE6()));
			geopoints[index++] = m;
		}
		map.put("geopoints", geopoints);
		return map;
	}
	
	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}

	public String getTotalDistance() {
		return totalDistance;
	}
	
	public void setGeoPoints(List<GeoPoint> geoPoints) {
		this.geoPoints = geoPoints;
	}

	public List<GeoPoint> getGeoPoints() {
		return geoPoints;
	}
	
	public void addGeoPoint (GeoPoint point)
	{
		if (geoPoints == null) {
			geoPoints = new ArrayList<GeoPoint>();
		}
		geoPoints.add(point);
	}

	public void setPlacemarks(List<Placemark> placemarks) {
		this.placemarks = placemarks;
	}

	public List<Placemark> getPlacemarks() {
		return placemarks;
	}
	
	public void addPlacemark (Placemark mark)
	{
		if (placemarks == null) {
			placemarks = new ArrayList<Placemark>();
		}
		placemarks.add(mark);
	}
}
