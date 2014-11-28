package com.example.hobosigns.rest;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class PostAPI extends AsyncTask<String, Void, HttpEntity> {
	
	public static final String SERVER_URL = "http://104.236.2.144:49165"; //TODO: add target url
	private static final String TAG = "POST_API";
	MyCallable<?> whatToDoWithData = null;
	
	public PostAPI(MyCallable<?> whatToDoWithData){
		this.whatToDoWithData = whatToDoWithData;
	}
	
	public PostAPI(){
		
	}
	
	@Override
	protected HttpEntity doInBackground(String... params) {
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
				HttpEntity entity = response.getEntity();
				return entity;	
			} else {
				String result = EntityUtils.toString(response.getEntity());  
				Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
			}
		} catch(Exception ex) {
			Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
		}
		return null;	
	}
	
	@Override
	public void onPostExecute(HttpEntity jsonResult){
		if(jsonResult == null){return;}
		try {
			if(whatToDoWithData != null){
				whatToDoWithData.call(jsonResult);
			}
			/**	In the callable do something like
			 * 
					String result = EntityUtils.toString(response.getEntity());  
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
