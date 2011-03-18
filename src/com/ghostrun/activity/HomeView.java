package com.ghostrun.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ghostrun.R;
import com.ghostrun.model.MazeGraph;
import com.ghostrun.model.MazeGraphPoint;
import com.ghostrun.model.Parser;

public class HomeView extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.homeview);
        Button play=(Button)findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Intent i = new Intent(HomeView.this, GameMapView.class);
                startActivity(i);
            }
        });
        
        Parser p = new Parser();
        MazeGraph g=new MazeGraph();
        try {
            g=p.parse(getAssets().open("maze.xml"));
            
            /*ArrayList<MazeGraphPoint> array = (ArrayList<MazeGraphPoint>)g.vertices();
            for (MazeGraphPoint v:array) {
                System.out.println(v);
                ArrayList<MazeGraphPoint> neighbor = v.getNeighbors();
                System.out.println("Neighbors:");
                for (MazeGraphPoint nei : neighbor) {
                    System.out.println("   "+nei);
                }
            }*/
            
            /*Vector<Edge> e = (Vector<Edge>)g.edges();
            for (Edge edge:e) {
                System.out.println(edge);
            }*/
            
        } catch (Exception e){
            System.out.println("Exception found HomeView.java: "+e.getMessage());
        }
    }
}