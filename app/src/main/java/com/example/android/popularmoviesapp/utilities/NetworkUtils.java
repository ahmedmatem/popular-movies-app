package com.example.android.popularmoviesapp.utilities;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.data.MovieDbHelper;

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

    private static final String API_KEY_VALUE = "api_key_value";
    private static final String API_KEY_PARAM = "api_key";

    public static final String SORT_ORDER_POPULAR = "popular";
    public static final String SORT_ORDER_TOP_RATED = "top_rated";
    public static final String SORT_ORDER_FAVORITE = "favorite";
    public static final String SORT_ORDER_DEFAULT = SORT_ORDER_POPULAR;

    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/";

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

    public static URL buildYoutubeVideoUrl(String videoKey){
        URL url = null;
        try {
            return new URL(BASE_YOUTUBE_URL + "watch?v=" + videoKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return  url;
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

    /**
     * Check if a movie has already marked as favorite
     *
     * @param context
     * @param movieId
     * @return
     */
    public static boolean isMarkedAsFavorite(Context context, String movieId) {
        SQLiteDatabase db = new MovieDbHelper(context).getReadableDatabase();
        Uri contentUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.valueOf(movieId));
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public static Cursor getMovieFromStorage(Context context, String movieId) {
        SQLiteDatabase db = new MovieDbHelper(context).getReadableDatabase();
        Uri contentUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.valueOf(movieId));
        return context.getContentResolver().query(contentUri, null, null, null, null);
    }
}
