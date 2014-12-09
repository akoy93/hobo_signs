package com.example.hobosigns;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hobosigns.models.Post;
import com.example.hobosigns.models.User;
import com.example.hobosigns.rest.MyCallable;

public class MySignsActivity extends Activity {

	GPSTracker gps;
	MySignsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_signs);
		
		// this.setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, SIGNS));
		ListView lv = (ListView) findViewById(R.id.my_signs_list);
		adapter = new MySignsAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Post post = (Post) adapter.getItem(pos);
				Toast.makeText(getApplicationContext(), post.getLocationName(), Toast.LENGTH_SHORT).show();
			}
			
		});
		
		gps = new GPSTracker(getApplicationContext());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.clear();
		getMySigns();
	}
		
	class MySignsAdapter extends BaseAdapter {
		
		private ArrayList<Post> list;
		
		public MySignsAdapter() {
			list = new ArrayList<Post>();
		}
		
		public void add(Post p) {
			list.add(p);
		}
		
		public void clear() {
			list.clear();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int pos, View convertView, ViewGroup p) {
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.sign_list_item, null);
			} else {
				view = convertView;
			}
			
			final Post post = (Post) getItem(pos);
			
			final TextView t = (TextView) view.findViewById(R.id.item_text);
			t.setText(post.getPostID() + " " + post.getAuthor().trim() + ": " + post.getCaption().trim());
			/*t.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Launch Sign View activity here with post?
					Toast.makeText(getApplicationContext(), post.getLocationName(), Toast.LENGTH_SHORT).show();
				}
			});*/
			
			return view;
		}
		
	}
		
	public void getMySigns() {
		if (gps.canGetLocation()) {
			gps.getLocation();
			double lat = gps.getLatitude();
			double lng = gps.getLongitude();
			Log.i("Get My Signs", lat + ", " + lng);
			
			Post.getMyPosts(new MyCallable<Void>() {
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
						adapter.add(p);
						Log.i("Get My Signs", arr.getString(i));
					}
					
					adapter.notifyDataSetChanged();
					
					return null;
				}
				
			}, User.getSavedUser(this).getAccessToken(), 39.0, -77.0);
		} else {
			Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
		}
	}
}
