package com.example.hobosigns;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hobosigns.models.Post;

public class SignMapListFragment extends Fragment {
	
	private static final String[] SIGNS = { "Sign 1", "Sign 2", "Sign 3" };
	
	SignMapActivity parent;
	ArrayList<Post> list;
	SignListAdapter adapter;
	
	public SignMapListFragment(SignMapActivity parent) {
		this.parent = parent;
		list = parent.posts;
		this.adapter = new SignListAdapter();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View view = inflater.inflate(R.layout.fragment_sign_list, container, false);
		
		ListView list = (ListView) view.findViewById(R.id.sign_list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> par, View view, int pos, long id) {
				Post post = (Post) adapter.getItem(pos);
				Toast.makeText(parent, post.getLocationName(), Toast.LENGTH_SHORT).show();
			}
			
		});
		
		Button b = (Button) view.findViewById(R.id.list_drop_sign_button);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Launch sign dropping activity
				Intent intent = new Intent(v.getContext(), CameraActivity.class);
				startActivity(intent);	
			}
		});
		
		return view;
	}

	class SignListAdapter extends BaseAdapter {
		
		private ArrayList<Post> list;
		
		public SignListAdapter() {
			list = parent.posts;
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

		@Override
		public View getView(int pos, View convertView, ViewGroup p) {
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(parent).inflate(R.layout.sign_list_item, null);
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
					Toast.makeText(parent, post.getLocationName(), Toast.LENGTH_SHORT).show();
				}
			});*/
			
			return view;
		}
		
		public void update(ArrayList<Post> posts) {
			list = posts;
			this.notifyDataSetChanged();
		}
		
	}
	
}
