package com.itweeti.isse.popmovies.data;

import android.content.ContentValues;
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
    private static final int DATABASE_VERSION = 8;

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



    // Adding new favorites to TABLE_FAVORITE
    public void addMovieAsFavorite(String movieId, String title, String image) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues detailValues = new ContentValues();
        detailValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);
        detailValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, title);
        detailValues.put(MovieContract.FavoriteEntry.COLUMN_IMAGE, image);

        // Inserting Row
        db.insert(MovieContract.FavoriteEntry.TABLE_FAVORITE, null, detailValues);

        db.close(); // Closing database connection
    }



    // Adding new details to TABLE_DETAIL
    public void addDetailsToList(String movieId, String title, String base64Poster, String overview, String year, String vote_average, String duration) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues detailValues = new ContentValues();
        detailValues.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, movieId);
        detailValues.put(MovieContract.DetailEntry.COLUMN_TITLE, title);
        detailValues.put(MovieContract.DetailEntry.COLUMN_YEAR, year);
        detailValues.put(MovieContract.DetailEntry.COLUMN_DURATION, duration);
        detailValues.put(MovieContract.DetailEntry.COLUMN_RATING, vote_average);
        detailValues.put(MovieContract.DetailEntry.COLUMN_OVERVIEW, overview);
        detailValues.put(MovieContract.DetailEntry.COLUMN_POSTER, base64Poster);

        // Inserting Row
        db.insert(MovieContract.DetailEntry.TABLE_DETAIL, null, detailValues);

        db.close(); // Closing database connection
    }

    //adding review to REVIEW_TBL
    public void addReviewsToList(String movieId, String author, String content) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);

        // Inserting Row
        db.insert(MovieContract.ReviewEntry.TABLE_REVIEW, null, reviewValues);

        db.close(); // Closing database connection

    }

    //adding trailer to TRAILER_TBL
    public void addTrailersToList(String movieId, String num, String url) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NUMBER, num);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_URL, url);

        // Inserting Row
        db.insert(MovieContract.TrailerEntry.TABLE_TRAILER, null, trailerValues);

        db.close(); // Closing database connection

    }


    public void removeFromFavorites(String movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MovieContract.FavoriteEntry.TABLE_FAVORITE, MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)});

        db.close();
    }


}
