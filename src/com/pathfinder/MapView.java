package com.pathfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.pathfinder.graph.Graph;
import com.pathfinder.graph.Loc;
import com.pathfinder.graph.Vertex;
import com.pathfinder.graph.exception.GraphException;

public class MapView extends View {
	
	

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	Bitmap bmp;
	List<Vertex> points = new ArrayList<Vertex>();
	List<Vertex> graph_points = new ArrayList<Vertex>();
	Paint paint = new Paint();
	Vertex dst = new Vertex();

	@Override
	protected void onDraw(Canvas canvas) {
		if (bmp != null)
			canvas.drawBitmap(bmp, null, new Rect(0, 0, 480, 800), paint);
		else
			canvas.drawColor(Color.CYAN);

		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(8);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setDither(true);
		paint.setAntiAlias(true);
		
		for(int i = 0; i < points.size()-1; i++){
			Loc cur = points.get(i).getLoc(), next = points.get(i+1).getLoc();
			float x1 = (float) cur.getLatitude(), y1 = (float) cur.getLongitude(),
					x2 = (float) next.getLatitude(), y2 = (float) next.getLongitude();
			canvas.drawPoint(x1, y1, paint);
			canvas.drawLine(x1, y1, x2, y2, paint);
			
			cur = graph_points.get(i).getLoc();
			next = graph_points.get(i+1).getLoc();
			x1 = (float) cur.getLatitude(); 
			y1 = (float) cur.getLongitude();
			x2 = (float) next.getLatitude(); 
			y2 = (float) next.getLongitude();
			canvas.drawPoint(x1, y1, paint);
			canvas.drawLine(x1, y1, x2, y2, paint);
		}

//		float[] pts = new float[points.size()];
//		for (int i = 0; i < points.size(); i++) {
//			pts[i] = points.get(i);
//		}
//
//		canvas.drawPoints(pts, paint);
//		canvas.drawLines(pts, 0, points.size(), paint);
//		if (points.size() >= 2)
//			canvas.drawLines(pts, 2, points.size(), paint);

		super.onDraw(canvas);
	}

	public void loadImage(String filename) {
		Options options = new Options();
		options.inSampleSize = 4;
		bmp = BitmapFactory.decodeFile(filename, options);
		this.invalidate();
	}

	public void placePoint(float x, float y) {
		Log.d("MapView", x + " " + y);
		points.add(new Vertex(x, y));
		//		points.add(x);
		//		points.add(y);
	}
	
	public void drawGraph(Graph g){
		graph_points.clear();
		Vertex start = g.getVertices().iterator().next();
		if(start == null){
			return;
		} else {
			for(Vertex v : g.getVertices()){
				v.setVisited(false);
			}
			start.setVisited(true);
			Stack<Vertex> S = new Stack<Vertex>();
			S.push(start);
			while(!S.empty()){
				Vertex v = S.peek();
				graph_points.add(v);
				for(Vertex u : v.getAdjacent()){
					graph_points.add(u);
					if(!u.getVisited()){
						boolean explored = true;
						for(Vertex w : u.getAdjacent()){
							if(!w.getVisited()){
								explored = false;
								break;
							}
						}
						if(explored == false){
							S.push(u);
							S.push(u); //gets around always popping (so hacky)
							break;
						}
						u.setVisited(true);
					}
					graph_points.add(v);
				}
				S.pop();
			}
		}
		this.invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(((MainActivity) this.getContext()).calibrate == true){
				Log.d("MapView", "Calibrate Start");
				((MainActivity) this.getContext()).calibrating = true;
				((MainActivity) this.getContext()).calibrate = false;
				placePoint(event.getX(), event.getY());
				this.invalidate();
				Toast.makeText(this.getContext(), "Please select a location and walk to it", Toast.LENGTH_SHORT).show();
				break;
			} else if (((MainActivity) this.getContext()).calibrating == true) {
				Log.d("MapView", "Calibrate End");
				((MainActivity) this.getContext()).calibrating = false;
				placePoint(event.getX(), event.getY());
				this.invalidate();
				((MainActivity) this.getContext()).avgStride = 
						points.get(0).getLoc().computeDist(points.get(1).getLoc()) / ((MainActivity) this.getContext()).numSteps;
				points.clear();
				placePoint(event.getX(), event.getY());
				this.invalidate();
				break;
			} else {
				Log.d("MapView", event.toString());
				placePoint(event.getX(), event.getY());
				dst = ((MainActivity) this.getContext()).graph.closestVertexToPath(
						new Loc((double) event.getX(), (double) event.getY())).getLeft();
				try{
					((MainActivity) this.getContext()).graph.computePathToGraph(points.get(0).getLoc(), dst);
					//plot this
				} catch (GraphException g){
					Toast.makeText(this.getContext(), "Invalid location", Toast.LENGTH_SHORT).show();
				}
				this.invalidate();
				break;
			}
		}
		return super.onTouchEvent(event);
	}
}
