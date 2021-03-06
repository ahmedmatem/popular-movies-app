package com.example.android.popularmoviesapp.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ahmed on 04/10/2017.
 */

public class MovieDetail implements Serializable {

    static final long serialVersionUID = 1L;

    private long mId;

    @SerializedName("id")
    @Expose
    private String mMovieId;

    @SerializedName("poster_path")
    @Expose
    private String mImageUrl;

    @SerializedName("title")
    @Expose
    private String mTitle;

    @SerializedName("overview")
    @Expose
    private String mOverview;

    @SerializedName("vote_average")
    @Expose
    private String mRating;

    @SerializedName("release_date")
    @Expose
    private String mReleaseDate;

    private String mRemoteImageUrl;
    private boolean mIsFavorite = false;
    private Bitmap mBitmap;

    public MovieDetail(long id, String movieId, String imageUrl, String title, String overview, String rating, String releaseDate) {
        this(movieId, imageUrl, title, overview, rating, releaseDate);
        mId = id;
    }
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

    public long getId() {
        return mId;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getRemoteImageUrl() {
        return mRemoteImageUrl;
    }

    public void setRemoteImageUrl(String remoteImageUrl) {
        mRemoteImageUrl = remoteImageUrl;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
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
