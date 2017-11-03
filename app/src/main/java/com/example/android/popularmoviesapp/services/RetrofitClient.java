package com.example.android.popularmoviesapp.services;

import android.net.Uri;

import com.example.android.popularmoviesapp.interfaces.MovieAPIService;
import com.example.android.popularmoviesapp.interfaces.OnMovieLoadListener;
import com.example.android.popularmoviesapp.models.MovieDetail;
import com.example.android.popularmoviesapp.models.MovieList;
import com.example.android.popularmoviesapp.utilities.NetworkUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ahmed on 03/11/2017.
 */

public class RetrofitClient {
    private Retrofit mRetrofit;
    private static MovieAPIService mMovieAPIService;

    private static OnMovieLoadListener mCallback;

    public static final String BASE_URL = NetworkUtils.API_BASE_URL;

    public RetrofitClient(OnMovieLoadListener callback) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMovieAPIService = mRetrofit.create(MovieAPIService.class);
        mCallback = callback;
    }

    public static void loadMovies(String path, String queryParam){
        Call<MovieList> call = mMovieAPIService.getMovies(path, queryParam);

        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if(response.isSuccessful()){
                    ArrayList<Uri> mPosterUris = new ArrayList<Uri>();
                    ArrayList<MovieDetail> mMovieDetails = response.body().getMovies();
                    if(mMovieDetails != null){
                        for(MovieDetail movie : mMovieDetails){
                            mPosterUris.add(NetworkUtils.buildPosterUri(movie.getImageUrl()));
                            movie.setRemoteImageUrl(
                                    NetworkUtils.buildPosterUri(movie.getImageUrl()).toString()
                            );
                        }
                    }
                    mCallback.onMovieLoaded(mPosterUris, mMovieDetails);
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
            }
        });
    }
}
