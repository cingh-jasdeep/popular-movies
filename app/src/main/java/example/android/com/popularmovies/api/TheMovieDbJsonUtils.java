/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * File copied from Sunshine project "S04.03-Solution-AddMapAndSharing"
 * and modified according to needs of this project
 */

package example.android.com.popularmovies.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.model.MovieEntry;
import example.android.com.popularmovies.model.MovieReviewEntry;
import example.android.com.popularmovies.model.MovieTrailerEntry;
import example.android.com.popularmovies.utilities.MovieUtils;
import example.android.com.popularmovies.utilities.PosterHelper;

import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_ID;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_PLOT_SYNOPSIS;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_POPULARITY_RATING;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_POSTER_PATH;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_RELEASE_DATE;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_RESULTS;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_TITLE;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_VOTE_AVG;
import static example.android.com.popularmovies.data.Constant.TMDB_REVIEW_JSON_AUTHOR;
import static example.android.com.popularmovies.data.Constant.TMDB_REVIEW_JSON_CONTENT;
import static example.android.com.popularmovies.data.Constant.TMDB_REVIEW_JSON_URL;
import static example.android.com.popularmovies.data.Constant.TMDB_STATUS_MESSAGE;
import static example.android.com.popularmovies.data.Constant.TMDB_TRAILER_JSON_KEY;
import static example.android.com.popularmovies.data.Constant.TMDB_TRAILER_JSON_SITE;
import static example.android.com.popularmovies.data.Constant.TMDB_TRAILER_JSON_SITE_VALUE_YOUTUBE;
import static example.android.com.popularmovies.data.Constant.TMDB_TRAILER_JSON_TYPE;
import static example.android.com.popularmovies.data.Constant.TMDB_TRAILER_JSON_TYPE_VALUE_TRAILER;

/**
 * Utility functions to handle TheMovieDb JSON data.
 */
public final class TheMovieDbJsonUtils {

