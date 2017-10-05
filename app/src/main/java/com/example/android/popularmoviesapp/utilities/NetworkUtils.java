package com.example.android.popularmoviesapp.utilities;

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

    private static final String API_BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static final String QUERY_PARAM_POPULAR = "popular";
    public static final String QUERY_PARAM_DEFAULT = QUERY_PARAM_POPULAR;
    public static final String QUERY_PARAM_TOP_RATED = "top_rated";

    private static final String API_KEY_VALUE = "Ap1KeYg0esHe1R";
    private static final String API_KEY_PARAM = "api_key";

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_DEFAULT_SIZE = "w185";

    public static URL buildApiUrl(String queryParam) {
        Uri builtUri = Uri.parse(API_BASE_URL + queryParam)
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
}
