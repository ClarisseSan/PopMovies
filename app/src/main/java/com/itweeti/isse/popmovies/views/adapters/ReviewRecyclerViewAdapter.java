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

package com.itweeti.isse.popmovies.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itweeti.isse.popmovies.R;
import com.itweeti.isse.popmovies.fragment.ReviewFragment.OnListFragmentInteractionListener;
import com.itweeti.isse.popmovies.models.Reviews;

import java.util.List;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private List<Reviews> reviewsList;
    private final OnListFragmentInteractionListener mListener;


    public ReviewRecyclerViewAdapter(List<Reviews> items, OnListFragmentInteractionListener listener) {
        reviewsList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = reviewsList.get(position);
        holder.mAuthorView.setText(reviewsList.get(position).getAuthor());
        holder.mContentView.setText(reviewsList.get(position).getContent());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (reviewsList != null)
            return reviewsList.size();
        return 0;
    }

    public void setItemList(List<Reviews> itemList) {
        this.reviewsList = itemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthorView;
        public final TextView mContentView;
        public Reviews mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAuthorView = (TextView) view.findViewById(R.id.author);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
