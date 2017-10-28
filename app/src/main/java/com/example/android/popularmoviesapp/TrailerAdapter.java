package com.example.android.popularmoviesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.data.PopularMoviesPreferences;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.models.Trailer;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ahmed on 23/10/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private onMovieDetailClickHandler mCallback;

    public interface onMovieDetailClickHandler {
        void onPlayButtonClicked(String key);
        void onFavoriteButtonClicked(Bitmap image);
        void onUnfavoriteButtonClicked(long id);
    }

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CELL = 100;

    private ArrayList<Trailer> mTrailers = null;
    private MovieDetail mMovieDetail;

    public TrailerAdapter(onMovieDetailClickHandler callback, ArrayList<Trailer> trailers, MovieDetail movieDetail) {
        mCallback = callback;
        mTrailers = trailers;
        mMovieDetail = movieDetail;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_HEADER) {
            //Create viewHolder for header view
            View view = layoutInflater.inflate(R.layout.trailer_list_item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            //Create viewHolder for your default cell
            View view = layoutInflater.inflate(R.layout.trailer_list_item, parent, false);
            return new CellViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CellViewHolder) {
            CellViewHolder cellViewHolder = (CellViewHolder) holder;
            Context context = cellViewHolder.mPlayImageView.getContext();

            Trailer trailer = mTrailers.get(position - 1);

            cellViewHolder.mName.setText(context.getString(R.string.trailer_name, position));

            cellViewHolder.itemView.setTag(trailer.getKey());
        }

    }

    @Override
    public int getItemCount() {
        if (mTrailers != null) {
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
            mCallback.onPlayButtonClicked(v.getTag().toString());
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mImageThumbnail;
        final TextView mOverview;
        final TextView mRating;
        final TextView mReleaseDate;
        final Button mUnfavoriteButton;
        final Button mFavoriteButton;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            mImageThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);

            mOverview = (TextView) itemView.findViewById(R.id.tv_overview);
            mRating = (TextView) itemView.findViewById(R.id.tv_rating);
            mReleaseDate = (TextView) itemView.findViewById(R.id.tv_release_date);

            Context context = itemView.getContext();
            String sortOrder = PopularMoviesPreferences.getMovieSortOrder(context);

            mUnfavoriteButton = (Button) itemView.findViewById(R.id.btn_unfavorite);
            mFavoriteButton = (Button) itemView.findViewById(R.id.btn_favorite);

            if (sortOrder.equals(NetworkUtils.SORT_ORDER_FAVORITE)) {
                // load images locally
                Picasso.with(context).load(mMovieDetail.getImageUrl())
                        .into(mImageThumbnail);
                mUnfavoriteButton.setVisibility(View.VISIBLE);
                mUnfavoriteButton.setTag(mMovieDetail.getId());
                mUnfavoriteButton.setOnClickListener(this);
                mFavoriteButton.setVisibility(View.GONE);
            } else {
                // the favorite button is  necessary
                mFavoriteButton.setOnClickListener(this);
                Picasso.with(context).load(NetworkUtils.buildPosterUri(mMovieDetail.getImageUrl()))
                        .into(mImageThumbnail);
            }
            mOverview.setText(mMovieDetail.getOverview());
            mRating.setText(String.format("%s%s", mMovieDetail.getRating(), context.getString(R.string.of_max_rating)));
            mReleaseDate.setText(mMovieDetail.getReleaseDate().split("-")[0]);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_favorite:
                    BitmapDrawable draw = (BitmapDrawable) mImageThumbnail.getDrawable();
                    Bitmap bitmap = draw.getBitmap();
                    mCallback.onFavoriteButtonClicked(bitmap);
                    break;
                case R.id.btn_unfavorite:
                    mCallback.onUnfavoriteButtonClicked((long)v.getTag());
                    break;
            }
        }
    }
}
