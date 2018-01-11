package com.mishu.vaibhav.popmoviestwo.popularmoviestwo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.data.MovieContract;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.MovieDbJsonUtils;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.NetworkUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.nio.channels.AsynchronousChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{

    static MovieList<MovieDbJsonUtils.Movie> movies;
    private MovieAdapter adapter;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int FROM_SERVER_LOADER_ID = 1, FAVOURITE_LOADER_ID=2;
    private SharedPreferences sharedPref;
    static ArrayList<Integer> favIds;
    private String listPreference;
    private MovieList<MovieDbJsonUtils.Movie> fetchedMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        listPreference = sharedPref.getString(getString(R.string.list_preference),NetworkUtils.POPULAR);

        adapter = new MovieAdapter(this,null);
        GridLayoutManager movieLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        RecyclerView movieRecyclerView = findViewById(R.id.movie_recycler_view);
        movieRecyclerView.setAdapter(adapter);
        movieRecyclerView.setLayoutManager(movieLayoutManager);

        if(fetchedMovies==null){
            if(isOnline()){
                if(Objects.equals(listPreference, NetworkUtils.FAVOURITE)){
                    GetFavourites task = new GetFavourites(this);
                    try {
                        fetchedMovies = task.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    CallMovieServer task = new CallMovieServer(this);
                    try {
                        fetchedMovies = task.execute(listPreference).get();//by popular or by ratings
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                Toast.makeText(this,"No connection to internet",Toast.LENGTH_LONG).show();
            }
        }
        adapter.swap(fetchedMovies);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //clearing list:
        adapter.swap(new ArrayList<MovieDbJsonUtils.Movie>());

        CallMovieServer task = new CallMovieServer(this);
        switch (item.getItemId()){
            case R.id.sort_popular:
                listPreference = NetworkUtils.POPULAR;
//              sharedPref.edit().putString(getString(R.string.list_preference),NetworkUtils.POPULAR).apply();
                task.execute(NetworkUtils.POPULAR);
                break;

            case R.id.sort_rating:
                listPreference = NetworkUtils.RATING;
//                sharedPref.edit().putString(getString(R.string.list_preference),NetworkUtils.RATING).apply();
                task.execute(NetworkUtils.RATING);
                break;

            case R.id.sort_favourites:
                //favourites selected
                listPreference = NetworkUtils.FAVOURITE;
//                sharedPref.edit().putString(getString(R.string.list_preference),NetworkUtils.FAVOURITE).apply();
                GetFavourites favTask = new GetFavourites(this);
                favTask.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    static class CallMovieServer extends AsyncTask<String,Void,MovieList<MovieDbJsonUtils.Movie>>{

        private WeakReference<MainActivity> activityReference;

        CallMovieServer(MainActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected MovieList<MovieDbJsonUtils.Movie> doInBackground(String... type) {
            Log.i(LOG_TAG,"doinback");
            try {
                movies = MovieDbJsonUtils.jsonStringToMovies(
                        NetworkUtils.getResponseFromHttpUrl(
                                NetworkUtils.buildMovieUrl(type[0])
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(MovieList<MovieDbJsonUtils.Movie> movies) {
            super.onPostExecute(movies);

            Log.i(LOG_TAG,"onpostexecute");

            MainActivity activity = activityReference.get();
            if (activity == null) return;

            Log.i(LOG_TAG,"activity not null... swapping list");
            activity.adapter.swap(movies);
        }
    }

    static class GetFavourites extends AsyncTask<Void,Void,MovieList<MovieDbJsonUtils.Movie>>{

        private WeakReference<MainActivity> activityReference;

        GetFavourites(MainActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected MovieList<MovieDbJsonUtils.Movie> doInBackground(Void... url) {
            Cursor cursor = activityReference.get().getContentResolver().query(MovieContract.FavouritesEntry.CONTENT_URI,null,null,null,null);
            MovieList<MovieDbJsonUtils.Movie> list = new MovieList<MovieDbJsonUtils.Movie>();
            if(cursor!=null && cursor.getCount()>0){
                cursor.moveToFirst();
                MovieDbJsonUtils.Movie movie;
                for (int i=0;i<cursor.getCount();i++){
                    movie = new MovieDbJsonUtils.Movie(
                            cursor.getInt(cursor.getColumnIndex(MovieContract.FavouritesEntry.COLUMN_TMDB_ID)),
                            cursor.getString(cursor.getColumnIndex(MovieContract.FavouritesEntry.COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndex(MovieContract.FavouritesEntry.COLUMN_URL_THUMBNAIL)),
                            cursor.getString(cursor.getColumnIndex(MovieContract.FavouritesEntry.COLUMN_OVERVIEW)),
                            cursor.getDouble(cursor.getColumnIndex(MovieContract.FavouritesEntry.COLUMN_RATING)),
                            cursor.getString(cursor.getColumnIndex(MovieContract.FavouritesEntry.COLUMN_RELEASE_DATE))
                    );
                    list.add(movie);
                    cursor.moveToNext();
                }
            }
            if (cursor != null) {
                Log.i(LOG_TAG, DatabaseUtils.dumpCursorToString(cursor));
                cursor.close();
            }
            return list;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        sharedPref.edit().putString(getString(R.string.list_preference),listPreference).apply();
        outState.putSerializable("movies",fetchedMovies);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fetchedMovies = (MovieList<MovieDbJsonUtils.Movie>) savedInstanceState.getSerializable("movies");

    }

    public static class MovieList<E> extends ArrayList<MovieDbJsonUtils.Movie> implements Serializable{}
}
