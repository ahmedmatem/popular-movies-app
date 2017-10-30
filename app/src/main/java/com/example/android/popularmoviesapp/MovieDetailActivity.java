package com.example.android.popularmoviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.models.Review;
import com.example.android.popularmoviesapp.models.Trailer;
import com.example.android.popularmoviesapp.parsers.ReviewJsonResultParser;
import com.example.android.popularmoviesapp.parsers.TrailerJsonResultParser;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.example.android.popularmoviesapp.utilities.StorageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.onMovieDetailClickHandler,
        LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = "MovieDetailActivity";

    public static final int ID_REVIEW_LOADER = 0;

    public static final String TRAILER_MOVIE_KEY = "trailer_movie_key";

    // TODO: override onSavedInstantState

    private MovieDetail mMovieDetail;

    private TrailerAdapter mAdapter;

    private final ArrayList<Trailer> mTrailers = new ArrayList<>();
    private final ArrayList<Review> mReviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        mMovieDetail = (MovieDetail) intent.getSerializableExtra(MainActivity.MOVIE_DETAIL);

        TextView mTitle = (TextView) findViewById(R.id.tv_original_title);
        mTitle.setText(mMovieDetail.getTitle());

        mAdapter = new TrailerAdapter(this, mTrailers, mReviews, mMovieDetail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView trailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        trailersRecyclerView.setLayoutManager(layoutManager);
        trailersRecyclerView.setAdapter(mAdapter);

        // load movie trailers
        String movieId = mMovieDetail.getMovieId();
        Log.d(TAG, "onCreate: movie id = " + movieId);
        if (NetworkUtils.isOnline(this)) {
            new TrailerAsyncTask().execute(NetworkUtils.buildMovieTrailersUrl(movieId));
        }

        getSupportLoaderManager().initLoader(ID_REVIEW_LOADER, null, this).forceLoad();
    }

    @Override
    public void onPlayButtonClicked(String movieKey) {
//        // start trailer in youtube
//        Intent youtubeIntent = new Intent(MovieDetailActivity.this, YouTubeActivity.class);
//        youtubeIntent.putExtra(TRAILER_MOVIE_KEY, movieKey);
//        startActivity(youtubeIntent);

        Uri webpage = Uri.parse(NetworkUtils.buildYoutubeVideoUrl(movieKey).toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteButtonClicked(Bitmap bitmap) {
        // insert image to internal memory
        String imageUrl =
                StorageUtils.saveToInternalStorage(getApplicationContext(), bitmap, mMovieDetail.getImageUrl());

        if (imageUrl != null) {
            Log.d(TAG, "inserting movie detail");
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, imageUrl + mMovieDetail.getImageUrl());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovieDetail.getMovieId());
            cv.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovieDetail.getTitle());
            cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovieDetail.getOverview());
            cv.put(MovieContract.MovieEntry.COLUMN_RATING, mMovieDetail.getRating());
            cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovieDetail.getReleaseDate());

            // Insert movie detail into database via a ContentResolver
            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
        }
    }

    @Override
    public void onUnfavoriteButtonClicked(long id) {
        // TODO: delete row from database by movieId using MovieContentProvider
        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
        int deletedRows = getContentResolver().delete(uri, null, null);

        if (deletedRows > 0) {
            finish();
        }
    }

    private class TrailerAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            Log.d(TAG, "doInBackground: url = " + url);
            String result = null;
            try {
                result = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) return;
            ArrayList<Trailer> trailers = TrailerJsonResultParser.parse(result);
            for (Trailer trailer : trailers) {
//                Log.d("MovieDetailActivity", "trailer: " + trailer + "\n\n\n\n");
                mTrailers.add(trailer);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_REVIEW_LOADER:
                return new AsyncTaskLoader<String>(this) {
                    @Override
                    public String loadInBackground() {
                        String data = null;
                        try {
                            data = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildMovieReviewsUrl(mMovieDetail.getMovieId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return data;
                    }
                };
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        ArrayList<Review> reviews = ReviewJsonResultParser.parse(data);
        if (reviews == null)
            return;

        for (Review review : reviews) {
            mReviews.add(review);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        if (mReviews != null) mReviews.clear();
    }
}
