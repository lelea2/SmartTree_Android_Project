package com.kdao.cmpe235_project.data.map;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    // Latitude of the place
    public String mLat = "";

    // Longitude of the place
    public String mLng = "";

    // Place Name
    public String mPlaceName = "";

    // Vicinity of the place
    public String mVicinity = "";

    // Photos of the place
    // Photo is a Parcelable class
    public Photo[] mPhotos = {};

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Writing Place object data to Parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLat);
        dest.writeString(mLng);
        dest.writeString(mPlaceName);
        dest.writeString(mVicinity);
        dest.writeParcelableArray(mPhotos, 0);
    }

    public Place() {
    }

    /**
     * Initializing Place object from Parcel object
     */
    private Place(Parcel in) {
        this.mLat = in.readString();
        this.mLng = in.readString();
        this.mPlaceName = in.readString();
        this.mVicinity = in.readString();
        this.mPhotos = (Photo[]) in.readParcelableArray(Photo.class.getClassLoader());
    }


    /**
     * Generates an instance of Place class from Parcel
     */
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            // TODO Auto-generated method stub
            return null;
        }
    };
}