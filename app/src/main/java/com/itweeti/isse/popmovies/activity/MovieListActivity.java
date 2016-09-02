package com.itweeti.isse.popmovies.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.carousel.MainCarousel;
import com.itweeti.isse.popmovies.data.MovieContract;
import com.itweeti.isse.popmovies.data.MovieHelper;
import com.itweeti.isse.popmovies.fragment.MovieDetailFragment;
import com.itweeti.isse.popmovies.models.MovieImage;
import com.itweeti.isse.popmovies.utils.Config;
import com.itweeti.isse.popmovies.utils.Utils;
import com.itweeti.isse.popmovies.views.adapters.gridview.GridSpacingItemDecoration;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private List<String> movieImages = new ArrayList<>();
    private ArrayList<MovieImage> list;

    private SharedPreferences mSharedPreferences;
    private String sortOption;
    private MovieItemRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;


    private ImageLoader imageLoader;
    private String encodedString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);


        //To actually create a database we have to call one of SQLitedatabase
        // method getReadableDatabase() or getWritableDatabase().
        MovieHelper movieHelper = new MovieHelper(this);
        movieHelper.getWritableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0f);
        }

        //initialize image loader
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }


        recyclerView = (RecyclerView) findViewById(R.id.movie_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //add spacing
        int spanCount = 2; // 2 columns
        int spacing = 20; // 20px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));


        assert recyclerView != null;
        setupRecyclerView(recyclerView);


        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;


        }

        // Get the instance of SharedPreferences object
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sortOption = mSharedPreferences.getString(getString(R.string.pref_sort_key), "popular");


        updateMovies();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        // Get the instance of SharedPreferences object
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // We retrieve foreground and background color value as a string
        sortOption = mSharedPreferences.getString(getString(R.string.pref_sort_key), "popular");

        // getMovies();
        requestMovies();

    }


    String addMovie(long movieId, String title, String image) {

        String id = String.valueOf(movieId);
        String movie_id = "";

        // First, check if the movieId exists in the db
        Cursor locationCursor = this.getContentResolver().query(
                MovieContract.DetailEntry.CONTENT_URI,
                new String[]{MovieContract.DetailEntry.COLUMN_MOVIE_ID},
                MovieContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{id},
                null);

        if (locationCursor.moveToFirst()) {
            int movieIdIndex = locationCursor.getColumnIndex(MovieContract.DetailEntry.COLUMN_MOVIE_ID);
            movie_id = locationCursor.getString(movieIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieContract.DetailEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.DetailEntry.COLUMN_POSTER, image);

            // Finally, insert location data into the database.
            Uri insertedUri = this.getContentResolver().insert(
                    MovieContract.DetailEntry.CONTENT_URI,
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movie_id = String.valueOf(ContentUris.parseId(insertedUri));

        }

        locationCursor.close();
        // Wait, that worked?  Yes!
        return movie_id;
    }

    private void requestMovies() {

        //http://api.themoviedb.org/3/movie/popular/?api_key=6d369d4e0676612d2d046b7f3e8424bd
        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String sort_order = sortOption;
        final String api_key = "?api_key=" + Config.API_KEY;
        ;

        Log.e("SORT OPTION", sort_order);


        String original_url = BASE_PATH + sort_order + api_key;
        System.out.println("ORIGINAL URL >>>>>>>>" + original_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MovieListActivity.this);


        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, original_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {

                            //loop through the content of that JSON object

                            list = new ArrayList<>();

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");


                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                long movie_id = obj.getLong("id");
                                String movie_name = obj.getString("title");
                                String movie_image = obj.getString("poster_path");


                                //save the images to a String array
                                //You will need to append a base path ahead of this relative path to build
                                //the complete url you will need to fetch the image using Picasso.

                                //1.base path
                                final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/";
                                //2. Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original"
                                String image_size = "w342";
                                //3. And finally the poster path returned by the query : movie_image

                                String posterPath = IMAGE_BASE_PATH + image_size + movie_image;


                                MovieImage movieImage = new MovieImage(movie_id, movie_name, posterPath);
                                list.add(movieImage);
                                Log.v("xxxxx-add", "adding movie: " + movie_name);

                                movieImages.add(posterPath);


                                loadImageBitmap(posterPath);

                                //add movie to database
                                addMovie(movie_id, movie_name, posterPath);
                            }


                            for (MovieImage img : list) {
                                Log.d("MOVIE ID: ", String.valueOf(img.getMovie_id()));
                                Log.d("MOVIE NAME: ", img.getMovie_name());
                                Log.d("MOVIE IMAGE: ", img.getMovie_image());

                            }

                            int itemCount = movieImages.size();
                            System.out.println("IMAGE COUNT...>>>>>>>>>" + itemCount);

                            mAdapter.setItemList(list);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.getAdapter().notifyDataSetChanged();

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
                            Utils.showSuccessDialog(MovieListActivity.this, R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void loadImageBitmap(String image_url) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        if (id == R.id.action_fav) {
            //if movies are available in the favoriteList
            //then show FavoritesActivity


            try {
                if (Utils.getFavoriteMovies(this) != null) {
                    Intent intent = new Intent(this, FavoriteListActivity.class);
                    startActivity(intent);
                } else {
                    //inform user that no movies are added to favorites
                    Utils.showSuccessDialog(this, R.string.fav, R.string.no_favorites).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        if (id == R.id.action_favorites) {
            //if movies are available in the favoriteList
            //then show FavoritesActivity
            Cursor c =
                    this.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                            new String[]{MovieContract.FavoriteEntry._ID},
                            null,
                            null,
                            null);
            if (c.getCount() == 0) {
                //inform user that no movies are added to favorites
                Utils.showSuccessDialog(this, R.string.fav, R.string.no_favorites).show();
            } else {
                Intent intent = new Intent(this, MainCarousel.class);
                startActivity(intent);
            }


        }


        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mAdapter = new MovieItemRecyclerViewAdapter(this, movieImages, list);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Attach loader to our  database query
        // run when loader is initialized
        return new CursorLoader(this,
                MovieContract.DetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class MovieItemRecyclerViewAdapter
            extends RecyclerView.Adapter<MovieItemRecyclerViewAdapter.ViewHolder> {

        private List<String> imageUrls;
        private List<MovieImage> movieImages;
        private Context context;

        public MovieItemRecyclerViewAdapter(Context context, List<String> imageUrls, List<MovieImage> movieImages) {
            this.context = context;
            this.imageUrls = imageUrls;
            this.movieImages = movieImages;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = movieImages.get(position);

            //set image into image view
            Picasso
                    .with(context)
                    .load(movieImages.get(position).getMovie_image())
                    .fit()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_loading)
                    .into(holder.mImageView);

            //set content description for blind
            //holder.mImageView.setContentDescription(movieImages.get(position).getMovie_name());

            //set movie name to textView
            holder.mTitleView.setText(movieImages.get(position).getMovie_name());


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(holder.mItem.getMovie_name());
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MovieDetailFragment.ARG_ITEM_ID, Long.toString(holder.mItem.getMovie_id()));
                        arguments.putString("movieId", Long.toString(holder.mItem.getMovie_id()));
                        arguments.putInt("flagData", 0);
                        arguments.putString("title", holder.mItem.getMovie_name());

                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();

                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra("movieId", Long.toString(holder.mItem.getMovie_id()))
                                .putExtra("flagData", 0)
                                .putExtra("title", holder.mItem.getMovie_name());


                        context.startActivity(intent);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            if (movieImages != null)
                return movieImages.size();
            return 0;
        }

        public void setItemList(List<MovieImage> movieImages) {
            this.movieImages = movieImages;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitleView;

            public MovieImage mItem;

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
