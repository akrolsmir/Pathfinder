package com.pathfinder.stepcounter;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.pathfinder.NavigateActivity;
import com.pathfinder.R;

public class StepService extends Service implements SensorEventListener{
	public final static String MY_ACTION = "STEP";
	private SensorManager mSensorManager;
	private StepListener stepListener;
	private List<Sensor> mSensorList;
	private static final String TAG = "StepDetector";
	private float sensitivity = 15;
	private float mLastValues[] = new float[3 * 2];
	private float mScale[] = new float[2];
	private float mYOffset;

	private float mLastDirections[] = new float[3 * 2];
	private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
	private float mLastDiff[] = new float[3 * 2];
	private int mLastMatch = -1;

	public class StepBinder extends Binder {
		public StepService getService() {
			return StepService.this;
		}
	}

	@Override
	public void onCreate() {

		// Start detecting
		Log.i(TAG, "Begin detecting");
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		for(Sensor sensor : mSensorList){
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}

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
		Log.i(TAG, "Destroying...");
		
		mSensorManager.unregisterListener(this);
		// Tell the user we stopped.
		Toast.makeText(this, getText(R.string.step_service_stopped),
				Toast.LENGTH_SHORT).show();
		
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void resetSteps() {
		stepListener.reset();
	}

	public int getNumSteps() {
		return stepListener.getSteps();
	}

	/**
	 * Receives messages from activity.
	 */
	private final IBinder mBinder = new StepBinder();

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

		Sensor sensor = event.sensor;
		synchronized (this) {
			int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
			if (j == 1) {
				float vSum = 0;
				for (int i = 0; i < 3; i++) {
					final float v = mYOffset + event.values[i] * mScale[j];
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

					if (diff > sensitivity) {

						boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
						boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
						boolean isNotContra = (mLastMatch != 1 - extType);

						if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough
								&& isNotContra) {
							Log.i(TAG, "step");
							Intent intent = new Intent(this, NavigateActivity.class);
							intent.putExtra("New step", 1);
							sendBroadcast(intent);
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
		}
	}

}
