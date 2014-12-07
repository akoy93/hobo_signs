package com.example.hobosigns;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SignMapFragment extends Fragment {

	private static final String TAG = "SignMapFragment";
	
	private MapView mapView;
	private GoogleMap map;
	private SignMapActivity parent;
	
	private ArrayList<Marker> markers;
	private HashMap<Marker, Post> markerToPost;
	
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_map, container, false);
		
		Log.i(TAG, "View inflated");
		
		int avail = GooglePlayServicesUtil.isGooglePlayServicesAvailable(parent);
		
		if (avail == ConnectionResult.SUCCESS ) {
			Log.i(TAG, "Google services are available");
			
			// Gets the MapView from the XML layout and creates it
	        MapView mapView = (MapView) view.findViewById(R.id.map_view);
	        mapView.onCreate(savedInstanceState);
	        // Log.i(TAG, "MapView is " + ((mapView == null) ? "null" : "NOT null"));
	        
	        // Gets to GoogleMap from the MapView and does initialization stuff
	        map = mapView.getMap();
	        // Log.i(TAG, "Map is " + ((map == null) ? "null" : "NOT null"));
	        
	        if (map != null) {
	        	map.getUiSettings().setMyLocationButtonEnabled(false);
	        	map.getUiSettings().setZoomControlsEnabled(true);
	        	map.setMyLocationEnabled(true);
	        	
	        	// Updates the location and zoom of the MapView
		        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(39.0, -77.0)));
		        map.animateCamera(CameraUpdateFactory.zoomTo(4));
		        
		        parent.updatePosts();
		        
		        /*markers.add(map.addMarker(new MarkerOptions()
				// Set the Marker's position
						.position(new LatLng(39.0, -77.0))
						// Set the title of the Marker's information window
						.title("Test")
						// Set the color for the Marker
						.icon(BitmapDescriptorFactory
								.defaultMarker())));*/
		        
		        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker m) {
						// Can launch Sign View activity from here?
						Post p = markerToPost.get(m);
						Toast.makeText(parent, p.getLocationName(), Toast.LENGTH_SHORT).show();
					}
				});
	        }
		} else {
			Log.i(TAG, "Google services are NOT available");
		}
		
		Button b = (Button) view.findViewById(R.id.map_drop_sign_button);
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
					.icon(BitmapDescriptorFactory
							.defaultMarker()));
			markers.add(m);
			markerToPost.put(m, p);
			Log.i(TAG, "Adding post " + p.getPostID());
		}
	}
	
	@Override
    public void onResume() {
		if (mapView != null) mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
