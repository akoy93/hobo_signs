package com.example.hobosigns.models;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import com.example.hobosigns.rest.PostAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Post {

	private long postID;
	private String author;
	private double lat;
	private double lon;
	private Date date;
	private static final String extension =""; //TODO: add extension
	
	public static Post jsonToPost(String jsonString){
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		Post post = gson.fromJson(jsonString, Post.class);
		return post;
	}
	
	public static Post gsonToPost(InputStream jsonStream){
		Reader reader = new InputStreamReader(jsonStream);
		GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
		Gson gson = gsonBuilder.create();
		Post post = gson.fromJson(reader, Post.class);
		return post;
	}

	public Post(long postID, String author, double lat, double lon, Date date){
		this.postID = postID;
		this.author = author;
		this.lat = lat;
		this.lon = lon;
		this.date = date;
	}
	
	public void postPost(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
		Gson gson = gsonBuilder.create();
		String jsonString = gson.toJson(this, Post.class);
		PostAPI post = new PostAPI();
		post.execute(extension, jsonString);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public long getPostID() {
		return postID;
	}
}
