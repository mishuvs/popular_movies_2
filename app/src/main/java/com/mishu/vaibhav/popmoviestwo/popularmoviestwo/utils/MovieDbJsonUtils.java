package com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils;

import android.util.Log;

import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.DetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vaibhav on 11/24/2017.
 */

public class MovieDbJsonUtils {

    private static final String LOG_TAG = MovieDbJsonUtils.class.getSimpleName();

    public static ArrayList<Movie> jsonStringToMovies(String jsonResponse){
        JSONObject rootJson, movieJsonObj;
        JSONArray jsonArray;
        String movieTitle;
        String movieUrlThumbnail;
        String movieOverview;
        double movieVoteAverage;
        String movieReleaseDate;
        int tmdbId;
        ArrayList<Movie> movies = new ArrayList<Movie>();
        try{
            rootJson = new JSONObject(jsonResponse);
            jsonArray = rootJson.getJSONArray("results");
            for(int i=0; i < jsonArray.length(); i++) {
                movieJsonObj = jsonArray.getJSONObject(i);
                movieOverview = movieJsonObj.getString("overview");
                movieVoteAverage = movieJsonObj.getDouble("vote_average");
                movieTitle = movieJsonObj.getString("title");
                movieUrlThumbnail = movieJsonObj.getString("poster_path");
                movieReleaseDate = movieJsonObj.getString("release_date");
                tmdbId = movieJsonObj.getInt("id");
                movies.add(i, new Movie(tmdbId, movieTitle,movieUrlThumbnail,movieOverview,movieVoteAverage,movieReleaseDate));
            }
        }
        catch (JSONException e){
            Log.e(LOG_TAG, "Problem converting to JSON array: " + e);
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "The json object is: " + movies.toString());
        return movies;
    }

    public static ArrayList<DetailActivity.Trailer> jsonStringToTrailers(String jsonResponse){
        JSONObject rootJson;
        JSONArray jsonArray;
        ArrayList<DetailActivity.Trailer> trailers = new ArrayList<DetailActivity.Trailer>();
        try{
            rootJson = new JSONObject(jsonResponse);
            jsonArray = rootJson.getJSONArray("results");
            for(int i=0; i < jsonArray.length(); i++) {
                trailers.add(new DetailActivity.Trailer(jsonArray.getJSONObject(i).getString("key"),jsonArray.getJSONObject(i).getString("name")));
            }
            return trailers;
        }
        catch (JSONException e){
            Log.e(LOG_TAG, "Problem converting to JSON array: " + e);
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<DetailActivity.Review> jsonStringToReviews(String jsonResponse){
        JSONObject rootJson;
        JSONArray jsonArray;
        ArrayList<DetailActivity.Review> reviews = new ArrayList<DetailActivity.Review>();
        try{
            rootJson = new JSONObject(jsonResponse);
            jsonArray = rootJson.getJSONArray("results");
            for(int i=0; i < jsonArray.length(); i++) {
                reviews.add(new DetailActivity.Review(jsonArray.getJSONObject(i).getString("author"), jsonArray.getJSONObject(i).getString("content")));
            }
            return reviews;
        }
        catch (JSONException e){
            Log.e(LOG_TAG, "Problem converting to JSON array: " + e);
            e.printStackTrace();
        }
        return null;
    }

    public static class Movie{
        public String movieTitle;
        public String movieUrlThumbnail;
        public String movieOverview;
        public double movieVoteAverage;
        public String movieReleaseDate;
        public int tmdbId;

        public Movie(int id, String title, String urlThumbnail, String overview, double voteAverage, String releaseDate)
        {
            movieTitle = title;
            movieUrlThumbnail = urlThumbnail;
            movieOverview = overview;
            movieVoteAverage = voteAverage;
            movieReleaseDate = releaseDate;
            tmdbId = id;
        }
    }

}
