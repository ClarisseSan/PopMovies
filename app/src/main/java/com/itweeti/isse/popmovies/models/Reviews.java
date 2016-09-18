
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
 * Created by isse on 21 Jun 2016.
 */


public class Reviews implements Parcelable {
    String author;
    String content;

    public Reviews(String author, String content) {
        this.author = author;
        this.content = content;
    }

    protected Reviews(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Creator<Reviews> getCREATOR() {
        return CREATOR;
    }
}
