package com.example.hobosigns.models;

import java.util.Date;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PicturePost extends Post {

	private Bitmap picture;
	private String caption;
	
	public static Post jsonToPicPost(String jsonString){
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		PicturePost post = gson.fromJson(jsonString, PicturePost.class);
		return post;
	}
	
	public PicturePost(Bitmap picture, String caption, long postID, String author, float lat, float lon, Date date) {
		super(postID, author, lat, lon, date);
		this.caption = caption;
		this.picture = picture;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

}
