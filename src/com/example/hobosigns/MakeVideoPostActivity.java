package com.example.hobosigns;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.example.hobosigns.models.User;
import com.example.hobosigns.models.VideoPost;

public class MakeVideoPostActivity extends Activity {
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_build_post);
	        
	        Intent startingIntent = getIntent();
	        final String fileName = (String)startingIntent.getStringExtra(CameraActivity.videoIntentTag);
	       // Uri video = Uri.parse(fileName);
	        VideoView vidView = (VideoView) findViewById(R.id.VideoView);
	        vidView.setVideoPath(fileName);
	        final double latitude = startingIntent.getDoubleExtra(CameraActivity.latIntentTag, 0);
	        final double longitude = startingIntent.getDoubleExtra(CameraActivity.lonIntentTag, 0);
	        //get linear layout and remove the background on it
	        LinearLayout backgroundLayout = (LinearLayout) findViewById(R.id.image_background);
	        backgroundLayout.setBackground(null);
	        EditText caption = (EditText) findViewById(R.id.post_text);
	        caption.requestFocus();	     
	        vidView.start();   	        
	        
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
	        		    VideoPost videoPost = new VideoPost(fileName,caption.getText().toString(), 0L, 
	        		    		user.getUsername(), latitude, longitude, now, "Video", null, 0, null);
	        		    videoPost.postPost(getApplicationContext(), user.getAccessToken());
	        		    Intent intent = new Intent(MakeVideoPostActivity.this, SignMapActivity.class);
	        		    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        		    startActivity(intent);
	                }
	            }
	        );
	    }
}
