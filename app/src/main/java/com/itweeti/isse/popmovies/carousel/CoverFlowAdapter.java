package com.itweeti.isse.popmovies.carousel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.activity.MovieDetailActivity;
import com.itweeti.isse.popmovies.models.FavoriteMovie;

import java.util.ArrayList;

//BaseAdapter

public class CoverFlowAdapter extends BaseAdapter {


    private ArrayList<FavoriteMovie> movieList;
    private Context context;

    public CoverFlowAdapter(Context context, ArrayList<FavoriteMovie> objects) {
        this.context = context;
        this.movieList = objects;
    }


    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public FavoriteMovie getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_flow_view, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //set movie poster
        viewHolder.imgMovieImage.setImageBitmap(decodeBase64Image(position));


        //set movie name
        viewHolder.txtMovieName.setText(movieList.get(position).getMovie_name());

        convertView.setOnClickListener(onClickListener(position));

        return convertView;
    }

    private Bitmap decodeBase64Image(int position) {
        byte[] decodedString = Base64.decode(movieList.get(position).getMovie_image(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                        Intent i = new Intent(context, MovieDetailActivity.class)
                        //pass the selected movie_id to the next Activity
                        .putExtra("flagData", 1)
                        .putExtra("movieId", movieList.get(position).getMovie_id())
                        .putExtra("title", movieList.get(position).getMovie_name())
                        .putExtra("year", movieList.get(position).getMovie_date())
                        .putExtra("rating", movieList.get(position).getMovie_vote())
                        .putExtra("overview", movieList.get(position).getMovie_overview())
                        .putExtra("poster", movieList.get(position).getMovie_image())
                        .putExtra("duration", movieList.get(position).getMovie_duration())
                        .putParcelableArrayListExtra("trailers", (ArrayList<? extends Parcelable>) movieList.get(position).getMovie_trailerList())
                        .putParcelableArrayListExtra("reviews", (ArrayList<? extends Parcelable>) movieList.get(position).getReviewsList());

                context.startActivity(i);


            }
        };
    }

    public void setItemList(ArrayList<FavoriteMovie> list) {
        this.movieList = list;
    }


    private static class ViewHolder {
        private TextView txtMovieName;
        private ImageView imgMovieImage;

        public ViewHolder(View v) {
            imgMovieImage = (ImageView) v.findViewById(R.id.image);
            txtMovieName = (TextView) v.findViewById(R.id.name);
        }
    }
}
