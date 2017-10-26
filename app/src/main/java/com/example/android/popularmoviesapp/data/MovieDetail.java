package com.example.android.popularmoviesapp.data;

import java.io.Serializable;

/**
 * Created by ahmed on 04/10/2017.
 */

public class MovieDetail implements Serializable {

    static final long serialVersionUID = 1L;

    private String mMovieId;
    private String mImageUrl;
    private String mTitle;
    private String mOverview;
    private String mRating;
    private String mReleaseDate;
    private int mTrailersCount;

    public MovieDetail(String movieId, String imageUrl, String title, String overview, String rating, String releaseDate) {
        mMovieId = movieId;
        mImageUrl = imageUrl;
        mTitle = title;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getTrailersCount() { return mTrailersCount; }

    @Override
    public String toString() {
        return "MovieDetail{" +
                "mImageUrl='" + mImageUrl + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mOverview='" + mOverview + '\'' +
                ", mRating='" + mRating + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                '}';
    }
}
