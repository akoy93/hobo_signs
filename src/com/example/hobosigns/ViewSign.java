package com.example.hobosigns;
import java.io.InputStream;

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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;


public class ViewSign extends Activity {
		
		 public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_view_post);
		        
		        //Remove video view
		        VideoView vidView = (VideoView) findViewById(R.id.VideoView2);
		        vidView.setVisibility(View.GONE);
		        //get the other views
		        FrameLayout backgroundLayout = (FrameLayout) findViewById(R.id.image_background_frame);
		        TextView captionView = (TextView) findViewById(R.id.sign_caption2);
		        TextView voteView = (TextView) findViewById(R.id.vote_view);
		        Button upButton = (Button) findViewById(R.id.sign_upvote);
		        Button downButton = (Button) findViewById(R.id.sign_downvote);
		        
		        //get intent info
		        Intent startingIntent = getIntent();
		        String caption = startingIntent.getStringExtra("Caption");
		        String uri = startingIntent.getStringExtra("postURI");
		        int voteCount = startingIntent.getIntExtra("postVoteCount", 0);
		        int myVote = startingIntent.getIntExtra("myVote",0);
		        //TODO disable buttons based on vote
		        
		        //set text views
		        captionView.setText("sdfasdfasdf");
		        voteView.setText(String.valueOf(voteCount));
		        
		        new DownloadImageTask( backgroundLayout)
	            .execute(uri);
		        
		     // Add a listener to the voting button
		        upButton.setOnClickListener(
		            new OnClickListener() {
		                @Override
		                public void onClick(View v) {
		                	
		                }
		            }
		        );
		        downButton.setOnClickListener(
		            new OnClickListener() {
		                @Override
		                public void onClick(View v) {
		                	
		                }
		            }
		        );
		 }

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		FrameLayout bmImage;
	
	    public DownloadImageTask(FrameLayout bmImage) {
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
}
