package com.example.hobosigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

import com.example.hobosigns.TouchableWrapper.UpdateMapAfterUserInterection;
import com.example.hobosigns.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
	private HashMap<Post, Marker> postToMarker;
	private static View v;
	private TouchableWrapper mTouchView;

	// roughly college park as default:
	private double lat = 39;
	private double lng = -77;
	
	private static int defaultZoomLevel = 15;
	
	private boolean locationButtonClicked = false;

	public SignMapFragment(SignMapActivity parent) {
		this.parent = parent;
		markers = new ArrayList<Marker>();
		markerToPost = new HashMap<Marker, Post>();
		postToMarker = new HashMap<Post, Marker>();
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

			if (map != null) {
				if (!this.parent.posts.isEmpty()) {
					Post p = this.parent.posts.get(0);
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
							p.getLat(), p.getLon())), 1000, new CancelableCallback() {

						@Override
						public void onFinish() {
							sync();
						}

						@Override
						public void onCancel() {
							sync();
						}
					});
				}
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
				map.setMyLocationEnabled(true);

				if (parent.gps.canGetLocation()) {
					parent.gps.getLocation();
					lat = parent.gps.getLatitude();
					lng = parent.gps.getLongitude();
				}

				// Move the camera instantly to current location with a zoom of
				// 5.
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						lat, lng), 5));

				// Zoom in, animating the camera.
				map.animateCamera(CameraUpdateFactory.zoomTo(SignMapFragment.defaultZoomLevel), 2000,
						new CancelableCallback() {

							@Override
							public void onFinish() {
								sync();
							}

							@Override
							public void onCancel() {
								sync();
							}
						});

				map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker m) {
						// Can launch Sign View activity from here?
						Post post = markerToPost.get(m);
						Log.i("ListView",
								"opening post of type: " + post.getMediaType());
						if (post.getMediaType() != null
								&& post.getMediaType().contains("image/jpeg")) {
							// picture post
							Intent intent = new Intent(v.getContext(),
									ViewSign.class);
							intent.putExtra("postURI", post.getMediaUri());
							intent.putExtra("postCaption", post.getCaption());
							intent.putExtra("postVoteCount",
									post.getVoteCount());
							intent.putExtra("myVote", post.getMy_vote());
							intent.putExtra("postId", post.getPostID());
							startActivity(intent);		
						} else {
							// Its a video
							Intent intent = new Intent(v.getContext(), ViewVideoSign.class);
							intent.putExtra("postURI", post.getMediaUri());
							intent.putExtra("postCaption", post.getCaption());
							intent.putExtra("postVoteCount", post.getVoteCount());
							intent.putExtra("myVote", post.getMy_vote());
							intent.putExtra("postId", post.getPostID());
							startActivity(intent);	
						}
					}
				});
				
				map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

					@Override
					public boolean onMyLocationButtonClick() {
						locationButtonClicked = true;
						return false;
					}
					
				});
				
				map.setOnCameraChangeListener(new OnCameraChangeListener() {

					@Override
					public void onCameraChange(CameraPosition arg0) {
						if (locationButtonClicked == true) {
							locationButtonClicked = false;
							sync();
						}
					}
					
				});
				
				// sync map after construction
				sync();
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

		mTouchView = new TouchableWrapper(getActivity());
		mTouchView.addView(v);
		mTouchView
				.setUpdateMapAfterUserInterection(new UpdateMapAfterUserInterection() {

					@Override
					public void onUpdateMapAfterUserInterection() {
						Log.i(TAG, "Map moved - updating posts");
						sync();
					}

				});
		return mTouchView;
	}

	@Override
	public View getView() {
		return v;
	}

	public void sync() {
		parent.updateMapLocation(map.getCameraPosition().target, map
				.getProjection().getVisibleRegion().latLngBounds);
		parent.updatePosts();
	}

	public void resetMarkers() {
		for (Marker m : markers) {
			m.remove();
		}
		markers.clear();
		markerToPost.clear();
	}

	public void updateMarkers(ArrayList<Post> oldPosts, ArrayList<Post> newPosts) {
		HashSet<Post> old = new HashSet<Post>();
		HashSet<Post> intersection = new HashSet<Post>();
		ArrayList<Post> toRemove = new ArrayList<Post>();
		ArrayList<Post> toAdd = new ArrayList<Post>();

		for (Post p : oldPosts) {
			old.add(p);
		}

		for (Post p : newPosts) {
			if (old.contains(p)) {
				intersection.add(p);
			}
		}

		for (Post p : oldPosts) {
			if (!intersection.contains(p)) {
				toRemove.add(p);
			}
		}

		for (Post p : newPosts) {
			if (!intersection.contains(p)) {
				toAdd.add(p);
			}
		}

		for (Post p : toRemove) {
			Marker m = postToMarker.get(p);
			m.remove();
			markers.remove(m);
			markerToPost.remove(m);
			postToMarker.remove(p);
		}

		for (Post p : toAdd) {
			int vcount = p.getVoteCount();
			String vote = String.valueOf(vcount);
			if (vcount >= 0) {
				vote = "+" + vote;
			}
			
			Marker m = map.addMarker(new MarkerOptions()
					.position(new LatLng(p.getLat(), p.getLon()))
					.title("(" + vote + ") " + p.getAuthor().trim() + ": " + p.getCaption().trim())
					.icon(BitmapDescriptorFactory.defaultMarker()));
			markers.add(m);
			markerToPost.put(m, p);
			postToMarker.put(p, m);
		}

		// for (Post p : parent.posts) {
		// Marker m = map.addMarker(new MarkerOptions()
		// .position(new LatLng(p.getLat(), p.getLon()))
		// .title(p.getAuthor().trim() + ": " + p.getCaption().trim())
		// .icon(BitmapDescriptorFactory.defaultMarker()));
		// markers.add(m);
		// markerToPost.put(m, p);
		// postToMarker.put(p, m);
		// Log.i(TAG, "Adding post " + p.getPostID());
		// }
		Log.i(TAG, "Intersection: " + intersection.size());
		Log.i(TAG,
				"Added " + toAdd.size() + " post(s) and removed "
						+ toRemove.size() + " post(s).");
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
