package com.example.android.popularmoviesapp.services;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmoviesapp.interfaces.OnMovieLoadListener;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.parsers.MovieJsonResultParser;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ahmed on 03/11/2017.
 */

public class MovieAsyncTask extends AsyncTask<URL, Void, String> {
    private OnMovieLoadListener mCallback;
    private Context mContext;

    private ArrayList<Uri> mPosterUris = new ArrayList<>();
    private ArrayList<MovieDetail> mMovieDetails = new ArrayList<>();

    public MovieAsyncTask(OnMovieLoadListener callback, Context context) {
        mCallback = callback;
        mContext = context;
    }

    @Override
    protected String doInBackground(URL... params) {
        URL url = params[0];
        String result = null;
        try {
            result = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        // clear previous posters and movie details
        if (mPosterUris != null) mPosterUris.clear();
        if (mMovieDetails != null) mMovieDetails.clear();

        MovieJsonResultParser parser = new MovieJsonResultParser(s);
        mMovieDetails = parser.getMovieDetails();

        ArrayList<String> posterPaths = parser.getPosterPaths();
        if (posterPaths != null) {
            for (String posterPath : posterPaths) {
                mPosterUris.add(NetworkUtils.buildPosterUri(posterPath));
            }
        }

        mCallback.onMovieLoaded(mPosterUris, mMovieDetails);

//        mLayoutManager.scrollToPositionWithOffset(mFirstVisibleItemPosition, mPaddingTop);
//
//        mAdapter.notifyDataSetChanged();
    }
}
