package com.mishu.vaibhav.popmoviestwo.popularmoviestwo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.data.MovieContract;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.databinding.ActivityDetailBinding;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.MovieDbJsonUtils;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static int id;
    private String movieTitle, movieUrlThumbnail, movieOverview, movieReleaseDate ;
    private double movieVoteAverage;

    private static List<Integer> favIds;
    private static ActivityDetailBinding binding;
    private static boolean isFavourite;

    private static DetailAdapter adapter;

    private static ArrayAdapter<String> trailerAdapter, reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        Intent i = getIntent();
        id = i.getIntExtra("tmdbID",0);
        movieTitle = i.getStringExtra("title");
        movieUrlThumbnail = i.getStringExtra("url_thumbnail");
        movieOverview = i.getStringExtra("overview");
        movieVoteAverage = i.getDoubleExtra("vote_average",0);
        movieReleaseDate = i.getStringExtra("release_date");

        CallTrailerServer task = new CallTrailerServer(this);
        task.execute(id);
        CallReviewServer reviewTask = new CallReviewServer(this);
        reviewTask.execute(id);

        adapter = new DetailAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.detailRecyclerView.setLayoutManager(layoutManager);
        binding.detailRecyclerView.setAdapter(adapter);

        String url = NetworkUtils.BASE_IMAGE_URL + movieUrlThumbnail;
        Picasso.with(this)
                .load(url)
                .fit()
                .into(binding.movieThumbnail);
        binding.title.setText(movieTitle);
        binding.overview.setText(String.format("Plot Synopsis: \n%s", movieOverview));
        binding.rating.setText(String.format("Rating: %1$,.1f",movieVoteAverage));
        binding.date.setText(String.format("Release date: %s", movieReleaseDate));
        binding.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavourite = !isFavourite;
                if(isFavourite){
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.FavouritesEntry.COLUMN_TMDB_ID, id);
                    values.put(MovieContract.FavouritesEntry.COLUMN_RATING, movieVoteAverage);
                    values.put(MovieContract.FavouritesEntry.COLUMN_TITLE, movieTitle);
                    values.put(MovieContract.FavouritesEntry.COLUMN_URL_THUMBNAIL, movieUrlThumbnail);
                    values.put(MovieContract.FavouritesEntry.COLUMN_OVERVIEW, movieOverview);
                    values.put(MovieContract.FavouritesEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
                    getContentResolver().insert(MovieContract.FavouritesEntry.CONTENT_URI, values);
                    binding.favButton.setText(R.string.remove_favourite);
                }
                else{
                    getContentResolver().delete(MovieContract.FavouritesEntry.CONTENT_URI,"id="+id,null);
                    binding.favButton.setText(R.string.add_favourite);
                }
            }
        });
    }

    static class FavToArrayList extends AsyncTask<Void,Void,List<MovieDbJsonUtils.Movie>> {

        private WeakReference<DetailActivity> activityReference;

        FavToArrayList(DetailActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<MovieDbJsonUtils.Movie> doInBackground(Void... voids) {
            Cursor c = activityReference.get().getContentResolver().query(
                    MovieContract.FavouritesEntry.CONTENT_URI,
                    new String[]{MovieContract.FavouritesEntry.COLUMN_TMDB_ID},
                    null,
                    null,
                    null
            );

            if(c!=null) c.moveToFirst();
            else return null;

            for (int i=0;i<c.getCount();i++){
                favIds.add(c.getInt(0));
                c.moveToNext();
            }
            Log.i("haha",favIds.toString() + "\n" + id);
            c.close();
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDbJsonUtils.Movie> movies) {
            super.onPostExecute(movies);
            if(favIds.contains(id)){
                isFavourite = true;
                ((Button)activityReference.get().findViewById(R.id.fav_button)).setText(R.string.remove_favourite);
            }
            else{
                isFavourite = false;
                ((Button)activityReference.get().findViewById(R.id.fav_button)).setText(R.string.add_favourite);
            }
        }
    }

    static class CallTrailerServer extends AsyncTask<Integer,Void,ArrayList<Trailer>>{

        private WeakReference<DetailActivity> activityReference;

        CallTrailerServer(DetailActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected ArrayList<Trailer> doInBackground(Integer... id) {
            Log.i(LOG_TAG,"doinback");
            try {
                return MovieDbJsonUtils.jsonStringToTrailers(
                        NetworkUtils.getResponseFromHttpUrl(
                                NetworkUtils.buildMovieTrailersUrl(id[0])
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            super.onPostExecute(trailers);
            adapter.swapTrailers(trailers);
        }
    }

    static class CallReviewServer extends AsyncTask<Integer,Void,ArrayList<Review>>{

        private WeakReference<DetailActivity> activityReference;

        CallReviewServer(DetailActivity context){
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected ArrayList<Review> doInBackground(Integer... id) {
            Log.i(LOG_TAG,"doinback");
            try {
                return MovieDbJsonUtils.jsonStringToReviews(
                        NetworkUtils.getResponseFromHttpUrl(
                                NetworkUtils.buildMovieReviewsUrl(id[0])
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            super.onPostExecute(reviews);
            adapter.swapReviews(reviews);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        favIds = new ArrayList<Integer>();
        FavToArrayList task = new FavToArrayList(this);
        task.execute();
    }

    public static class Review{
        String reviewText, reviewAuthor;

        public Review(String author, String text)
        {
            reviewText = text;
            reviewAuthor = author;
        }
    }

    public static class Trailer{
        String trailerLink, trailerTitle;

        public Trailer(String link, String title)
        {
            trailerLink = link;
            trailerTitle = title;
        }
    }
}
