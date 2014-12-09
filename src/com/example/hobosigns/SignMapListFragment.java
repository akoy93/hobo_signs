package com.example.hobosigns;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.hobosigns.models.Post;

public class SignMapListFragment extends Fragment {
	
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
				// Launch Sign View activity here with post?
				Log.i("ListView", "opening post of type: "+post.getMediaType());
				if(post.getMediaType() != null && post.getMediaType().contains("image/jpeg")){
					// picture post
					Intent intent = new Intent(view.getContext(), ViewSign.class);
					intent.putExtra("postURI", post.getMediaUri());
					intent.putExtra("postCaption", post.getCaption());
					intent.putExtra("postVoteCount", post.getVoteCount());
					intent.putExtra("myVote", post.getMy_vote());
					intent.putExtra("postId", post.getPostID());
					startActivity(intent);		
				} else {
					//Its a video
					Intent intent = new Intent(view.getContext(), ViewVideoSign.class);
					intent.putExtra("postURI", post.getMediaUri());
					intent.putExtra("postCaption", post.getCaption());
					intent.putExtra("postVoteCount", post.getVoteCount());
					intent.putExtra("myVote", post.getMy_vote());
					intent.putExtra("postId", post.getPostID());
					startActivity(intent);	
				}
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

		@SuppressLint("InflateParams")
		@Override
		public View getView(int pos, View convertView, ViewGroup p) {
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(parent).inflate(R.layout.sign_list_item, null);
			} else {
				view = convertView;
			}
			
			final Post post = (Post) getItem(pos);
			
			final TextView caption = (TextView) view.findViewById(R.id.item_caption);
			caption.setText(post.getCaption().trim());
			// caption.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			
			final TextView loc = (TextView) view.findViewById(R.id.item_loc);
			loc.setText(post.getLocationName().trim());
			
			final TextView date = (TextView) view.findViewById(R.id.item_date);
			date.setText(post.getDate().toString());
			
			final TextView dist = (TextView) view.findViewById(R.id.item_dist);
			dist.setText(String.valueOf(post.getDistance()));
			
			final TextView votes = (TextView) view.findViewById(R.id.item_votes);
			int numVotes = post.getVoteCount();
			String voteString = String.valueOf(numVotes);
			if (numVotes > 0) {
				voteString = "+" + voteString;
				votes.setTextColor(Color.GREEN);
			} else if (numVotes < 0) {
				votes.setTextColor(Color.RED);
			} else {
				votes.setTextColor(Color.BLACK);
			}
			votes.setText(voteString);
			
			return view;
		}
		
		public void update(ArrayList<Post> posts) {
			list = posts;
			this.notifyDataSetChanged();
		}
	}
	
}
