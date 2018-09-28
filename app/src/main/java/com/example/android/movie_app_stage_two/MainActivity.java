package com.example.android.movie_app_stage_two;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movie_app_stage_two.DatabaseFiles.DatabaseClient;
import com.example.android.movie_app_stage_two.DatabaseFiles.FavoriteEntry;
import com.example.android.movie_app_stage_two.adapter.FavoriteMoviesAdapter;
import com.example.android.movie_app_stage_two.adapter.MovieAdapter;
import com.example.android.movie_app_stage_two.model.Movie;
import com.example.android.movie_app_stage_two.utils.JsonUtils;
import com.example.android.movie_app_stage_two.utils.UrlUtils;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener, FavoriteMoviesAdapter.MovieClickListener {

    @SuppressLint("StaticFieldLeak")
    public static TextView tv_error;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar pb_show_progress;

    @SuppressLint("StaticFieldLeak")
    public static Button btn_retry;

    private RecyclerView mRecyclerView;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String SAVED_TITLE= "savedTitle";
    private static final String SAVED_QUERY = "savedQuery";

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    MenuItem menuItem;

    private String queryMovie = "popular", appTitle = "Popular Movies", isFavorite;
    private Movie[] mMovie = null;

    private List<FavoriteEntry> movieList = null;

    int favSelected;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_error = findViewById(R.id.tv_error);
        pb_show_progress = findViewById(R.id.pb_show_progress);
        btn_retry = findViewById(R.id.btn_retry);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });

        mRecyclerView = findViewById(R.id.rv_show_movies_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        setTitle(appTitle);

        if (!(CheckNetwork.isInternetAvailable(this))){
            networkError();
            return;
        }

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(SAVED_TITLE) || savedInstanceState.containsKey(SAVED_QUERY)){

                selected = savedInstanceState.getInt(MENU_SELECTED);
                queryMovie = savedInstanceState.getString(SAVED_QUERY);
                appTitle = savedInstanceState.getString(SAVED_TITLE);
                setTitle(appTitle);

                if (Objects.equals(queryMovie, "favorites")){
                    new LoadFavoriteMovies().execute(queryMovie);
                    return;
                }else {
                    new MovieFetchTask().execute(queryMovie);
                    return;
                }
            }
        }

        new MovieFetchTask().execute(queryMovie);

    }

    public static void networkError(){
        pb_show_progress.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.VISIBLE);
        btn_retry.setVisibility(View.VISIBLE);
    }

    public static void hideViews(){
        tv_error.setVisibility(View.INVISIBLE);
        pb_show_progress.setVisibility(View.INVISIBLE);
        btn_retry.setVisibility(View.INVISIBLE);
    }

    private void retry(){

        if (!(CheckNetwork.isInternetAvailable(this))){
            networkError();
            return;
        }

        hideViews();

        new MovieFetchTask().execute(queryMovie);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClickMovie(int position) {

        //if (!(CheckNetwork.isInternetAvailable(this))){
        if ((!(CheckNetwork.isInternetAvailable(this))) & (!check(selected, R.id.favorites))){
            mRecyclerView.setVisibility(View.INVISIBLE);
            networkError();
            return;
        }

        Intent intent = new Intent(this, DetailActivity.class);

        if (check(selected, R.id.favorites)){

            FavoriteEntry movieData = movieList.get(position);

            intent.putExtra("MovieTitle", movieData.getTitle());
            intent.putExtra("MoviePlot", movieData.getPlot());
            intent.putExtra("MovieRating", movieData.getUserRating());
            intent.putExtra("ReleaseDate", movieData.getReleaseDate());
            intent.putExtra("MovieImage", movieData.getPoster());
            intent.putExtra("MovieId", movieData.getId());

        } else {

            intent.putExtra("MovieTitle", mMovie[position].getMovieTitle());
            intent.putExtra("MoviePlot", mMovie[position].getMoviePLot());
            intent.putExtra("MovieRating", mMovie[position].getMovieRating());
            intent.putExtra("ReleaseDate", mMovie[position].getReleaseDate());
            intent.putExtra("MovieImage", mMovie[position].getImage());
            intent.putExtra("MovieId", mMovie[position].getId());

        }

        startActivity(intent);

    }


    @SuppressLint("StaticFieldLeak")
    private class MovieFetchTask extends AsyncTask<String, Void, Movie[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRecyclerView.setVisibility(View.INVISIBLE);
            pb_show_progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Movie[] doInBackground(String... strings) {

            if ((!(CheckNetwork.isInternetAvailable(MainActivity.this)))){
                networkError();
                return null;
            }

            if (UrlUtils.API_KEY.equals("")){
                networkError();
                tv_error.setText(R.string.missing_api_key);
                btn_retry.setVisibility(View.INVISIBLE);
                return null;
            }

            String movieQueryResponse;
            URL theMovieUrl = UrlUtils.buildUrl(strings[0]);

            try {

                movieQueryResponse = UrlUtils.getResponseFromHttp(theMovieUrl);
                mMovie = JsonUtils.parseMovieJson(movieQueryResponse);

            } catch (Exception e){
                e.printStackTrace();
            }

            return mMovie;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            new MovieFetchTask().cancel(true);

            if (movies !=null){

                mMovie = movies;
                MovieAdapter adapter = new MovieAdapter(movies, MainActivity.this, MainActivity.this);
                mRecyclerView.setAdapter(adapter);

                mRecyclerView.setVisibility(View.VISIBLE);
                hideViews();

            } else {
                Log.e(LOG_TAG, "Problems with the adapter");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        if (selected == -1){
            return true;
        }

        switch (selected){

            case R.id.popularity:
                menuItem = menu.findItem(R.id.popularity);
                menuItem.setChecked(true);
                break;

            case R.id.top_rated:
                menuItem = menu.findItem(R.id.top_rated);
                menuItem.setChecked(true);
                break;

            case R.id.favorites:
                menuItem = menu.findItem(R.id.favorites);
                menuItem.setChecked(true);
                break;
        }

        return true;
    }

    public static boolean check (int id, int theId) {
        return id == theId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        int theId = R.id.favorites;

        if (UrlUtils.API_KEY.equals("")) return false;

        if ((!(CheckNetwork.isInternetAvailable(this))) & (!check(id, theId))){
            return false;
        }

        switch (id){
            case R.id.popularity:

                selected = id;
                item.setChecked(true);

                queryMovie = "popular";
                new MovieFetchTask().execute(queryMovie);

                appTitle = "Popular Movies";
                setTitle(appTitle);

                break;

            case R.id.top_rated:

                selected = id;
                item.setChecked(true);

                queryMovie = "top_rated";
                new MovieFetchTask().execute(queryMovie);

                appTitle = "Top Rated Movies";
                setTitle(appTitle);

                break;

            case R.id.favorites:

                selected = id;
                item.setChecked(true);

                queryMovie = "favorites";
                new LoadFavoriteMovies().execute(queryMovie);

                appTitle = "Favorite Movies";
                setTitle(appTitle);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadFavoriteMovies extends AsyncTask<String, Void, List<FavoriteEntry>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRecyclerView.setVisibility(View.INVISIBLE);
            pb_show_progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<FavoriteEntry> doInBackground(String... strings) {

            return DatabaseClient
                    .getInstance(getApplicationContext())
                    .getAppDatabase()
                    .favoriteDao()
                    .getAllMovies();

        }

        @Override
        protected void onPostExecute(List<FavoriteEntry> favoriteEntries) {

            new LoadFavoriteMovies().cancel(true);

            movieList = favoriteEntries;

            FavoriteMoviesAdapter adapter = new FavoriteMoviesAdapter(favoriteEntries,MainActivity.this, MainActivity.this);
            mRecyclerView.setAdapter(adapter);

            mRecyclerView.setVisibility(View.VISIBLE);
            hideViews();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        String currQueryMovie = queryMovie;
        String currAppTitle = appTitle;

        savedInstanceState.putString(SAVED_QUERY, currQueryMovie);
        savedInstanceState.putString(SAVED_TITLE, currAppTitle);

        savedInstanceState.putInt(MENU_SELECTED, selected);

    }
}