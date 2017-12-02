package com.mishu.vaibhav.popmoviestwo.popularmoviestwo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.data.MovieContract;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.databinding.ActivityDetailBinding;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.MovieDbJsonUtils;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static int id;
    private String movieTitle, movieUrlThumbnail, movieOverview, movieReleaseDate ;
    private double movieVoteAverage;

    private static List<Integer> favIds;
    private ActivityDetailBinding binding;
    private static boolean isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDetailBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        Intent i = getIntent();
        id = i.getIntExtra("tmdbID",0);
        movieTitle = i.getStringExtra("title");
        movieUrlThumbnail = i.getStringExtra("url_thumbnail");
        movieOverview = i.getStringExtra("overview");
        movieVoteAverage = i.getDoubleExtra("vote_average",0);
        movieReleaseDate = i.getStringExtra("release_date");

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
                    binding.favButton.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
                else{
                    getContentResolver().delete(MovieContract.FavouritesEntry.CONTENT_URI,"id="+id,null);
                    binding.favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
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
                ((ImageButton)activityReference.get().findViewById(R.id.fav_button)).setImageResource(R.drawable.ic_favorite_white_24dp);
            }
            else{
                isFavourite = false;
                ((ImageButton)activityReference.get().findViewById(R.id.fav_button)).setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        favIds = new ArrayList<Integer>();
        FavToArrayList task = new FavToArrayList(this);
        task.execute();
    }
}
