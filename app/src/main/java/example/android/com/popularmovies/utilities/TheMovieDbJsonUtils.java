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

package example.android.com.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import example.android.com.popularmovies.data.Movie;

import static example.android.com.popularmovies.data.Constant.TMDB_JSON_ID;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_PLOT_SYNOPSIS;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_POSTER_PATH;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_RELEASE_DATE;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_RESULTS;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_TITLE;
import static example.android.com.popularmovies.data.Constant.TMDB_JSON_VOTE_AVG;
import static example.android.com.popularmovies.data.Constant.TMDB_STATUS_MESSAGE;

/**
 * Utility functions to handle TheMovieDb JSON data.
 */
public final class TheMovieDbJsonUtils {

    private static final String TAG = TheMovieDbJsonUtils.class.getSimpleName();
    /**
     * Parse the JSON and convert it into Movie objects array.
     *
     * @param context         An application context, such as a service or activity context.
     * @param movieDbJsonStr The JSON to parse into Movie object array.
     *
     * @return An array of Movie objects parsed from the JSON.
     */
    public static Movie[] getMovieDataFromJson(Context context, String movieDbJsonStr)
        throws JSONException {


        /* Movie array to hold each movie's information */
        Movie[] parsedMovieData;

        JSONObject movieDbJson = new JSONObject(movieDbJsonStr);

        /* Is there an error? */
        if (movieDbJson.has(TMDB_STATUS_MESSAGE)) {
            String responseErrorMessage = movieDbJson.optString(TMDB_STATUS_MESSAGE);
            Log.i(TAG, "getMovieDataFromJson: "+responseErrorMessage);
            return null;
        }

        JSONArray movieArray = movieDbJson.optJSONArray(TMDB_JSON_RESULTS);

        parsedMovieData = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {

            /* These are the values that will be collected */
            int movieId;
            String movieTitle;
            Date releaseDate; //parse in date object
            String moviePosterUrl; //full url of movie poster
            String voteAverage;
            String plotSynopsis;

            /* Get the JSON object representing the day */
            JSONObject movieInfo = movieArray.optJSONObject(i);

            movieId = movieInfo.optInt(TMDB_JSON_ID);

            movieTitle = movieInfo.optString(TMDB_JSON_TITLE);

            releaseDate = MovieUtils.getDate(context,
                    movieInfo.optString(TMDB_JSON_RELEASE_DATE));

            moviePosterUrl = PosterHelper
                    .buildPosterUrl(movieInfo.optString(TMDB_JSON_POSTER_PATH));

            voteAverage = movieInfo.optString(TMDB_JSON_VOTE_AVG);

            plotSynopsis = movieInfo.optString(TMDB_JSON_PLOT_SYNOPSIS);

            parsedMovieData[i] = new Movie(movieId, movieTitle, releaseDate, moviePosterUrl,
                    voteAverage, plotSynopsis);
        }

        return parsedMovieData;
    }
}