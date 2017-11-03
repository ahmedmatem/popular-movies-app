package com.example.android.popularmoviesapp.interfaces;

import android.net.Uri;

import com.example.android.popularmoviesapp.models.MovieDetail;

import java.util.ArrayList;

/**
 * Created by ahmed on 03/11/2017.
 */

public interface OnMovieLoadListener {
    void onMovieLoaded(ArrayList<Uri> posterUris, ArrayList<MovieDetail> movieDetails);
}
