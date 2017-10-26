package com.example.android.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieDetail;
import com.example.android.popularmoviesapp.data.Trailer;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ahmed on 23/10/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnTrailerClickedHandler mCallbacl;

    public interface OnTrailerClickedHandler {
        void onPlayButtonClicked(String key);
    }

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CELL = 100;

    private ArrayList<Trailer> mTrailers = null;
    private MovieDetail mMovieDetail;

    public TrailerAdapter(OnTrailerClickedHandler callback, ArrayList<Trailer> trailers, MovieDetail movieDetail) {
        mCallbacl = callback;
        mTrailers = trailers;
        mMovieDetail = movieDetail;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if(viewType == VIEW_TYPE_HEADER){
            //Create viewHolder for header view
            View view = layoutInflater.inflate(R.layout.trailer_list_item_header, parent, false);

            ImageView mImageThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            TextView mOverview = (TextView) view.findViewById(R.id.tv_overview);
            TextView mRating = (TextView) view.findViewById(R.id.tv_rating);
            TextView mReleaseDate = (TextView) view.findViewById(R.id.tv_release_date);

            Picasso.with(context).load(NetworkUtils.buildPosterUri(mMovieDetail.getImageUrl()))
                    .into(mImageThumbnail);
            mOverview.setText(mMovieDetail.getOverview());
            mRating.setText(String.format("%s%s", mMovieDetail.getRating(), context.getString(R.string.of_max_rating)));
            mReleaseDate.setText(mMovieDetail.getReleaseDate());

            return new HeaderViewHolder(view);
        } else {
            //Create viewHolder for your default cell
            View view = layoutInflater.inflate(R.layout.trailer_list_item, parent, false);
            return new CellViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CellViewHolder){
            CellViewHolder cellViewHolder = (CellViewHolder) holder;
            Context context = cellViewHolder.mPlayImageView.getContext();

            Trailer trailer = mTrailers.get(position - 1);

            cellViewHolder.mName.setText(context.getString(R.string.trailer_name, position));

            cellViewHolder.itemView.setTag(trailer.getKey());
        }

    }

    @Override
    public int getItemCount() {
        if(mTrailers != null){
            // return size of trailers + one more item for header
            return mTrailers.size() + 1;
        }
        // return one item only for header
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_CELL;
    }

    class CellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mPlayImageView;
        final TextView mName;

        public CellViewHolder(View itemView) {
            super(itemView);

            mPlayImageView = (ImageView) itemView.findViewById(R.id.iv_play);
//            mPlayImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);

            mName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        @Override
        public void onClick(View v) {
            mCallbacl.onPlayButtonClicked(v.getTag().toString());
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mImageThumbnail;
        final TextView mOverview;
        final TextView mRating;
        final TextView mReleaseDate;



        public HeaderViewHolder(View itemView) {
            super(itemView);
            mImageThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);

            mOverview = (TextView) itemView.findViewById(R.id.tv_overview);
            mRating = (TextView) itemView.findViewById(R.id.tv_rating);
            mReleaseDate = (TextView) itemView.findViewById(R.id.tv_release_date);

            Context context = itemView.getContext();
            Picasso.with(context).load(NetworkUtils.buildPosterUri(mMovieDetail.getImageUrl()))
                    .into(mImageThumbnail);
            mOverview.setText(mMovieDetail.getOverview());
            mRating.setText(String.format("%s%s", mMovieDetail.getRating(), context.getString(R.string.of_max_rating)));
            mReleaseDate.setText(mMovieDetail.getReleaseDate());
        }

        @Override
        public void onClick(View v) {
            Log.d("TrailerAdapter", "onClick: Play image was clicked");
        }
    }
}
