package com.itweeti.isse.popmovies.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.activity.MovieDetailActivity;
import com.itweeti.isse.popmovies.activity.MovieListActivity;
import com.itweeti.isse.popmovies.data.MovieContract;
import com.itweeti.isse.popmovies.models.Reviews;
import com.itweeti.isse.popmovies.models.Trailer;
import com.itweeti.isse.popmovies.utils.Config;
import com.itweeti.isse.popmovies.utils.Utils;
import com.itweeti.isse.popmovies.views.adapters.ReviewRecyclerViewAdapter;
import com.itweeti.isse.popmovies.views.adapters.TrailerRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "movieId";


    private static final String LOG_TAG = "OverviewFragment";
    private static final String STATE_ID = "movie_id";
    private static final String STATE_DATA = "flagDataType";
    private static final String STATE_TITLE = "title";
    private static final String STATE_YEAR = "year";
    private static final String STATE_DURATION = "duration";
    private static final String STATE_RATING = "rating";
    private static final String STATE_VOTE = "vote_ave";
    private static final String STATE_OVERVIEW = "overview";
    private static final String STATE_POSTER = "poster";

    private String movieId;

    //overview variables
    private String mTitle;
    private String mYear;
    private String mDuration;
    private String mRating;
    private String mOverview;
    private String mPoster;
    private String first_trailer_url = "";

    private TextView txtYear;
    private TextView txtDuration;
    private TextView txtDescription;
    private ImageView imgPoster;
    private RatingBar ratingBar;

    private float vote_average;

    //trailer variables
    private RecyclerView trailerRecyclerView;
    private TrailerRecyclerViewAdapter trailerListAdapter;
    private List<Trailer> movieTrailersList;
    private TrailerFragment.OnListFragmentInteractionListener mTrailerListener;

    //review variables
    private List<Reviews> movieReviewList;
    private RecyclerView reviewRecvyclerView;
    private ReviewRecyclerViewAdapter reviewListAdapter;
    private ReviewFragment.OnListFragmentInteractionListener mReviewListener;


    //if from api or local data
    //flagDataType = 0 --> from popular movies Activty
    //flagDataType = 1 --> from favorites activity
    private int flagDataType;


    private int mColumnCount = 1;
    private Intent intent;
    private TextView txtTrailer;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getActivity().getIntent();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            //get movieId from MovieDetailActivity
            flagDataType = getArguments().getInt("flagData", 0);
            movieId = getArguments().getString("movieId");
            mTitle = getArguments().getString("title");

            mYear = getArguments().getString("year");
            mDuration = getArguments().getString("duration");
            mRating = getArguments().getString("rating");
            vote_average = getArguments().getFloat("vote_ave");
            mOverview = getArguments().getString("overview");
            mPoster = getArguments().getString("poster");
            movieTrailersList = getArguments().getParcelableArrayList("trailer_list");
            movieReviewList = getArguments().getParcelableArrayList("review_list");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mTitle);
            }
        }


        if (savedInstanceState != null) {
            movieId = savedInstanceState.getString(STATE_ID);
            flagDataType = savedInstanceState.getInt(STATE_DATA);
            mTitle = savedInstanceState.getString(STATE_TITLE);
            mYear = savedInstanceState.getString(STATE_YEAR);
            mDuration = savedInstanceState.getString(STATE_DURATION);
            mRating = savedInstanceState.getString(STATE_RATING);
            vote_average = savedInstanceState.getFloat(STATE_VOTE);
            mOverview = savedInstanceState.getString(STATE_OVERVIEW);
            mPoster = savedInstanceState.getString(STATE_POSTER);
            //TODO: include trailer and reviews
        }


        //for allowing access in movie poster
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //Check for any issues
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getActivity());

        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(getActivity(), 0).show();
        }

        Log.v("HEHEHE - 1", "flagDataType  => " + flagDataType);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current movieID state
        outState.putString(STATE_ID, movieId);
        outState.putInt(STATE_DATA, flagDataType);
        outState.putString(STATE_TITLE, mTitle);
        outState.putString(STATE_YEAR, mYear);
        outState.putString(STATE_DURATION, mDuration);
        outState.putString(STATE_RATING, mRating);
        outState.putFloat(STATE_VOTE, vote_average);
        outState.putString(STATE_OVERVIEW, mOverview);
        outState.putString(STATE_POSTER, mPoster);
    }

    private void getLocalData() {

        List<Trailer> trailers = null;
        List<Reviews> reviews = null;

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_ITEM_ID)) {
            flagDataType = arguments.getInt("flagData", 0);
            mTitle = arguments.getString("title");
            mYear = arguments.getString("year");
            mDuration = arguments.getString("duration");
            mRating = arguments.getString("rating");
            vote_average = Float.parseFloat(mRating) / 2;
            mOverview = arguments.getString("overview");
            //mPoster = arguments.getString("poster");
            //trailers = arguments.getParcelableArrayList("trailers");
            //reviews = arguments.getParcelableArrayList("reviews");
        } else {
            flagDataType = intent.getIntExtra("flagData", 0);
            mTitle = intent.getStringExtra("title");
            mYear = intent.getStringExtra("year");
            mDuration = intent.getStringExtra("duration");
            mRating = intent.getStringExtra("rating");
            vote_average = Float.parseFloat(mRating) / 2;
            mOverview = intent.getStringExtra("overview");
            //mPoster = intent.getStringExtra("poster");
            //trailers = intent.getParcelableArrayListExtra("trailers");
            //reviews = intent.getParcelableArrayListExtra("reviews");
        }

        //movieTrailersList = trailers;
        //movieReviewList = reviews;

        /* get poster in Favorite TABLE*/
        Uri uri = MovieContract.FavoriteEntry.buildFavoriteUri(Long.parseLong(movieId));
        Cursor cursor = getContext().getContentResolver().query(
                uri,
                null,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieId},
                null);

        if (cursor.moveToFirst()) {
            int posterIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_IMAGE);
            mPoster = cursor.getString(posterIndex); //column_image is 3rd column
        }

        //finally, close the cursor
        cursor.close();


         /* get ReviewList in REVIEW TABLE*/
        movieReviewList = new ArrayList<>();
        uri = MovieContract.ReviewEntry.buildReviewUri(Long.parseLong(movieId));
        cursor = getContext().getContentResolver().query(
                uri,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieId},
                null);

        int authorIndex = 0;
        int contentIndex = 0;
        if (cursor.moveToFirst()) {
            authorIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
            contentIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
        }

        while (cursor.isAfterLast() == false) {
            //get the trailer num, url
            String author = cursor.getString(authorIndex);
            String content = cursor.getString(contentIndex);
            //add to list
            Reviews r = new Reviews(author, content);
            movieReviewList.add(r);
        }
        //finally, close the cursor
        cursor.close();


         /* get movieTrailersList in TRAILER TABLE*/
        movieTrailersList = new ArrayList<>();
        uri = MovieContract.TrailerEntry.buildTrailerUri(Long.parseLong(movieId));
        cursor = getContext().getContentResolver().query(
                uri,
                null,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieId},
                null);

        int numIndex = 0;
        int urlIndex = 0;
        if (cursor.moveToFirst()) {
            numIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_NUMBER);
            urlIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_URL);
        }

        while (cursor.isAfterLast() == false) {
            //get the trailer num, url
            String num = cursor.getString(numIndex);
            String url = cursor.getString(urlIndex);
            //add to list
            Trailer t = new Trailer(num, url);
            movieTrailersList.add(t);
        }
        //finally, close the cursor
        cursor.close();

        setValuesOfView(mYear, mDuration, mOverview, vote_average, mPoster);
    }

    private void requestMovieReviews(String movieId) {
        //http://api.themoviedb.org/3/reviews/293660/videos?api_key=6d369d4e0676612d2d046b7f3e8424bd

        movieReviewList = new ArrayList<>();

        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=" + Config.API_KEY;
        String id = movieId;
        final String vid = "/reviews";
        final String reviews_url = BASE_PATH + id + vid + api_key;

        Log.d("TRAILER URL--------> ", reviews_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());


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
                            String author = "";
                            String content = "";

                            if (results != null) {
                                for (int i = 0; i < results.length(); i++) {

                                    JSONObject obj = results.getJSONObject(i);
                                    author = obj.getString("author");
                                    content = obj.getString("content");

                                    //add to a review object
                                    Reviews reviews = new Reviews(author, content);
                                    movieReviewList.add(reviews);

                                }

                            }

                            if (movieReviewList.isEmpty()) {
                                author = "No Reviews Available";
                                content = " ";

                                //add to a review object
                                Reviews reviews = new Reviews(author, content);
                                movieReviewList.add(reviews);
                            }

                            Log.d("Review list size ", String.valueOf(movieReviewList.size()));

                            //updates recyclerview once data is fetched from the API call
                            reviewListAdapter.setItemList(movieReviewList);
                            reviewListAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //other catches
                        if (error instanceof NoConnectionError) {
                            //show dialog no net connection
                            Utils.showSuccessDialog(getActivity(), R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void requestMovieTrailer(String movieId) {

        movieTrailersList = new ArrayList<>();

        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=6d369d4e0676612d2d046b7f3e8424bd";
        String id = movieId;
        final String vid = "/videos";
        String trailer_url = BASE_PATH + id + vid + api_key;

        Log.d(LOG_TAG, "TRAILER URL----------> " + trailer_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, trailer_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                String trailer_key = obj.getString("key");
                                //String youtube_trailer = "https://www.youtube.com/watch?v=" + trailer_key;
                                String youtube_trailer = trailer_key;
                                String trailer_num = "Trailer " + (i + 1);

                                Trailer trailer = new Trailer(trailer_num, youtube_trailer);

                                // save trailers in a list
                                movieTrailersList.add(trailer);
                            }

                            // no trailers fetched from API
                            if (movieTrailersList.isEmpty()) {
                                txtTrailer.setText(R.string.no_trailers);
                            }

                            trailerListAdapter.setItemList(movieTrailersList);
                            trailerListAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //other catches
                        if (error instanceof NoConnectionError) {
                            //show dialog no net connection
                            Utils.showSuccessDialog(getActivity(), R.string.no_connection, R.string.net).show();
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
        RequestQueue queue = Volley.newRequestQueue(getActivity());


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
                            Log.v("mRating:>>>>>>>>>>>>>> ", mRating);
                            Log.v("mOverview:>>>>>>>>>>>> ", mOverview);
                            Log.v("mPoster:>>>>>>>>>>>>>> ", mPoster);

                            setValuesOfView(mYear, mDuration, mOverview, vote_average, mPoster);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //other catches
                        if (error instanceof NoConnectionError) {
                            //show dialog no net connection
                            Utils.showSuccessDialog(getActivity(), R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void setValuesOfView(String mYear, String mDuration, String mOverview, float vote_average, String mPoster) {
        txtYear.setText(mYear);
        txtDuration.setText(mDuration);
        txtDescription.setText(mOverview);
        ratingBar.setRating(vote_average / 2);

        switch (flagDataType) {
            case 0:
                Glide
                        .with(getActivity())
                        .load(mPoster)
                        .placeholder(R.drawable.ic_loading)
                        .fitCenter()
                        .error(R.drawable.ic_error)
                        .into(imgPoster);
                break;
            case 1:
                //set movie poster
                imgPoster.setImageBitmap(Utils.decodeBase64Image(mPoster));
                break;

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        Log.v("HEHEHE - 1", "flagDataType=" + flagDataType + "  movieId=" + movieId);

        // Show the content
        if (movieId != null) {
            //Movie Release Year
            txtYear = (TextView) rootView.findViewById(R.id.txt_year);

            //Movie Duration
            txtDuration = (TextView) rootView.findViewById(R.id.txt_duration);

            //Movie Description
            txtDescription = (TextView) rootView.findViewById(R.id.txt_description);

            //Movie poster
            imgPoster = (ImageView) rootView.findViewById(R.id.img_movie);

            //rating
            ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);

            //trailer list
            trailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_recyclerview);
            setTrailerRecyclerView(trailerRecyclerView);

            //review list
            reviewRecvyclerView = (RecyclerView) rootView.findViewById(R.id.review_recyclerview);
            setReviewRecyclerView(reviewRecvyclerView);

            //trailer label
            txtTrailer = (TextView) rootView.findViewById(R.id.txt_trailer);

            //request data from moviedb.org using API call or from shared preferences
            switch (flagDataType) {
                case 0:
                    requestMovieDetail(movieId);
                    requestMovieTrailer(movieId);
                    requestMovieReviews(movieId);
                    break;
                case 1:
                    getLocalData();
                    break;
            }


        }

        return rootView;
    }

    private void setReviewRecyclerView(RecyclerView reviewRecvyclerView) {
        // Set the adapter
        if (mColumnCount <= 1) {
            reviewRecvyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            reviewRecvyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        }
        reviewListAdapter = new ReviewRecyclerViewAdapter(movieReviewList, mReviewListener);
        reviewRecvyclerView.setAdapter(reviewListAdapter);
    }

    private void setTrailerRecyclerView(RecyclerView trailer_recyclerView) {
        // Set the adapter
        if (mColumnCount <= 1) {
            trailer_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            trailer_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        }

        trailerListAdapter = new TrailerRecyclerViewAdapter(movieTrailersList, mTrailerListener);
        trailer_recyclerView.setAdapter(trailerListAdapter);


        trailer_recyclerView.addOnItemTouchListener(new TrailerFragment.RecyclerTouchListener(getActivity(), trailer_recyclerView, new TrailerFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Trailer trailer = movieTrailersList.get(position);
                String url = trailer.getTrailerUrl();
                Toast.makeText(getActivity(), trailer.getTrailerUrl() + " is selected!", Toast.LENGTH_SHORT).show();
                launchYoutube(Config.DEVELOPER_KEY, url);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    /**
     * Launch Youtube to watch  URL
     */
    private void launchYoutube(String api_key, String video_id) {

        Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), api_key, video_id);
        startActivity(intent);

    }


}