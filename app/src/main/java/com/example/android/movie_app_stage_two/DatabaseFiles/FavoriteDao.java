package com.example.android.movie_app_stage_two.DatabaseFiles;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM Favorites ORDER BY id ASC")
    List<FavoriteEntry> getAllMovies();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFavorite(FavoriteEntry favoriteEntry);

    @Query("SELECT * FROM Favorites WHERE id = :id")
    FavoriteEntry getMovieById(String id);

    @Query("DELETE FROM Favorites WHERE id = :id")
    void deleteFavoriteById(String id);

}
