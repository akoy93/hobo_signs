package com.example.hobosigns;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.hobosigns.models.User;

public class LoginActivity extends Activity{
	final public static String Tag= "Hobo Signs";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//check the user is not already logged in:
		User user = User.getSavedUser(this);
		if(user != null){
			User.checkLoggedIn(this);
		}
		//otherwise we make them do so
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final EditText username = (EditText) findViewById(R.id.username_field);
		final EditText password = (EditText) findViewById(R.id.password_field);
		
		final Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Check username and password stuff
				// If valid open up map activity thing
				User user = new User(null, null, username.getText().toString(),
						password.getText().toString(), password.getText().toString());
				user.login(LoginActivity.this);			
			}
			
		});
	}
}
