package com.itweeti.isse.popmovies.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.fragment.TrailerFragment.OnListFragmentInteractionListener;
import com.itweeti.isse.popmovies.models.Trailer;

import java.util.List;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {


    private List<Trailer> trailerList;
    private final OnListFragmentInteractionListener mListener;

    public TrailerRecyclerViewAdapter(List<Trailer> trailers, OnListFragmentInteractionListener listener) {
        trailerList = trailers;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.trailer = trailerList.get(position);
        holder.trailer_num.setText(trailerList.get(position).getTrailerNumber());
        //holder.trailer_url.setText(trailerList.get(position).getTrailerUrl());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.trailer);
                }
            }
        });
    }

    public void setItemList(List<Trailer> trailers) {
        this.trailerList = trailers;
    }

    @Override
    public int getItemCount() {
        if (trailerList != null)
            return trailerList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView trailer_num;
        //public TextView trailer_url;
        public Trailer trailer;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            trailer_num = (TextView) view.findViewById(R.id.txt_trailer);
            //trailer_url = (TextView) view.findViewById(R.id.txt_url);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + trailer_num.getText() + "'";
        }
    }
}
