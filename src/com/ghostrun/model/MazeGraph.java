package com.ghostrun.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ghostrun.driving.Node;
import com.google.android.maps.GeoPoint;

public class MazeGraph {
	
	public MazeGraph() {
		
	}
	
	public MazeGraph(List<Node> nodes) {
		Map<Integer, MazeGraphPoint> nodeMap = new HashMap<Integer, MazeGraphPoint>();
		for (Node node : nodes) {
			nodeMap.put(node.id, addPoint(node.latlng));
			System.out.println("added: " + node.id);
		}
		
		for (Node node : nodes) {
			MazeGraphPoint p1 = nodeMap.get(node.id);
			for (Node n : node.neighbors) {
				MazeGraphPoint p2 = nodeMap.get(n.id);
				System.out.println(p1 + " " + p2);
				addEdge(p1, p2);
			}
		}
	}

    public MazeGraphPoint addPoint(GeoPoint point) {
        MazeGraphPoint mgPoint = new MazeGraphPoint(point);
        points.add(mgPoint);
        return mgPoint;
    }
    
    public void addEdge(MazeGraphPoint p1, MazeGraphPoint p2) {
        p1.addConncetionTo(p2);
        p2.addConncetionTo(p1);
    }

    public MazeGraphPoint getRandomPoint() {
        Random rand = new Random();
        int nPoints = points.size();
        if (nPoints > 0) {
            return points.get(rand.nextInt(nPoints));
        } else {
            return null;
        }
    }

    public static MazeGraph createSimpleMap() {
        MazeGraph m = new MazeGraph();

        // Campanile
        MazeGraphPoint campanilePoint = m.addPoint(new GeoPoint(37871944, -122257778));
        // NE corner of campus
        MazeGraphPoint neCornerPoint = m.addPoint(new GeoPoint(37875522,-122256825));
        // NW corner of campus
        MazeGraphPoint nwCornerPoint = m.addPoint(new GeoPoint(37875522,-122256825));

        // Edges
        m.addEdge(neCornerPoint, nwCornerPoint);
        m.addEdge(neCornerPoint, campanilePoint);
        
        return m;
    }

    private ArrayList<MazeGraphPoint> points = new ArrayList<MazeGraphPoint>();

}
