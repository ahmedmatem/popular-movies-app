package com.example.android.popularmoviesapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.popularmoviesapp.data.MovieContentProvider;
import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.data.PopularMoviesPreferences;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.parsers.MovieJsonResultParser;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.example.android.popularmoviesapp.utilities.StorageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String SORT_ORDER_KEY = "sort_order";

    private static final int NUMBER_OF_COLUMNS = 2;
    public static final String MOVIE_DETAIL = "Movie detail";

    private final ArrayList<Uri> mPosterUris = new ArrayList<>();
    private ArrayList<MovieDetail> mMovieDetails = new ArrayList<>();
    String sortQueryParamValue;

    private MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView moviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        moviesRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MovieAdapter(mPosterUris, this);
        moviesRecyclerView.setAdapter(mAdapter);

        sortQueryParamValue = NetworkUtils.SORT_ORDER_DEFAULT;
        if (savedInstanceState != null) {
            sortQueryParamValue = savedInstanceState.getString(SORT_ORDER_KEY);
        }
        PopularMoviesPreferences.setMovieSortOrder(this, sortQueryParamValue);
    }

    private void setActivityTitle(String sortQueryParamValue) {
        String title;
        switch (sortQueryParamValue) {
            case NetworkUtils.SORT_ORDER_POPULAR:
                title = getString(R.string.title_popular_movies);
                break;
            case NetworkUtils.SORT_ORDER_TOP_RATED:
                title = getString(R.string.title_top_rated_movies);
                break;
            case NetworkUtils.SORT_ORDER_FAVORITE:
                title = getString(R.string.title_favorite_movies);
                break;
            default:
                title = getString(R.string.title_popular_movies);
        }

        setTitle(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_ORDER_KEY, sortQueryParamValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sortQueryParamValue.equals(NetworkUtils.SORT_ORDER_FAVORITE)){
            showFavoriteMovies(sortQueryParamValue);
        } else {
            showMoviePosters(sortQueryParamValue);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.mi_most_popular:
                showMoviePosters(NetworkUtils.SORT_ORDER_POPULAR);
                return true;
            case R.id.mi_top_rated:
                showMoviePosters(NetworkUtils.SORT_ORDER_TOP_RATED);
                return true;
            case R.id.mi_favorite:
                showFavoriteMovies(NetworkUtils.SORT_ORDER_FAVORITE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void movieOnClick(int itemIndex) {
        MovieDetail currentMovieDetail = mMovieDetails.get(itemIndex);
        Cursor cursor = NetworkUtils.getMovieFromStorage(this, currentMovieDetail.getMovieId());
        if(cursor.moveToNext()){
            // movie is in the favorite table
            currentMovieDetail.setFavorite(true);
            // set local image URL
            currentMovieDetail
                    .setImageUrl(cursor.getString(
                            cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL)));
        } else {
            currentMovieDetail.setFavorite(false);
        }

        Intent intent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MOVIE_DETAIL, currentMovieDetail);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class FavoriteMoviesAsyncTask extends AsyncTask<Uri, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Uri... params) {
            Uri uri = params[0];
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri,
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

            mAdapter.notifyDataSetChanged();
        }
    }

    private class MoviePostersAsyncTask extends AsyncTask<URL, Void, String> {

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

            mAdapter.notifyDataSetChanged();
        }
    }

    private void showMoviePosters(String sortQueryParamValue) {
        this.sortQueryParamValue = sortQueryParamValue;
        setActivityTitle(sortQueryParamValue);
        new MoviePostersAsyncTask().execute(NetworkUtils.buildMovieUrl(sortQueryParamValue));
    }

    private void showFavoriteMovies(String sortQueryParamValue) {
        this.sortQueryParamValue = sortQueryParamValue;
        setActivityTitle(NetworkUtils.SORT_ORDER_FAVORITE);
        new FavoriteMoviesAsyncTask().execute(MovieContract.MovieEntry.CONTENT_URI);
    }
}
