package com.ghostrun.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ghostrun.R;

public class HomeView extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.homeview);
        Button play = (Button)findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeView.this, FileBrowserView.class);
                startActivity(i);
            }
        });
        
        Button mazes = (Button)findViewById(R.id.mazes);
        mazes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(HomeView.this, MapEditor.class);
                startActivity(i);
            }
        });
        
        Button scoregraph = (Button)findViewById(R.id.scoregraph);
        scoregraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i= new Intent(HomeView.this, FileBrowserView.class);
                i.putExtra("graph", true);
                startActivity(i);
            }
        });
        
        Button guide = (Button)findViewById(R.id.guide);
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i= new Intent(HomeView.this, GuideView.class);
                startActivity(i);
            }
        });
    }
}