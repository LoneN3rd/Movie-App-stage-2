package com.example.android.movie_app_stage_two.DatabaseFiles;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "Favorites") //define the table name here)
public class FavoriteEntry implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = false) //define id as the primary key
    private String id;

    @ColumnInfo(name = "movie_title")
    private String title;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @ColumnInfo(name = "user_rating")
    private String userRating;

    @ColumnInfo(name = "movie_poster")
    private String poster;

    @ColumnInfo(name = "movie_plot")
    private String plot;

    @Ignore
    public FavoriteEntry(@NonNull String id){
        this.id = id;
    }

    public FavoriteEntry(@NonNull String id, String title, String releaseDate, String userRating, String poster, String plot){
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.poster = poster;
        this.plot = plot;
    }

    ////ID getter and setter
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    ////Title getter and setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    ////Release Date getter and setter
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    ////User Rating getter and setter
    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    ////Poster getter and setter
    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    ////Plot getter and setter
    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }
}
