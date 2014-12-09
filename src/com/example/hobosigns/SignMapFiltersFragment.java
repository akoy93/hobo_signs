package com.example.hobosigns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignMapFiltersFragment extends Fragment {

	private SignMapActivity parent;
	private FilterListAdapter adapter;

	public static String FILTER_SETTINGS_KEY = "HoboSignFilterSettings";
	public static String FILTER_ENABLED_KEY = "HoboSignFilterEnabled";
	public static String FILTER_TAGS_KEY = "HoboSignFilterTags";
	private boolean resetEnabled = false;
	private boolean applyEnabled = true;

	public SignMapFiltersFragment(SignMapActivity parent) {
		this.parent = parent;
		this.adapter = new FilterListAdapter();
		this.adapter.reset(); // reset sharedpreferences
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.adapter.update();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_filter_list, container,
				false);

		ListView list = (ListView) view.findViewById(R.id.filter_list);
		// list.setAdapter(new ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_list_item_1, SIGNS));
		list.setAdapter(adapter);

		final Button apply = (Button) view.findViewById(R.id.filter_apply_button);
		final Button reset = (Button) view.findViewById(R.id.filter_reset_button);
		apply.setEnabled(SignMapFiltersFragment.this.applyEnabled);
		reset.setEnabled(SignMapFiltersFragment.this.resetEnabled);
		
		apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SignMapFiltersFragment.this.applyEnabled = false;
				SignMapFiltersFragment.this.resetEnabled = true;
				apply.setEnabled(SignMapFiltersFragment.this.applyEnabled);
				reset.setEnabled(SignMapFiltersFragment.this.resetEnabled);
				
				Toast.makeText(parent, "Filtering posts by hashtag...",
						Toast.LENGTH_SHORT).show();
				adapter.saveTags();
				parent.updatePosts();
			}
		});

		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SignMapFiltersFragment.this.applyEnabled = true;
				SignMapFiltersFragment.this.resetEnabled = false;
				apply.setEnabled(SignMapFiltersFragment.this.applyEnabled);
				reset.setEnabled(SignMapFiltersFragment.this.resetEnabled);
				
				Toast.makeText(parent, "Undoing hashtag filters...",
						Toast.LENGTH_SHORT).show();
				adapter.reset();
				adapter.update();
				parent.updatePosts();
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("FiltersFragment", "onActivityCreated");
	}

	public void resetAndUpdateAdapter() {
		this.adapter.reset();
		this.adapter.update();
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

	@SuppressLint("InflateParams")
	protected class FilterListAdapter extends BaseAdapter implements
			ListAdapter {

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

		public void saveTags() {
			SharedPreferences preferencesReader = parent.getSharedPreferences(
					FILTER_SETTINGS_KEY, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferencesReader.edit();
			editor.putBoolean(FILTER_ENABLED_KEY, true);

			HashSet<String> set = null;
			if (list.size() > 0) {
				set = new HashSet<String>();
				for (Tag t : list) {
					if (t.isChecked)
						set.add(t.tag);
				}
			}
			editor.putStringSet(FILTER_TAGS_KEY, set);
			editor.commit();
		}

		public void setupCheckedSavedTags() {
			SharedPreferences preferencesReader = parent.getSharedPreferences(
					FILTER_SETTINGS_KEY, Context.MODE_PRIVATE);
			if (preferencesReader.getBoolean(FILTER_ENABLED_KEY, false)) {
				Set<String> tags = preferencesReader.getStringSet(
						FILTER_TAGS_KEY, null);
				if (tags != null) {
					for (Tag t : list) {
						if (tags.contains(t.tag))
							t.setIsChecked(true);
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
				LayoutInflater inflate = (LayoutInflater) parent
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				itemLayout = (RelativeLayout) inflate.inflate(
						R.layout.filter_list_item, null);
			} else {
				itemLayout = (RelativeLayout) convertView;
			}

			final CheckBox cb = (CheckBox) itemLayout
					.findViewById(R.id.filterCheckBox);
			cb.setChecked(item.isChecked);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					item.setIsChecked(isChecked);
				}
			});

			final TextView filterTextView = (TextView) itemLayout
					.findViewById(R.id.filterText);
			filterTextView.setText("#" + item.tag);
			filterTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cb.toggle();
				}
			});

			final TextView filterNumView = (TextView) itemLayout
					.findViewById(R.id.filterNum);
			filterNumView.setText(item.numPosts + " Sign(s)");
			filterNumView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cb.toggle();
				}
			});
			
			return itemLayout;
		}

		public void reset() {
			resetChecked();
			SharedPreferences preferencesReader = parent.getSharedPreferences(
					FILTER_SETTINGS_KEY, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferencesReader.edit();
			editor.putBoolean(FILTER_ENABLED_KEY, false);
			editor.commit();
			list.clear();
		}

		public void update() {
			SharedPreferences preferencesReader = parent.getSharedPreferences(
					SignMapFiltersFragment.FILTER_SETTINGS_KEY,
					Context.MODE_PRIVATE);
			if (!preferencesReader.getBoolean(
					SignMapFiltersFragment.FILTER_ENABLED_KEY, false)) {
				Log.i("FilterFragment", "Filters enabled");
				parent.getHashtags(this);
			}
		}
	}
}
