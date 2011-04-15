package com.ghostrun.driving;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ghostrun.driving.impl.RouteImpl;
import com.google.android.maps.GeoPoint;

public class NodeFactory {
	public class NodesAndRoutes {
		public List<Node> nodes;
		public Map<NodePair, Route> routesMap;
		NodesAndRoutes(List<Node> nodes, Map<NodePair, Route> routesMap) {
			this.nodes = nodes;
			this.routesMap = routesMap;
		}
	}
	public GeoPoint getGeoPointFromMap(Map<Object, Object> map) {
		return new GeoPoint(((Long)map.get("lat")).intValue(), 
				((Long)map.get("lng")).intValue());
	}
	public NodesAndRoutes fromMap(String serialized) {
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){
		    public List creatArrayContainer() {
		      return new ArrayList();
		    }

		    public Map createObjectContainer() {
		      return new HashMap();
		    }
		                        
		  };
		Map map = null;
		try {
			map = (Map)parser.parse(serialized, containerFactory);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Node> nodes = new ArrayList<Node>();
		Map<NodePair, Route> routesMap = new HashMap<NodePair, Route>();
		
		List<Map> nodesList = (List<Map>) map.get("nodes");
		System.out.println("nodes: " + nodesList.size());
		Object[] jsonNodes = (Object[]) (nodesList.toArray());
		Map<Integer, Node> nodesMap = new HashMap<Integer, Node>();
		for (int i = 0; i < jsonNodes.length; i++) {
			Map<Object, Object> m = (Map)jsonNodes[i];
			int id = ((Long) m.get("id")).intValue();
			Node n = new Node(getGeoPointFromMap(m), id);
			nodes.add(n);
			nodesMap.put(id, n);
		}
		
		for (int i = 0; i < jsonNodes.length; i++) {
			Node n = nodes.get(i);
			Object[] neighbor = (Object[]) ((List)((Map) jsonNodes[i]).get("neighbors")).toArray();
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