package com.itweeti.isse.popmovies.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.Utils.Config;
import com.itweeti.isse.popmovies.Utils.Utils;
import com.itweeti.isse.popmovies.fragment.MovieDetailFragment;
import com.itweeti.isse.popmovies.object.Reviews;
import com.itweeti.isse.popmovies.object.Trailer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MovieDetailActivity" ;
    short flagSave; //on/off for the mark as favorite button
    private String movieId;
    private String movieName;
    private int flagData;

    private FloatingActionButton fab;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();


    private String first_trailer_url;

    private String mTitle;
    private String mYear;
    private String mDuration;
    private String mRating;
    private String mOverview;
    private String mPoster;
    private float vote_average;


    private static final String STATE_ID = "movie_id" ;
    private static final String STATE_DATA = "flagDataType";
    private static final String STATE_TITLE = "title";
    private static final String STATE_YEAR = "year";
    private static final String STATE_DURATION = "duration";
    private static final String STATE_RATING = "rating";
    private static final String STATE_VOTE = "vote_ave";
    private static final String STATE_OVERVIEW = "overview";
    private static final String STATE_POSTER ="poster" ;

    private List<Reviews> reviewList;
    private List<Trailer> movieTrailersList;

    private Intent intent;
    private String encodedString = "";
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        intent = this.getIntent();

        if (intent != null) {
            movieId = intent.getStringExtra("movieId");
            movieName = intent.getStringExtra("title");
            flagData = intent.getIntExtra("flagData", 0);
            Toast.makeText(this, LOG_TAG + " MY ID: " + movieId, Toast.LENGTH_SHORT).show();
        }


        if(savedInstanceState!=null) {
            movieId = savedInstanceState.getString(STATE_ID);
            flagData = savedInstanceState.getInt(STATE_DATA);
            mTitle = savedInstanceState.getString(STATE_TITLE);
            mYear = savedInstanceState.getString(STATE_YEAR);
            mDuration = savedInstanceState.getString(STATE_DURATION);
            mRating = savedInstanceState.getString(STATE_RATING);
            vote_average = savedInstanceState.getFloat(STATE_VOTE);
            mOverview = savedInstanceState.getString(STATE_OVERVIEW);
            mPoster = savedInstanceState.getString(STATE_POSTER);
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);

        try {
            if(Utils.getFavoriteMovies(this)!=null){
                checkMovieID();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if button is clicked
                //determine the value of flag
                switch (flagSave) {
                    case 0:
                        //if flag = 0, then highlight the star...
                        fab.setImageResource(R.mipmap.ic_action_fav);

                        //add MOVIEID to the list
                        try {
                            saveAsFavorite(view);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        flagSave = 1;
                        break;

                    case 1:


                        switch (flagData){
                            case 0:
                                //flagData = 0 --> from popular movies Activty
                                //if flag = 1 , then unhighlight the star...
                                fab.setImageResource(R.mipmap.ic_action_unfav);
                                break;
                            case 1:
                                //flagData = 1 --> from favorites activity
                                //if flag = 1 , then hide the floating action button...
                                fab.setVisibility(View.INVISIBLE);
                                break;
                        }


                        // delete MOVIEID from the list
                        try {
                            Utils.removeFromFavorites(MovieDetailActivity.this, movieId, view);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        flagSave = 0;
                        break;
                }





            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            //set action bar title
            getSupportActionBar().setTitle(movieName);
        }

        //request data from moviedb.org using API call or from shared preferences
        switch (flagData){
            case 0:
                requestMovieDetail(movieId);
                requestMovieTrailer(movieId);
                requestMovieReviews(movieId);
                break;
            case 1:
                getLocalData();
                break;
        }

        //initiliaze image loader

        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = generateBundle();

            arguments.putString(MovieDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(MovieDetailFragment.ARG_ITEM_ID));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }



    }


        private Bundle generateBundle() {
            Bundle bundle = new Bundle();
            bundle.putInt("flagData", flagData);
            bundle.putString("movieId", movieId);
            bundle.putString("title",mTitle);
            bundle.putString("year", mYear);
            bundle.putString("duration", mDuration);
            bundle.putString("rating", mRating);
            bundle.putFloat("vote_ave", vote_average);
            bundle.putString("overview", mOverview);
            bundle.putString("poster", mPoster);
            bundle.putParcelableArrayList("review_list", (ArrayList<? extends Parcelable>) reviewList);
            bundle.putParcelableArrayList("trailer_list", (ArrayList<? extends Parcelable>) movieTrailersList);

            return bundle;
        }



    private void loadImageBitmap(String image_url){
        imageLoader.loadImage(image_url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Do whatever you want with Bitmap

                ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);

                encodedString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
            }
        });
    }



    private void saveAsFavorite(View view) throws JSONException {

        //generate JSON(itemlist) so php can process it
        JSONObject item = new JSONObject();
        try {
            item.put("movie_id", movieId);
            item.put("movie_name", mTitle);

            //save as base64 image
            item.put("movie_image",encodedString);
            Log.e("BASE64=========>", encodedString);

            item.put("movie_overview", mOverview);
            item.put("movie_year", mYear);
            item.put("movie_date", mYear);
            item.put("movie_vote", vote_average);
            item.put("movie_duration", mDuration);
            item.put("movie_trailers", saveTrailers());
            item.put("movie_reviews", saveReviews());


            int size = saveTrailers().length();
            System.out.println("FAVORITE TRAILER SIZE " + size);

            //save favoriteMovie in a list
            Utils.saveFavoriteMovies(this,item, view);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray saveReviews(){
        //generate new JSON Array
        JSONArray reviews = new JSONArray();

        String author = " ";
        String content = " ";

        //if reviews are available

        if(reviewList.size()!=0){
            //loop through the trailer list and save each item in the JSONArray
            for (int i = 0; i < reviewList.size() ; i++) {
                author = reviewList.get(i).getAuthor();
                content = reviewList.get(i).getContent();


                JSONObject reviewObject = new JSONObject();
                try {
                    reviewObject.put("review_author", author);
                    reviewObject.put("review_content", content);

                    reviews.put(reviewObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        if(reviewList.size()==0){

            author = "No Reviews Available";
            content = " ";

            //add to a review object
            JSONObject reviewObject = new JSONObject();
            try {
                reviewObject.put("review_author", author);
                reviewObject.put("review_content", content);

                reviews.put(reviewObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        Log.d(LOG_TAG, "NUMBER OF REVIEWS: %%%%%%%%%%%%%%%" + reviewList.size());
        return reviews;
    }

    private JSONArray saveTrailers(){
        //generate new JSON Array
        JSONArray trailers = new JSONArray();

        //loop through the trailer list and save each item in the JSONArray
        for (int i = 0; i < movieTrailersList.size() ; i++) {
            String trailer_num = movieTrailersList.get(i).getTrailerNumber();
            String trailer_url = movieTrailersList.get(i).getTrailerUrl();


            JSONObject trailer = new JSONObject();
            try {
                trailer.put("trailer_num", trailer_num);
                trailer.put("trailer_url", trailer_url);

                trailers.put(trailer);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return trailers;
    }

    private void getLocalData() {
        mTitle = intent.getStringExtra("title");
        mYear = intent.getStringExtra("year");
        mDuration = intent.getStringExtra("duration");
        mRating = intent.getStringExtra("rating");
        vote_average = Float.parseFloat(mRating)/2;
        mOverview = intent.getStringExtra("overview");
        mPoster = intent.getStringExtra("poster");

        movieTrailersList = new ArrayList<>();
        List<Trailer> trailers = intent.getParcelableArrayListExtra("trailers");
        movieTrailersList = trailers;

        reviewList = new ArrayList<>();
        List<Reviews> reviews = intent.getParcelableArrayListExtra("reviews");
        reviewList = reviews;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // Save the current movieID state
        outState.putString(STATE_ID, movieId);
        outState.putInt(STATE_DATA, flagData);
        outState.putString(STATE_TITLE, mTitle);
        outState.putString(STATE_YEAR, mYear);
        outState.putString(STATE_DURATION, mDuration);
        outState.putString(STATE_RATING, mRating);
        outState.putFloat(STATE_VOTE, vote_average);
        outState.putString(STATE_OVERVIEW, mOverview);
        outState.putString(STATE_POSTER, mPoster);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        movieId = savedInstanceState.getString(STATE_ID);
        flagData = savedInstanceState.getInt(STATE_DATA);
        mTitle = savedInstanceState.getString(STATE_TITLE);
        mYear = savedInstanceState.getString(STATE_YEAR);
        mDuration = savedInstanceState.getString(STATE_DURATION);
        mRating = savedInstanceState.getString(STATE_RATING);
        vote_average = savedInstanceState.getFloat(STATE_VOTE);
        mOverview = savedInstanceState.getString(STATE_OVERVIEW);
        mPoster = savedInstanceState.getString(STATE_POSTER);

    }


    private void requestMovieReviews(String movieId){
        //http://api.themoviedb.org/3/reviews/293660/videos?api_key=6d369d4e0676612d2d046b7f3e8424bd

        reviewList = new ArrayList<>();

        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=" + Config.API_KEY;
        String id = movieId;
        final String vid = "/reviews";
        final String reviews_url = BASE_PATH + id + vid + api_key;

        Log.d("TRAILER URL--------> ", reviews_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MovieDetailActivity.this);


        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, reviews_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");


                            if (results!=null){


                                for (int i = 0; i < results.length(); i++) {

                                    JSONObject obj = results.getJSONObject(i);
                                    String author = obj.getString("author");
                                    String content = obj.getString("content");

                                    Reviews reviews = new Reviews(author, content);
                                    reviewList.add(reviews);

                                }

                                //==============================LOGS=================================/
                                if (reviewList!=null){
                                    for (Reviews review:reviewList) {
                                        Log.d("AUTHOR: ",String.valueOf(review.getAuthor()));
                                        Log.d("CONTENT: ",review.getContent());
                                    }
                                }
                                //====================================================================/

                            }

                            if(reviewList.size()==0){
                                String author = "No Reviews Available";
                                String content = " ";

                                //add to a review object
                                Reviews reviews = new Reviews(author, content);
                                reviewList.add(reviews);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //other catches
                        if(error instanceof NoConnectionError) {
                            //show dialog no net connection
                            Utils.showSuccessDialog(MovieDetailActivity.this, R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void requestMovieDetail(String movieId) {

        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=" + Config.API_KEY;
        String id = movieId;


        final String original_url = BASE_PATH + id + api_key;
        Log.v(LOG_TAG, "ORIGINAL URL >>>>>>>>" + original_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MovieDetailActivity.this);


        //generate url for fetching movie poster
        //1.base path
        final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/";
        //2. Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original"
        final String image_size = "w500";
        //3. And finally the poster path returned by the query : movie_image


        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, original_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mTitle = jsonObject.getString("title");
                            mYear = jsonObject.getString("release_date").substring(0, 4);
                            mDuration = jsonObject.getString("runtime") + "min";
                            mRating = jsonObject.getString("vote_average") + "/10";
                            vote_average = Float.parseFloat(jsonObject.getString("vote_average"));
                            mOverview = jsonObject.getString("overview");
                            mPoster = IMAGE_BASE_PATH + image_size + jsonObject.getString("poster_path");


                            Log.v("TITLE:>>>>>>>>>>>> ", mTitle);
                            Log.v("mYear:>>>>>>>>>>>> ", mYear);
                            Log.v("mDuration:>>>>>>>>>>> ", mDuration);
                            Log.v("mRating:>>>>>>>>>>>>>> ",mRating);
                            Log.v("mOverview:>>>>>>>>>>>> ",mOverview);
                            Log.v("mPoster:>>>>>>>>>>>>>> ", mPoster);

                            loadImageBitmap(mPoster);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //other catches
                        if(error instanceof NoConnectionError) {
                            //show dialog no net connection
                            Utils.showSuccessDialog(MovieDetailActivity.this, R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void requestMovieTrailer(final String movieId){
        //http://api.themoviedb.org/3/movie/246655/videos?api_key=6d369d4e0676612d2d046b7f3e8424bd
        movieTrailersList = new ArrayList<>();


        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=" + Config.API_KEY;
        String id = movieId;
        final String vid = "/videos";
        String trailer_url = BASE_PATH + id + vid + api_key;

        Log.d("TRAILER URL----------> ", trailer_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);


        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, trailer_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");


                            for (int i = 0; i < results.length(); i++) {

                                JSONObject obj = results.getJSONObject(i);
                                String trailer_key = obj.getString("key");
                                //String youtube_trailer = "https://www.youtube.com/watch?v=" + trailer_key;
                                String youtube_trailer =  trailer_key;
                                String trailer_num = "Trailer " + (i+1);

                                System.out.println("TRAILER NUMBER --------->" + trailer_num);
                                System.out.println("TRAILER URL --------->" + youtube_trailer);

                                Trailer trailer = new Trailer(trailer_num, youtube_trailer);

                                //save trailers in a list
                                movieTrailersList.add(trailer);

                            }



                            for (Trailer trailer:movieTrailersList
                                    ) {
                                System.out.println("TRAILER NUMBER-----------> " + trailer.getTrailerNumber());

                            }

                            Log.d("Trailer list size SIZE", String.valueOf(movieTrailersList.size()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //other catches
                        if(error instanceof NoConnectionError) {
                            //show dialog no net connection
                            Utils.showSuccessDialog(MovieDetailActivity.this, R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate the menu; this adds item form the action bar

        getMenuInflater().inflate(R.menu.menu_movie_detail,menu);;
        //retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        //get the provider and hold unto it to set/change the sharedIntent
        ShareActionProvider mShareActionProvider;
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        //attach an intent to this ShareActionProvider. You can update it anytime
        //like when the users select a pice of data they might like to share
        if (mShareActionProvider!=null){

            //first trailer to send at share intent
            if (movieTrailersList.size()!=0){
                first_trailer_url = movieName + ": https://www.youtube.com/watch?v=" + movieTrailersList.get(0).getTrailerUrl();
            }else{
                //no trailer available, return movie name instead
                first_trailer_url = movieName;
            }

            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }else{
            Log.e(LOG_TAG,"share action provider is null");
        }

        return true;
    }

    public Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String mMovieString = "Check out this movie, " + first_trailer_url;
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieString);

        return shareIntent;
    }



    private ArrayList<String> getListOfFavMovies() {

        ArrayList<String> list = new ArrayList<>();

        try {
            // this calls the json
            //1. initialization of aactivity.... button is not yet clicked
            //screen shows, determine if movie ID is included in a list of favorite movies

            JSONArray arr = Utils.getFavoriteMovies(this);

            Log.e("xxxxx-add", "called(" + arr.length() + "): " + arr);


            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Long movie_id = obj.getLong("movie_id");
                String movie_name = obj.getString("movie_name");
                list.add(String.valueOf(movie_id));
                Log.d("xxxxx-add", "adding movie: " + movie_name);
            }

            System.out.println("FAVORITES SIZE---------> " + list.size());


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

        return list;
    }

    private void checkMovieID() {
        //if movieID is included in the list then flag = 1, highlight star
        //else  star has no highlight flag = 0
        ArrayList<String> favMovies = getListOfFavMovies();
        if (favMovies.contains(movieId)) {
            flagSave = 1;
            fab.setImageResource(R.mipmap.ic_action_fav);
        } else {
            flagSave = 0;
            fab.setImageResource(R.mipmap.ic_action_unfav);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
