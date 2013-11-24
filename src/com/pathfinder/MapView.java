package com.pathfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
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

	Bitmap bmp;
	PointF start, flag;
	List<Vertex> points = new ArrayList<Vertex>();
	List<Vertex> graph_points = new ArrayList<Vertex>();
	Iterable<Vertex> sp; 
	Paint paint = new Paint();
	Vertex dst = new Vertex();

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setColor(Color.CYAN);
		paint.setStrokeWidth(8);
		paint.setStyle(Style.STROKE);
		paint.setDither(true);
		paint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (bmp != null)
//			canvas.drawBitmap(bmp, 0, 0, null);
			canvas.drawBitmap(bmp, null, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), null);
		else
			canvas.drawColor(Color.GRAY);
		
		paint.setColor(Color.YELLOW);
		drawEdges(((MainActivity) this.getContext()).graph, canvas);

		paint.setColor(Color.CYAN);
		if (start != null)
			canvas.drawCircle(start.x, start.y, 16, paint);
		if (flag != null)
			canvas.drawCircle(flag.x, flag.y, 16, paint);
		
		for (int i = 0; i < points.size() - 1; i++) {
			Loc cur = points.get(i).getLoc(), next = points.get(i + 1).getLoc();
			float x1 = (float) cur.getLatitude(), y1 = (float) cur.getLongitude(), x2 = (float) next.getLatitude(), y2 = (float) next.getLongitude();
			canvas.drawPoint(x1, y1, paint);
			canvas.drawLine(x1, y1, x2, y2, paint);
		}
		
//		for(int i = 0; i < graph_points.size()-1; i++){
//			Loc cur = graph_points.get(i).getLoc();
//			Loc next = graph_points.get(i+1).getLoc();
//			float x1 = (float) cur.getLatitude(); 
//			float y1 = (float) cur.getLongitude();
//			float x2 = (float) next.getLatitude(); 
//			float y2 = (float) next.getLongitude();
//			canvas.drawPoint(x1, y1, paint);
//			canvas.drawLine(x1, y1, x2, y2, paint);
//		}
		
		paint.setColor(Color.GREEN);
		if(sp != null){
			Vertex prev = sp.iterator().next();
			for(Vertex vertex : sp){
				Loc cur = prev.getLoc(), next = vertex.getLoc();
				float x1 = (float) cur.getLatitude(), y1 = (float) cur.getLongitude(),
						x2 = (float) next.getLatitude(), y2 = (float) next.getLongitude();
				canvas.drawLine(x1, y1, x2, y2, paint);
			}
		}
		
		
//		canvas.scale(canvas.getWidth()/bmp.getWidth(), canvas.getHeight()/bmp.getHeight());
		super.onDraw(canvas);
	}

	public void loadImage(String filename) {
		Options options = new Options();
		options.inSampleSize = 4;
//		Matrix matrix = new Matrix();
//		matrix.postRotate(90);
//		Bitmap scaledBitmap = BitmapFactory.decodeFile(filename, options);
//		bmp = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		bmp = BitmapFactory.decodeFile(filename, options);
		this.invalidate();
	}

	public void placePoint(double x, double y) {
		Log.d("MapView", x + " " + y);
		points.add(new Vertex(x, y));
		this.invalidate();
	}
	
	public void drawEdges(Graph g, Canvas canvas){
		if(g == null)
			return;
		for(Vertex vertex : g.getVertices()){
			Loc loc = vertex.getLoc();
			canvas.drawCircle((float)loc.getLatitude(), (float)loc.getLongitude(), 4, paint);
		}
		for(int i = 0; i < g.edges.size(); i+=4){
			float x1 = g.edges.get(i);
			float y1 = g.edges.get(i+1);
			float x2 = g.edges.get(i+2);
			float y2 = g.edges.get(i+3);
			Log.d("EDGES", x1 + " " + y1 + " " + x2 + " " + y2);
			canvas.drawLine(x1, y1, x2, y2, paint);
		}
		
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
			MainActivity mainActivity = (MainActivity) this.getContext();
			float x = event.getX(), y = event.getY();
			if (mainActivity.calibrate == true) {
				Log.d("MapView", "Calibrate Start");
				mainActivity.calibrating = true;
				mainActivity.calibrate = false;
				placePoint(x, y);
				start = new PointF(x, y);
				Toast.makeText(this.getContext(), "Please walk to a location and select it.",
						Toast.LENGTH_SHORT).show();
				break;
			} else if (mainActivity.calibrating == true) {
				Log.d("MapView", "Calibrate End");
				mainActivity.calibrating = false;
				placePoint(x, y);
				this.invalidate();
				mainActivity.avgStride = points.get(0).getLoc().computeDist(points.get(1).getLoc())
						/ mainActivity.numSteps;
				// points.clear();
				// placePoint(x, y);
				break;
			} else {
				Log.d("MapView", event.toString());
				flag = new PointF(x, y);
				dst = mainActivity.graph.closestVertexToPath(new Loc((double) x, (double) y)).getLeft();
				Log.d("nomnom", dst.toString());
				try {
					sp = mainActivity.graph.computePathToGraph(points.get(points.size() - 1).getLoc(), dst);
					if(sp != null)
					for (Vertex v : sp){
						Log.d("nom", v.toString());
					}
					//plot this
				} catch (GraphException g) {
					Toast.makeText(this.getContext(), "Invalid location G", Toast.LENGTH_SHORT).show();
				}
				break;
			}

//			Log.d("MapView", event.toString());
//			float x = event.getX(),
//			y = event.getY();
//			if (start == null) {
//				start = new PointF(x, y);
//				points.add(new Vertex(x, y));
//			} else if (flag == null) {
//				flag = new PointF(x, y);
//				points.add(new Vertex(x, y));
//				calibrate(Math.PI / 4);
//			} else {
//				step(temp);
//				temp += 0.5 - Math.random();
//			}
//			this.invalidate();
//			break;
		}
		return super.onTouchEvent(event);
	}

	double temp = Math.PI / 2;

	double stride, north;

	public void calibrate(double compass) {
		stride = 40;
		north = Math.atan2(start.y - flag.y, flag.x - start.x);
		north -= compass;
	}

	public void step(double compass) {
		compass += north;
		Loc last = points.get(points.size() - 1).getLoc();
		double x = last.getLatitude() + stride * Math.cos(compass);
		double y = last.getLongitude() - stride * Math.sin(compass);
		points.add(new Vertex(x, y));
	}

//	public double compass() {
//
//	}
}
