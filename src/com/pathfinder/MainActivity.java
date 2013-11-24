package com.pathfinder;

import java.util.Iterator;

import imageProc.ImageProcessor;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.pathfinder.graph.Graph;
import com.pathfinder.graph.Vertex;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String fileName = Environment.getExternalStorageDirectory()
				.getPath() + "/pathfinder_image.jpg";
		Graph g = ImageProcessor.process(Highgui.imread(fileName));
		Mat img = Highgui.imread(fileName);
		Iterator<Vertex>vertices = g.getVertices();
		MapView mapView = (MapView) findViewById(R.id.mapView);
		mapView.loadImage(fileName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
