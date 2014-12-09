package com.example.hobosigns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hobosigns.SignMapFiltersFragment.FilterListAdapter;
import com.example.hobosigns.models.Post;
import com.example.hobosigns.models.User;
import com.example.hobosigns.rest.MyCallable;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class SignMapActivity extends Activity implements ActionBar.TabListener {

	private static final String TAG = "SignMapActivity";

	GPSTracker gps;

	SharedPreferences preferencesReader;

	SignMapFragment mapFrag;
	SignMapListFragment listFrag;
	SignMapFiltersFragment filterFrag;

	ArrayList<Post> posts;

	// college park is default location
	private double lat = 39;
	private double lng = -77;
	private int rad = 1500;

	private boolean viewMySigns = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_map);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		preferencesReader = getApplicationContext().getSharedPreferences(
				SettingsActivity.SETTINGS_KEY, Context.MODE_PRIVATE);
		gps = new GPSTracker(getApplicationContext());
		posts = new ArrayList<Post>();

		mapFrag = new SignMapFragment(this);
		actionBar.addTab(actionBar.newTab().setText("Map")
				.setTabListener(new TabListener(mapFrag)));

		Log.i(TAG, "Map fragment added");

		listFrag = new SignMapListFragment(this);
		actionBar.addTab(actionBar.newTab().setText("List")
				.setTabListener(new TabListener(listFrag)));

		Log.i(TAG, "List fragment added");

		filterFrag = new SignMapFiltersFragment(this);
		actionBar.addTab(actionBar.newTab().setText("Hashtags")
				.setTabListener(new TabListener(filterFrag)));

		Log.i(TAG, "Filter fragment added");
	}
	
	protected void onResume(){
		super.onResume();
		if(User.getSavedUser(this) == null){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		mapFrag.sync();
	}

	public void updateMapLocation(LatLng location, LatLngBounds bounds) {
		lat = location.latitude;
		lng = location.longitude;
		float results[] = new float[1];
		Location.distanceBetween(bounds.southwest.latitude, 0,
				bounds.northeast.latitude, 0, results);
		int newRad = (int) results[0] / 2;
		if (newRad != 0) {
			rad = newRad;
		}
	}

	public void updatePosts() {
		SharedPreferences preferencesReader = getSharedPreferences(
				SignMapFiltersFragment.FILTER_SETTINGS_KEY,
				Context.MODE_PRIVATE);
		if (preferencesReader.getBoolean(
				SignMapFiltersFragment.FILTER_ENABLED_KEY, false)) {
			Log.i(TAG, "Filters ENABLED");
			getHashtaggedSigns(preferencesReader.getStringSet(
					SignMapFiltersFragment.FILTER_TAGS_KEY, null));
		} else {
			Log.i(TAG, "Filters DISABLED");
			if (viewMySigns) {
				Log.i(TAG, "Populating user's signs");
				getMySigns();
			} else {
				getNearbySigns();
			}
		}
	}

	public void resetHashtags() {
		// update hashtags
		SharedPreferences preferencesReader = this.getSharedPreferences(
				SignMapFiltersFragment.FILTER_SETTINGS_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencesReader.edit();
		editor.putBoolean(SignMapFiltersFragment.FILTER_ENABLED_KEY, false);
		editor.commit();
		filterFrag.resetAndUpdateAdapter();
	}

	public void getMySigns() {
		Log.i("Get My Signs", lat + ", " + lng);

		Post.getMyPosts(new MyCallable<Void>() {
			@Override
			public Void call() throws Exception {
				return null;
			}

			@Override
			public Void call(JSONObject jsonObject) throws Exception {
				ArrayList<Post> newPosts = new ArrayList<Post>();
				@SuppressWarnings("unchecked")
				ArrayList<Post> oldPosts = (ArrayList<Post>) posts.clone();

				JSONArray arr = jsonObject.getJSONArray("response");

				for (int i = 0; i < arr.length(); i++) {
					Post p = Post.jsonToPost(arr.getString(i));
					newPosts.add(p);
					// Log.i("Get My Signs", arr.getString(i));
				}

				posts = newPosts;
				listFrag.adapter.update(posts);

				mapFrag.updateMarkers(oldPosts, newPosts);

				return null;
			}

		}, User.getSavedUser(this).getAccessToken(), lat, lng);
	}

	public void getNearbySigns() {
		// if (gps.canGetLocation()) {
		// gps.getLocation();
		// double lat = gps.getLatitude();
		// double lng = gps.getLongitude();
		// int rad = preferencesReader.getInt(SettingsActivity.RADIUS_KEY,
		// 1000000);

		Log.i("Get Nearby Signs", "Lat: " + lat + ", Lng: " + lng
				+ ", Radius: " + rad);

		Post.getRangePosts(new MyCallable<Void>() {
			@Override
			public Void call() throws Exception {
				return null;
			}

			@Override
			public Void call(JSONObject jsonObject) throws Exception {
				ArrayList<Post> newPosts = new ArrayList<Post>();
				@SuppressWarnings("unchecked")
				ArrayList<Post> oldPosts = (ArrayList<Post>) posts.clone();

				JSONArray arr = jsonObject.getJSONArray("response");

				for (int i = 0; i < arr.length(); i++) {
					Post p = Post.jsonToPost(arr.getString(i));
					newPosts.add(p);
				}

				posts = newPosts;
				listFrag.adapter.update(posts);

				mapFrag.updateMarkers(oldPosts, newPosts);

				return null;
			}

		}, User.getSavedUser(this).getAccessToken(), lat, lng, rad);
		// } else {
		// Toast.makeText(this, "Cannot get location",
		// Toast.LENGTH_SHORT).show();
		// }
	}

	public void getHashtaggedSigns(Set<String> tags) {
		// TODO multiple hashtags not working
		Log.i("Get Hashtagged Signs", "Lat: " + lat + ", Lng: " + lng
				+ ", Radius: " + rad);
//		ArrayList<Post> unfiltered = listFrag.adapter.getUnfiltered();
//		listFrag.adapter.setUnfilteredPosts((ArrayList<Post>) posts.clone());
		
		ArrayList<Post> newPosts = new ArrayList<Post>();
		@SuppressWarnings("unchecked")
		ArrayList<Post> oldPosts = (ArrayList<Post>) posts.clone();
		HashSet<Post> newPostSet = new HashSet<Post>();

//		// swap filtered and unfiltered posts
//		if (unfiltered != null) {
//			posts = unfiltered;
//		}
//		
//		Log.i("Hashtags", tags.toString());
		
		for (String tag : tags) {
			for (Post p : posts) {
				Log.i("Posts", posts.toString());
				if (p.getHashtags().indexOf(tag) >= 0
						&& !newPostSet.contains(p)) {
					newPosts.add(p);
					newPostSet.add(p);
				}
			}
		}

		Collections.sort(newPosts, new Comparator<Post>() {

			@Override
			public int compare(Post a, Post b) {
				return (int) (a.getDistance() - b.getDistance());
			}
			
		});
		
		posts = newPosts;
		listFrag.adapter.update(posts);
		mapFrag.updateMarkers(oldPosts, newPosts);

		// if (viewMySigns) {
		//
		// } else {
		// for (final String tag : tags) {
		// Log.i("Get Hashtagged Signs", "Tag search: " + tag);
		// Post.getHashtaggedPosts(new MyCallable<Void>() {
		// @Override
		// public Void call() throws Exception {
		// return null;
		// }
		//
		// @Override
		// public Void call(JSONObject jsonObject) throws Exception {
		// ArrayList<Post> newPosts = new ArrayList<Post>();
		// ArrayList<Post> oldPosts = (ArrayList<Post>) posts
		// .clone();
		// // posts.clear();
		// // mapFrag.resetMarkers();
		//
		// Log.i(tag, jsonObject.toString());
		// JSONArray arr = jsonObject.getJSONArray("response");
		// // Log.i("Get Hashtagged Signs", arr.toString());
		//
		// for (int i = 0; i < arr.length(); i++) {
		// Post p = Post.jsonToPost(arr.getString(i));
		// newPosts.add(p);
		// // Log.i("Get Hashtagged Signs", arr.getString(i));
		// }
		//
		// posts = newPosts;
		// listFrag.adapter.update(posts);
		// synchronized (mapFrag) {
		// // mapFrag.resetMarkers();
		// mapFrag.updateMarkers(oldPosts, newPosts);
		// }
		//
		// return null;
		// }
		// }, User.getSavedUser(this).getAccessToken(), lat, lng, rad, tag);
		// }
		// }
		// // } else {
		// // Toast.makeText(this, "Cannot get location",
		// // Toast.LENGTH_SHORT).show();
		// // }
	}

	public void getHashtags(final FilterListAdapter adapter) {
		// if (parent.gps.canGetLocation()) {
		// parent.gps.getLocation();
		// double lat = parent.gps.getLatitude();
		// double lng = parent.gps.getLongitude();
		// int rad =
		// parent.preferencesReader.getInt(SettingsActivity.RADIUS_KEY,
		// 1000000);

		Log.i("Get All Hashtags", "Lat: " + lat + ", Lng: " + lng
				+ ", Radius: " + rad);

		// handle hashtags for "my signs case"
		int radToUse = viewMySigns ? Integer.MAX_VALUE : rad;

		Post.getAvailableHashtags(new MyCallable<Void>() {
			@Override
			public Void call() throws Exception {
				return null;
			}

			@Override
			public Void call(JSONObject jsonObject) throws Exception {
				adapter.reset();
				Log.i("Get All Hashtags", jsonObject.toString());
				JSONArray arr = jsonObject.getJSONArray("response");
				Log.i("Get All Hashtags", arr.toString());

				for (int i = 0; i < arr.length(); i++) {
					JSONObject o = arr.getJSONObject(i);
					String tag = o.getString("hashtag");
					int num_posts = o.getInt("num_posts");

					adapter.add(tag, num_posts);

					Log.i("Filters", tag + ": " + num_posts);
				}

				adapter.setupCheckedSavedTags();
				return null;
			}

		}, User.getSavedUser(this).getAccessToken(), lat, lng, radToUse);
		// }
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
		if (id == R.id.my_signs) {
			if (viewMySigns) {
				Toast.makeText(this, "Getting all signs...", Toast.LENGTH_SHORT)
						.show();
				viewMySigns = false;
				filterFrag.resetAndUpdateAdapter();
				this.updatePosts();
				item.setTitle(R.string.my_signs);
			} else {
				Toast.makeText(this, "Getting your signs...",
						Toast.LENGTH_SHORT).show();
				viewMySigns = true;
				filterFrag.resetAndUpdateAdapter();
				this.updatePosts();
				item.setTitle(R.string.all_signs);
			}

			return true;
		} else if (id == R.id.logout) {
			// Launch My Signs activity
			User.logout(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		// mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
