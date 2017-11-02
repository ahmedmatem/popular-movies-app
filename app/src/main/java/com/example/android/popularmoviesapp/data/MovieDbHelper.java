package com.example.android.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ahmed on 27/10/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    public static final int DATABASE_VERSION = 26;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "(" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
