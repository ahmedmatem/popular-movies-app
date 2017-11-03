package com.example.android.popularmoviesapp.interfaces;

/**
 * Created by ahmed on 03/11/2017.
 */

import com.example.android.popularmoviesapp.models.MovieList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPIService {
    @GET("{sortBy}")
    Call<MovieList> getMovies(@Path("sortBy") String sortBy, @Query("api_key") String apiKeyValue);
}
