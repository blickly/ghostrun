package com.ghostrun.driving;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;

public class Node {

	public int id;
	public GeoPoint latlng;
	public List<Node> neighbors;
	
	public Node(GeoPoint latlng, int id) {
		this.id = id;
		
		this.latlng = latlng;
		this.neighbors = new ArrayList<Node>();
	}
	
	public Node clone() {
		return new Node(this.latlng, this.id);
	}
	
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node n = (Node)o;
			return n.id == this.id;
		}
		return false;
	}
	
	public int hashCode() {
		return this.id;
	}
	
	public void addNeighbor(Node n) {
		if (!this.neighbors.contains(n))
			this.neighbors.add(n);
	}
	public void removeNeighbor(Node n) {
		this.neighbors.remove(n);
	}
	
	//json helper
	private Integer[] getJsonNeighbors() {
		Integer[] array = new Integer[this.neighbors.size()];
		int index = 0;
		for (Node n : this.neighbors) {
			array[index++] = n.id;
		}
		return array;
	}
	public Map<Object, Object> toJson() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("id", new Integer(id));
		map.put("lat", new Integer(latlng.getLatitudeE6()));
		map.put("lng", new Integer(latlng.getLongitudeE6()));
		map.put("neighbors", getJsonNeighbors());
		return map;
	}
}
