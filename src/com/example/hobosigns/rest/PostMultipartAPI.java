package com.example.hobosigns.rest;

import java.io.File;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class PostMultipartAPI extends PostAPI {

	public PostMultipartAPI(MyCallable<?> whatToDoWithData, String extension) {
		super(whatToDoWithData, extension);
	}
	
	
	@Override
	protected JSONObject doInBackground(List<NameValuePair>... params) {
		List<NameValuePair> nameValuePairs = params[0];
		try {
			//Create an HTTP client
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter("Connection", "Keep-Alive");
			client.getParams().setParameter("Content-Type", "multipart/form-data;");
			HttpPost post = new HttpPost(SERVER_URL+extension);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			for (NameValuePair nameValuePair : nameValuePairs) {
			    if (nameValuePair.getName().equalsIgnoreCase("image")) {
			        File imgFile = new File(nameValuePair.getValue());
					FileBody fileBody = new FileBody(imgFile,  "image/jpeg"); //image should be a String
					builder.addPart("image", fileBody);
			    } 
			    else {
			        if (nameValuePair.getValue()!=null) {
		                builder.addPart(nameValuePair.getName(), new StringBody((String)nameValuePair.getValue()));
			        }
			    }
			}
			
			if(nameValuePairs != null){
				client.getParams().setParameter("Content-Type", "multipart/form-data;");
			}
			HttpEntity multipartEntity = builder.build();
			post.setEntity(multipartEntity);
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

}
