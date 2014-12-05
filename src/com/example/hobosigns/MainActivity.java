package com.example.hobosigns;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.hobosigns.models.User;

public class MainActivity extends Activity {

	final public static String Tag= "Hobo Signs";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(Tag, "Entered MainActivity");
		//check the user is not already logged in:
		Log.i(Tag, "checking if logged in");
		User.checkLoggedIn(this);
		// we just wait until we find out from the network
		Log.i(Tag, "Setting up entry screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
	}
	
	public void onResume(){
		Log.i(Tag, "resuming main activity");
		Log.i(Tag, "checking if logged in");
		User.checkLoggedIn(this);
		super.onResume();
	}
}
