package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieDetail;
import com.example.android.popularmoviesapp.data.Trailer;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.OnTrailerClickedHandler {

    private static final String TAG = "MovieDetailActivity";

    public static final String TRAILER_MOVIE_KEY = "trailer_movie_key";

    // TODO: override onSavedInstantState

    private MovieDetail mMovieDetail;

    private ImageView mImageThumbnail;
    private TextView mTile;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;

    private TrailerAdapter mAdapter;

    private final ArrayList<Trailer> mTrailers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        mMovieDetail = (MovieDetail) intent.getSerializableExtra(MainActivity.MOVIE_DETAIL);

        TextView mTitle = (TextView) findViewById(R.id.tv_original_title);
        mTitle.setText(mMovieDetail.getTitle());

        mAdapter = new TrailerAdapter(this, mTrailers, mMovieDetail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView trailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        trailersRecyclerView.setLayoutManager(layoutManager);
        trailersRecyclerView.setAdapter(mAdapter);

        // load movie trailers
        String movieId = mMovieDetail.getMovieId();
        Log.d(TAG, "onCreate: movie id = " + movieId);
        new TrailerAsyncTask().execute(NetworkUtils.buildMovieTrailersUrl(movieId));
    }

    @Override
    public void onPlayButtonClicked(String movieKey) {
        // start trailer in youtube
        Intent youtubeIntent = new Intent(MovieDetailActivity.this, YouTubeActivity.class);
        youtubeIntent.putExtra(TRAILER_MOVIE_KEY, movieKey);
        startActivity(youtubeIntent);
    }

    private class TrailerAsyncTask extends AsyncTask<URL, Void, String>{

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
            Log.d(TAG, "onPostExecute: result = " + result);
            ArrayList<Trailer> trailers = TrailerJsonResultParser.parse(result);
            for(Trailer trailer : trailers){
                Log.d("MovieDetailActivity", "trailer: " + trailer + "\n\n\n\n");
                mTrailers.add(trailer);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
