package com.example.hobosigns.rest;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class PostAPI extends AsyncTask<String, Void, Void> {
	
	public static final String SERVER_URL = ""; //TODO: add target url
	private static final String TAG = "POST_API";
	
	@Override
	protected Void doInBackground(String... params) {
		System.out.println(params.toString());
		String extension = params[0];
		String json = params[1];
		try {
			//Create an HTTP client
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(SERVER_URL+extension);
			post.setEntity(new StringEntity(json));
			//Perform the request and check the status code
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == 200) {
				// TODO: decide what to do				
			} else {
				Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
			}
		} catch(Exception ex) {
			Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
		}
		return null;	
	}

}
