package com.example.hobosigns.rest;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetAPI extends AsyncTask<List<NameValuePair>, Void, JSONObject> {

	private static final String TAG = "GetRequest";
	MyCallable<?> whatToDoWithData;
	String extension;

	public GetAPI(MyCallable<?> onResponseMethod, String extension){
		this.whatToDoWithData = onResponseMethod;
		this.extension = extension;
	}

	@Override
	protected JSONObject doInBackground(List<NameValuePair>... params) {
		List<NameValuePair> nameValuePairs = params[0];
		try {
			//Create an HTTP client
			HttpClient client = new DefaultHttpClient();
			if(nameValuePairs != null){   
				String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
				extension += ("?"+paramString);
			}
			HttpGet get = new HttpGet(PostAPI.SERVER_URL+extension);
			//Perform the request and check the status code
			HttpResponse response = client.execute(get);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
	            String retSrc = EntityUtils.toString(entity); 
				JSONObject mainObject = new JSONObject(retSrc);
				return mainObject;
			} else {
				Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
			}
		} catch(Exception ex) {
			Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
		}
		return null;
	}
	
	public void onPostExecute(JSONObject jsonObj){
		if(jsonObj == null){return;}
		try {
			whatToDoWithData.call(jsonObj);
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
