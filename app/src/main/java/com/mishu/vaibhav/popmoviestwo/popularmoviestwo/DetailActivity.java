package com.mishu.vaibhav.popmoviestwo.popularmoviestwo;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.data.MovieContract;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.databinding.ActivityDetailBinding;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private int id;
    private String movieTitle, movieUrlThumbnail, movieOverview, movieReleaseDate ;
    private double movieVoteAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

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
                ContentValues values = new ContentValues();
                values.put(MovieContract.FavouritesEntry.COLUMN_TMDB_ID, id);
                values.put(MovieContract.FavouritesEntry.COLUMN_RATING, movieVoteAverage);
                values.put(MovieContract.FavouritesEntry.COLUMN_TITLE, movieTitle);
                values.put(MovieContract.FavouritesEntry.COLUMN_URL_THUMBNAIL, movieUrlThumbnail);
                values.put(MovieContract.FavouritesEntry.COLUMN_OVERVIEW, movieOverview);
                values.put(MovieContract.FavouritesEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
                getContentResolver().insert(MovieContract.FavouritesEntry.CONTENT_URI, values);
            }
        });

    }
}
