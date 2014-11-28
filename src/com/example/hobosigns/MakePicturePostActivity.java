package com.example.hobosigns;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class MakePicturePostActivity extends Activity {
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_camera);

	        // Create our Preview view and set it as the content of our activity.
	        FrameLayout preview = (FrameLayout) findViewById(R.id.make_picture_post);
	        
	        Intent startingIntent = getIntent();
	        final Bitmap bitmap = (Bitmap) startingIntent.getParcelableExtra("Picture");//TODO: change to be a puclic static variable
	        //TODO: get image view and add this bitmap to it
	        
	        //TODO: create an xml view with an iamge view in the bckgd an editabletext view in the front and a button
	        
	        // TODO: make a button to cancel the opperation, send the user back with a failure intent
	        // Add a listener to the Capture button
	        Button captureButton = (Button) findViewById(R.id.button_send_post);
	        captureButton.setOnClickListener(
	            new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	// convert image to jpeg
	                	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	                	bitmap.compress(CompressFormat.JPEG, 100, stream);
	        		    byte[] bytes=stream.toByteArray();
	        		    //TODO get text from the caption
	        		    String caption = null;
	        		    //start a service to send the pic 
	        		    // http://www.slideshare.net/RanNachmany/androd-rest-client-architecture
	        		    // return an intent signifying success
	                }
	            }
	        );
	    }

}
