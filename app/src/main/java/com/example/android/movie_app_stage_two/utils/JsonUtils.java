package com.example.android.movie_app_stage_two.utils;

import com.example.android.movie_app_stage_two.model.Movie;
import com.example.android.movie_app_stage_two.model.Reviews;
import com.example.android.movie_app_stage_two.model.Trailers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    private static final String MOVIE_IMAGE = "poster_path";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_PLOT = "overview";
    private static final String MOVIE_RATING = "vote_average";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_ID = "id";

    private static final String MOVIE_RESULTS = "results";

    private static final String MOVIE_REVIEW_AUTHOR = "author";
    private static final String MOVIE_REVIEW_CONTENT = "content";

    private static final String MOVIE_TRAILER_NAME = "name";
    private static final String MOVIE_TRAILER_KEY = "key";

    public static Movie[] parseMovieJson(String jsonMovieDetails) throws JSONException{

        JSONObject jsonObject = new JSONObject(jsonMovieDetails);

        JSONArray jsonArrayResult = jsonObject.getJSONArray(MOVIE_RESULTS);

        Movie[] result = new Movie[jsonArrayResult.length()];

        for (int i = 0; i < jsonArrayResult.length(); i++) {
            Movie movie = new Movie();

            movie.setImage(jsonArrayResult.getJSONObject(i).optString(MOVIE_IMAGE));
            movie.setMovieTitle(jsonArrayResult.getJSONObject(i).optString(MOVIE_TITLE));
            movie.setMoviePLot(jsonArrayResult.getJSONObject(i).optString(MOVIE_PLOT));
            movie.setMovieRating(jsonArrayResult.getJSONObject(i).optString(MOVIE_RATING));
            movie.setReleaseDate(jsonArrayResult.getJSONObject(i).optString(MOVIE_RELEASE_DATE));
            movie.setId(jsonArrayResult.getJSONObject(i).optString(MOVIE_ID));
            result[i] = movie;
        }

        return result;

    }

    public static Reviews[] parseReviewsJson(String jsonReviewsDetails) throws JSONException{

        JSONObject jsonObject = new JSONObject(jsonReviewsDetails);

        JSONArray jsonArrayResult = jsonObject.getJSONArray(MOVIE_RESULTS);

        Reviews[] result = new Reviews[jsonArrayResult.length()];

        for (int i = 0; i < result.length; i++){
            Reviews reviews = new Reviews();

            reviews.setAuthor(jsonArrayResult.getJSONObject(i).optString(MOVIE_REVIEW_AUTHOR));
            reviews.setContent(jsonArrayResult.getJSONObject(i).optString(MOVIE_REVIEW_CONTENT));

            result[i] = reviews;
        }

        return result;

    }

    public static Trailers[] parseTrailersJson(String jsonTrailersDetails) throws JSONException{

        JSONObject jsonObject = new JSONObject(jsonTrailersDetails);

        JSONArray jsonArrayResult = jsonObject.getJSONArray(MOVIE_RESULTS);

        Trailers[] result = new Trailers[jsonArrayResult.length()];

        for (int i = 0; i < result.length; i++){
            Trailers trailers = new Trailers();

            trailers.setName(jsonArrayResult.getJSONObject(i).optString(MOVIE_TRAILER_NAME));
            trailers.setKey(jsonArrayResult.getJSONObject(i).optString(MOVIE_TRAILER_KEY));

            result[i] = trailers;
        }

        return result;
    }

}
