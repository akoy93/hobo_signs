package com.example.hobosigns;
import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.example.hobosigns.models.PicturePost;
import com.example.hobosigns.models.User;


public class ViewSign extends Activity {
		
		 public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_build_post);
		        
		        //Remove video view
		        VideoView vidView = (VideoView) findViewById(R.id.VideoView);
		        vidView.setVisibility(View.GONE);
		        
		        Intent startingIntent = getIntent();
		        final File file = (File)startingIntent.getSerializableExtra(CameraActivity.pictureIntentTag);
		        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		        
		        Matrix matrix = new Matrix();
		        matrix.postRotate(90);
		        
		        //Get screen dimensions
		        Display display = getWindowManager().getDefaultDisplay();
		        Point size = new Point();
		        display.getSize(size);
		        int screenWidth = size.y;
		        
		        //original dimensions
		        int origWidth = bitmap.getWidth();
		        int origHeight = bitmap.getHeight();
		        
		        // Pick picture dimensions
		        int dispWidth = screenWidth <= origWidth? screenWidth : origWidth;
	    		int dispHeight = (int) ((((float)dispWidth)/((float)origWidth))*((float)origHeight));
	    		
	    		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, dispWidth, dispHeight, true);
		        
		        Bitmap rotated = Bitmap.createBitmap(scaled, 0, 0, 
		                                      dispWidth, dispHeight, 
		                                      matrix, true);
		        
		        final double latitude = startingIntent.getDoubleExtra(CameraActivity.latIntentTag, 0);
		        final double longitude = startingIntent.getDoubleExtra(CameraActivity.lonIntentTag, 0);
		        //get linear layout and add this bitmap to it
		        LinearLayout backgroundLayout = (LinearLayout) findViewById(R.id.image_background);
		        BitmapDrawable background = new BitmapDrawable(getResources(), rotated);
		        backgroundLayout.setBackground(background);
		        EditText caption = (EditText) findViewById(R.id.post_text);
		        caption.requestFocus();	        	        
		        
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
		        		    PicturePost picturePost = new PicturePost(file.getAbsolutePath(),caption.getText().toString(), 0L, 
		        		    		user.getUsername(), latitude, longitude, now, "Picture", null, 0, null);
		        		    picturePost.postPost(getApplicationContext(), user.getAccessToken());
		        		    Intent intent = new Intent(ViewSign.this, SignMapActivity.class);
		        		    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        		    startActivity(intent);
		                }
		            }
		        );
		    }

}
