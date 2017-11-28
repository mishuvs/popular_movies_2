package com.mishu.vaibhav.popmoviestwo.popularmoviestwo.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Vaibhav on 11/26/2017.
 */

public class MovieContract {

    private static final String TAG = MovieContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.mishu.vaibhav.popmoviestwo.popularmoviestwo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static class FavouritesEntry implements BaseColumns {

        public static final String PATH = "weather";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_TMDB_ID = "id";
        public static final String COLUMN_RELEASE_DATE = "date";
        public static final String COLUMN_URL_THUMBNAIL = "posterUrl";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                COLUMN_TMDB_ID + " INTEGER NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_RATING + " REAL NOT NULL, " +
                COLUMN_URL_THUMBNAIL + " TEXT NOT NULL, " +

                /*
                 * To ensure this table can only contain one weather entry per date, we declare
                 * the date column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a weather entry for a certain date and we attempt to
                 * insert another weather entry with that date, we replace the old weather entry.
                 */
                " UNIQUE (" + COLUMN_TMDB_ID + ") ON CONFLICT REPLACE);";

        }
    }