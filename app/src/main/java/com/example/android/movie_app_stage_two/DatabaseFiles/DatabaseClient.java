package com.example.android.movie_app_stage_two.DatabaseFiles;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseClient {

    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static DatabaseClient mInstance;
    private static final String DATABASE_NAME = "FavoriteMovies";

    private MovieDatabase movieDatabase;

    private DatabaseClient(Context context) {
        this.context = context;

        movieDatabase = Room.databaseBuilder(context, MovieDatabase.class, DATABASE_NAME).build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public MovieDatabase getAppDatabase() {
        return movieDatabase;
    }

}
