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
 * Created by gerry on 26/5/16.
 */
public class Trailer implements Parcelable {

    String trailer_number;
    String trailer_url;


    public Trailer(String trailer_number, String trailer_url) {
        this.trailer_number = trailer_number;
        this.trailer_url = trailer_url;
    }


    protected Trailer(Parcel in) {
        trailer_number = in.readString();
        trailer_url = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailer_number);
        dest.writeString(trailer_url);
    }

    public String getTrailerNumber() {
        return trailer_number;
    }

    public String getTrailerUrl() {
        return trailer_url;
    }

}
