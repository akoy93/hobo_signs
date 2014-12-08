package com.example.hobosigns;
import java.io.InputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.hobosigns.models.Post;
import com.example.hobosigns.models.User;
import com.example.hobosigns.rest.MyCallable;


public class ViewSign extends Activity {
	
		 int voteCount;
	     int myVote;
	     long postId;
	     String accessToken;
	     TextView voteView;
	     Button upButton;
	     Button downButton;
	     private static TextView captionView;
		
		 public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_view_post);
		        
		        //Remove video view
		        VideoView vidView = (VideoView) findViewById(R.id.VideoView2);
		        vidView.setVisibility(View.GONE);
		        //get the other views
		        RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.image_background_frame);
		        voteView = (TextView) findViewById(R.id.vote_view);
		        upButton = (Button) findViewById(R.id.sign_upvote);
		        downButton = (Button) findViewById(R.id.sign_downvote);
		        captionView = (TextView) findViewById(R.id.sign_caption_view);
		        		
		        //get intent info
		        Intent startingIntent = getIntent();
		        String caption = startingIntent.getStringExtra("postCaption");
		        Log.i("View Sign", "Caption is: "+ caption);
		        String uri = startingIntent.getStringExtra("postURI");
		        voteCount = startingIntent.getIntExtra("postVoteCount", 0);
		        myVote = startingIntent.getIntExtra("myVote",0);
		        postId = startingIntent.getLongExtra("postId", 0);

		        //set button states
		        setVoteButtons(myVote, upButton, downButton);

		        //get access token
		        User user= User.getSavedUser(this);
		        accessToken = user.getAccessToken();
		        
		        //set text views
		        captionView.setText(caption);
		        voteView.setText(String.valueOf(voteCount));
		        Log.i("View Sign", "(onCreate) Viewing caption is: "+ captionView.getText());
		        
		        new DownloadImageTask( backgroundLayout).execute(uri);
		        
		     // Add a listener to the voting button
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
		 
		 
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		RelativeLayout bmImage;
	
	    public DownloadImageTask(RelativeLayout bmImage) {
	        this.bmImage = bmImage;
	    }
	
	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }
	
	    protected void onPostExecute(Bitmap result) {
	        BitmapDrawable background = new BitmapDrawable(getResources(), result);
	        bmImage.setBackground(background);
	    }
	}
	 
	 private void setVoteButtons(int myVote, Button upButton, Button downButton){
		 	upButton.setEnabled(true);
		 	downButton.setEnabled(true);
	        if(myVote == 1){upButton.setEnabled(false); }
	        else if(myVote == -1){downButton.setEnabled(false); }
	 }
}
