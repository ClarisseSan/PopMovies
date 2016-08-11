package com.itweeti.isse.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by isse on 16 Jul 2016.
 *
 * A contract class is a container for constants that define names for URIs, tables, and columns.
 * The contract class allows you to use the same constants across all the other classes in the same package.
 * This lets you change a column name in one place and have it propagate throughout your code.
 */
public final class MovieContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MovieContract() {}



    //Content provider variables
    public static final String CONTENT_AUTHORITY = "com.itweeti.isse.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);




    /* Inner class that defines the detail table contents */
    public static  abstract class DetailEntry implements BaseColumns{
        //DETAIL TABLE
        public static final String TABLE_DETAIL = "detail_tbl";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public final static String COLUMN_YEAR = "year";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_VIEWED = "viewed";




        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_DETAIL).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_DETAIL;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_DETAIL;

        // for building URIs with ID
        public static Uri buildDetailsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    /* Inner class that defines the detail table contents */
    public static  abstract class ReviewEntry implements BaseColumns{

        //REVIEW TABLE
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String TABLE_REVIEW = "review_tbl";
        public static final String COLUMN_AUTHOR = "author";
        public final static String COLUMN_CONTENT = "content";


        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_REVIEW).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_REVIEW;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_REVIEW;

        // for building URIs on insertion
        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    /* Inner class that defines the detail table contents */
    public static  abstract class TrailerEntry implements BaseColumns{
        //TRAILER TABLE
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String TABLE_TRAILER = "trailer_tbl";
        public static final String COLUMN_TRAILER_NUMBER = "trailer_num";
        public final static String COLUMN_TRAILER_URL = "trailer_url";


        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_TRAILER).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_TRAILER;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_TRAILER;

        // for building URIs on insertion
        public static Uri buildTrailerUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    /* Inner class that defines the Favorite table contents */
    public static  abstract class FavoriteEntry implements BaseColumns{
        //TRAILER TABLE
        public static final String TABLE_FAVORITE = "favorite_tbl";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_IMAGE = "encoded_image";


        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_FAVORITE).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE;

        // for building URIs on insertion
        public static Uri buildFavoriteUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
