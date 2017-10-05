package com.example.android.popularmoviesapp.data;

import java.io.Serializable;

/**
 * Created by ahmed on 04/10/2017.
 */

public class MovieDetail implements Serializable {

    static final long serialVersionUID = 1L;

    private String mImageUrl;
    private String mTitle;
    private String mOverview;
    private String mRating;
    private String mReleaseDate;

    public MovieDetail(String imageUrl, String title, String overview, String rating, String releaseDate) {
        mImageUrl = imageUrl;
        mTitle = title;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
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