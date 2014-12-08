package com.example.hobosigns;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.hobosigns.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

public class SignMapFragment extends Fragment {

	private static final String TAG = "SignMapFragment";

	private MapView mapView;
	private GoogleMap map;
	private SignMapActivity parent;

	private ArrayList<Marker> markers;
	private HashMap<Marker, Post> markerToPost;
	private static View v;

	// roughly college park as default:
	private double lat = 39;
	private double lng = -77; 
	
	public SignMapFragment(SignMapActivity parent) {
		this.parent = parent;
		markers = new ArrayList<Marker>();
		markerToPost = new HashMap<Marker, Post>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MapsInitializer.initialize(parent);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (v != null) {
			ViewGroup parent = (ViewGroup) v.getParent();
			if (parent != null) {
				parent.removeView(v);
			}
		}

		try {
			v = inflater.inflate(R.layout.fragment_sign_map, container, false);

			Log.i(TAG, "View inflated");

			int avail = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(parent);

			if (avail == ConnectionResult.SUCCESS) {
				Log.i(TAG, "Google services are available");
				map = ((MapFragment) getFragmentManager().findFragmentById(
						R.id.map)).getMap();
				
				if (parent.gps.canGetLocation()) {
					parent.gps.getLocation();
					lat = parent.gps.getLatitude();
					lng = parent.gps.getLongitude();
				}
					
				
				// Move the camera instantly to current location with a zoom of 21.
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 21));

				// Zoom in, animating the camera.
				map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
				
				parent.updatePosts();

				map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker m) {
						// Can launch Sign View activity from here?
						Post post = markerToPost.get(m);
						Log.i("ListView", "opening post of type: "+post.getMediaType());
						if(post.getMediaType() != null && post.getMediaType().contains("image/jpeg")){
							// picture post
							Intent intent = new Intent(v.getContext(), ViewSign.class);
							intent.putExtra("postURI", post.getMediaUri());
							intent.putExtra("postCaption", post.getCaption());
							intent.putExtra("postVoteCount", post.getVoteCount());
							intent.putExtra("myVote", post.getMy_vote());
							startActivity(intent);		
						} else {
							//Its a video
							// picture post
							Intent intent = new Intent(v.getContext(), ViewVidSign.class);
							intent.putExtra("postURI", post.getMediaUri());
							intent.putExtra("postCaption", post.getCaption());
							intent.putExtra("postVoteCount", post.getVoteCount());
							intent.putExtra("myVote", post.getMy_vote());
							startActivity(intent);	
						}
					}
				});
			} else {
				Log.i(TAG, "Google services are NOT available");
			}

			Button b = (Button) v.findViewById(R.id.map_drop_sign_button);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Launch sign dropping activity
					Intent intent = new Intent(v.getContext(),
							CameraActivity.class);
					startActivity(intent);
				}
			});
		} catch (InflateException e) {
			Log.i(TAG, "we already have the map");
		}

		return v;
	}

	public void resetMarkers() {
		for (Marker m : markers) {
			m.remove();
		}
		markers.clear();
		markerToPost.clear();
	}

	public void updateMarkers() {
		for (Post p : parent.posts) {
			Marker m = map.addMarker(new MarkerOptions()
					.position(new LatLng(p.getLat(), p.getLon()))
					.title(p.getAuthor().trim() + ": " + p.getCaption().trim())
					.icon(BitmapDescriptorFactory.defaultMarker()));
			markers.add(m);
			markerToPost.put(m, p);
			Log.i(TAG, "Adding post " + p.getPostID());
		}
	}

	@Override
	public void onResume() {
		if (mapView != null)
			mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mapView != null)
			mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mapView != null)
			mapView.onLowMemory();
	}
}
