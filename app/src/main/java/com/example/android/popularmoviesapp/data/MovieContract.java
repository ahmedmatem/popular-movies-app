package com.example.android.popularmoviesapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ahmed on 27/10/2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE_MOVIE = "favorite";

    public static class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIE)
                .build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_IMAGE_URL = "image_url";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
