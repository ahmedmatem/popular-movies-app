package com.example.android.popularmoviesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.PopularMoviesPreferences;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.models.Review;
import com.example.android.popularmoviesapp.models.Trailer;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ahmed on 23/10/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "TrailerAdapter";

    private onMovieDetailClickHandler mCallback;

    public interface onMovieDetailClickHandler {
        void onPlayButtonClicked(String key);

        void onFavoriteButtonClicked(Bitmap image);

        void onUnfavoriteButtonClicked(long id);
    }

    private static final int VIEW_TYPE_HEADER = 100;
    private static final int VIEW_TYPE_TRAILER_CELL = 101;
    private static final int VIEW_TYPE_REVIEW_HEADER = 200;
    private static final int VIEW_TYPE_REVIEW_CELL = 201;

    private ArrayList<Trailer> mTrailers = null;
    private ArrayList<Review> mReviews = null;
    private MovieDetail mMovieDetail;

    public TrailerAdapter(onMovieDetailClickHandler callback, ArrayList<Trailer> trailers,
                          ArrayList<Review> reviews, MovieDetail movieDetail) {
        mCallback = callback;
        mTrailers = trailers;
        mReviews = reviews;
        mMovieDetail = movieDetail;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_HEADER) {
            View view = layoutInflater.inflate(R.layout.trailer_list_item_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_TRAILER_CELL) {
            View view = layoutInflater.inflate(R.layout.trailer_list_item, parent, false);
            return new TrailerCellViewHolder(view);
        } else if (viewType == VIEW_TYPE_REVIEW_HEADER) {
            View view = layoutInflater.inflate(R.layout.review_list_item_header, parent, false);
            return new ReviewHeaderViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.review_list_item, parent, false);
            return new ReviewCellViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position is " + position);
        Log.d(TAG, "onBindViewHolder: view type is " + getItemViewType(position));
        if (holder instanceof TrailerCellViewHolder) {
            TrailerCellViewHolder trailerCellViewHolder = (TrailerCellViewHolder) holder;
            Context context = trailerCellViewHolder.mPlayImageView.getContext();

            Trailer trailer = mTrailers.get(position - 1);

            trailerCellViewHolder.mName.setText(context.getString(R.string.trailer_name, position));

            trailerCellViewHolder.itemView.setTag(trailer.getKey());
        }
        if (holder instanceof ReviewCellViewHolder) {
            ReviewCellViewHolder reviewCellViewHolder = (ReviewCellViewHolder) holder;
            if (mReviews != null) {
                int numberOfTrailers = 0;
                if (mTrailers != null) {
                    numberOfTrailers += mTrailers.size();
                }
                Review review = mReviews.get(position - numberOfTrailers - 2);
                reviewCellViewHolder.author.setText(review.getAuthor());
            }
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = 1;  //one item for header
        if (mTrailers != null) {
            itemCount += mTrailers.size();
        }
        if (mReviews != null && mReviews.size() > 0) {
            itemCount += mReviews.size() + 1;   // + 1 for header
        }

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_HEADER;
        if(mTrailers.size() > 0){
            // there are trailers
            if(0 < position && position <= mTrailers.size())
                return VIEW_TYPE_TRAILER_CELL;
            if (mTrailers.size() < position){
                // there are reviews
                if(position == mTrailers.size() + 1)
                    return VIEW_TYPE_REVIEW_HEADER;
                if(position > mTrailers.size() + 1)
                    return VIEW_TYPE_REVIEW_CELL;
            }
        } else {
            // there are not trailers
            if(mReviews.size() > 0){
                // there are reviews
                if(position == 1)
                    return VIEW_TYPE_REVIEW_HEADER;
                if(position > 1)
                    return VIEW_TYPE_REVIEW_CELL;
            }
        }
        return VIEW_TYPE_REVIEW_HEADER;
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
            switch (v.getId()) {
                case R.id.btn_favorite:
                    BitmapDrawable draw = (BitmapDrawable) mImageThumbnail.getDrawable();
                    Bitmap bitmap = draw.getBitmap();
                    mCallback.onFavoriteButtonClicked(bitmap);
                    break;
                case R.id.btn_unfavorite:
                    mCallback.onUnfavoriteButtonClicked((long) v.getTag());
                    break;
            }
        }
    }

    class TrailerCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mPlayImageView;
        final TextView mName;

        public TrailerCellViewHolder(View itemView) {
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

    class ReviewHeaderViewHolder extends RecyclerView.ViewHolder {
        public ReviewHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ReviewCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView author;

        public ReviewCellViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_review_author);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
