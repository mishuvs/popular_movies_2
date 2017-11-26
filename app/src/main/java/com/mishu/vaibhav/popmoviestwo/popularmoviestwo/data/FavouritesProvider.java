package com.mishu.vaibhav.popmoviestwo.popularmoviestwo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Vaibhav on 11/26/2017.
 */

public class FavouritesProvider extends ContentProvider {

    private static final int CODE_FAVOURITES_LIST = 101;
    private static final String TAG = FavouritesProvider.class.getName();

    SQLiteOpenHelper mDbHelper;
    UriMatcher sUriMatcher = buildUriMatcher();

    public UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.FavouritesEntry.PATH,CODE_FAVOURITES_LIST);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)){

            case CODE_FAVOURITES_LIST:
                cursor = db.query(MovieContract.FavouritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Sunshine.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(MovieContract.FavouritesEntry.TABLE_NAME, null, values);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numberOfItems = 0;
        switch (sUriMatcher.match(uri)){
            case CODE_FAVOURITES_LIST:
                numberOfItems = db.delete(
                        MovieContract.FavouritesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            //We don't need to delete single items therefore no delete option for that
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(numberOfItems>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return numberOfItems;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException(
                "We are not implementing insert in Popular Movies app");
    }
}

