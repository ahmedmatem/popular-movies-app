package com.example.android.popularmoviesapp.services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.interfaces.OnMovieLoadListener;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.example.android.popularmoviesapp.utilities.StorageUtils;

import java.util.ArrayList;

/**
 * Created by ahmed on 03/11/2017.
 */

public class FavoriteMovieAsyncTask extends AsyncTask<Uri, Void, Cursor> {

    private OnMovieLoadListener mCallback;
    private Context mContext;

    private ArrayList<Uri> mPosterUris = new ArrayList<>();
    private ArrayList<MovieDetail> mMovieDetails = new ArrayList<>();

    public FavoriteMovieAsyncTask(Context context, OnMovieLoadListener callback) {
        mCallback = callback;
        mContext = context;
    }

    @Override
    protected Cursor doInBackground(Uri... params) {
        Uri uri = params[0];
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        if (cursor == null)
            return;

        if (mPosterUris != null) mPosterUris.clear();
        if (mMovieDetails != null) {
            mMovieDetails.clear();
        } else {
            mMovieDetails = new ArrayList<>();
        }

        long id;
        String posterUrl, movieId, title, overview, rating, releaseDate;
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
            posterUrl = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL));
            movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            rating = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));

            mPosterUris.add(Uri.parse(posterUrl));

            MovieDetail movieDetail = new MovieDetail(id, movieId, posterUrl, title, overview, rating, releaseDate);
            String remoteImageUrl = NetworkUtils.buildPosterUri(
                    "/" + StorageUtils.getPathLastSegment(posterUrl)
            ).toString();
            // set remote image URL
            movieDetail.setRemoteImageUrl(remoteImageUrl);
            mMovieDetails.add(movieDetail);
        }

        mCallback.onMovieLoaded(mPosterUris, mMovieDetails);
    }


}
