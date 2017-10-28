package com.example.android.popularmoviesapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ahmed on 03/10/2017.
 */

public class NetworkUtils {
    /**
     * Example API Request
     * https://api.themoviedb.org/3/movie/popular|top_rated/api_key=<api-key>
     */

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String API_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String API_KEY_VALUE = "34908b2a2093affdee976f3885ae1c0e";
    private static final String API_KEY_PARAM = "api_key";

    public static final String SORT_ORDER_POPULAR = "popular";
    public static final String SORT_ORDER_TOP_RATED = "top_rated";
    public static final String SORT_ORDER_FAVORITE = "favorite";
    public static final String SORT_ORDER_DEFAULT = SORT_ORDER_POPULAR;

    private static final String TRAILERS_PATH = "/videos";
    public static final String REVIEWS_PATH = "/reviews";

    private static final String POSTER_DEFAULT_SIZE = "w185";

    public static URL buildMovieUrl(String sortQueryParam) {
        Uri builtUri = Uri.parse(API_BASE_URL + sortQueryParam)
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieTrailersUrl(String movieId){
        Uri buildUri = Uri.parse(API_BASE_URL + movieId + TRAILERS_PATH)
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();

        URL url = null;
        try{
           url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieReviewsUrl(String movieId){
        Uri buildUri = Uri.parse(API_BASE_URL + movieId + REVIEWS_PATH)
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();

        URL url = null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static Uri buildPosterUri(String posterPath){
        return Uri.parse(BASE_URL + POSTER_DEFAULT_SIZE + posterPath);
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
