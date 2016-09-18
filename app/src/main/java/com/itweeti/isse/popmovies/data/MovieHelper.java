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

package com.itweeti.isse.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by isse on 16 Jul 2016.
 * SQLiteOpenHelper:
 * this is the most important class that you will work with in Android SQLite.
 * You will use SQLiteOpenHelper to create and upgrade your SQLite Database.
 * In other words, SQLiteOpenHelper removes the effort required to install
 * and configure database in other systems.
 */
public class MovieHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 10;

    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String INTEGER_TYPE = " INTEGER DEFAULT 0";
    private static final String COMMA_SEP = ",";

    /* query for creating detail_tbl*/
    private static final String SQL_CREATE_TABLE_DETAIL =
            "CREATE TABLE " + MovieContract.DetailEntry.TABLE_DETAIL + " (" +
                    MovieContract.DetailEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieContract.DetailEntry.COLUMN_MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                    MovieContract.DetailEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieContract.DetailEntry.COLUMN_YEAR + TEXT_TYPE + COMMA_SEP +
                    MovieContract.DetailEntry.COLUMN_DURATION + TEXT_TYPE + COMMA_SEP +
                    MovieContract.DetailEntry.COLUMN_RATING + TEXT_TYPE + COMMA_SEP +
                    MovieContract.DetailEntry.COLUMN_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    MovieContract.DetailEntry.COLUMN_POSTER + BLOB_TYPE + COMMA_SEP +
                    MovieContract.DetailEntry.COLUMN_VIEWED + INTEGER_TYPE +

                    " )";


    /* query for creating review_tbl*/
    private static final String SQL_CREATE_TABLE_REVIEWS =
            "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_REVIEW + " (" +
                    MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieContract.ReviewEntry.COLUMN_MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                    MovieContract.ReviewEntry.COLUMN_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    MovieContract.ReviewEntry.COLUMN_CONTENT + TEXT_TYPE +
                    " )";

    /* query for creating review_tbl*/
    private static final String SQL_CREATE_TABLE_TRAILERS =
            "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_TRAILER + " (" +
                    MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                    MovieContract.TrailerEntry.COLUMN_TRAILER_NUMBER + TEXT_TYPE + COMMA_SEP +
                    MovieContract.TrailerEntry.COLUMN_TRAILER_URL + TEXT_TYPE +
                    " )";

    /* query for creating favorite_tbl*/
    private static final String SQL_CREATE_TABLE_FAVORITE =
            "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_FAVORITE + " (" +
                    MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                    MovieContract.FavoriteEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieContract.FavoriteEntry.COLUMN_IMAGE + TEXT_TYPE +
                    " )";


    /* query for deleting review_tbl*/
    private static final String SQL_DELETE_DETAIL =
            "DROP TABLE IF EXISTS " + MovieContract.DetailEntry.TABLE_DETAIL;

    /* query for deleting review_tbl*/
    private static final String SQL_DELETE_REVIEW =
            "DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_REVIEW;


    /* query for deleting review_tbl*/
    private static final String SQL_DELETE_TRAILER =
            "DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_TRAILER;

    /* query for deleting review_tbl*/
    private static final String SQL_DELETE_FAVORITE =
            "DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_FAVORITE;


    private static final String LOG_TAG = MovieHelper.class.getSimpleName();


    public MovieHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //sql statement to create a table
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_DETAIL);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_REVIEWS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TRAILERS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_FAVORITE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(SQL_DELETE_DETAIL);
        sqLiteDatabase.execSQL(SQL_DELETE_TRAILER);
        sqLiteDatabase.execSQL(SQL_DELETE_REVIEW);
        sqLiteDatabase.execSQL(SQL_DELETE_FAVORITE);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}
