package com.example.android.movie_app_stage_two;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movie_app_stage_two.DatabaseFiles.DatabaseClient;
import com.example.android.movie_app_stage_two.DatabaseFiles.FavoriteEntry;
import com.example.android.movie_app_stage_two.DatabaseFiles.MovieDatabase;
import com.example.android.movie_app_stage_two.adapter.ReviewAdapter;
import com.example.android.movie_app_stage_two.adapter.TrailerAdapter;
import com.example.android.movie_app_stage_two.model.Reviews;
import com.example.android.movie_app_stage_two.model.Trailers;
import com.example.android.movie_app_stage_two.utils.JsonUtils;
import com.example.android.movie_app_stage_two.utils.UrlUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import static com.example.android.movie_app_stage_two.MainActivity.tv_error;
import static com.example.android.movie_app_stage_two.MainActivity.btn_retry;
import static com.example.android.movie_app_stage_two.MainActivity.pb_show_progress;

public class DetailActivity extends AppCompatActivity {

    private static final String URL_IMAGE_PATH = "http://image.tmdb.org/t/p/w185";

    ImageView iv_poster;
    TextView tv_title, tv_plot, tv_rating, tv_release_date, trailers_label, reviews_label;
    View ln_plot;
    RecyclerView rRecyclerview, tRecyclerView;
    private Reviews[] mReviews = null;
    private Trailers[] mTrailers = null;

    Button btn_favorite, btn_favorited;

    String poster, title, plot, rating, releaseDate, activityTitle = "Movie Details", id, queryReviews, queryTrailers, queryMovie;

    private MovieDatabase mDb;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();

        rRecyclerview = findViewById(R.id.rv_reviews);
        rRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rRecyclerview.setHasFixedSize(true);

        tRecyclerView = findViewById(R.id.rv_trailers);
        tRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tRecyclerView.setHasFixedSize(true);

        btn_favorited = findViewById(R.id.btn_favorited);
        iv_poster = findViewById(R.id.iv_movie_poster);
        tv_title = findViewById(R.id.tv_movie_title);
        tv_plot = findViewById(R.id.tv_movie_plot);
        tv_rating = findViewById(R.id.tv_movie_rating);
        tv_release_date = findViewById(R.id.tv_movie_release_date);
        reviews_label = findViewById(R.id.reviews_label);
        trailers_label = findViewById(R.id.trailers_label);
        ln_plot = findViewById(R.id.ln_plot);

        poster = getIntent().getStringExtra("MovieImage");
        title = getIntent().getStringExtra("MovieTitle");
        plot = getIntent().getStringExtra("MoviePlot");
        rating = getIntent().getStringExtra("MovieRating");
        releaseDate = getIntent().getStringExtra("ReleaseDate");
        id = getIntent().getStringExtra("MovieId");

        setTitle(activityTitle);

        Picasso.with(this)
                .load(URL_IMAGE_PATH.concat(poster))
                .placeholder(R.mipmap.ic_launcher)
                .into(iv_poster);

        if (title.equals("")){
            tv_title.setText(R.string.no_title);
        }else {
            tv_title.setText(title);
        }

        if (plot.equals("")){
            tv_plot.setText(R.string.no_synopsis);
        }else {
            tv_plot.setText(plot);
        }

        if (rating.equals("")){
            tv_rating.setText(R.string.no_rating);
        }else {
            tv_rating.setText(rating);
        }

        if (releaseDate.equals("")){
            tv_release_date.setText(R.string.no_date);
        }else {
            tv_release_date.setText(releaseDate);
        }

