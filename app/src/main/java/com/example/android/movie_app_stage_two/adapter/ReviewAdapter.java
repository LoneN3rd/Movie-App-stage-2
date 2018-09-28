package com.example.android.movie_app_stage_two.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movie_app_stage_two.R;
import com.example.android.movie_app_stage_two.model.Reviews;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private TextView tv_author, tv_content, tv_no_review;
    private Reviews[] rReviews;

    public ReviewAdapter(Reviews[] reviews){
        this.rReviews = reviews;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_reviews_items, parent, false);

        return new ReviewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewHolder holder, int position) {

        String author = rReviews[position].getAuthor();
        String review = rReviews[position].getContent();

        if (!(rReviews.length == 0)){

            tv_content.setText(review);
            tv_author.setText(author);

        } else {

            tv_no_review.setText(R.string.no_review);
        }
    }

    @Override
    public int getItemCount() {
        return rReviews.length;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        ReviewHolder(View itemView) {
            super(itemView);

            tv_author = itemView.findViewById(R.id.tv_review_author);
            tv_content = itemView.findViewById(R.id.tv_review);
            tv_no_review = itemView.findViewById(R.id.tv_no_review);

        }
    }
}
