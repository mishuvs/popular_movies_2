package com.mishu.vaibhav.popmoviestwo.popularmoviestwo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mishu.vaibhav.popmoviestwo.popularmoviestwo.utils.MovieDbJsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaibhav on 12/4/2017.
 */

public class DetailAdapter extends RecyclerView.Adapter{

    private ArrayList<DetailActivity.Review> movieReviews;
    private ArrayList<DetailActivity.Trailer> movieTrailers;

    private final int VIEW_TYPE_TRAILER = 0;
    private final int VIEW_TYPE_REVIEW = 1;

    private Context mContext;

    DetailAdapter(Context context) {
        super();
        movieReviews = new ArrayList<DetailActivity.Review>();
        movieTrailers = new ArrayList<DetailActivity.Trailer>();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case VIEW_TYPE_TRAILER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_view, parent, false);
                return new TrailerHolder(view);
            case VIEW_TYPE_REVIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_view, parent, false);
                return new ReviewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){

            case VIEW_TYPE_REVIEW:
                ReviewHolder reviewHolder = (ReviewHolder) holder;
                reviewHolder.author.setText(movieReviews.get(position).reviewAuthor);
                reviewHolder.reviewText.setText(movieReviews.get(position).reviewText);
                break;

            case VIEW_TYPE_TRAILER:
                ((TrailerHolder) holder).trailerKey = movieTrailers.get(position - movieReviews.size()).trailerLink;
                ((TrailerHolder) holder).trailerTitle.setText(movieTrailers.get(position - movieReviews.size()).trailerTitle);
                break;

        }

    }

    @Override
    public int getItemCount() {
        return movieReviews.size() + movieTrailers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position < movieReviews.size()) return VIEW_TYPE_REVIEW;
        else return VIEW_TYPE_TRAILER;
    }

    void swapReviews(ArrayList<DetailActivity.Review> reviews)
    {
        if(reviews!=null){
            movieReviews = reviews;
            notifyDataSetChanged();
        }
    }

    void swapTrailers(ArrayList<DetailActivity.Trailer> trailers)
    {
        if(trailers!=null){
            movieTrailers = trailers;
            notifyDataSetChanged();
        }
    }

    public class TrailerHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        String trailerKey;
        final TextView trailerTitle;

        TrailerHolder(View itemView) {
            super(itemView);
            trailerTitle = itemView.findViewById(R.id.trailer_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerKey)));
        }
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {

        final TextView author, reviewText;

        ReviewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            reviewText = itemView.findViewById(R.id.review_text);
        }
    }
}
