package com.example.hobosigns;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignMapFiltersFragment extends ListFragment {

	private static final String[] TAGS = { "Tag 1", "Tag 2", "Tag 3" };
	
	private Activity app;
	
	public SignMapFiltersFragment(Activity parent) {
		this.app = parent;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// use different layout definition, depending on whether device is pre-
		// or post-honeycomb

		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
				: android.R.layout.simple_list_item_1;

		// Set the list adapter for this ListFragment
		setListAdapter(new FilterListAdapter());
		// getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
	}
	
	// Note: ListFragments come with a default onCreateView() method.
	// For other Fragments you'll normally implement this method.
	// 	@Override
	//  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	//		Bundle savedInstanceState)

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		// Notify the hosting Activity that a selection has been made.

		Log.i("Filter List","List item clicked");
		
		CheckBox cb = (CheckBox) view.findViewById(R.id.filterCheckBox);
		cb.toggle();
	}
	
	private class FilterListAdapter extends BaseAdapter implements ListAdapter {

		ArrayList<String> list;
		
		public FilterListAdapter() {
			list = new ArrayList<String>();
			for (String tag : TAGS) {
				list.add(tag);
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
		public View getView(int pos, View convertView, ViewGroup parent) {
			String item = (String) getItem(pos);
			
			RelativeLayout itemLayout;
			
			if (convertView == null) {
				LayoutInflater inflate = (LayoutInflater) app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				itemLayout = (RelativeLayout) inflate.inflate(R.layout.filter_list_item, null);
			} else {
				itemLayout = (RelativeLayout) convertView;
			}
			
			final CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.filterCheckBox);
			
			final TextView filterTextView = (TextView) itemLayout.findViewById(R.id.filterText);
			filterTextView.setText(item);
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
