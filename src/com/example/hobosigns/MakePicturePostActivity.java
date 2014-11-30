package com.example.hobosigns;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.hobosigns.models.PicturePost;
import com.example.hobosigns.models.User;

public class MakePicturePostActivity extends Activity {
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_build_post);
	        
	        Intent startingIntent = getIntent();
	        final byte[] bitmapAsBytes = startingIntent.getByteArrayExtra(CameraActivity.pictureIntentTag);
	        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapAsBytes, 0, bitmapAsBytes.length);
	        final double latitude = startingIntent.getDoubleExtra(CameraActivity.latIntentTag, 0);
	        final double longitude = startingIntent.getDoubleExtra(CameraActivity.lonIntentTag, 0);
	        //get linear layout and add this bitmap to it
	        LinearLayout backgroundLayout = (LinearLayout) findViewById(R.id.image_background);
	        BitmapDrawable background = new BitmapDrawable(getResources(), bitmap);
	        backgroundLayout.setBackground(background);
	        EditText caption = (EditText) findViewById(R.id.post_text);
	        caption.requestFocus();	        	        
	        
	        // TODO: make a button to cancel the opperation, send the user back with a failure intent
	        // Add a listener to the Capture button
	        Button captureButton = (Button) findViewById(R.id.button_send_post);
	        captureButton.setOnClickListener(
	            new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	EditText caption = (EditText) findViewById(R.id.post_text);
	        		    Date now = new Date();
	        		    User user = User.getSavedUser(v.getContext());
	        		    //create and post the sign
	        		    PicturePost picturePost = new PicturePost(bitmapAsBytes,caption.getText().toString(), 0L, 
	        		    		user.getUsername(), latitude, longitude, now, "Picture", null, 0, null);
	        		    picturePost.postPost(getApplicationContext(), user.getAccessToken());
	        		    Intent intent = new Intent(MakePicturePostActivity.this, SignMapActivity.class);
	        		    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        		    startActivity(intent);
	                }
	            }
	        );
	    }

}
