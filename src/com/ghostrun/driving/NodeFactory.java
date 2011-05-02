package com.ghostrun.driving;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;

import com.ghostrun.driving.impl.RouteImpl;
import com.google.android.maps.GeoPoint;

public class NodeFactory {
	public class NodesAndRoutes {
		public List<Node> nodes;
		public Map<NodePair, Route> routesMap;
		public NodesAndRoutes(List<Node> nodes, Map<NodePair, Route> routesMap) {
			this.nodes = nodes;
			this.routesMap = routesMap;
		}
		
		public List<Node> toNodes() {
			Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
			List<Node> result = new ArrayList<Node>();
			
			int max_id = 0;
			for (Node n : nodes) {
				nodeMap.put(n.id, n);
				max_id = Math.max(n.id, max_id);
			}
			for (Map.Entry<NodePair, Route> entry : routesMap.entrySet()) {
				NodePair p = entry.getKey();
				Route r = entry.getValue();
				
				Node node1 = nodeMap.get(p.id1);
				Node node2 = nodeMap.get(p.id2);
				
				Node lastNode = node1;
				List<GeoPoint> lst = ((RouteImpl)r).getGeoPoints();
				lst.remove(0);
				lst.remove(lst.size()-1);
				
				if (lst.size() > 0) {
					node1.removeNeighbor(node2);
					node2.removeNeighbor(node1);
				}
				
				if (!result.contains(node1)) {
					result.add(node1);
				}
				
				if (!result.contains(node2)) {
					result.add(node2);
				}
				
				for (GeoPoint pt : lst) {
					// combine pts into nodes
					Node node = new Node(pt, max_id++);
					lastNode.addNeighbor(node);
					node.addNeighbor(lastNode);
					lastNode = node;
					
					result.add(node);
				}
				lastNode.addNeighbor(node2);
				node2.addNeighbor(lastNode);
			}
			return result;
		}
	}
	public GeoPoint getGeoPointFromMap(Map<String, Object> map) {
		return new GeoPoint(((Long)map.get("lat")).intValue(), 
				((Long)map.get("lng")).intValue());
	}
	
	@SuppressWarnings("unchecked")
	public static List<Node> fromStaticMap(String serialized) {
		
		List<Node> results = new ArrayList<Node>();
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){
		    public List<Map<String, Object>> creatArrayContainer() {
		      return new ArrayList<Map<String, Object>>();
		    }

		    public Map<String, Object> createObjectContainer() {
		      return new HashMap<String, Object>();
		    }
		                        
		  };
		Map<String, Object> map = null;
		try {
			map = (Map<String, Object>)parser.parse(serialized, containerFactory);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		Collection<Object> values = map.values();
		for (Object val : values) {
			Map<String, Object> tmp = (HashMap<String, Object>)val;
			System.out.println("ERROR CAST: " + (Double)tmp.get("lat"));
			Node n = new Node(new GeoPoint(
						(int)((Double)tmp.get("lat") * 1e6), 
						(int)((Double)tmp.get("lng") * 1e6)),
						((Long)tmp.get("id")).intValue());
			nodeMap.put(((Long)tmp.get("id")).intValue(), n);
			
			results.add(n);
		}
		
		for (Object val : values) {
			Map<String, Object> tmp = (HashMap<String, Object>)val;
			Node n = nodeMap.get(((Long) tmp.get("id")).intValue());
			for (Object neighborId : (List<Integer>)tmp.get("neighbors")) {
				Node tmpNode = nodeMap.get(((Long)neighborId).intValue());
				n.addNeighbor(tmpNode);				
			}
		}
		return results;
	}
	
	public static List<Node> generateRandomMap(String serialized) {
		List<Node> nodes = fromStaticMap(serialized);
		List<Node> results = new ArrayList<Node>();
		Set<Node> doneNodes = new HashSet<Node>();
		final double randomTh = 0.7;
		Random random = new Random();
		
		Queue<Node> queue = new LinkedList<Node>();
		Node n = nodes.get(random.nextInt(nodes.size()));
		queue.offer(n);
		doneNodes.add(n);
		while (queue.size() > 0) {
			Node curNode = queue.poll();
			Node newNode = curNode.clone();
			
			results.add(newNode);
			
			for (Node neighbor : n.neighbors) {
				if (!doneNodes.contains(neighbor)) {
					double r = random.nextDouble();
					if (r < randomTh) {
						queue.offer(neighbor);
						doneNodes.add(neighbor);
						newNode.addNeighbor(neighbor);
					}
				}
			}
		}
		
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public NodesAndRoutes fromMap(String serialized) {
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){
		    public List<Map<String, Object>> creatArrayContainer() {
		      return new ArrayList<Map<String, Object>>();
		    }

		    public Map<String, Object> createObjectContainer() {
		      return new HashMap<String, Object>();
		    }
		                        
		  };
		Map<String, Object> map = null;
		try {
			map = (Map<String, Object>)parser.parse(serialized, containerFactory);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Node> nodes = new ArrayList<Node>();
		Map<NodePair, Route> routesMap = new HashMap<NodePair, Route>();
		
		List<Map<String, Object>> nodesList = 
			(List<Map<String, Object>>) map.get("nodes");
		System.out.println("nodes: " + nodesList.size());
		Object[] jsonNodes = (Object[]) (nodesList.toArray());
		Map<Integer, Node> nodesMap = new HashMap<Integer, Node>();
		for (int i = 0; i < jsonNodes.length; i++) {
			Map<String, Object> m = (Map<String, Object>)jsonNodes[i];
			int id = ((Long) m.get("id")).intValue();
			Node n = new Node(getGeoPointFromMap(m), id);
			nodes.add(n);
			nodesMap.put(id, n);
		}
		
		for (int i = 0; i < jsonNodes.length; i++) {
			Node n = nodes.get(i);
			Object[] neighbor = (Object[]) ((List<Map<String, Object>>)
					((Map<String, Object>) jsonNodes[i]).get("neighbors")).toArray();
			for (int j = 0; j < neighbor.length; j++) {
				n.addNeighbor(nodesMap.get(((Long)neighbor[j]).intValue()));
			}
		}
		
		Map<Object, Object> jsonRoutes = (Map<Object, Object>) map.get("routes");
		Set<Entry<Object,Object>> entries = jsonRoutes.entrySet();
		for (Entry<Object, Object> entry : entries) {
			String key = (String) entry.getKey();
			int i = key.indexOf(" ");
			int id1 = new Integer(key.substring(0, i));
			int id2 = new Integer(key.substring(i+1));
			
			routesMap.put(new NodePair(id1, id2), 
					new RouteImpl((Map<Object, Object>) entry.getValue()));
		}
		return new NodesAndRoutes(nodes, routesMap);
	}
}