package com.example.hobosigns;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.hobosigns.models.Post;
import com.example.hobosigns.models.User;
import com.example.hobosigns.rest.MyCallable;

public class ViewVideoSign extends Activity {
	
	 private int voteCount;
	 private int myVote;
     private long postId;
     private String accessToken;
     private TextView voteView;
     private Button upButton;
     private Button downButton;
     private String caption;
     private static TextView captionView;
     RelativeLayout backgroundLayout;
     VideoView vidView;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_view_post);
	        
	        //get the views
	        vidView = (VideoView) findViewById(R.id.VideoView2);
	        backgroundLayout = (RelativeLayout) findViewById(R.id.image_background_frame);
	        voteView = (TextView) findViewById(R.id.vote_view);               
	        upButton = (Button) findViewById(R.id.sign_upvote);
        	downButton = (Button) findViewById(R.id.sign_downvote);
	        captionView = (TextView) findViewById(R.id.sign_caption_view);
	        
	        //get intent info
	        Intent startingIntent = getIntent();
	        caption = startingIntent.getStringExtra("postCaption");
	        String uriString = startingIntent.getStringExtra("postURI");
	        voteCount = startingIntent.getIntExtra("postVoteCount", 0);
	        postId = startingIntent.getLongExtra("postId", 0);
	        myVote = startingIntent.getIntExtra("myVote",0);
	        
	        //get access token
	        User user= User.getSavedUser(this);
	        accessToken = user.getAccessToken();
	        
	        //set text views
	        voteView.setText(String.valueOf(voteCount));
	     	captionView.setText(caption);
	        //set button states
	        setVoteButtons(myVote, upButton, downButton);
	        
	        Uri uri = Uri.parse(uriString);
	        vidView.setVideoURI(uri);
	        vidView.start();
	        
	        //make the video restart on tap
	        backgroundLayout.setOnTouchListener(new View.OnTouchListener() {  
	            @Override
	            public boolean onTouch(View arg0, MotionEvent arg1) {
	                //gesture detector to detect swipe.
	            	vidView.start();
	                return true;//always return true to consume event
	            }
	        });
	        
	        	        
	        upButton.setOnClickListener(new OnClickListener(){				
				@Override
				public void onClick(View v) {
					Post.upvote(
						new MyCallable<Void>() {
							@Override
							public Void call() throws Exception {
								return null;
							}

							@Override
							public Void call(JSONObject jsonObject) throws Exception {
								Log.i("upvote", jsonObject.toString());
								boolean success = jsonObject.getBoolean("success");
								Log.i("upvote was success?: ", String.valueOf(success));
								
								if(success){
					             	voteCount = 1 - myVote + voteCount;
									myVote = 1;
					     	        voteView.setText(String.valueOf(voteCount));
					     	        setVoteButtons(1, upButton, downButton);
								}
								return null;
						}}, accessToken, postId );
				}} 
    		);
	        
	        downButton.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					Post.downvote(
						new MyCallable<Void>() {
							@Override
							public Void call() throws Exception {
								return null;
							}
							@Override
							public Void call(JSONObject jsonObject) throws Exception {
								Log.i("downvote", jsonObject.toString());
								boolean success = jsonObject.getBoolean("success");
								Log.i("downvote was success?: ", String.valueOf(success));
								if(success){
					             	voteCount = -myVote -1 + voteCount;
									myVote = -1;
					     	        voteView.setText(String.valueOf(voteCount));
					     	        setVoteButtons(-1, upButton, downButton);
								}
								return null;
						}}, accessToken, postId );
				}}
    		);
	 }
	 
	 private void setVoteButtons(int myVote, Button upButton, Button downButton){
		 	upButton.setEnabled(true);
		 	downButton.setEnabled(true);
	        if(myVote == 1){upButton.setEnabled(false); }
	        else if(myVote == -1){downButton.setEnabled(false); }
	 }
}
