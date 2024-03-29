package com.example.hobosigns.models;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import android.util.Log;

import com.example.hobosigns.rest.GetAPI;
import com.example.hobosigns.rest.MyCallable;
import com.example.hobosigns.rest.PostAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


class Response {
	boolean success;
	String error;
	String response;
}

interface HoboSignsService {
	@Multipart
	@POST("/add_post")
	void addPost(@Part("image") TypedFile image,
			@Part("access_token") String accessToken,
			@Part("caption") String caption, @Part("latitude") double latitude,
			@Part("longitude") double longitude, Callback<Response> callback);
}

public class Post {

	@SerializedName("id")
	private long postID;
	@SerializedName("owner")
	private String author;
	@SerializedName("latitude")
	private double lat;
	@SerializedName("longitude")
	private double lon;
	@SerializedName("created_at")
	private Date date;
	@SerializedName("media_type")
	private String mediaType;
	@SerializedName("media_url")
	private String mediaUri;
	@SerializedName("location_name")
	private String locationName;
	@SerializedName("vote_count")
	private int voteCount;
	@SerializedName("my_vote")
	private int my_vote;
	private String caption;
	private double distance;
	private String hashtags;
	
	private static final String get_mine_extension ="/my_posts";
	private static final String get_hashtagged_extension ="/get_posts_with_hashtag";
	private static final String get_hashtags_extension ="/hashtags";
	private static final String get_range_extension ="/get_posts";
	private static final String downvote ="/downvote";
	private static final String upvote ="/upvote";
	
	public static Post jsonToPost(String jsonString){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS z");
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
	
	public Post(long postID, String author, double lat, double lon, Date date,
			String mediaType, String locationName, double distance, String caption, String mediaUri) {
		super();
		this.postID = postID;
		this.author = author;
		this.lat = lat;
		this.lon = lon;
		this.date = date;
		this.mediaUri = mediaUri;
		this.mediaType = mediaType;
		this.locationName = locationName;
		this.distance = distance;
		this.setCaption((caption!=null?caption:"default"));
	}
	
	public Post(long postID, String author, double lat, double lon, Date date,
			String mediaType, String locationName, double distance, String caption, String mediaUri, String hashtags) {
		super();
		this.postID = postID;
		this.author = author;
		this.lat = lat;
		this.lon = lon;
		this.date = date;
		this.mediaUri = mediaUri;
		this.mediaType = mediaType;
		this.locationName = locationName;
		this.distance = distance;
		this.setCaption((caption!=null?caption:"default"));
		this.hashtags = hashtags;
	}

	@SuppressWarnings("unchecked")
	public static void getMyPosts(MyCallable<?> onResponseMethod, String accessToken, double lat, double lon){  
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(3);
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
    	parameters.add(new BasicNameValuePair("longitude", String.valueOf(lon)));
		GetAPI get = new GetAPI(onResponseMethod, get_mine_extension);
		get.execute(parameters);
	}
	
	@SuppressWarnings("unchecked")
	public static void getRangePosts(MyCallable<?> onResponseMethod, String accessToken, double lat, double lon, int range){  
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
    	parameters.add(new BasicNameValuePair("longitude", String.valueOf(lon)));
    	parameters.add(new BasicNameValuePair("radius", String.valueOf(range)));
		GetAPI get = new GetAPI(onResponseMethod, get_range_extension);
		get.execute(parameters);
	}
	
	@SuppressWarnings("unchecked")
	public static void getHashtaggedPosts(MyCallable<?> onResponseMethod, String accessToken, double lat, double lon, int range, String hashtag){  
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
    	parameters.add(new BasicNameValuePair("longitude", String.valueOf(lon)));
    	parameters.add(new BasicNameValuePair("radius", String.valueOf(range)));
    	parameters.add(new BasicNameValuePair("hashtag", hashtag));
		GetAPI get = new GetAPI(onResponseMethod, get_hashtagged_extension);
		get.execute(parameters);
	}
	
	@SuppressWarnings("unchecked")
	public static void getAvailableHashtags(MyCallable<?> onResponseMethod, String accessToken, double lat, double lon, int range){  
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
    	parameters.add(new BasicNameValuePair("longitude", String.valueOf(lon)));
    	parameters.add(new BasicNameValuePair("radius", String.valueOf(range)));
		GetAPI get = new GetAPI(onResponseMethod, get_hashtags_extension);
		get.execute(parameters);
	}

	@SuppressWarnings("unchecked")
	public static void downvote(MyCallable<?> onResponseMethod, String accessToken, long postId ){  
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(1);
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("post_id", String.valueOf(postId)));
    	PostAPI post = new PostAPI(onResponseMethod, downvote);
		post.execute(parameters);
	}

	@SuppressWarnings("unchecked")
	public static void upvote(MyCallable<?> onResponseMethod, String accessToken, long postId ){  
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(1);
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("post_id", String.valueOf(postId)));
		PostAPI post = new PostAPI(onResponseMethod, upvote);
		Log.i("upvote request", parameters.toString());
		Log.i("upvote request", accessToken + postId);
		post.execute(parameters);
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public void setPostID(long postID) {
		this.postID = postID;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (this.getClass() != other.getClass()) return false;
		if (this.postID != ((Post) other).postID) return false;
		if (this.voteCount != ((Post) other).voteCount) return false;
		return true;
	}

	public String getMediaUri() {
		return mediaUri;
	}

	public void setMediaUri(String mediaUri) {
		this.mediaUri = mediaUri;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public int getMy_vote() {
		return my_vote;
	}

	public void setMy_vote(int my_vote) {
		this.my_vote = my_vote;
	}	

	@Override
	public int hashCode() {
		return (int) this.postID*37*this.voteCount;
	}
}
