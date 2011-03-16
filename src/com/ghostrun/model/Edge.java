package com.ghostrun.model;

public class Edge {
    private Vertex src;
    private Vertex dest;
    
    public Edge(Vertex src, Vertex dest) {
        this.src = src;
        this.dest = dest;
    }
    
    public Vertex getSourceVertex() {
        return src;
    }
    
    public Vertex getDstVertex() {
        return dest;
    }
    
    public String toString(){
        return "Edge from vertex " + src.getLabel() + " to vertex " + dest.getLabel();
    }
    
    
}