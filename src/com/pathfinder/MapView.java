package com.pathfinder;

import java.util.ArrayList;
import java.util.List;

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

import com.pathfinder.graph.Loc;
import com.pathfinder.graph.Vertex;

public class MapView extends View {

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	Bitmap bmp;
	List<Vertex> points = new ArrayList<Vertex>();
	Paint paint = new Paint();

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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("MapView", event.toString());
			placePoint(event.getX(), event.getY());
			this.invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}
}
