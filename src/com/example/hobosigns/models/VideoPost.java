package com.example.hobosigns.models;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.hobosigns.rest.PostAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class VideoPost extends Post {

	private String file;

	public static Post jsonToPicPost(String jsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		PicturePost post = gson.fromJson(jsonString, PicturePost.class);
		return post;
	}

	public VideoPost(String file, String caption, long postID, String author,
			double lat, double lon, Date date, String mediaType,
			String locationName, double distance, String mediaUri) {
		super(postID, author, lat, lon, date, mediaType, locationName,
				distance, caption, mediaUri);
		this.file = file;
	}

	public static byte[] bitmapToByte(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}

	public Bitmap getBitmapImage() {
		return BitmapFactory.decodeFile(file);
	}

	public void postPost(final Context context, String accessToken) {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(
				PostAPI.SERVER_URL).build();
		HoboSignsService service = restAdapter.create(HoboSignsService.class);

		service.addPost(new TypedFile("video/mp4", new File(file)),
			accessToken, this.getCaption(), this.getLat(), this.getLon(),
			new Callback<Response>() {

				@Override
				public void failure(RetrofitError e) {
					Log.e("postPost", e.toString());
				}

				@Override
				public void success(Response res,
						retrofit.client.Response arg1) {
					if (res.success) {
						Toast.makeText(context, "Sign has been posted",
									Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, res.error,
								Toast.LENGTH_SHORT).show();
					}
				}
		});
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
