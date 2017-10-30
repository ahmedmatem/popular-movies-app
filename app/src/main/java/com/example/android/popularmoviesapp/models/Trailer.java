package com.example.android.popularmoviesapp.models;

/**
 * Created by ahmed on 23/10/2017.
 */

public class Trailer {
    private String mId;
    private String mIso_639_1;  // "en"
    private String mIso_3166_1; // "US"
    private String mKey;
    private String mName;
    private String mSite;       // "YouTube"
    private int mSize;
    private String mType;

    public Trailer(String id, String iso_639_1, String iso_3166_1,
                   String key, String name, String site,
                   int size, String type) {
        mId = id;
        mIso_639_1 = iso_639_1;
        mIso_3166_1 = iso_3166_1;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public String getIso_639_1() {
        return mIso_639_1;
    }

    public String getIso_3166_1() {
        return mIso_3166_1;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getSite() {
        return mSite;
    }

    public int getSize() {
        return mSize;
    }

    public String getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "mId='" + mId + '\'' +
                ", mIso_639_1='" + mIso_639_1 + '\'' +
                ", mIso_3166_1='" + mIso_3166_1 + '\'' +
                ", mKey='" + mKey + '\'' +
                ", mName='" + mName + '\'' +
                ", mSite='" + mSite + '\'' +
                ", mSize=" + mSize +
                ", mType='" + mType + '\'' +
                '}';
    }
}
