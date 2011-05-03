package com.ghostrun.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ghostrun.driving.Node;
import com.ghostrun.util.RandUtils;
import com.google.android.maps.GeoPoint;

public class MazeGraph {
	
	public MazeGraph(List<Node> nodes) {
		Map<Integer, MazeGraphPoint> nodeMap = new HashMap<Integer, MazeGraphPoint>();
		for (Node node : nodes) {
			nodeMap.put(node.id, addPoint(node.latlng));
			//System.out.println("added: " + node.id);
		}
		
		for (Node node : nodes) {
			MazeGraphPoint p1 = nodeMap.get(node.id);
			for (Node n : node.neighbors) {
				MazeGraphPoint p2 = nodeMap.get(n.id);
				//System.out.println(p1 + " " + p2);
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
        int nPoints = points.size();
        if (nPoints > 0) {
            return points.get(RandUtils.nextInt(nPoints));
        } else {
            return null;
        }
    }

    private ArrayList<MazeGraphPoint> points = new ArrayList<MazeGraphPoint>();

}
