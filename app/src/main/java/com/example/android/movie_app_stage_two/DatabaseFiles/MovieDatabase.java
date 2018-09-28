package com.example.android.movie_app_stage_two.DatabaseFiles;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.example.android.movie_app_stage_two.Converter.DateConverter;

@Database(entities = {FavoriteEntry.class}, version = 1, exportSchema = false)

public abstract class MovieDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();

}
