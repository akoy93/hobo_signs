package com.example.hobosigns.models;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.hobosigns.SignMapActivity;
import com.example.hobosigns.rest.MyCallable;
import com.example.hobosigns.rest.PostAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
	@Expose
	private String id;
	@SerializedName("access_token")
	@Expose
	private String accessToken;
	@Expose
	private String username;
	@Expose
	private String password;
	@SerializedName("password_confirm")
	@Expose
	private String passwordConfirm;
	private static final String login_extension ="/login";
	private static final String create_extension ="/create_account";
	private static final String check_logged_in_extension ="/is_logged_in";
	private static final String logout_extension ="/logout";
	static final String PREFS_KEY = "HoboSignsUserInformationKey";
	static final String PREFS_NAME = "HoboSignsUserInformation";
	
	public User(String id, String accessToken, String username,
			String password, String passwordConfirm) {
		super();
		this.id = id;
		this.accessToken = accessToken;
		this.username = username;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
	}
	
	public static User gsonToUser(InputStream jsonStream){
		Reader reader = new InputStreamReader(jsonStream);
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		User user = gson.fromJson(reader, User.class);
		return user;
	}
	
	public void login(final Context context){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
		Gson gson = gsonBuilder.create();
		String jsonString = gson.toJson(this, User.class);
		PostAPI post = new PostAPI(
				new MyCallable<Integer>(){
					@Override
					public Integer call() throws Exception { return null;}

					@Override
					public Integer call(HttpEntity jsonReader)
							throws Exception {
						JSONObject mainObject = new JSONObject(jsonReader.toString());
						boolean success = mainObject.getBoolean("success");
						if(success == true && User.this != null){
							JSONObject responseObject = mainObject.getJSONObject("response");
							String  uniName = responseObject.getString("access_token");
							User.this.setAccessToken(uniName);
							//write the user to app context
							User.this.saveUserToSharedContext(context);
							//start signmap activity
							Intent intent = new Intent(context, SignMapActivity.class);
							context.startActivity(intent);
						} else {
							//TODO make toast informing you of failure.
							Toast.makeText(context, "Failed to log in.", Toast.LENGTH_SHORT).show();
						}
						return null;
					}
					
				}
		);

		post.execute(login_extension, jsonString);
	}
	
	public void saveUserToSharedContext(Context appContext){
		// Serialize the object into a string
		String serializedData = this.serialize();
		
		// Save the serialized data into a shared preference
		SharedPreferences preferencesReader = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencesReader.edit();
		editor.putString(PREFS_KEY, serializedData);
		editor.commit();
	}

	public static User getSavedUser(Context appContext){
		// Read the shared preference value
		SharedPreferences preferencesReader = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String serializedDataFromPreference = preferencesReader.getString(PREFS_KEY, null);
		// Create a new object from the serialized data with the same state
        Gson gson = new Gson();
        User user = gson.fromJson(serializedDataFromPreference, User.class);
        return user;
	}
	 
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
	
	public void checkLoggedIn(final Context context){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
		Gson gson = gsonBuilder.create();
		String jsonString = gson.toJson(this, User.class);
		PostAPI post = new PostAPI(new MyCallable<Integer>(){
			@Override
			public Integer call() throws Exception { return null;}

			@Override
			public Integer call(HttpEntity jsonReader)
					throws Exception {
				JSONObject mainObject = new JSONObject(jsonReader.toString());
				boolean success = mainObject.getBoolean("success");
				if(success == true){
					//start signmap activity
					Intent intent = new Intent(context, SignMapActivity.class);
					context.startActivity(intent);
				} else {
					SharedPreferences preferencesReader = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferencesReader.edit();
					editor.putString(PREFS_KEY, null);	
					editor.commit();
				}
				return null;
			}
			
		});
		post.execute(check_logged_in_extension, jsonString);
	}
	
	public void logout(Context context){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
		Gson gson = gsonBuilder.create();
		String jsonString = gson.toJson(this, User.class);
		PostAPI post = new PostAPI();
		SharedPreferences preferencesReader = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencesReader.edit();
		editor.putString(PREFS_KEY, null);	
		editor.commit();
		post.execute(logout_extension, jsonString);
	}
	
	public void createAccount(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
		Gson gson = gsonBuilder.create();
		String jsonString = gson.toJson(this, User.class);
		PostAPI post = new PostAPI();
		post.execute(create_extension, jsonString);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

}
