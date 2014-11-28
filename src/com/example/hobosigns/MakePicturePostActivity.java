package com.example.hobosigns;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import com.example.hobosigns.models.PicturePost;
import com.example.hobosigns.models.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MakePicturePostActivity extends Activity {
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_build_post);
	        
	        Intent startingIntent = getIntent();
	        final Bitmap bitmap = (Bitmap) startingIntent.getParcelableExtra(CameraActivity.pictureIntentTag);//TODO: change to be a public static variable
	        final double latitude = startingIntent.getDoubleExtra(CameraActivity.latIntentTag, 0);
	        final double longitude = startingIntent.getDoubleExtra(CameraActivity.lonIntentTag, 0);
	        //get linear layout and add this bitmap to it
	        LinearLayout backgroundLayout = (LinearLayout) findViewById(R.id.image_background);
	        BitmapDrawable background = new BitmapDrawable(getResources(), bitmap);
	        backgroundLayout.setBackground(background);
	        	        
	        // TODO: make a button to cancel the opperation, send the user back with a failure intent
	        // Add a listener to the Capture button
	        Button captureButton = (Button) findViewById(R.id.button_send_post);
	        captureButton.setOnClickListener(
	            new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	        	        EditText caption = (EditText) findViewById(R.id.post_text);
	        		    // to convert the byte array to a bitmap: Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);
	        		    //get text from the caption
	        	        //convert image to byte array
	        		    byte[] byteBitmap = PicturePost.bitmapToByte(bitmap);
	        		    Calendar c = Calendar.getInstance(); 
	        		    Date now = new Date();
	        		    User user = User.getSavedUser(v.getContext());
	        		    //create and post the sign
	        		    PicturePost picturePost = new PicturePost(byteBitmap,caption.getText().toString(), 0L, user.getId(), latitude, longitude, now);
	        		    picturePost.postPost();
	                }
	            }
	        );
	    }

}
