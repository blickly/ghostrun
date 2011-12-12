package com.ghostrun.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ghostrun.R;

public class HomeView extends Activity {
	
	public static final String SERVER = "http://pacmanplusplus.appspot.com";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.homeview);
        
        ((Button)findViewById(R.id.play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeView.this, GameMenu.class));
            }
        });
        
        ((Button)findViewById(R.id.mazes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeView.this, MapEditor.class));
            }
        });
        
        ((Button)findViewById(R.id.scoregraph)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeView.this, FileBrowserView.class);
                i.putExtra("graph", true);
                startActivity(i);
            }
        });
        
        ((Button)findViewById(R.id.guide)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeView.this, GuideView.class));
            }
        });
    }
}