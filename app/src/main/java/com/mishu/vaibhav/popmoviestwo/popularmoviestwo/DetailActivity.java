package com.mishu.vaibhav.popmoviestwo.popularmoviestwo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.databinding.ActivityDetailBinding;
import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        Intent i = getIntent();
        String movieTitle = i.getStringExtra("title");
        String movieUrlThumbnail = i.getStringExtra("url_thumbnail");
        String movieOverview = i.getStringExtra("overview");
        double movieVoteAverage = i.getDoubleExtra("vote_average",0);
        String movieReleaseDate = i.getStringExtra("release_date");

        String url = NetworkUtils.BASE_IMAGE_URL + movieUrlThumbnail;
        Picasso.with(this)
                .load(url)
                .fit()
                .into(binding.movieThumbnail);
        binding.title.setText(movieTitle);
        binding.overview.setText(String.format("Plot Synopsis: \n%s", movieOverview));
        binding.rating.setText(String.format("Rating: %1$,.1f",movieVoteAverage));
        binding.date.setText(String.format("Release date: %s", movieReleaseDate));

    }
}
