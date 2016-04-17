package com.kdao.cmpe235_project.data.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.kdao.cmpe235_project.data.map.Attribution;
import com.kdao.cmpe235_project.data.map.Place;
import com.kdao.cmpe235_project.data.map.Photo;

public class PlaceJSONParser {

    /**
     * Receives a JSONObject and returns a list
     */
    public Place[] parse(JSONObject jObject) {

        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            jPlaces = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces);
    }


    private Place[] getPlaces(JSONArray jPlaces) {
        int placesCount = jPlaces.length();
        Place[] places = new Place[placesCount];

        /** Taking each place, parses and adds to list object */
        for (int i = 0; i < placesCount; i++) {
            try {
                /** Call getPlace with place JSON object to parse the place */
                places[i] = getPlace((JSONObject) jPlaces.get(i));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return places;
    }

    /**
     * Parsing the Place JSON object
     */
    private Place getPlace(JSONObject jPlace) {
        Place place = new Place();
        try {
            // Extracting Place name, if available
            if (!jPlace.isNull("name")) {
                place.mPlaceName = jPlace.getString("name");
            }

            // Extracting Place Vicinity, if available
            if (!jPlace.isNull("vicinity")) {
                place.mVicinity = jPlace.getString("vicinity");
            }

            if (!jPlace.isNull("photos")) {
                JSONArray photos = jPlace.getJSONArray("photos");
                place.mPhotos = new Photo[photos.length()];
                for (int i = 0; i < photos.length(); i++) {
                    place.mPhotos[i] = new Photo();
                    place.mPhotos[i].mWidth = ((JSONObject) photos.get(i)).getInt("width");
                    place.mPhotos[i].mHeight = ((JSONObject) photos.get(i)).getInt("height");
                    place.mPhotos[i].mPhotoReference = ((JSONObject) photos.get(i)).getString("photo_reference");
                    JSONArray attributions = ((JSONObject) photos.get(i)).getJSONArray("html_attributions");
                    place.mPhotos[i].mAttributions = new Attribution[attributions.length()];
                    for (int j = 0; j < attributions.length(); j++) {
                        place.mPhotos[i].mAttributions[j] = new Attribution();
                        place.mPhotos[i].mAttributions[j].mHtmlAttribution = attributions.getString(j);
                    }
                }
            }
            place.mLat = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            place.mLng = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("EXCEPTION", e.toString());
        }
        return place;
    }
}