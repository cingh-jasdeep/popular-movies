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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import example.android.com.popularmovies.data.Movie;

//TODO #2 check implementation
/**
 * Utility functions to handle TheMovieDb JSON data.
 */
public final class TheMovieDbJsonUtils {

    /**
     * Parse the JSON and convert it into Movie objects array.
     *
     * @param context         An application context, such as a service or activity context.
     * @param movieDbJsonStr The JSON to parse into Movie object array.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static Movie[] getMovieDataFromJson(Context context, String movieDbJsonStr)
        throws JSONException {

        /* Movie information.
        *  Note: Each movie's info is an element of the "results" array */
        final String TMDB_RESULTS = "results";

        /* Properties to read from one movie's JSON object */

        /* Movie title */
        final String TMDB_TITLE = "title";

        /* Movie release date
        *  Note: return format is yyyy-mm-dd */
        final String TMDB_RELEASE_DATE = "release_date";

        /* Movie poster url
        *  Note: will return partial url, for eg. "/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg" */
        final String TMDB_POSTER_PATH = "poster_path";

        /* Movie vote average
        *  Note: will return vote average out of 10, for eg. "9.3" */
        final String TMDB_VOTE_AVG = "vote_average";

        /* Movie plot synopsis */
        final String TMDB_PLOT_SYNOPSIS = "overview";


        /* Movie array to hold each movie's information */
        Movie[] parsedMovieData = null;

        JSONObject movieDbJson = new JSONObject(movieDbJsonStr);

        /* Is there an error? */
        //TODO if there is an error in parsed json detect and throw error here
//        if (forecastJson.has(OWM_MESSAGE_CODE)) {
//            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);
//
//            switch (errorCode) {
//                case HttpURLConnection.HTTP_OK:
//                    break;
//                case HttpURLConnection.HTTP_NOT_FOUND:
//                    /* Location invalid */
//                    return null;
//                default:
//                    /* Server probably down */
//                    return null;
//            }
//        }

        JSONArray movieArray = movieDbJson.optJSONArray(TMDB_RESULTS);

        parsedMovieData = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {

            /* These are the values that will be collected */
            String movieTitle;
            Date releaseDate; //parse in date object
            String moviePosterUrl; //full url of movie poster
            String voteAverage;
            String plotSynopsis;

            /* Get the JSON object representing the day */
            JSONObject movieInfo = movieArray.optJSONObject(i);

            movieTitle = movieInfo.optString(TMDB_TITLE);

            releaseDate = MovieDateUtils.getDate(context,
                    movieInfo.optString(TMDB_RELEASE_DATE));

            moviePosterUrl = PosterHelper
                    .buildPosterUrl(movieInfo.optString(TMDB_POSTER_PATH));

            voteAverage = movieInfo.optString(TMDB_VOTE_AVG);

            plotSynopsis = movieInfo.optString(TMDB_PLOT_SYNOPSIS);

            parsedMovieData[i] = new Movie(movieTitle, releaseDate, moviePosterUrl,
                    voteAverage, plotSynopsis);
        }

        return parsedMovieData;
    }
}