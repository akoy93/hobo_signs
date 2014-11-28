package com.example.hobosigns;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SignMapFragment extends Fragment {

	private static final String TAG = "SignMapFragment";
	
	private MapView mapView;
	private GoogleMap map;
	private Activity parent;
	
	public SignMapFragment(Activity parent) {
		this.parent = parent;
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
		View view = inflater.inflate(R.layout.fragment_sign_map, container, false);
		
		Log.i(TAG, "View inflated");
		
		int avail = GooglePlayServicesUtil.isGooglePlayServicesAvailable(parent);
		
		if (avail == ConnectionResult.SUCCESS || avail == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
			Log.i(TAG, "Google services are available");
			
			MapsInitializer.initialize(this.getActivity());
			
			// Gets the MapView from the XML layout and creates it
	        MapView mapView = (MapView) view.findViewById(R.id.map_view);
	        mapView.onCreate(savedInstanceState);
	        Log.i(TAG, "MapView is " + ((mapView == null) ? "null" : "NOT null"));
	        
	        // Gets to GoogleMap from the MapView and does initialization stuff
	        map = mapView.getMap();
	        Log.i(TAG, "Map is " + ((map == null) ? "null" : "NOT null"));
	        
	        if (map != null) {
	        	map.getUiSettings().setMyLocationButtonEnabled(false);
	        	map.setMyLocationEnabled(true);
	        	
	        	// Updates the location and zoom of the MapView
		        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 4);
		        map.animateCamera(cameraUpdate);
		        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.1, -87.9)));
		        map.animateCamera(CameraUpdateFactory.zoomTo(3));
		        
		        map.addMarker(new MarkerOptions()

				// Set the Marker's position
						.position(new LatLng(43.1, -87.9))

						// Set the title of the Marker's information window
						.title("Test")

						// Set the color for the Marker
						.icon(BitmapDescriptorFactory
								.defaultMarker()));
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
