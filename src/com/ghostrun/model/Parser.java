package com.ghostrun.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;

import com.google.android.maps.GeoPoint;

public class Parser {
    private Vector<Double> lats;
    private Vector<Double> lons;
    private Vector<Integer> src;
    private Vector<Integer> dest;
    
    public Parser() {
        lats = new Vector<Double>();
        lons = new Vector<Double>();
        src = new Vector<Integer>();
        dest = new Vector<Integer>();
    }
    
    /**
     * Construct a MazeGraph object from an input xml file.
     * @param fileName
     */
    public MazeGraph parse(InputStream instream) {
        MazeGraph g = new MazeGraph();
        
        try {
            //BufferedReader in = new BufferedReader(new FileReader(fileName));
            BufferedReader in = new BufferedReader(new InputStreamReader(instream));
            Scanner scanner = new Scanner(in);
            String s, num;
            
            while (scanner.hasNext()) {
                s=scanner.next();
                if (s.contains("lat")) {
                    num = s.substring("lat=".length()+1,s.length()-1);
                    //System.out.println("lat "+num);
                    lats.add(Double.parseDouble(num));
                } else if (s.contains("lon")) {
                    num = s.substring("lon=".length()+1,s.indexOf("/")-1);
                    //System.out.println("lon "+num);
                    lons.add(Double.parseDouble(num));
                } else if (s.contains("source")) {
                    num = s.substring("source=".length()+1,s.length()-1);
                    //System.out.println("source "+num);
                    src.add(Integer.parseInt(num));
                } else if (s.contains("dest")) {
                    num = s.substring("dest=".length()+1,s.indexOf("/")-1);
                    //System.out.println("dest "+num);
                    dest.add(Integer.parseInt(num));
                }
            }
            
            for (int i=0;i<lats.size();++i) {
                g.addVertex(new GeoPoint((int)(lats.get(i).doubleValue()*1E6),
                                         (int)(lons.get(i).doubleValue()*1E6)));
            }
            
            for (int i=0;i<src.size();++i) {
                Vertex srcVertex = g.getVertex(src.get(i));
                Vertex dstVertex = g.getVertex(dest.get(i));
                g.addEdge(srcVertex,dstVertex);
            }
            
            in.close();
            instream.close();
        } catch (IOException e) {
            System.out.println("Exception from reading file "+e.getMessage());
        }
        return g;
    }
}