    private static final String TAG = TheMovieDbJsonUtils.class.getSimpleName();
    /**
     * Parse the JSON and convert it into MovieEntry objects array.
     *
     * @param context         An application context, such as a service or activity context.
     * @param movieDbJsonStr The JSON to parse into MovieEntry object array.
     *
     * @return An array of MovieEntry objects parsed from the JSON.
     */
    public static MovieEntry[] getMovieDataFromJson(Context context, String movieDbJsonStr,
                                                    String queryType)
        throws JSONException {


        /* MovieEntry array to hold each movie's information */
        MovieEntry[] parsedMovieData;

        JSONObject movieDbJson = new JSONObject(movieDbJsonStr);

        /* Is there an error? */
        if (movieDbJson.has(TMDB_STATUS_MESSAGE)) {
            String responseErrorMessage = movieDbJson.optString(TMDB_STATUS_MESSAGE).trim();
            Log.i(TAG, "getMovieDataFromJson: "+responseErrorMessage);
            return null;
        }

        JSONArray movieArray = movieDbJson.optJSONArray(TMDB_JSON_RESULTS);

        parsedMovieData = new MovieEntry[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {

            /* These are the values that will be collected */
            int movieId;
            String movieTitle;
            Date releaseDate; //parse in date object
            String moviePosterUrl; //full url of movie poster
            double voteAverage;
            String plotSynopsis;
            double popularityRating;

            /* Get the JSON object representing the day */
            JSONObject movieInfo = movieArray.optJSONObject(i);

            movieId = movieInfo.optInt(TMDB_JSON_ID);

            movieTitle = movieInfo.optString(TMDB_JSON_TITLE).trim();

            releaseDate = MovieUtils.getDate(context,
                    movieInfo.optString(TMDB_JSON_RELEASE_DATE).trim());

            moviePosterUrl = PosterHelper
                    .buildPosterUrl(movieInfo.optString(TMDB_JSON_POSTER_PATH).trim());

            voteAverage = movieInfo.optDouble(TMDB_JSON_VOTE_AVG);

            plotSynopsis = movieInfo.optString(TMDB_JSON_PLOT_SYNOPSIS).trim();

            popularityRating = movieInfo.optDouble(TMDB_JSON_POPULARITY_RATING);

            parsedMovieData[i] = new MovieEntry(movieId, movieTitle, releaseDate, moviePosterUrl,
                    voteAverage, plotSynopsis, popularityRating);


            //add query attributes to the movieEntry object

            String popularQuery = context.getString(R.string.pref_sort_popular);
            String topRatedQuery = context.getString(R.string.pref_sort_top_rated);

            //update sort member variables in each movieEntry object
            if(queryType.equals(popularQuery)) {
                parsedMovieData[i].setIsPopular(MOVIE_ATTR_FLAG_TRUE);
            } else if(queryType.equals(topRatedQuery)) {
                parsedMovieData[i].setIsTopRated(MOVIE_ATTR_FLAG_TRUE);
            }

        }

        return parsedMovieData;
    }

    public static List<MovieTrailerEntry> getMovieTrailersDataFromJson(Context context, String movieDbTrailerJsonStr)
            throws JSONException {

        /* MovieEntry array to hold each movie's information */
        List<MovieTrailerEntry> parsedTrailerData;

        JSONObject movieTrailerJson = new JSONObject(movieDbTrailerJsonStr);

        /* Is there an error? */
        if (movieTrailerJson.has(TMDB_STATUS_MESSAGE)) {
            String responseErrorMessage = movieTrailerJson.optString(TMDB_STATUS_MESSAGE).trim();
            Log.i(TAG, "getMovieTrailersDataFromJson: "+responseErrorMessage);
            return null;
        }

        int movieId = movieTrailerJson.optInt(TMDB_JSON_ID);

        JSONArray trailersArray = movieTrailerJson.optJSONArray(TMDB_JSON_RESULTS);

        parsedTrailerData = new ArrayList<>();

        for (int i = 0; i < trailersArray.length(); i++) {

            /* These are the values that will be collected */
            String trailerId;
            String ytUrlKey;
            String site;
            String type;

            /* Get the JSON object representing the day */
            JSONObject videoInfo = trailersArray.optJSONObject(i);

            trailerId = videoInfo.optString(TMDB_JSON_ID);

            //only add to list if site is youtube and type is trailer
            type = videoInfo.optString(TMDB_TRAILER_JSON_TYPE);
            site = videoInfo.optString(TMDB_TRAILER_JSON_SITE);

            if(TMDB_TRAILER_JSON_SITE_VALUE_YOUTUBE.equals(site)
                    && TMDB_TRAILER_JSON_TYPE_VALUE_TRAILER.equals(type)) {

                ytUrlKey = videoInfo.optString(TMDB_TRAILER_JSON_KEY);
                parsedTrailerData.add(new MovieTrailerEntry(movieId, trailerId, ytUrlKey));
            }

        }
        return parsedTrailerData;
    }

    public static List<MovieReviewEntry> getMovieReviewsDataFromJson(Context context, String movieDbReviewJsonStr)
            throws JSONException {

        /* MovieEntry array to hold each movie's information */
        List<MovieReviewEntry> parsedReviewData;

        JSONObject movieReviewJson = new JSONObject(movieDbReviewJsonStr);

        /* Is there an error? */
        if (movieReviewJson.has(TMDB_STATUS_MESSAGE)) {
            String responseErrorMessage = movieReviewJson.optString(TMDB_STATUS_MESSAGE).trim();
            Log.i(TAG, "getMovieTrailersDataFromJson: "+responseErrorMessage);
            return null;
        }

        int movieId = movieReviewJson.optInt(TMDB_JSON_ID);

        JSONArray reviewsArray = movieReviewJson.optJSONArray(TMDB_JSON_RESULTS);

        parsedReviewData = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {

            /* These are the values that will be collected */
            int reviewId;
            String author;
            String content;
            String url;

            /* Get the JSON object representing the day */
            JSONObject reviewInfo = reviewsArray.optJSONObject(i);

            reviewId = reviewInfo.optInt(TMDB_JSON_ID);
            author = reviewInfo.optString(TMDB_REVIEW_JSON_AUTHOR);
            content = reviewInfo.optString(TMDB_REVIEW_JSON_CONTENT);
            url = reviewInfo.optString(TMDB_REVIEW_JSON_URL);

            parsedReviewData.add(new MovieReviewEntry(reviewId, movieId, author, content, url));
        }
        return parsedReviewData;
    }
}