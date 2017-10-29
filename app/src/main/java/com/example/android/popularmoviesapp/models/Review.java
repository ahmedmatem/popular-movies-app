package com.example.android.popularmoviesapp.models;

/**
 * Created by ahmed on 29/10/2017.
 */

public class Review {
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mURL;

    public Review(String id, String author, String content, String URL) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mURL = URL;
    }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getURL() {
        return mURL;
    }
}
