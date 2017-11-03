package com.example.android.popularmoviesapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ahmed on 03/11/2017.
 */

public class MovieList {

    @SerializedName("results")
    private ArrayList<MovieDetail> movies = new ArrayList<>();

    public ArrayList<MovieDetail> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<MovieDetail> movies) {
        this.movies = movies;
    }
}
