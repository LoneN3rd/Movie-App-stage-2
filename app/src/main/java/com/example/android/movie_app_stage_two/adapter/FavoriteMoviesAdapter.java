package com.example.android.movie_app_stage_two.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movie_app_stage_two.DatabaseFiles.FavoriteEntry;
import com.example.android.movie_app_stage_two.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.FavoriteMovieHolder> {

    private static final String IMAGE_URL_PATH = "http://image.tmdb.org/t/p/w185";
    private final Context mContext;
    private List<FavoriteEntry> mMovie = null;
    private final FavoriteMoviesAdapter.MovieClickListener mMovieClickListener;


    public FavoriteMoviesAdapter(List<FavoriteEntry> favoriteEntryList, Context context, FavoriteMoviesAdapter.MovieClickListener movieClickListener) {
        mMovie = favoriteEntryList;
        mContext = context;
        mMovieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public FavoriteMoviesAdapter.FavoriteMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_movie_items, parent, false);

        return new FavoriteMovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMovieHolder holder, int position) {

        FavoriteEntry favoriteEntry = mMovie.get(position);

        Picasso.with(mContext)
                .load(IMAGE_URL_PATH.concat(favoriteEntry.getPoster()))
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .into(holder.imageViewHolder);

    }


    @Override
    public int getItemCount() {
        return mMovie.size();
    }

    class FavoriteMovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageViewHolder;

        FavoriteMovieHolder(View itemView) {
            super(itemView);

            imageViewHolder = itemView.findViewById(R.id.iv_movie_item_poster);
            imageViewHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickPosition = getAdapterPosition();
            mMovieClickListener.onClickMovie(clickPosition);
        }
    }

    public interface MovieClickListener {
        void onClickMovie(int position);
    }
}
