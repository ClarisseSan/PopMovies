package com.itweeti.isse.popmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.utils.Utils;
import com.itweeti.isse.popmovies.fragment.MovieDetailFragment;
import com.itweeti.isse.popmovies.models.FavoriteMovie;
import com.itweeti.isse.popmovies.views.adapters.gridview.GridSpacingItemDecoration;
import com.itweeti.isse.popmovies.models.Reviews;
import com.itweeti.isse.popmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;


public class FavoriteListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public boolean mTwoPane;
    private RecyclerView recyclerView;
    private FavoriteItemRecyclerViewAdapter mAdapter;

    private ArrayList<FavoriteMovie> list;
    private List<Trailer> movieTrailersList;
    private List<Reviews> movieReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0f);
        }


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.favorite_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        int spanCount = 2; // 2 columns
        int spacing = 20; // 20px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));


        assert recyclerView != null;
        displayFavoriteMovies(recyclerView);


        //assert recyclerView != null;
        //setupRecyclerView(recyclerView);


        if (findViewById(R.id.favorite_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayFavoriteMovies(recyclerView);
    }

    private JSONArray getFavoriteMovies() throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String items = preferences.getString("favorites", "");
        return new JSONArray(items);
    }


    private void displayFavoriteMovies(RecyclerView recyclerView) {


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
                if (trailers.length() > 0) {
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
                if (reviews.length() >= 0) {
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
                for (Trailer t : movieTrailersList
                        ) {
                    System.out.println("TRAILER: " + t.getTrailerUrl());
                }
                //**************************//


                FavoriteMovie fav = new FavoriteMovie(movie_id, movie_name, movie_image, movie_overview, movie_date, movie_vote, movie_duration, movieTrailersList, movieReviewsList);
                list.add(fav);

                System.out.println("FAVORITE MOVIES SIZE---------> " + list.size());

/*
                mAdapter = new FavoriteItemRecyclerViewAdapter(list);
                mAdapter.setItemList(list);
                mAdapter.notifyDataSetChanged();

                this.recyclerView.setAdapter(mAdapter);

                //updates recyclerview once data is fetched from the API call
                this.recyclerView.getAdapter().notifyDataSetChanged();
                */
                setupRecyclerView(recyclerView);

            }



        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

       // mAdapter = new FavoriteItemRecyclerViewAdapter(list);
       // recyclerView.setAdapter(mAdapter);
       // mAdapter.setItemList(list);

        mAdapter = new FavoriteItemRecyclerViewAdapter(list);
        mAdapter.setItemList(list);
        mAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(mAdapter);

        //updates recyclerview once data is fetched from the API call
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    public class FavoriteItemRecyclerViewAdapter
            extends RecyclerView.Adapter<FavoriteItemRecyclerViewAdapter.ViewHolder> {

        private ArrayList<FavoriteMovie> movieList;

        public FavoriteItemRecyclerViewAdapter(ArrayList<FavoriteMovie> list) {
            this.movieList = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = movieList.get(position);
            //set movie name to textView
            holder.mTitleView.setText(holder.mItem.getMovie_name());


            //set movie poster
            holder.mImageView.setImageBitmap(Utils.decodeBase64Image(holder.mItem.getMovie_image()));


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.getMovie_id());
                        arguments.putString("movieId", holder.mItem.getMovie_id());
                        arguments.putInt("flagData", 1);
                        arguments.putString("title", holder.mItem.getMovie_name());

                        arguments.putString("year", holder.mItem.getMovie_date());
                        arguments.putString("rating", holder.mItem.getMovie_vote());
                        arguments.putString("overview", holder.mItem.getMovie_overview());
                        arguments.putString("poster", holder.mItem.getMovie_image());
                        arguments.putString("duration", holder.mItem.getMovie_duration());
                        arguments.putParcelableArrayList("trailers", (ArrayList<? extends Parcelable>) holder.mItem.getMovie_trailerList());
                        arguments.putParcelableArrayList("reviews", (ArrayList<? extends Parcelable>) holder.mItem.getReviewsList());


                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.favorite_detail_container, fragment)
                                .commit();


                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.getMovie_id())
                                .putExtra("flagData", 1)
                                .putExtra("movieId", holder.mItem.getMovie_id())
                                .putExtra("title", holder.mItem.getMovie_name())
                                .putExtra("year", holder.mItem.getMovie_date())
                                .putExtra("rating", holder.mItem.getMovie_vote())
                                .putExtra("overview", holder.mItem.getMovie_overview())
                                .putExtra("poster", holder.mItem.getMovie_image())
                                .putExtra("duration",holder.mItem.getMovie_duration())
                                .putParcelableArrayListExtra("trailers", (ArrayList<? extends Parcelable>) holder.mItem.getMovie_trailerList())
                                .putParcelableArrayListExtra("reviews", (ArrayList<? extends Parcelable>) holder.mItem.getReviewsList());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (movieList != null)
                return movieList.size();
            return 0;
        }

        public void setItemList(ArrayList<FavoriteMovie> list) {
            this.movieList = list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitleView;

            public FavoriteMovie mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.img_movie);
                mTitleView = (TextView) view.findViewById(R.id.txt_name);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView.getText() + "'";
            }
        }
    }
}

