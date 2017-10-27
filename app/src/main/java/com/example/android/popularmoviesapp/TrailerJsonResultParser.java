package com.example.android.popularmoviesapp;

import com.example.android.popularmoviesapp.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ahmed on 23/10/2017.
 */

public class TrailerJsonResultParser {

    public static ArrayList<Trailer> parse(String jsonResult){
        ArrayList<Trailer> trailers = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            trailers = new ArrayList<>();
            JSONArray results = jsonObject.getJSONArray("results");
            for(int i = 0; i < results.length(); i++){
                JSONObject currentResult = results.getJSONObject(i);
                String id = currentResult.getString("id");
                String iso_639_1 = currentResult.getString("iso_639_1");
                String iso_3166_1 = currentResult.getString("iso_3166_1");
                String key = currentResult.getString("key");
                String name = currentResult.getString("name");
                String site = currentResult.getString("site");
                int size = currentResult.getInt("size");
                String type = currentResult.getString("type");

                trailers.add(
                        new Trailer(id, iso_639_1, iso_3166_1, key, name, site, size, type)
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return trailers;
    }
}
