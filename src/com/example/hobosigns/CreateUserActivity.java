package com.example.hobosigns;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.hobosigns.models.User;

public class CreateUserActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//otherwise we make them do so
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		
		final EditText username = (EditText) findViewById(R.id.create_username_field);
		final EditText password = (EditText) findViewById(R.id.create_password_field);
		final EditText confPassword = (EditText) findViewById(R.id.create_confirm_field);
		
		final Button createButton = (Button) findViewById(R.id.create_user_button);
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Check username and password stuff
				// If valid open up map activity thing
				User user = new User(null, null, username.getText().toString(),
						password.getText().toString(), confPassword.getText().toString());
				user.createAccount(CreateUserActivity.this);			
			}
			
		});
	}
	

}
