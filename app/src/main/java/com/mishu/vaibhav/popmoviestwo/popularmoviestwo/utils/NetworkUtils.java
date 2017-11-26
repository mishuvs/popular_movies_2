package com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Vaibhav on 11/24/2017.
 * A utility class for the network operations
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY = "8308357e1aad02f2ad09203c257a9a65";
    public static final String POPULAR = "popular", RATING="top_rated";

    /**
     * Builds the URL used to talk to the movie server.
     * @param typeParam can be either POPULAR or RATING
     * @return The URL to use to query the tmdb server.
     */
    public static URL buildMovieUrl(String typeParam) {
        Uri builtUri = Uri.parse(BASE_URL);
        Uri finalUri = builtUri.buildUpon()
                .appendPath(typeParam)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        Log.i(TAG,"This is the built uri: " + finalUri.toString());

        try {
            return new URL(finalUri.toString());
        } catch (MalformedURLException e) {
            Log.i(TAG,"error buildUrl");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
