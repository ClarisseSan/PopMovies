package com.itweeti.isse.popmovies.carousel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.activity.MovieDetailActivity;
import com.itweeti.isse.popmovies.data.MovieContract;
import com.itweeti.isse.popmovies.models.FavoriteMovie;
import com.itweeti.isse.popmovies.models.Reviews;
import com.itweeti.isse.popmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

/**
 * code tutorial from :
 * http://www.devexchanges.info/2015/11/making-carousel-layout-in-android.html
 */

public class MainCarousel extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FeatureCoverFlow mCoverFlow;
    //private CoverFlowAdapter mAdapter;
    private CoverFlowCursorAdapter mAdapter;
    private static final int CURSOR_LOADER_ID = 0;

    private ArrayList<FavoriteMovie> list;
    private List<Trailer> movieTrailersList;
    private List<Reviews> movieReviewsList;


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_YEAR= 3;
    static final int COL_MOVIE_DURATION = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_OVERVIEW = 6;
    static final int COL_MOVIE_POSTER = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_carousel);
        mCoverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);

        //displayFavoriteMovies();

        // initialize our CursorAdapter
        mAdapter = new CoverFlowCursorAdapter(this, null, 0);

        mCoverFlow.setAdapter(mAdapter);
        mCoverFlow.setOnScrollPositionListener(onScrollListener());

        mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor)  parent.getItemAtPosition(position);
                if (cursor != null) {

                    Intent i = new Intent(MainCarousel.this, MovieDetailActivity.class)
                            //pass the selected movie_id to the next Activity
                            .putExtra("flagData", 1)
                            .putExtra("movieId", cursor.getString(COL_MOVIE_ID))
                            .putExtra("title", cursor.getString(COL_MOVIE_TITLE))
                            .putExtra("year", cursor.getString(COL_MOVIE_YEAR))
                            .putExtra("rating", cursor.getString(COL_MOVIE_RATING))
                            .putExtra("overview", cursor.getString(COL_MOVIE_OVERVIEW))
                            .putExtra("duration", cursor.getString(COL_MOVIE_DURATION));

                    MainCarousel.this.startActivity(i);



                }
            }
        });

    }

    @Override
    protected void onStart() {


        // initialize loader
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onStart();

    }

    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActivity", "position: " + position);
            }

            @Override
            public void onScrolling() {
                Log.i("MainCarousel", "scrolling");
            }
        };
    }


    private JSONArray getFavoriteMovies() throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String items = preferences.getString("favorites", "");
        return new JSONArray(items);
    }

    private void displayFavoriteMovies() {



        list = new ArrayList<>();

        try {
            // this calls the json
            JSONArray arr = getFavoriteMovies();

            //TODO: check JSON in Jsonlint
            Log.e("xxxxx-add", "called JSON(" + arr.length() + "): " + arr);


            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String movie_id = obj.getString("movie_id");
                String movie_name = obj.getString("movie_name");
                String movie_image = obj.getString("movie_image");
                String movie_overview = obj.getString("movie_overview");
                String movie_date = obj.getString("movie_date");
                String movie_vote = obj.getString("movie_vote");
                String movie_duration = obj.getString("movie_duration");
                JSONArray trailers = obj.getJSONArray("movie_trailers");
                JSONArray reviews = obj.getJSONArray("movie_reviews");

                //FETCH TRAIlERS
                if (trailers.length()>0){
                    //if there are trailers available
                    movieTrailersList = new ArrayList<>();
                    for (int j = 0; j < trailers.length(); j++) {

                        JSONObject trailer = trailers.getJSONObject(j);
                        String trailer_num = trailer.getString("trailer_num");
                        String trailer_url = trailer.getString("trailer_url");

                        Trailer t = new Trailer(trailer_num, trailer_url);
                        //save trailers in a list
                        movieTrailersList.add(t);


                    }
                }

                //FETCH REVIEWS
                if (reviews.length()>=0){
                    //if there are trailers available
                    movieReviewsList = new ArrayList<>();
                    for (int j = 0; j < reviews.length(); j++) {

                        JSONObject reviewObj = reviews.getJSONObject(j);

                        String review_author = reviewObj.getString("review_author");
                        String review_content = reviewObj.getString("review_content");

                        Reviews rev = new Reviews(review_author, review_content);
                        //save reviews in a list
                        movieReviewsList.add(rev);
                    }
                }

                //**********LOGS************//
                Log.d("xxxxx-add", "adding movie: " + movie_name);
                System.out.println("TRAILER SIZE---------->" + movieTrailersList.size());

                System.out.println("MOVIE NAME = " + movie_name);
                for (Trailer t:movieTrailersList
                        ) {
                    System.out.println("TRAILER: " + t.getTrailerUrl());
                }
                //**************************//


                FavoriteMovie fav = new FavoriteMovie(movie_id,movie_name,movie_image,movie_overview,movie_date,movie_vote,movie_duration, movieTrailersList, movieReviewsList);
                list.add(fav);

                System.out.println("FAVORITE MOVIES SIZE---------> " + list.size());

                //mAdapter = new CoverFlowAdapter(this, list);
                //mAdapter.setItemList(list);


                mAdapter.notifyDataSetChanged();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Attach loader to our flavors database query
        // run when loader is initialized
        return new CursorLoader(this,
                MovieContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Set the cursor in our CursorAdapter once the Cursor is loaded
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
