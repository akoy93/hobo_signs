package com.example.hobosigns.models;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

import com.example.hobosigns.rest.PostAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PicturePost extends Post {

	private byte[] picture;
	private String caption;
	private static final String extension =""; //TODO: add extension
	
	public static Post jsonToPicPost(String jsonString){
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		PicturePost post = gson.fromJson(jsonString, PicturePost.class);
		return post;
	}
	
	public PicturePost(byte[] picture, String caption, long postID, String author, double lat, double lon, Date date) {
		super(postID, author, lat, lon, date);
		this.caption = caption;
		this.picture = picture;
	}
	
	public static byte[] bitmapToByte(Bitmap bitmap){
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bitmap.compress(CompressFormat.JPEG, 100, stream);
	    return stream.toByteArray();
	}
	
	public Bitmap getBitmapImage(){
		return BitmapFactory.decodeByteArray(picture , 0, picture.length);
	}
	
	public void postPost(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
		Gson gson = gsonBuilder.create();
		String jsonString = gson.toJson(this, PicturePost.class);
		PostAPI post = new PostAPI();
		post.execute(extension, jsonString);
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

}
