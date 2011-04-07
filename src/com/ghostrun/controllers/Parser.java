package com.ghostrun.controllers;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.MazeGraphPoint;
import com.google.android.maps.GeoPoint;

public class Parser {
    private MazeGraph graph;
    private HashMap<Integer, MazeGraphPoint> points;

    private void initialize() {
        graph = new MazeGraph();
        points = new HashMap<Integer, MazeGraphPoint>();
    }

    /**
     *  Construct a MazeGraph object from an input xml file.
     *  @param instream Input stream to read from
     *  @return The completed MazeGraph
     */
    public MazeGraph parse(InputStream instream) {
        initialize();
        try {
            DocumentBuilder doc_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = doc_builder.parse(instream);

            NodeList nodes = doc.getElementsByTagName("Node");
            if (nodes != null) {
                for (int i=0; i < nodes.getLength(); ++i) {
                    parseNode(nodes.item(i));
                }
            }

            NodeList edges = doc.getElementsByTagName("Edge");
            if (edges != null) {
                for (int i=0; i < edges.getLength(); ++i) {
                    parseEdge(edges.item(i));
                }
            }
        } catch (Exception e) {
            System.out.print("Exception found in Parser.java: "+e.getMessage());
        }
        return graph;
    }

    private void addNode(int id, GeoPoint location) {
        points.put(id, graph.addPoint(location));
    }

    private void addEdge(int node1, int node2) {
        graph.addEdge(points.get(node1), points.get(node2));
    }

    private void parseNode(Node node) {
        Node id = node.getAttributes().getNamedItem("id");
        Node latLng = node.getFirstChild();
        addNode(Integer.parseInt(id.getNodeValue()), parseLatLng(latLng));
    }

    private GeoPoint parseLatLng(Node latLngNode) {
        Node lat = latLngNode.getAttributes().getNamedItem("lat");
        Node lng = latLngNode.getAttributes().getNamedItem("lon");
        return new GeoPoint(
                (int)(Double.parseDouble(lat.getNodeValue())*1E6),
                (int)(Double.parseDouble(lng.getNodeValue())*1E6));
    }

    private void parseEdge(Node edge) {
        Node src = edge.getAttributes().getNamedItem("source");
        Node dst = edge.getAttributes().getNamedItem("dest");
        addEdge(Integer.parseInt(src.getNodeValue()),
                Integer.parseInt(dst.getNodeValue()));
    }
}