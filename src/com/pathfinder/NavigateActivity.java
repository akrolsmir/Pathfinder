package com.pathfinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pathfinder.stepcounter.StepService;

public class NavigateActivity extends Activity {

	private static final String TAG = "NavActivity";
	private boolean mIsRunning;
	private TextView mStepValueView;
	private Button mResetButton;

	public NavigateActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[SERVICE] Create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nav_view_layout);

		startStepService();
		bindStepService();

		mStepValueView = (TextView) findViewById(R.id.counter);
		mResetButton = (Button) findViewById(R.id.reset_button);
		mResetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				resetValues(true);
			}
		});
		
		resetValues(true);

	}

	@Override
	public void onStart() {
		Log.i(TAG, "[SERVICE] Start");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "[SERVICE] Pause");
		super.onPause();
		if (mIsRunning) {
			unbindStepService();
		}
	}

	@Override
	public void onResume() {
		Log.i(TAG, "[SERVICE] Resume");
		super.onResume();
		if (!mIsRunning) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}
	}

	public void onDestroy() {
		Log.i(TAG, "[SERVICE] Destroy");
		super.onDestroy();
	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			Log.i(TAG, "[SERVICE] Start");
			mIsRunning = true;
			startService(new Intent(NavigateActivity.this, StepService.class));
		}
	}

	private void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(NavigateActivity.this, StepService.class),
				mConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() {
		Log.i(TAG, "[SERVICE] Unbind");
		unbindService(mConnection);
	}

	private void stopStepService() {
		Log.i(TAG, "[SERVICE] Stop");
		if (mService != null) {
			Log.i(TAG, "[SERVICE] stopService");
			stopService(new Intent(NavigateActivity.this, StepService.class));
		}
		mIsRunning = false;
	}

	private void resetValues(boolean updateDisplay) {
		if (mService != null && mIsRunning) {
			mService.resetSteps();
		} else {
			mStepValueView.setText("0");
			SharedPreferences state = getSharedPreferences("state", 0);
			SharedPreferences.Editor stateEditor = state.edit();
			if (updateDisplay) {
				stateEditor.putInt("steps", 0);
				stateEditor.commit();
			}
		}
	}

}
