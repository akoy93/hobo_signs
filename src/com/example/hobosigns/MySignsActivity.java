package com.example.hobosigns;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MySignsActivity extends ListActivity {

	private static final String[] SIGNS = { "My Sign 1", "My Sign 2", "My Sign 3" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_my_signs);
		
		this.setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, SIGNS));
	}
		
}
