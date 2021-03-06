package com.example.android.popularmoviesapp.parsers;

import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;
import com.example.android.popularmoviesapp.utilities.StorageUtils;

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
            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    String posterPath = results.getJSONObject(i).getString("poster_path");
                    posterPaths.add(posterPath);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
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
                String movieId = currentObject.getString("id");
                String title = currentObject.getString("original_title");
                String overview = currentObject.getString("overview");
                String releaseDate = currentObject.getString("release_date");
                String rating = currentObject.getString("vote_average");
                String imageUrl = currentObject.getString("poster_path");

                MovieDetail movieDetail = new MovieDetail(movieId, imageUrl, title, overview, rating, releaseDate);
                // set remote image URL
                String remoteImageUrl = NetworkUtils.buildPosterUri(
                        "/" + StorageUtils.getPathLastSegment(imageUrl)
                ).toString();
                movieDetail.setRemoteImageUrl(remoteImageUrl);
                movieDetails.add(movieDetail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return movieDetails;
    }
}
