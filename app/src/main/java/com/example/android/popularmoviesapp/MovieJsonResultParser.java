package com.example.android.popularmoviesapp;

import com.example.android.popularmoviesapp.data.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ahmed on 04/10/2017.
 */

public class MovieJsonResultParser {

    private final String mJsonResult;

    public MovieJsonResultParser(String jsonResult) {
        this.mJsonResult = jsonResult;
    }

    public ArrayList<String> getPosterPaths() {
        ArrayList<String> posterPaths = null;
        try {
            JSONObject jsonResult = new JSONObject(mJsonResult);
            posterPaths = new ArrayList<>();
            JSONArray results = jsonResult.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                String posterPath = results.getJSONObject(i).getString("poster_path");
                posterPaths.add(posterPath);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return posterPaths;
    }

    public ArrayList<MovieDetail> getMovieDetails() {
        ArrayList<MovieDetail> movieDetails = null;
        try {
            JSONObject jsonResult = new JSONObject(mJsonResult);
            movieDetails = new ArrayList<>();
            JSONArray results = jsonResult.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentObject = results.getJSONObject(i);
                String title = currentObject.getString("original_title");
                String overview = currentObject.getString("overview");
                String releaseDate = currentObject.getString("release_date");
                String rating = currentObject.getString("vote_average");
                String imageUrl = currentObject.getString("poster_path");

                MovieDetail movieDetail = new MovieDetail(imageUrl, title, overview, rating, releaseDate);
                movieDetails.add(movieDetail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieDetails;
    }
}
