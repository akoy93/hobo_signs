package com.example.hobosigns;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hobosigns.models.Post;
import com.example.hobosigns.models.User;
import com.example.hobosigns.rest.MyCallable;

public class SignMapActivity extends Activity implements ActionBar.TabListener {

	private static final String TAG = "SignMapActivity";
	
	GPSTracker gps;
	
	SharedPreferences preferencesReader;
	
	SignMapFragment mapFrag;
	SignMapListFragment listFrag;
	SignMapFiltersFragment filterFrag;
	
	ArrayList<Post> posts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_map);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		preferencesReader = getApplicationContext().getSharedPreferences(SettingsActivity.SETTINGS_KEY, Context.MODE_PRIVATE);
		gps = new GPSTracker(getApplicationContext());
		posts = new ArrayList<Post>();
		
		mapFrag = new SignMapFragment(this);
		actionBar.addTab(actionBar.newTab()
				.setText("Map")
				.setTabListener(new TabListener(mapFrag)));
		
		Log.i(TAG, "Map fragment added");
		
		listFrag = new SignMapListFragment(this);
		actionBar.addTab(actionBar.newTab()
				.setText("List")
				.setTabListener(new TabListener(listFrag)));
		
		Log.i(TAG, "List fragment added");
		
		filterFrag = new SignMapFiltersFragment(this);
		actionBar.addTab(actionBar.newTab()
				.setText("Filters")
				.setTabListener(new TabListener(filterFrag)));
		
		Log.i(TAG, "Filter fragment added");
	}

	public void updatePosts() {
		SharedPreferences preferencesReader = getSharedPreferences(SignMapFiltersFragment.FILTER_SETTINGS_KEY, Context.MODE_PRIVATE);
		if (preferencesReader.getBoolean(SignMapFiltersFragment.FILTER_ENABLED_KEY, false)) {
			Log.i(TAG, "Filters ENABLED");
			posts.clear();
			mapFrag.resetMarkers();
			getHashtaggedSigns(preferencesReader.getStringSet(SignMapFiltersFragment.FILTER_TAGS_KEY, null));
		} else {
			Log.i(TAG, "Filters DISABLED");
			posts.clear();
			mapFrag.resetMarkers();
			getNearbySigns();
		}
	}
	
	public void getNearbySigns() {
		if (gps.canGetLocation()) {
			gps.getLocation();
			double lat = gps.getLatitude();
			double lng = gps.getLongitude();
			int rad = preferencesReader.getInt(SettingsActivity.RADIUS_KEY, 1000000);
			
			Log.i("Get Nearby Signs", "Lat: " + lat + ", Lng: " + lng + ", Radius: " + rad);
			
			Post.getRangePosts(new MyCallable<Void>() {
				@Override
				public Void call() throws Exception {
					return null;
				}
	
				@Override
				public Void call(JSONObject jsonObject) throws Exception {
					JSONArray arr = jsonObject.getJSONArray("response");
					// Log.i("Get Nearby Signs", arr.toString());
					
					for (int i = 0; i < arr.length(); i++) {
						Post p = Post.jsonToPost(arr.getString(i));
						posts.add(p);
						Log.i("Get Nearby Signs", arr.getString(i));
					}
					
					if (listFrag.adapter != null) listFrag.adapter.notifyDataSetChanged();
					mapFrag.updateMarkers();
					
					return null;
				}
				
			}, User.getSavedUser(this).getAccessToken(), 39.0, -77.0, rad);
		} else {
			Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
		}
	}

	public void getHashtaggedSigns(Set<String> tags) {
		if (gps.canGetLocation()) {
			gps.getLocation();
			double lat = gps.getLatitude();
			double lng = gps.getLongitude();
			int rad = preferencesReader.getInt(SettingsActivity.RADIUS_KEY, 1000000);
			
			Log.i("Get Nearby Signs", "Lat: " + lat + ", Lng: " + lng + ", Radius: " + rad);
			
			
			for (final String tag : tags) {
				Log.i("Get Hashtagged Signs", "Tag search: " + tag);
				Post.getHashtaggedPosts(new MyCallable<Void>() {
					@Override
					public Void call() throws Exception {
						return null;
					}
		
					@Override
					public Void call(JSONObject jsonObject) throws Exception {
						Log.i(tag, jsonObject.toString());
						JSONArray arr = jsonObject.getJSONArray("response");
						// Log.i("Get Hashtagged Signs", arr.toString());
						
						for (int i = 0; i < arr.length(); i++) {
							Post p = Post.jsonToPost(arr.getString(i));
							synchronized(posts) {
								if (!posts.contains(p)) {
									posts.add(p);
								}
							}
							Log.i("Get Hashtagged Signs", arr.getString(i));
						}
						
						listFrag.adapter.notifyDataSetChanged();
						synchronized (mapFrag) {
							mapFrag.resetMarkers();
							mapFrag.updateMarkers();
						}
						
						return null;
					}
				}, User.getSavedUser(this).getAccessToken(), 39.0, -77.0, rad, tag);
			}
				
		} else {
			Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// Launch options activity
			Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(i);
			return true;
		} else if (id == R.id.my_signs) {
			// Launch My Signs activity
			Intent i = new Intent(getApplicationContext(), MySignsActivity.class);
			startActivity(i);
			return true;
		} else if (id == R.id.logout) {
			// Launch My Signs activity
			User.logout(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		// mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public static class TabListener implements ActionBar.TabListener {
		private static final String TAG = "TabListener";
		private final Fragment mFragment;

		public TabListener(Fragment fragment) {
			mFragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		// When a tab is selected, change the currently visible Fragment
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Log.i(TAG, "onTabSelected called");

			if (null != mFragment) {
				ft.replace(R.id.fragment_container, mFragment);
			}
		}

		// When a tab is unselected, remove the currently visible Fragment
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			Log.i(TAG, "onTabUnselected called");

			if (null != mFragment)
				ft.remove(mFragment);
		}
	}
	
	
}
