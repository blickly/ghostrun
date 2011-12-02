package com.ghostrun.driving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

public class NodeFactory {
	private static ContainerFactory containerFactory = new ContainerFactory(){
		public List<Object> creatArrayContainer() {
			return new ArrayList<Object>();
        }

        public Map<String, Object> createObjectContainer() {
            return new HashMap<String, Object>();
        }
    };
    
    private static JSONParser parser = new JSONParser(); 

    public static List<Node> getNodesFromJson(File serializedFile) {
    	try {
    		BufferedReader reader =
    			new BufferedReader(new FileReader(serializedFile));
    		String s = reader.readLine();
    		reader.close();
    		
    		return getNodesFromJson(s);
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
	@SuppressWarnings("unchecked")
	public static List<Node> getNodesFromJson(String serialized) {
		List<Node> nodes = new ArrayList<Node>();
		List<Object> json;
		try {
			json = (List<Object>)parser.parse(serialized, containerFactory);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		Map<Integer, Node> idToNode = new HashMap<Integer, Node>();
		for (Object n : json) {
			Node node = getNodeFromJson((Map<String, Object>)n);
			idToNode.put(node.id, node);
			nodes.add(node);
		}
		
		for (Object n : json) {
			addNeighbors((Map<String, Object>)n, idToNode);
		}
		
		return nodes;
	}
	
	@SuppressWarnings("unchecked")
	private static void addNeighbors(Map<String, Object> nodeMap, 
									 Map<Integer, Node> idToNode) {
		
		Node n = idToNode.get(((Long)nodeMap.get("id")).intValue());
		for (Object neighbor : (List<Object>)nodeMap.get("neighbors")) {
			n.addNeighbor(idToNode.get(((Long)neighbor).intValue()));
		}		
	}
	
	private static Node getNodeFromJson(Map<String, Object> nodeMap) {
		Node n = new Node(new GeoPoint(((Long)nodeMap.get("lat")).intValue(),
									   ((Long)nodeMap.get("lng")).intValue()),
									   ((Long)nodeMap.get("id")).intValue(), 
									   (Boolean)nodeMap.get("user_added"));
									   
		return n;									   
	}
	
	@SuppressWarnings("unchecked")
	public static String getJsonFromNodes(List<Node> nodes) {
		Map<Object, Object>[] jsonNodes = new HashMap[nodes.size()];
		for (int i = 0; i < nodes.size(); i ++) {
			jsonNodes[i] = nodes.get(i).toJson();
		}
		return new Gson().toJson(jsonNodes);
	}
}
