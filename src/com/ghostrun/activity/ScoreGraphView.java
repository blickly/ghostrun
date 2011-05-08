package com.ghostrun.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.ghostrun.R;
 
public class ScoreGraphView extends Activity
{
    private XYPlot scoreXYPlot;
 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoregraphview);
        
        // Stop the current activity and return to the previous view.
        Button logobutton=(Button)findViewById(R.id.scoregraphview_paclogo);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScoreGraphView.this, HomeView.class);
                startActivity(i);
            }
        });
 
        scoreXYPlot = (XYPlot) findViewById(R.id.scoreXYPlot);
        scoreXYPlot.setTitle("Scores on Maze ...");
        Number[] scores = {0,0,0,0,0};
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            String filename = b.getString("graphname");
            scoreXYPlot.setTitle("Scores on Maze "+filename.substring(filename.indexOf("card/")+"card/".length()));
            System.out.println(filename);
            if (filename!=null && filename.contains(".score")) {
                int i=0;
                try {
                    File root = Environment.getExternalStorageDirectory();
                    if (root.canRead()){
                        File inputFile = new File(filename);
                        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
                        String s=in.readLine();
                        while (s!=null) {
                            scores[i]=Integer.parseInt(s);
                            System.out.println(scores[i]);
                            ++i;
                            s=in.readLine();
                        }
                        in.close();
                    }
                } catch (IOException e) {
                    Log.e("Oops","Read file error " + e.getMessage());
                }
            }
        }
        
        Number[] games = {1, 2, 3, 4, 5 };
        
        scoreXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        scoreXYPlot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        scoreXYPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        scoreXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        scoreXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
        scoreXYPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        scoreXYPlot.getBorderPaint().setStrokeWidth(1);
        scoreXYPlot.getBorderPaint().setAntiAlias(false);
        scoreXYPlot.getBorderPaint().setColor(Color.WHITE);
 
        // create our series from our array of nums:
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(games),
                Arrays.asList(scores),
                "Score on game i");

        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
        formatter.setFillPaint(lineFill);
        scoreXYPlot.getGraphWidget().setPaddingRight(2);
        scoreXYPlot.addSeries(series2, formatter);
 
        // draw a domain tick for each game:
        scoreXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, games.length);
 
        // customize our domain/range labels
        scoreXYPlot.setDomainLabel("Game");
        scoreXYPlot.setRangeLabel("Score");
 
        // get rid of decimal points in our range and domain labels:
        scoreXYPlot.setRangeValueFormat(new DecimalFormat("0"));
        scoreXYPlot.setDomainValueFormat(new DecimalFormat("0"));
 
        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        scoreXYPlot.disableAllMarkup();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Select Maze.score");
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i= new Intent(ScoreGraphView.this, FileBrowserView.class);
                i.putExtra("graph", true);
                startActivity(i);
                return true;
            }
        });
        return true;
    }
}