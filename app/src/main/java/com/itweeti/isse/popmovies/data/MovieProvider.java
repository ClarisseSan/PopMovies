package com.itweeti.isse.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by isse on 9 Aug 2016.
 */
public class MovieProvider extends ContentProvider {


    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    private MovieHelper mOpenHelper;


    // Codes for the UriMatcher //////
    private static final int DETAIL = 1;
    private static final int DETAIL_WITH_ID = 2;

    private static final int FAVORITE = 3;
    private static final int FAVORITE_WITH_ID = 4;

    private static final int TRAILER = 5;
    private static final int REVIEWS = 6;
    private static final int REVIEWS_WITH_ID = 7;
    private static final int TRAILER_WITH_ID = 8;


    private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        //URI for details
        matcher.addURI(authority, MovieContract.DetailEntry.TABLE_DETAIL, DETAIL);
        matcher.addURI(authority, MovieContract.DetailEntry.TABLE_DETAIL + "/#", DETAIL_WITH_ID);

        //URI for favorites
        matcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_FAVORITE, FAVORITE);

        matcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_FAVORITE + "/#", FAVORITE_WITH_ID);

        //URI for insert Trailer table
        //content://com.itweeti.isse.popmovies/trailer_tbl
        matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_TRAILER + "/#", TRAILER_WITH_ID);

        //URI for reviews
        matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_REVIEW, REVIEWS);

        matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_REVIEW + "/#", REVIEWS_WITH_ID);


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
        switch (sUriMatcher.match(uri)) {
            // All Flavors selected
            case DETAIL: {
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
            case DETAIL_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.DetailEntry.TABLE_DETAIL,
                        projection,//returns all columns
                        MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",//with a movie id of ?
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;

            }

            case FAVORITE: {
                //readableDatabase because you're ony gonna fetch data
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_FAVORITE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            case FAVORITE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_FAVORITE,
                        projection,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);

                return retCursor;

            }


            case TRAILER: {
                //readableDatabase because you're ony gonna fetch data
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_TRAILER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            case TRAILER_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_TRAILER,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);

                return retCursor;
            }

            case REVIEWS: {
                //readableDatabase because you're ony gonna fetch data
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_REVIEW,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            case REVIEWS_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_REVIEW,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);

                return retCursor;
            }

            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        //uri --> content://com.itweeti.isse.popmovies/detail_tbl
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DETAIL: {
                //returns all movies
                return MovieContract.DetailEntry.CONTENT_DIR_TYPE;
            }
            case DETAIL_WITH_ID: {
                //return a movie with the id
                return MovieContract.DetailEntry.CONTENT_ITEM_TYPE;
            }
            case FAVORITE: {
                //returns all favorite movies
                return MovieContract.FavoriteEntry.CONTENT_DIR_TYPE;
            }
            case FAVORITE_WITH_ID: {
                //return a favorite movie with the id
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            }

            case TRAILER: {
                //returns all trailers
                return MovieContract.TrailerEntry.CONTENT_DIR_TYPE;
            }

            case TRAILER_WITH_ID: {
                //return a favorite movie with the id
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            }

            case REVIEWS: {
                //returns all reviews
                return MovieContract.ReviewEntry.CONTENT_DIR_TYPE;
            }

            case REVIEWS_WITH_ID: {
                //return a favorite movie with the id
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            }
            default: {
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

        Log.e("My URI =============>", uri.toString());

        switch (sUriMatcher.match(uri)) {
            case DETAIL: {
                long _id = db.insert(MovieContract.DetailEntry.TABLE_DETAIL, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.DetailEntry.buildDetailsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_TRAILER, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_REVIEW, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            case FAVORITE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_FAVORITE, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(_id);
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {

            case FAVORITE_WITH_ID:
                numDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_FAVORITE,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});

                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case DETAIL: {
                numUpdated = db.update(MovieContract.DetailEntry.TABLE_DETAIL,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case DETAIL_WITH_ID: {
                numUpdated = db.update(MovieContract.DetailEntry.TABLE_DETAIL,
                        contentValues,
                        MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
