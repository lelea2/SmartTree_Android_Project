package com.kdao.cmpe235_project;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.kdao.cmpe235_project.data.map.Attribution;
import com.kdao.cmpe235_project.data.map.Place;
import com.kdao.cmpe235_project.data.map.Photo;
import com.kdao.cmpe235_project.util.Config;
import com.kdao.cmpe235_project.util.Http;

// Defining DialogFragment class to show the place details with photo
public class PlaceDialogFragment extends DialogFragment {
    TextView mTVPhotosCount = null;
    TextView mTVVicinity = null;
    Button mTVButton = null;
    ViewFlipper mFlipper = null;
    Place mPlace = null;
    DisplayMetrics mMetrics = null;
    Http http = new Http();

    /**
     * Constructor
     */
    public PlaceDialogFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // For retaining the fragment on screen rotation
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_layout, null);
        // Getting reference to ViewFlipper
        mFlipper = (ViewFlipper) v.findViewById(R.id.flipper);
        // Getting reference to TextView to display photo count
        mTVPhotosCount = (TextView) v.findViewById(R.id.tv_photos_count);
        // Getting reference to TextView to display place vicinity
        mTVVicinity = (TextView) v.findViewById(R.id.tv_vicinity);
        //Handle button click
        handleGetDirection();
        if (mPlace != null) {
            // Setting the title for the Dialog Fragment
            getDialog().setTitle(mPlace.mPlaceName);
            // Array of references of the photos
            Photo[] photos = mPlace.mPhotos;
            // Setting Photos count
            mTVPhotosCount.setText("Photos available : " + photos.length);
            // Setting the vicinity of the place
            mTVVicinity.setText(mPlace.mVicinity);
            // Creating an array of ImageDownloadTask to download photos
            ImageDownloadTask[] imageDownloadTask = new ImageDownloadTask[photos.length];
            int width = (int) (mMetrics.widthPixels * 3) / 4;
            int height = (int) (mMetrics.heightPixels * 1) / 2;
            String url = "https://maps.googleapis.com/maps/api/place/photo?";
            String key = "key=" + Config.GOOGLE_API;
            String sensor = "sensor=true";
            String maxWidth = "maxwidth=" + width;
            String maxHeight = "maxheight=" + height;
            url = url + "&" + key + "&" + sensor + "&" + maxWidth + "&" + maxHeight;
            // Traversing through all the photoreferences
            for (int i = 0; i < photos.length; i++) {
                // Creating a task to download i-th photo
                imageDownloadTask[i] = new ImageDownloadTask();
                String photoReference = "photoreference=" + photos[i].mPhotoReference;
                // URL for downloading the photo from Google Services
                url = url + "&" + photoReference;
                // Downloading i-th photo from the above url
                imageDownloadTask[i].execute(url);
            }
        }
        return v;
    }

    /**
     * Helper function handle get direction
     */
    private void handleGetDirection() {

    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    /**
     * Image download task handling
     */
    private class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {
        Bitmap bitmap = null;

        @Override
        protected Bitmap doInBackground(String... url) {
            try {
                // Starting image download
                bitmap = http.downloadImage(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Creating an instance of ImageView to display the downloaded image
            ImageView iView = new ImageView(getActivity().getBaseContext());
            // Setting the downloaded image in ImageView
            iView.setImageBitmap(result);
            // Adding the ImageView to ViewFlipper
            mFlipper.addView(iView);
            // Showing download completion message
            Toast.makeText(getActivity().getBaseContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
