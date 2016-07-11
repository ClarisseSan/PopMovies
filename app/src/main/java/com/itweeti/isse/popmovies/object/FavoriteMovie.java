package com.itweeti.isse.popmovies.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gerry on 27/5/16.
 */
public class FavoriteMovie extends Movie implements Parcelable {


    public FavoriteMovie(String movie_id, String movie_name, String movie_image, String movie_overview, String movie_date, String movie_vote, String duration, List<Trailer> trailerList, List<Reviews> reviewList) {
        super(movie_id, movie_name, movie_image, movie_overview, movie_date, movie_vote, duration, trailerList,reviewList);
    }

    protected FavoriteMovie(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavoriteMovie> CREATOR = new Creator<FavoriteMovie>() {
        @Override
        public FavoriteMovie createFromParcel(Parcel in) {
            return new FavoriteMovie(in);
        }

        @Override
        public FavoriteMovie[] newArray(int size) {
            return new FavoriteMovie[size];
        }
    };
}
