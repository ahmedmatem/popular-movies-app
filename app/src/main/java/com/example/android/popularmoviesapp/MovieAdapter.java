package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ahmed on 03/10/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Cursor mCursor;
    private final ArrayList<Uri> mPosterUris;

    private final MovieOnClickHandler mMovieOnClickHandler;

    public interface MovieOnClickHandler{
        void movieOnClick(int itemIndex);
    }

    public MovieAdapter(ArrayList<Uri> posterUrls, MovieOnClickHandler handler) {
        mPosterUris = posterUrls;
        mMovieOnClickHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movie_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri uri = mPosterUris.get(position);
        Context context = holder.mPoster.getContext();
        Picasso.with(context).load(uri).into(holder.mPoster);
    }

    @Override
    public int getItemCount() {
        return mPosterUris.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView mPoster;

        public ViewHolder(View itemView) {
            super(itemView);

            mPoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            mPoster.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mMovieOnClickHandler.movieOnClick(getAdapterPosition());
        }
    }
}
