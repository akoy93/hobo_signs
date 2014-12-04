package com.example.hobosigns;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.hobosigns.models.User;

public class MainActivity extends Activity {

	final public static String Tag= "Hobo Signs";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//check the user is not already logged in:
		User user = User.getSavedUser(this);
		if(user != null){
			User.checkLoggedIn(this);
		}
		// we just wait until we find out from the network
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
	}
}
