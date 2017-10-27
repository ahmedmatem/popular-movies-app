package com.example.android.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ahmed on 27/10/2017.
 */

public class MovieContentProvider extends ContentProvider {

    private MovieDbHelper mMovieDbHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = MovieContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITE_MOVIE, MOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITE_MOVIE + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor returnCursor = null;
        switch (match){
            case MOVIES:
                returnCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_ID:
                break;
            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch (match){
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
