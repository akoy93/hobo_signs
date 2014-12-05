package com.example.hobosigns.models;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.hobosigns.LoginActivity;
import com.example.hobosigns.MainActivity;
import com.example.hobosigns.SignMapActivity;
import com.example.hobosigns.rest.GetAPI;
import com.example.hobosigns.rest.MyCallable;
import com.example.hobosigns.rest.PostAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
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
	
	private List<NameValuePair> getAsNameValPair(){
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(4);
    	parameters.add(new BasicNameValuePair("access_token", accessToken));
    	parameters.add(new BasicNameValuePair("username", username));
    	parameters.add(new BasicNameValuePair("password", password));
    	parameters.add(new BasicNameValuePair("password_confirm", passwordConfirm));
		return parameters;
	}
	
	public void login(final Context context){
		List<NameValuePair> params = this.getAsNameValPair();
		PostAPI post = new PostAPI(
				new MyCallable<Integer>(){
					@Override
					public Integer call() throws Exception { return null;}

					@Override
					public Integer call(JSONObject jsonObj)
							throws Exception { 
						boolean success = jsonObj.getBoolean("success");
						if(success == true && User.this != null){
							String uniName = jsonObj.getString("response");
							User.this.setAccessToken(uniName);
							//write the user to app context
							User.this.saveUserToSharedContext(context);
							Log.i(MainActivity.Tag, "Successful login");
							//start signmap activity
							Intent intent = new Intent(context, SignMapActivity.class);
							context.startActivity(intent);
						} else {
							//TODO make toast informing you of failure.
							Toast.makeText(context, "Failed to log in.", Toast.LENGTH_SHORT).show();
							Log.i(MainActivity.Tag, "Unsuccessful login");		
							SharedPreferences preferencesReader = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = preferencesReader.edit();
							editor.putString(PREFS_KEY, null);	
							editor.commit();
						}
						return null;
					}
					
				}
		, login_extension);

		post.execute(params);
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
	
	public static void checkLoggedIn(final Context context){
		User user = User.getSavedUser(context);
		if(user == null){
			// Start a login activity
			Intent intent = new Intent(context,LoginActivity.class);
			context.startActivity(intent);
			return;
		}
		List<NameValuePair> params = user.getAsNameValPair();
		GetAPI get = new GetAPI(new MyCallable<Integer>(){
			@Override
			public Integer call() throws Exception { return null;}

			@Override
			public Integer call(JSONObject jsonObj)
					throws Exception {
				boolean success = jsonObj.getBoolean("response");
				if(success == true){
					//start signmap activity
					Intent intent = new Intent(context, SignMapActivity.class);
					context.startActivity(intent);
				} else {
					SharedPreferences preferencesReader = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferencesReader.edit();
					editor.putString(PREFS_KEY, null);	
					editor.commit();
					// Start a login activity
					Intent intent = new Intent(context,LoginActivity.class);
					context.startActivity(intent);
				}
				return null;
			}
			
		}, check_logged_in_extension);
		get.execute(params);
	}
	
	public static void logout(final Context context){
		User user = User.getSavedUser(context);
		if(user != null){
			List<NameValuePair> params = user.getAsNameValPair();
			PostAPI post = new PostAPI(logout_extension);
			SharedPreferences preferencesReader = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferencesReader.edit();
			editor.putString(PREFS_KEY, null);	
			editor.commit();
			post.execute(params);
		}
		Intent intent = new Intent(context, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}
	
	public void createAccount(){
		List<NameValuePair> params = this.getAsNameValPair();
		PostAPI post = new PostAPI(create_extension);
		post.execute(params);
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
