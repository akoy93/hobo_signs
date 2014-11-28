package com.example.hobosigns.rest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.example.hobosigns.models.Post;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetAPI extends AsyncTask<String, Void, HttpEntity> {

	private static final String TAG = "GetRequest";
	MyCallable<Integer> whatToDoWithData;

	public GetAPI(MyCallable<Integer> whatToDoWithData){
		this.whatToDoWithData = whatToDoWithData;
	}

	@Override
	protected HttpEntity doInBackground(String... params) {

		try {
			//Create an HTTP client
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(PostAPI.SERVER_URL);
			
			//Perform the request and check the status code
			HttpResponse response = client.execute(get);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				return entity;
			} else {
				Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
			}
		} catch(Exception ex) {
			Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
		}
		return null;
	}
	
	public void onPostExecute(HttpEntity jsonResult){
		if(jsonResult == null){return;}
		try {
			whatToDoWithData.call(jsonResult);
			/**	In the callable do something like
			 * 
					InputStream content = entity.getContent();
					Reader reader = new InputStreamReader(jsonResult);
					GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
					Gson gson = gsonBuilder.create();
			 		List<Post> posts = new ArrayList<Post>();
					posts = Arrays.asList(jsonResult.fromJson(reader, class used--Post[].class););
					content.close();
			 * */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	}

}
