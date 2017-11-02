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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContentProvider;
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

    public static final String MOVIE_DETAIL_KEY = "movie_detail";

    private TrailerAdapter mAdapter;

    private final ArrayList<Trailer> mTrailers = new ArrayList<>();
    private final ArrayList<Review> mReviews = new ArrayList<>();
    private MovieDetail mMovieDetail;

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // set movieDetail
        if (savedInstanceState != null) {
            // from activity state
            if (savedInstanceState.containsKey(MOVIE_DETAIL_KEY)) {
                mMovieDetail = (MovieDetail) savedInstanceState.getSerializable(MOVIE_DETAIL_KEY);
            }
        } else {
            // from intent
            Intent intent = getIntent();
            mMovieDetail = (MovieDetail) intent.getSerializableExtra(MainActivity.MOVIE_DETAIL);
        }

        TextView mTitle = (TextView) findViewById(R.id.tv_original_title);
        mTitle.setText(mMovieDetail.getTitle());

        mAdapter = new TrailerAdapter(this, mTrailers, mReviews, mMovieDetail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView trailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        trailersRecyclerView.setLayoutManager(layoutManager);
        trailersRecyclerView.setAdapter(mAdapter);

        // load trailers
        String movieId = mMovieDetail.getMovieId();
        if (NetworkUtils.isOnline(this)) {
            new TrailerAsyncTask().execute(NetworkUtils.buildMovieTrailersUrl(movieId));
        }

        getSupportLoaderManager().initLoader(ID_REVIEW_LOADER, null, this).forceLoad();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(MOVIE_DETAIL_KEY, mAdapter.getMovieDetail());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
//        mAdapter.getMovieDetail().setFavorite(!mAdapter.getMovieDetail().isFavorite());
        super.onStop();
    }

    @Override
    public void onPlayButtonClicked(String movieKey) {
        Uri webPage = Uri.parse(NetworkUtils.buildYoutubeVideoUrl(movieKey).toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteMarkClicked(View v, boolean isFavorite) {
        ImageView iv = (ImageView) v;
        if (isFavorite) {
            iv.setImageResource(R.mipmap.ic_favorite_star_disable);
        } else {
            iv.setImageResource(R.mipmap.ic_favorite_star_enable);
        }
        mAdapter.getMovieDetail().setFavorite(!isFavorite);
    }

    private void performFavoriteAction() {
        if (mAdapter.getMovieDetail().isFavorite()) {
            Bitmap bitmap = mAdapter.getMovieDetail().getBitmap();
            tryMarkMovieAsFavorite(bitmap);
        } else {
            String movieId = mAdapter.getMovieDetail().getMovieId();
            TryDeleteFavoriteMovie(movieId);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            performFavoriteAction();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        performFavoriteAction();
        super.onBackPressed();
    }

    private void tryMarkMovieAsFavorite(Bitmap bitmap) {
        String movieId = mAdapter.getMovieDetail().getMovieId();
        if (MovieContentProvider.isMovieFavorite(this, movieId)) {
            // movie has already marked as favorite
            return;
        }

        imagePath =
                StorageUtils.saveToInternalStorage(getApplicationContext(), bitmap,
                        mMovieDetail.getImageUrl());
        if (imagePath != null) {
            String localImageUrl = StorageUtils.buildImageUri(imagePath + mMovieDetail.getImageUrl()).toString();
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, localImageUrl);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovieDetail.getMovieId());
            cv.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovieDetail.getTitle());
            cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovieDetail.getOverview());
            cv.put(MovieContract.MovieEntry.COLUMN_RATING, mMovieDetail.getRating());
            cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovieDetail.getReleaseDate());

            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);

            // set local image urls in adapter
            mAdapter.getMovieDetail().setImageUrl(localImageUrl);
            mAdapter.notifyDataSetChanged();
        }
    }

    private int TryDeleteFavoriteMovie(String movieId) {
        if (MovieContentProvider.isMovieFavorite(this, movieId)) {
            StorageUtils.deleteImageFromStorage(getApplicationContext(),
                    mAdapter.getMovieDetail().getImageUrl());
            mAdapter.notifyDataSetChanged();

            Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.valueOf(movieId));
            return getContentResolver().delete(uri, null, null);
        }
        return -1;
    }

    private class TrailerAsyncTask extends AsyncTask<URL, Void, String> {

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
        protected void onPostExecute(String result) {
            if (result == null)
                return;
            ArrayList<Trailer> trailers = TrailerJsonResultParser.parse(result);
            for (Trailer trailer : trailers) {
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
        if (mReviews != null) {
            mReviews.clear();
        }
    }
}
