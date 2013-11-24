package com.pathfinder;

import imageProc.ImageProcessor;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.pathfinder.graph.Graph;
import com.pathfinder.graph.Loc;
import com.pathfinder.graph.Pair;
import com.pathfinder.graph.Vertex;
import com.pathfinder.graph.exception.GraphException;

public class MainActivity extends Activity implements SensorEventListener{
	
	/*
	 * variables for sensors
	 */
	private SensorManager mSensorManager;
	private List<Sensor> mSensorList;
	private float sensitivity = 3; //may change later
	private float mLastValues[] = new float[3 * 2];
	private float mScale[] = new float[2];
	private float mYOffset;
	private float mLastDirections[] = new float[3 * 2];
	private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
	private float mLastDiff[] = new float[3 * 2];
	private int mLastMatch = -1;
	private float[] compassValues = {0, 0, 0};
	private float[] accelValues = {0, 0, 0};
	private float[] rot = {0, 0, 0, 0, 0, 0, 0, 0, 0};
	private float[] incl = {0, 0, 0, 0, 0, 0, 0, 0, 0};
	private float[] orient = {0, 0, 0};
	private float prevAzimuth = 0;
	protected boolean calibrate = false;
	protected boolean calibrating = false;
	protected int numSteps = 0;
	protected double avgStride = 0;
	protected double north = 0;
	MapView mapView;
	Graph graph;
	
	//FLAG FOR WHETHER A STEP WAS MADE OR NOT

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String fileName = Environment.getExternalStorageDirectory()
				.getPath() + "/pathfinder_image.jpg";
//		Graph g = ImageProcessor.process(Highgui.imread(fileName));
//		Mat img = Highgui.imread(fileName);
		graph = ImageProcessor.process(Highgui.imread(fileName));
//		graph = getTestGraph();
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.loadImage(fileName);
		
//		mapView.drawGraph(graph);
		
		Log.i("SENSOR", "Begin detecting");
		int h = 480;
		mYOffset = h * 0.5f;
		mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
		mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		for(Sensor sensor : mSensorList){
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		Toast.makeText(this, "Please select your current location", Toast.LENGTH_SHORT).show();
		calibrate = true;
	}
	
	private Graph getTestGraph(){
		Vertex A = new Vertex();
		Vertex B = new Vertex(0, 100);
		Vertex C = new Vertex(100, 0);
		Vertex D = new Vertex(0, 200);
		Vertex E = new Vertex(100, 100);
		Vertex F = new Vertex(200, 0);
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		verts.add(A);
		verts.add(B);
		verts.add(C);
		verts.add(D);
		verts.add(E);
		verts.add(F);
		Pair<Vertex, Vertex> AC = new Pair<Vertex, Vertex>(A, C);
		Pair<Vertex, Vertex> CD = new Pair<Vertex, Vertex>(C, D);
		Pair<Vertex, Vertex> AB = new Pair<Vertex, Vertex>(A, B);
		Pair<Vertex, Vertex> BC = new Pair<Vertex, Vertex>(B, C);
		Pair<Vertex, Vertex> CE = new Pair<Vertex, Vertex>(C, E);
		Pair<Vertex, Vertex> DE = new Pair<Vertex, Vertex>(D, E);
		Pair<Vertex, Vertex> DF = new Pair<Vertex, Vertex>(D, F);
		Pair<Vertex, Vertex> EF = new Pair<Vertex, Vertex>(E, F);
		Pair<Vertex, Vertex> AF = new Pair<Vertex, Vertex>(A, F);
		ArrayList<Pair<Vertex, Vertex>> edges = new ArrayList<Pair<Vertex, Vertex>>();
		edges.add(AC);
		edges.add(CD);
		edges.add(AB);
		edges.add(BC);
		edges.add(CE);
		edges.add(DE);
		edges.add(DF);
		edges.add(EF);
		edges.add(AF);
		double ac = .05;
		double cd = .08;
		double ab = 1.1;
		double bc = .04;
		double ce = .05;
		double de = 5;
		double df = 1;
		double ef = 2;
		double af = 11.234;
		ArrayList<Double> weights = new ArrayList<Double>();
		weights.add(ac);
		weights.add(cd);
		weights.add(ab);
		weights.add(bc);
		weights.add(ce);
		weights.add(de);
		weights.add(df);
		weights.add(ef);
		weights.add(af);
		Graph foo = null;
		try{
			foo = new Graph(verts, edges, weights);
			ArrayList<Vertex> path = foo.computePath(A, F);
			System.out.println(path.toString());
			E.setWeight(F, 1.0);
			path = foo.computePath(A, F);
			Log.d("Shortest path from A to F: ", path.toString());
		} catch (GraphException g){
//			throw g;
		}
		return foo;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		//NOT NEEDED
	}
	
