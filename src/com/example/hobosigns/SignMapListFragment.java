package com.example.hobosigns;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SignMapListFragment extends Fragment {
	
	private static final String[] SIGNS = { "Sign 1", "Sign 2", "Sign 3" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		// use different layout definition, depending on whether device is pre-
		// or post-honeycomb

		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
				: android.R.layout.simple_list_item_1;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_list, container, false);
		
		ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, SIGNS));
		
		Button b = (Button) view.findViewById(R.id.list_drop_sign_button);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Launch sign dropping activity
				Intent intent = new Intent(v.getContext(),CameraActivity.class);
				startActivity(intent);	
			}
		});
		
		return view;
	}

}
