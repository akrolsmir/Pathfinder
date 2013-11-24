package com.pathfinder.stepcounter;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.pathfinder.R;

public class StepService extends Service {
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private StepDetector stepDetector;
	private StepListener stepListener;
	
	public class StepBinder extends Binder {
		StepService getService() {
			return StepService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Start detecting
		stepListener = new StepListener();
		stepDetector = new StepDetector(stepListener);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		registerDetector();

		// Tell the user we started.
		Toast.makeText(this, getText(R.string.step_service_started),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {

		// Unregister our receiver.
		unregisterDetector();

		super.onDestroy();

		// Stop detecting
		mSensorManager.unregisterListener(stepDetector);

		// Tell the user we stopped.
		Toast.makeText(this, getText(R.string.step_service_stopped),
				Toast.LENGTH_SHORT).show();
	}

	private void registerDetector() {
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(stepDetector, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void unregisterDetector() {
		mSensorManager.unregisterListener(stepDetector);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public void resetSteps(){
		stepListener.reset();
	}
	public int getNumSteps(){
		return stepListener.getSteps();
	}
	
	/**
	 * Receives messages from activity.
	 */
	private final IBinder mBinder = new StepBinder();

}