        btn_favorite = findViewById(R.id.btn_favorite);
        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFavorite();
            }
        });

        if ((isFavorite(id))){
            btn_favorited.setVisibility(View.VISIBLE);
            btn_favorite.setVisibility(View.INVISIBLE);
        } else {
            btn_favorited.setVisibility(View.INVISIBLE);
            btn_favorite.setVisibility(View.VISIBLE);
        }

        btn_favorited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removedFavorite();
            }
        });

        queryReviews = "reviews";
        queryTrailers = "videos";

        new MovieReviewsFetchTask().execute(id, queryReviews);
        new MovieTrailersFetchTask().execute(id, queryTrailers);

        if (!(CheckNetwork.isInternetAvailable(this))){
            hideSomeViews();
        }

    }

    private void hideSomeViews() {
        reviews_label.setVisibility(View.INVISIBLE);
        trailers_label.setVisibility(View.INVISIBLE);
        rRecyclerview.setVisibility(View.INVISIBLE);
        tRecyclerView.setVisibility(View.INVISIBLE);
        ln_plot.setVisibility(View.INVISIBLE);
    }

    //call this method when the Mark as Favorite button is clicked


    public void saveFavorite() {

        @SuppressLint("StaticFieldLeak")
        class SaveFavorite extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                FavoriteEntry favoriteEntry = new FavoriteEntry(id, title, releaseDate, rating, poster, plot);
                favoriteEntry.setId(id);
                favoriteEntry.setTitle(title);
                favoriteEntry.setReleaseDate(releaseDate);
                favoriteEntry.setUserRating(rating);
                favoriteEntry.setPoster(poster);
                favoriteEntry.setPlot(plot);

                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .favoriteDao()
                        .insertFavorite(favoriteEntry);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                finish();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(), "Movie "+title+" added to favorites", Toast.LENGTH_LONG).show();

            }
        }

        SaveFavorite saveFavorite = new SaveFavorite();
        saveFavorite.execute();

    }

    //call this method when the Marked as Favorite button is clicked
    public void removedFavorite() {

        @SuppressLint("StaticFieldLeak")
        class RemovedFavorite extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                FavoriteEntry favoriteEntry = new FavoriteEntry(id);
                favoriteEntry.setId(id);

                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .favoriteDao()
                        .deleteFavoriteById(id);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                finish();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(), "Movie "+title+" removed from favorites", Toast.LENGTH_LONG).show();

            }
        }

        RemovedFavorite removedFavorite = new RemovedFavorite();
        removedFavorite.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class MovieReviewsFetchTask extends AsyncTask<String, Void, Reviews[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            rRecyclerview.setVisibility(View.INVISIBLE);
            pb_show_progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Reviews[] doInBackground(String... strings) {

            if (UrlUtils.API_KEY.equals("")){

                MainActivity.networkError();
                tv_error.setText(R.string.missing_api_key);
                btn_retry.setVisibility(View.INVISIBLE);
                return null;

            }

            URL theReviewsUrl;
            String movieReviewResponse;

            theReviewsUrl = UrlUtils.buildReviewsUrl(strings[0].concat("/"), strings[1]);

            try {

                movieReviewResponse = UrlUtils.getResponseFromHttp(theReviewsUrl);
                mReviews = JsonUtils.parseReviewsJson(movieReviewResponse);

            } catch (Exception e){
                e.printStackTrace();
            }

            return mReviews;
        }


        @Override
        protected void onPostExecute(Reviews[] reviews) {

            new MovieReviewsFetchTask().cancel(true);

            if (reviews != null){

                mReviews = reviews;
                ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
                rRecyclerview.setAdapter(reviewAdapter);

                rRecyclerview.setVisibility(View.VISIBLE);

                MainActivity.hideViews();

            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MovieTrailersFetchTask extends AsyncTask<String, Void, Trailers[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            tRecyclerView.setVisibility(View.INVISIBLE);
            pb_show_progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Trailers[] doInBackground(String... strings) {

            if (UrlUtils.API_KEY.equals("")){

                MainActivity.networkError();
                tv_error.setText(R.string.missing_api_key);
                btn_retry.setVisibility(View.INVISIBLE);
                return null;

            }

            URL theTrailersUrl;
            String movieTrailersResponse;

            theTrailersUrl = UrlUtils.buildTrailersUrl(strings[0].concat("/"), strings[1]);

            try {

                movieTrailersResponse = UrlUtils.getResponseFromHttp(theTrailersUrl);
                mTrailers = JsonUtils.parseTrailersJson(movieTrailersResponse);

            } catch (Exception e){

                e.printStackTrace();

            }

            return mTrailers;
        }

        @Override
        protected void onPostExecute(Trailers[] trailers) {

            new MovieTrailersFetchTask().cancel(true);

            if (trailers !=null ){

                mTrailers = trailers;
                TrailerAdapter trailerAdapter = new TrailerAdapter(trailers, DetailActivity.this);
                tRecyclerView.setAdapter(trailerAdapter);

                tRecyclerView.setVisibility(View.VISIBLE);
                MainActivity.hideViews();

            }
        }
    }

    public boolean isFavorite(String _id) throws SQLException {
        int count = -1;
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM Favorites WHERE id = :_id";
            cursor = mDb.query(query, new String[] {_id});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            return count > 0;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
