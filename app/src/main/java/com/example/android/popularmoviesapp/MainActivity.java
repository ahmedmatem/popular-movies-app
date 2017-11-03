package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.data.PopularMoviesPreferences;
import com.example.android.popularmoviesapp.interfaces.OnMovieLoadListener;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.services.FavoriteMovieAsyncTask;
import com.example.android.popularmoviesapp.services.MovieAsyncTask;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieOnClickHandler,
        OnMovieLoadListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String FIRST_VISIBLE_POSITION = "first_position";
    public static final String PADDING_TOP = "padding_top";
    private int mFirstVisibleItemPosition, mPaddingTop;

    public static final String SORT_ORDER_KEY = "sort_order";

    private static final int NUMBER_OF_COLUMNS = 2;
    public static final String MOVIE_DETAIL = "Movie detail";

    private GridLayoutManager mLayoutManager;
    private RecyclerView mMoviesRecyclerView;

    private final ArrayList<Uri> mPosterUris = new ArrayList<>();
    private ArrayList<MovieDetail> mMovieDetails = new ArrayList<>();
    String sortQueryParamValue;

    private MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mAdapter = new MovieAdapter(mPosterUris, this);
        mMoviesRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        mMoviesRecyclerView.setLayoutManager(mLayoutManager);

        sortQueryParamValue = NetworkUtils.SORT_ORDER_DEFAULT;
        if (savedInstanceState != null) {
            sortQueryParamValue = savedInstanceState.getString(SORT_ORDER_KEY);
            mFirstVisibleItemPosition =
                    savedInstanceState.getInt(FIRST_VISIBLE_POSITION);
            mPaddingTop = savedInstanceState.getInt(PADDING_TOP);
            Log.d(TAG, "onCreate: padding top: " + mPaddingTop);
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
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(FIRST_VISIBLE_POSITION, firstVisibleItemPosition);
        View firstVisibleView = mLayoutManager.findViewByPosition(firstVisibleItemPosition);
        outState.putInt(PADDING_TOP, firstVisibleView.getTop());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sortQueryParamValue.equals(NetworkUtils.SORT_ORDER_FAVORITE)) {
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
        if (cursor.moveToNext()) {
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

    @Override
    public void onMovieLoaded(ArrayList<Uri> posterUris, ArrayList<MovieDetail> movieDetails) {
        if (posterUris != null) {
            if (mPosterUris != null) {
                mPosterUris.clear();
                for (Uri posterUri : posterUris) {
                    mPosterUris.add(posterUri);
                }
            }
        }

        mMovieDetails = movieDetails;

        // set scroll position
        mLayoutManager.scrollToPositionWithOffset(mFirstVisibleItemPosition, mPaddingTop);

        mAdapter.notifyDataSetChanged();
    }

    private void showMoviePosters(String sortQueryParamValue) {
        this.sortQueryParamValue = sortQueryParamValue;
        setActivityTitle(sortQueryParamValue);
        new MovieAsyncTask(this, this)
                .execute(NetworkUtils.buildMovieUrl(sortQueryParamValue));
    }

    private void showFavoriteMovies(String sortQueryParamValue) {
        this.sortQueryParamValue = sortQueryParamValue;
        setActivityTitle(NetworkUtils.SORT_ORDER_FAVORITE);
        new FavoriteMovieAsyncTask(this, this)
                .execute(MovieContract.MovieEntry.CONTENT_URI);
    }
}
