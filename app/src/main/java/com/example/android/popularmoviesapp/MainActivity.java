package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.android.popularmoviesapp.data.MovieDetail;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler {

    private static final int NUMBER_OF_COLUMNS = 2;
    public static final String MOVIE_DETAIL = "Movie detail";

    private static final String TAG = MainActivity.class.getSimpleName();

    private final ArrayList<Uri> mPosterUris = new ArrayList<>();
    private ArrayList<MovieDetail> mMovieDetails = new ArrayList<>();

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

        // TODO: Check the internet connection

        if (isOnline()) {
            showMoviePosters(NetworkUtils.QUERY_PARAM_DEFAULT);
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
                showMoviePosters(NetworkUtils.QUERY_PARAM_POPULAR);
                return true;
            case R.id.mi_top_rated:
                showMoviePosters(NetworkUtils.QUERY_PARAM_TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void movieOnClick(int itemIndex) {
        Log.d(TAG, "movieOnClick: itemIndex = " + itemIndex);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MOVIE_DETAIL, mMovieDetails.get(itemIndex));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class MoviePostersAsyncTask extends AsyncTask<URL, Void, String> {

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

    private void showMoviePosters(String queryParam) {
        new MoviePostersAsyncTask().execute(NetworkUtils.buildApiUrl(queryParam));
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
