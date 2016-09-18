/*
* Copyright 2016 Angela Sanchez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
* */

package com.itweeti.isse.popmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gerry on 21/5/16.
 */
public class MovieImage implements Parcelable {
    public long movie_id;
    public String movie_name;
    public String movie_image;

    public MovieImage(long movie_id, String movie_name, String movie_image) {
        this.movie_id = movie_id;
        this.movie_name = movie_name;
        this.movie_image = movie_image;
    }

    public long getMovie_id() {
        return movie_id;
    }

    public String getMovie_image() {
        return movie_image;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_id(long movie_id) {
        this.movie_id = movie_id;
    }

    public void setMovie_image(String movie_image) {
        this.movie_image = movie_image;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    protected MovieImage(Parcel in) {
        movie_id = in.readLong();
        movie_name = in.readString();
        movie_image = in.readString();
    }

    public static final Creator<MovieImage> CREATOR = new Creator<MovieImage>() {
        @Override
        public MovieImage createFromParcel(Parcel in) {
            return new MovieImage(in);
        }

        @Override
        public MovieImage[] newArray(int size) {
            return new MovieImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movie_id);
        dest.writeString(movie_name);
        dest.writeString(movie_image);
    }
}