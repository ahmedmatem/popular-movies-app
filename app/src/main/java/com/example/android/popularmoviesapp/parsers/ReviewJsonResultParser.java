package com.example.android.popularmoviesapp.parsers;

import com.example.android.popularmoviesapp.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ahmed on 29/10/2017.
 */

public class ReviewJsonResultParser {

    public static ArrayList<Review> parse(String json){
        ArrayList<Review> reviews = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray results = jsonObject.getJSONArray("results");
            reviews = new ArrayList<>();
            for(int i = 0; i < results.length(); i++){
                JSONObject result = results.getJSONObject(i);
                String id = result.getString("id");
                String author = result.getString("author");
                String content = result.getString("content");
                String url = result.getString("url");

                Review review = new Review(id, author, content, url);
                reviews.add(review);
            }

        } catch (JSONException e){
            e.printStackTrace();
        } catch (NullPointerException e){
            return null;
        }
        return reviews;
    }
}