	/**
	 * Computes information for whether a step occurs and for computing a new rotation
	 * matrix for the azimuth.
	 * @param event -- An event from the accelerometer
	 */
	private void handleAccelerometer(SensorEvent event){
		for(int i = 0; i < 3; i++){
			accelValues[i] = event.values[i];
		}
		float vSum = 0;
		for (int i = 0; i < 3; i++) {
			final float v = mYOffset + event.values[i] * mScale[1];
			vSum += v;
		}
		int k = 0;
		float v = vSum / 3;

		float direction = (v > mLastValues[k] ? 1
				: (v < mLastValues[k] ? -1 : 0));
		if (direction == -mLastDirections[k]) {
			// Direction changed
			int extType = (direction > 0 ? 0 : 1); // minimum or
													// maximum?
			mLastExtremes[extType][k] = mLastValues[k];
			float diff = Math.abs(mLastExtremes[extType][k]
					- mLastExtremes[1 - extType][k]);
			//Log.i("step", "Diff: " + diff);
			if (diff > sensitivity) {
				//Log.i("I STEP", "step");
				boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
				boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
				boolean isNotContra = (mLastMatch != 1 - extType);

				if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough
						&& isNotContra) {
					Log.i("STEPPING", "step");
					doStep();
					mLastMatch = extType;
				} else {
					mLastMatch = -1;
				}
			}
			mLastDiff[k] = diff;
		}
		mLastDirections[k] = direction;
		mLastValues[k] = v;
	}
	
	private void handleMagneticField(SensorEvent event){
		for(int i = 0; i < 3; i++){
			compassValues[i] = event.values[i];
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		synchronized (this) {
			if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				handleAccelerometer(event);
			} else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
				handleMagneticField(event);
			}
			boolean del = SensorManager.getRotationMatrix(rot, incl, accelValues, compassValues);
			if(del){
				SensorManager.getOrientation(rot, orient);
				// update azimuth
//				Log.d("azimuth", orient[0] * 360 / Math.PI + "");
				azimuths.add(2 * orient[0]);
				if(azimuths.size() > 10)
					azimuths.remove(0);
			}
		}
	}
	
//	/**
//	 * @return the rotation about the z-axis relative to true north
//	 */
//	private float getAzimuth(){
//		return orient[0];
//	}
	
	private ArrayList<Float> azimuths =  new ArrayList<Float>();
	
	private void setNorth(double x, double y, double angle){
		north = Math.atan2(-y, x) - angle;
	}
	
	
	private void doStep(){
		if(calibrating){ //If calibrating steps...
			numSteps++;
		} else {
//			float azimuth = averageAzimuth();
//			double threshold = 3.14/18;
//			if(Math.abs(azimuth - prevAzimuth) > threshold){
//				prevAzimuth = azimuth; 
//			}
			double angle = averageAzimuth() + north;
			if(mapView.points.size() == 0)
				return;
			Loc last = mapView.points.get(mapView.points.size() - 1).getLoc();
			double x = last.getLatitude() + avgStride*Math.cos(angle);
			double y = last.getLongitude() - avgStride*Math.sin(angle);
			mapView.placePoint(x, y);
			float thresh = 3;
			Pair<Vertex, Double> dat = graph.closestVertexToPath(new Loc(x, y));
			if(dat.getRight() > thresh*avgStride){
				try{
					graph.computePathToGraph(new Loc(x, y), mapView.dst);
				} catch (GraphException g){
					Toast.makeText(this, "Invalid location", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	private float averageAzimuth(){
		float sum = 0;
		for(float azimuth : azimuths){
			sum += azimuth;
		}
		return sum / azimuths.size();
	}

}
