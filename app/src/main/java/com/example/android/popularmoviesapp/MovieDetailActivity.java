package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieDetail;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private MovieDetail mMovieDetail;

    private ImageView mImageThumbnail;
    private TextView mTile;
    private TextView mOverview;
    private TextView mRating;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        mMovieDetail = (MovieDetail) intent.getSerializableExtra(MainActivity.MOVIE_DETAIL);

        mImageThumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
        mTile = (TextView) findViewById(R.id.tv_original_title);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        Picasso.with(this).load(NetworkUtils.buildPosterUri(mMovieDetail.getImageUrl()))
                .into(mImageThumbnail);
        mTile.setText(mMovieDetail.getTitle());
        mOverview.setText(mMovieDetail.getOverview());
        mRating.setText(String.format("%s%s", mMovieDetail.getRating(), getString(R.string.of_max_rating)));
        mReleaseDate.setText(mMovieDetail.getReleaseDate());
    }
}
