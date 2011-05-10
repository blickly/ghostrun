package com.ghostrun.overlays;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.ghostrun.driving.Node;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MazeOverlay extends Overlay {
    List<Node> nodes;
    Paint mPaint;	

    public MazeOverlay(List<Node> nodes) {
        super();

        this.nodes = nodes;
        System.out.println("MazeOverlay: number of nodes " + this.nodes.size());

        this.mPaint = new Paint();
        this.mPaint.setDither(true);
        this.mPaint.setColor(Color.RED);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(2); 
    }

    @Override
    public void draw(Canvas canvas, MapView mapv, boolean shadow){
        super.draw(canvas, mapv, shadow);

        // TODO: draw between point. there is no routes anymore...
        Set<Node> doneNodes = new HashSet<Node>();
        for (Node node : this.nodes) {
            doneNodes.add(node);

            for (Node n : node.neighbors) {
                //if (!doneNodes.contains(n)) {
                /*
        		 	if (node.latlng.equals(n.latlng))
        		 		System.out.println("neighbor with itself...." + node.latlng);
                 */
                Point p1 = new Point();
                Point p2 = new Point();

                Path path = new Path();
                Projection projection = mapv.getProjection();
                projection.toPixels(node.latlng, p1);
                projection.toPixels(n.latlng, p2);

                path.moveTo(p2.x, p2.y);
                path.lineTo(p1.x, p1.y);
                canvas.drawPath(path, this.mPaint);
                //}
            }
        }
        mapv.invalidate();
    }	
}
