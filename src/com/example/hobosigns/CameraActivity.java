package com.example.hobosigns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity {

	private Camera mCamera;
	private Preview mPreview;
	public static final int MEDIA_TYPE_IMAGE = 1;
	protected static final String TAG = "Camera Error:";
	public static final String latIntentTag = "Langitude";
	public static final String lonIntentTag = "Latitude";
	public static final String pictureIntentTag = "Picture";
	private double doubleLatitude;
	private double doubleLongitude;
	
	// Max picture dimensions
	final static int MAX_WIDTH = 1080;
	final static int MAX_HEIGHT = 1080;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Either remove this from the backstack or set history off

		// check if GPS enabled
		GPSTracker gpsTracker = new GPSTracker(this);

		if (gpsTracker.canGetLocation()) {
			doubleLatitude = gpsTracker.latitude;
			doubleLongitude = gpsTracker.longitude;
			/*
			 * String country = gpsTracker.getCountryName(this); String city =
			 * gpsTracker.getLocality(this); String postalCode =
			 * gpsTracker.getPostalCode(this); String addressLine =
			 * gpsTracker.getAddressLine(this);
			 */
		} else {
			Toast.makeText(this, "Please Check that your GPS is turned on",
					Toast.LENGTH_LONG).show();
			// gpsTracker.showSettingsAlert();
			finish();
			return;
		}
		if (!checkCameraHardware()) {
			Toast.makeText(this, "A camera could not be detected",
					Toast.LENGTH_LONG).show();
		}

		setContentView(R.layout.activity_camera);
		// Create an instance of Camera
		mCamera = getCameraInstance();
		// Create our Preview view and set it as the content of our activity.
		mPreview = new Preview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				mCamera.takePicture(null, null, mPicture);
				// send it to the make a post activity
				/*
				 * That one should make the post and then send the user back to
				 * here to finidh it.
				 */
			}
		});
	}

	public void onRestart() {
		// Create an instance of Camera
		mCamera = getCameraInstance();
		// Create our Preview view and set it as the content of our activity.
		mPreview = new Preview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
	}

	private boolean checkCameraHardware() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
			c.setDisplayOrientation(90);
//			Camera.Parameters params = c.getParameters();
//			if (params == null) {
//				Log.i("Camera", "params are null");
//			} else {
//				Log.i("Camera", "params are NOT null");
//			}
//			params.setPictureSize(1024, 576);
//			c.setParameters(params);
		} catch (Exception e) {
			Log.e("cameraError", e.toString());
			Log.i("Camera", "camera not available");
		}
		return c; // returns null if camera is unavailable
	}

	private static Camera.Size getBestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;

		Log.i("Camera", "getBestPictureSize");
		
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			Log.i("Camera", "getBestPictureSize");
			Log.i("resolution", result.width + ", " + result.height);
			if (result == null
					|| (size.width <= MAX_WIDTH && size.height <= MAX_HEIGHT && size.width
							* size.height > result.width * result.height)) {
				result = size;
			}
		}

		return result;
	}
	
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// create intent and pass this picture to it
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");
				return;
			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				
				// rotate image before saving
				
				
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

			Intent intent = new Intent(getApplicationContext(),
					MakePicturePostActivity.class);
			intent.putExtra(pictureIntentTag, pictureFile);
			intent.putExtra(latIntentTag, doubleLatitude);
			intent.putExtra(lonIntentTag, doubleLongitude);
			startActivity(intent);
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(MainActivity.Tag, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

}
