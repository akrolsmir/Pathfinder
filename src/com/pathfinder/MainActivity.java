package com.pathfinder;

<<<<<<< HEAD
import java.util.Iterator;
=======
import java.util.List;
>>>>>>> 94d9c9187dc6d2c25be0252ba43f528c5a4c76a1

import imageProc.ImageProcessor;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

<<<<<<< HEAD
import com.pathfinder.graph.Graph;
import com.pathfinder.graph.Vertex;

=======
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
>>>>>>> 94d9c9187dc6d2c25be0252ba43f528c5a4c76a1
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements SensorEventListener{
	private SensorManager mSensorManager;
	private List<Sensor> mSensorList;
	private float sensitivity = 3;
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
	
	//FLAG FOR WHETHER A STEP WAS MADE OR NOT
	private boolean stepped = false;

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
					stepped = true;
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
				//Log.i("New Orientation", "Angle: " + getAzimuth());
			}
		}
		
	}
	
	/**
	 * 
	 * @return the rotation about the z-axis relative to true north
	 */
	private float getAzimuth(){
		return orient[0];
	}

}
