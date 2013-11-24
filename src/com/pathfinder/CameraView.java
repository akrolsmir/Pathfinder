package com.pathfinder;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.JavaCameraView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

public class CameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "CameraView";
    private String mPictureFileName;
    private String mIntermediateFileName;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void makeMono() {
        // Set the display image to be in black and white
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(Camera.Parameters.EFFECT_MONO);
        mCamera.setParameters(params);
    }

    public void takePicture(final String IntermediateFileName, final String fileName) {
        Log.i(TAG, "[CAMERA VIEW] taking picture");
        this.mIntermediateFileName = IntermediateFileName;
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    private void setCameraDisplayOrientation(Activity activity) {
            // int cameraId) {
//        android.hardware.Camera.CameraInfo info =
//                new android.hardware.Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

//        int result;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - degrees + 360) % 360;
//        }
//        mCamera.setDisplayOrientation(result);
        mCamera.setDisplayOrientation(360 - degrees);
    }
    
    private Bitmap getImage1(String path) throws IOException
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        Log.i(TAG, "[CAMERA VIEW] dimensions = (" + srcWidth + ", " + srcHeight);
        int[] newWH =  new int[2];
        newWH[0] = srcWidth/2;
        newWH[1] = (newWH[0]*srcHeight)/srcWidth;

        int inSampleSize = 1;
        while(srcWidth / 2 >= newWH[0]){
            srcWidth /= 2;
            srcHeight /= 2;
            inSampleSize *= 2;
        }

         options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(path,options);
        ExifInterface exif = new ExifInterface(path);
        String s=exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        System.out.println("Orientation>>>>>>>>>>>>>>>>>>>>"+s);
        Matrix matrix = new Matrix();
        float rotation = 90; // TODO: use line below
        // float rotation = rotationForImage(this.getContext(), Uri.fromFile(new File(path)));
        if (rotation != 0f) {
            matrix.preRotate(rotation);
        }

        Bitmap pqr=Bitmap.createBitmap(
                sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);


        return pqr;
    }   


    private float rotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = { Images.ImageColumns.ORIENTATION };
            Cursor c = context.getContentResolver().query(
                    uri, projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int)exifOrientationToDegrees(
                        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                Log.i(TAG, "[CAMERA VIEW] " + rotation);
                return rotation;
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
             }

        }
        return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // Write the image in a file (in jpeg format)
        try {
            Log.i(TAG, "[CAMERA VIEW] write intermediate");
            FileOutputStream fos1 = new FileOutputStream(mIntermediateFileName);
            fos1.write(data);
            fos1.close();
            Log.i(TAG, "[CAMERA VIEW] starting getImage1");
            Bitmap bmp = getImage1(mIntermediateFileName);
            Log.i(TAG, "[CAMERA VIEW] finished getImage1");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Log.i(TAG, "[CAMERA VIEW] write image");
            FileOutputStream fos2 = new FileOutputStream(mPictureFileName);
            fos2.write(byteArray);
            fos2.close();
            Log.i(TAG, "[CAMERA VIEW] done");
        } catch (java.io.IOException e) {
            Log.e(TAG, "[CAMERA VIEW] exception raised", e);
        }
        
		Intent intent = new Intent(getContext(), MainActivity.class);
		getContext().startActivity(intent);
    }
}
