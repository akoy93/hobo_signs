package com.example.hobosigns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hobosigns.models.Post;
import com.example.hobosigns.models.User;
import com.example.hobosigns.rest.MyCallable;

public class SignMapFiltersFragment extends Fragment {

	private static final String[] TAGS = { "Tag 1", "Tag 2", "Tag 3" };
	
	private SignMapActivity parent;
	private FilterListAdapter adapter;
	
	public static String FILTER_SETTINGS_KEY = "HoboSignFilterSettings";
	public static String FILTER_ENABLED_KEY = "HoboSignFilterEnabled";
	public static String FILTER_TAGS_KEY = "HoboSignFilterTags";
	
	public SignMapFiltersFragment(SignMapActivity parent) {
		this.parent = parent;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_filter_list, container, false);
		
		ListView list = (ListView) view.findViewById(R.id.filter_list);
		// list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, SIGNS));
		adapter = new FilterListAdapter();
		list.setAdapter(adapter);
		
		Button apply = (Button) view.findViewById(R.id.filter_apply_button);
		apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.saveTags();
			}
		});
		
		Button reset = (Button) view.findViewById(R.id.filter_reset_button);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.resetChecked();
				SharedPreferences preferencesReader = parent.getSharedPreferences(FILTER_SETTINGS_KEY, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferencesReader.edit();
				editor.putBoolean(FILTER_ENABLED_KEY, false);
				editor.commit();
			}
		});
		
		getHashtags();
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void getHashtags() {
		if (parent.gps.canGetLocation()) {
			parent.gps.getLocation();
			double lat = parent.gps.getLatitude();
			double lng = parent.gps.getLongitude();
			int rad = parent.preferencesReader.getInt(SettingsActivity.RADIUS_KEY, 1000000);
			
			Log.i("Get All Hashtags", "Lat: " + lat + ", Lng: " + lng + ", Radius: " + rad);
		
			Post.getAvailableHashtags(new MyCallable<Void>() {
				@Override
				public Void call() throws Exception {
					return null;
				}

				@Override
				public Void call(JSONObject jsonObject) throws Exception {
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
				
			}, User.getSavedUser(parent).getAccessToken(), 39.0, -77.0, rad);
		}
	}
	
	private class Tag {
		String tag;
		int numPosts;
		boolean isChecked;
		
		public Tag(String tag, int numPosts) {
			this.tag = tag;
			this.numPosts = numPosts;
			this.isChecked = false;
		}
				
		public void setIsChecked(boolean isChecked) {
			this.isChecked = isChecked;
			// Log.i("Filters", tag + " is " + isChecked);
		}
	}
	
	private class FilterListAdapter extends BaseAdapter implements ListAdapter {

		ArrayList<Tag> list;
		
		public FilterListAdapter() {
			list = new ArrayList<Tag>();
		}
		
		public void add(String tag, int numPosts) {
			list.add(new Tag(tag, numPosts));
			notifyDataSetChanged();
		}
		
		public void resetChecked() {
			for (Tag t : list) {
				t.setIsChecked(false);
			}
			notifyDataSetChanged();
		}
		
		private void saveTags() {
			SharedPreferences preferencesReader = parent.getSharedPreferences(FILTER_SETTINGS_KEY, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferencesReader.edit();
			editor.putBoolean(FILTER_ENABLED_KEY, true);
			
			HashSet<String> set = null;
			if (list.size() > 0) {
				set = new HashSet<String>();
				for (Tag t : list) {
					if (t.isChecked) set.add(t.tag);
				}
			}
			editor.putStringSet(FILTER_TAGS_KEY, set);
			editor.commit();
		}
		
		private void setupCheckedSavedTags() {
			SharedPreferences preferencesReader = parent.getSharedPreferences(FILTER_SETTINGS_KEY, Context.MODE_PRIVATE);
			if (preferencesReader.getBoolean(FILTER_ENABLED_KEY, false)) {
				Set<String> tags = preferencesReader.getStringSet(FILTER_TAGS_KEY, null);
				if (tags != null) {
					for (Tag t : list) {
						if (tags.contains(t.tag)) t.setIsChecked(true);
					}
				}
			}
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int pos) {
			return list.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup par) {
			final Tag item = (Tag) getItem(pos);
			
			RelativeLayout itemLayout;
			
			if (convertView == null) {
				LayoutInflater inflate = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				itemLayout = (RelativeLayout) inflate.inflate(R.layout.filter_list_item, null);
			} else {
				itemLayout = (RelativeLayout) convertView;
			}
			
			final CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.filterCheckBox);
			cb.setChecked(item.isChecked);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					item.setIsChecked(isChecked);
				}
			});
			
			final TextView filterTextView = (TextView) itemLayout.findViewById(R.id.filterText);
			filterTextView.setText(item.tag  + ": " + item.numPosts);
			filterTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cb.toggle();
				}
			});
			
			return itemLayout;
		}
		
	}
}
