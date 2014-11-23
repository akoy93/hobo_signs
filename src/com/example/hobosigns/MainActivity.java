package com.example.hobosigns;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final EditText username = (EditText) findViewById(R.id.username_field);
		final EditText password = (EditText) findViewById(R.id.password_field);
		
		final Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Check username and password stuff
				username.getText();
				password.getText();
				
				// If valid open up map activity thing
				Intent i = new Intent(getApplicationContext(), SignMapActivity.class);
				startActivity(i);				
			}
			
		});
	}
}
