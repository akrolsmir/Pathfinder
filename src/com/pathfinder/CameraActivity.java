package com.pathfinder;

import imageProc.ImageProcessor;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class CameraActivity extends Activity implements CvCameraViewListener2,
		OnTouchListener {
	private static final String TAG = "CameraActivity";

	private CameraView mOpenCvCameraView;
	private boolean pictureTaken;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "[CAMERA] OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView.setOnTouchListener(CameraActivity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public CameraActivity() {
		Log.i(TAG, "[CAMERA] Instantiated");
		pictureTaken = false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[CAMERA] onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.camera_view_layout);

		mOpenCvCameraView = (CameraView) findViewById(R.id.camera_surface_view);

		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_7, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mOpenCvCameraView.makeMono();
	}

	public void onCameraViewStopped() {
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		return inputFrame.rgba();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!pictureTaken) {
			Log.i(TAG, "[CAMERA] taking picture");
			String basePath = Environment.getExternalStorageDirectory().getPath();
			mOpenCvCameraView.takePicture(basePath + "/pathfinder_intermediate.jpg", basePath + "/pathfinder_image.jpg");
			pictureTaken = true;
			// processImage(basePath + "/pathfinder_image.jpg");
		}
		return false;
	}
	
	void processImage(String fileName){
		ImageProcessor.process(Highgui.imread(fileName));
//		ImageProcessor.process(Highgui.imread(Environment.getExternalStorageDirectory().getPath() + "/map.jpg"));
	}
	
}
