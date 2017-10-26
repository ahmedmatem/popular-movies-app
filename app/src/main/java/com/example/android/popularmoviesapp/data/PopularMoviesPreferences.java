package com.example.android.popularmoviesapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.popularmoviesapp.utilities.NetworkUtils;

/**
 * Created by ahmed on 25/10/2017.
 */

public class PopularMoviesPreferences {

    public static final String SORT_ORDER_DEFAULT = NetworkUtils.SORT_ORDER_DEFAULT;
    public static final String SORT_ORDER = "sort_order";

    public static void setMovieSortOrder(Context context, String sortOrder){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SORT_ORDER, sortOrder);
        editor.apply();
    }

    public static final String getMovieSortOrder(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = sp.getString(SORT_ORDER, SORT_ORDER_DEFAULT);
        return sortOrder;
    }
}
