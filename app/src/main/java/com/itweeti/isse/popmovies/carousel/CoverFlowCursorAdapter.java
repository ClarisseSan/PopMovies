/*
* Copyright 2016 Angela Sanchez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
* */

package com.itweeti.isse.popmovies.carousel;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.data.MovieContract;

/**
 * Created by isse on 12 Aug 2016.
 */
public class CoverFlowCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = CoverFlowCursorAdapter.class.getSimpleName();
    private Context context;

    public CoverFlowCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        int layoutId = R.layout.item_flow_view;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "In bind View");


        int columnIdIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
        final String movieID = cursor.getString(columnIdIndex);

        int columnTitleIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_TITLE);
        final String title = cursor.getString(columnTitleIndex);
        viewHolder.txtMovieName.setText(title);

        int imageIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_IMAGE);
        String image = cursor.getString(imageIndex);
        //Log.i(LOG_TAG, "Image reference extracted: " + image);

        viewHolder.imgMovieImage.setImageBitmap(decodeBase64Image(image));

    }

    private Bitmap decodeBase64Image(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
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
