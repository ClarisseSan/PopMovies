package com.itweeti.isse.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by isse on 9 Aug 2016.
 */
public class MovieProvider extends ContentProvider {



    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieHelper mOpenHelper;


    // Codes for the UriMatcher //////
    private static final int FLAVOR = 100;
    private static final int FLAVOR_WITH_ID = 200;
    ////////

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, MovieContract.DetailEntry.TABLE_DETAIL, FLAVOR);
        matcher.addURI(authority, MovieContract.DetailEntry.TABLE_DETAIL + "/#", FLAVOR_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //query --> select statement in SQL

        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Flavors selected
            case FLAVOR:{
                //readableDatabase because you're ony gonna fetch data
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.DetailEntry.TABLE_DETAIL,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case FLAVOR_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.DetailEntry.TABLE_DETAIL,
                        projection,//returns all columns
                        MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",//with a movie id of ___
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FLAVOR:{
                //returns all movie
                return MovieContract.DetailEntry.CONTENT_DIR_TYPE;
            }
            case FLAVOR_WITH_ID:{
                //return a movie with the id
                return MovieContract.DetailEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //open writable database for you're gonna insert data
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case FLAVOR: {
                long _id = db.insert(MovieContract.DetailEntry.TABLE_DETAIL, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.DetailEntry.buildDetailsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
