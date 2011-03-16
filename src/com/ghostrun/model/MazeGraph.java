package com.ghostrun.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.google.android.maps.GeoPoint;

public class MazeGraph {
   private Vector<Vertex> vertices;
   private Vector<Edge> edges;
   private int counter; // give each vertex a unique id.
   private HashMap<Integer,Vertex> h; // allow fast retrieval of a Vertex based on its label.
   
   public MazeGraph() {
       vertices = new Vector<Vertex>();
       edges = new Vector<Edge>();
       h = new HashMap<Integer,Vertex>();
       counter = 0;
   }

   public Vertex addVertex(GeoPoint point) {
       Vertex v = new Vertex(counter,point);
       h.put(counter, v);
       vertices.add(v);
       ++counter;
       return v;
   }
    
   public void addEdge(Vertex p1, Vertex p2) {
       Edge e = new Edge(p1,p2);
       p1.addNeighbor(p2);
       p2.addNeighbor(p1);
       edges.add(e);
   }
    
   public Vertex getVertex(int label) {
       return h.get(label);
   }
   
   public Object vertices() {
       return vertices.clone();
   }
   
   public Object edges(){
       return edges.clone();
   }

}
