package com.example.hobosigns;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	public static String SETTINGS_KEY = "HoboSignUserSettings";
	public static String RADIUS_KEY = "HoboSignUserRadius";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		final SharedPreferences preferencesReader = getApplicationContext().getSharedPreferences(SETTINGS_KEY, Context.MODE_PRIVATE);
		
		final EditText radius = (EditText) findViewById(R.id.radius_field);
		radius.setText(preferencesReader.getInt(RADIUS_KEY, 1000000) + "");
		
		final Button save = (Button) findViewById(R.id.save_button); 
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = preferencesReader.edit();
				try {
					int i = Integer.parseInt(radius.getText().toString());
					if (i > 0) {
						editor.putInt(RADIUS_KEY, i);
						editor.commit();
						finish();
					} else {
						invalidRadiusToast();
					}
				} catch (NumberFormatException e) {
					invalidRadiusToast();
				}
			}
		});
		
		final Button cancel = (Button) findViewById(R.id.cancel_button); 
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void invalidRadiusToast() {
		Toast.makeText(getApplicationContext(), "Radius must be a positive integer", Toast.LENGTH_SHORT).show();
	}
	
}
