package com.ghostrun.driving;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;

/**
 * @author Darren
 * Node class representing individual points on mapview.
 */
public class Node {

	public final int id;
	public final GeoPoint latlng;
	public List<Node> neighbors;
	public boolean user_added;
	
	public Node(GeoPoint latlng, int id, boolean user_added) {
		this.id = id;
		this.latlng = latlng;
		this.user_added = user_added;
		this.neighbors = new ArrayList<Node>();
	}
	
	public Node clone() {
		return new Node(this.latlng, this.id, this.user_added);
	}
	
	public boolean equals(Object o) {
		return (o instanceof Node) && ((Node)o).id == this.id;
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
		map.put("id", new Integer(this.id));
		map.put("user_added", new Boolean(this.user_added));
		map.put("lat", new Integer(this.latlng.getLatitudeE6()));
		map.put("lng", new Integer(this.latlng.getLongitudeE6()));
		map.put("neighbors", getJsonNeighbors());
		return map;
	}
}