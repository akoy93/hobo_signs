package com.example.hobosigns.rest;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class PostAPI extends AsyncTask<List<NameValuePair>, Void, JSONObject> {
	
	public static final String SERVER_URL = "http://104.236.2.144:49172";
	protected static final String TAG = "POST_API";
	MyCallable<?> whatToDoWithData = null;
	String extension;
	
	public PostAPI(MyCallable<?> whatToDoWithData, String extension){
		this.whatToDoWithData = whatToDoWithData;
		this.extension = extension;
	}
	
	public PostAPI(String extension){
		this.extension = extension;
	}
	
	@Override
	protected JSONObject doInBackground(List<NameValuePair>... params) {
		List<NameValuePair> nameValuePairs = params[0];
		try {
			//Create an HTTP client
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(SERVER_URL+extension);
			if(nameValuePairs != null){
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
				client.getParams().setParameter("Content-Type", "application/x-www-form-urlencoded;");
			}
			//Perform the request and check the status code
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
	            String retSrc = EntityUtils.toString(entity); 
				JSONObject mainObject = new JSONObject(retSrc);
				return mainObject;	
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
	public void onPostExecute(JSONObject jsonObj){
		if(jsonObj == null){return;}
		try {
			if(whatToDoWithData != null){
				whatToDoWithData.call(jsonObj);
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
