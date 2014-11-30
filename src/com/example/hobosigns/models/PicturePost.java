package com.example.hobosigns.models;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import com.example.hobosigns.rest.MyCallable;
import com.example.hobosigns.rest.PostAPI;
import com.example.hobosigns.rest.PostMultipartAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PicturePost extends Post {

	private String file;
	private static final String add_post_extension ="/add_post";
	
	public static Post jsonToPicPost(String jsonString){
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		PicturePost post = gson.fromJson(jsonString, PicturePost.class);
		return post;
	}
	
	public PicturePost(String file, String caption, long postID, String author, 
			double lat, double lon, Date date, String mediaType, String locationName, 
			double distance, String mediaUri) {
		super(postID, author, lat, lon, date, mediaType, locationName, distance, caption, mediaUri);
		this.file = file;
	}
	
	public static byte[] bitmapToByte(Bitmap bitmap){
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bitmap.compress(CompressFormat.JPEG, 100, stream);
	    return stream.toByteArray();
	}
	
	public Bitmap getBitmapImage(){
		return BitmapFactory.decodeFile(file);
	}
	
	private List<NameValuePair> getAsNameValPair(String accessToken){
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(5);
    	parameters.add(new BasicNameValuePair("caption", getCaption()));
    	parameters.add(new BasicNameValuePair("longitude", String.valueOf(getLon())));
    	parameters.add(new BasicNameValuePair("latitude", String.valueOf(getLat())));
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("image", file));
		return parameters;
	}
	
	public void postPost(final Context context, String accessToken){
		List<NameValuePair> params = getAsNameValPair(accessToken);
		PostAPI post = new PostMultipartAPI(new MyCallable<Integer>() {

			@Override
			public Integer call() throws Exception {return null;}

			@Override
			public Integer call(JSONObject jsonObj) throws Exception {
				if(context != null){
					boolean success = jsonObj.getBoolean("success");
					if(success){
						Toast.makeText(context, "Sign has been posted", Toast.LENGTH_SHORT).show();
					} else{
						Toast.makeText(context, "Failed to upload sign", Toast.LENGTH_SHORT).show();
					}
				}
				return null;
			}
			
		}, add_post_extension);
		post.execute(params);
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
