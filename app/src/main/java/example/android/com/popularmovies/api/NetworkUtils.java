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
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import example.android.com.popularmovies.R;

import static example.android.com.popularmovies.data.Constant.TMDB_API_KEY;
import static example.android.com.popularmovies.data.Constant.TMDB_MOVIE_BASE_URL;
import static example.android.com.popularmovies.data.Constant.TMDB_PARAM_API_KEY;
import static example.android.com.popularmovies.data.Constant.TMDB_PARAM_PAGE;
import static example.android.com.popularmovies.data.Constant.TMDB_PATH_PARAM_REVIEWS;
import static example.android.com.popularmovies.data.Constant.TMDB_PATH_PARAM_VIDEOS;
import static example.android.com.popularmovies.data.Constant.TMDB_POPULAR_MOVIES_URL;
import static example.android.com.popularmovies.data.Constant.TMDB_TOP_RATED_MOVIES_URL;

/**
 * These utilities will be used to communicate with the movie servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();



    /**
     * Builds the URL used to fetch movie lists from to the movie database
     *
     * @param type The sort_order of movie list to be fetched. See {@link example.android.com.popularmovies.data.MoviesPreferences}
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(Context context, String type, int pageNumber) {

        String baseUrl;

        if(type.equals(context.getString(R.string.pref_sort_popular))) {
            baseUrl = TMDB_POPULAR_MOVIES_URL;
        } else if(type.equals(context.getString(R.string.pref_sort_top_rated))) {
            baseUrl = TMDB_TOP_RATED_MOVIES_URL;
        } else {
            Log.d(TAG, "buildUrl() called with: sort_order = [" + type + "], pageNumber = [" + pageNumber + "]");
            return null;
        }

        // check for invalid page number
        if(pageNumber <= 0) {
            Log.d(TAG, "buildUrl() called with: sort_order = [" + type + "], pageNumber = [" + pageNumber + "]");
            return null;
        }

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(TMDB_PARAM_API_KEY, TMDB_API_KEY)
                .appendQueryParameter(TMDB_PARAM_PAGE, Integer.toString(pageNumber))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildTrailersUrl(Context context, int movieId) {
        String baseUrl = TMDB_MOVIE_BASE_URL;

        // check for invalid movieId
        if(movieId <= 0) {
            Log.d(TAG, "buildTrailersUrl() called with: context = [" + context + "], movieId = [" + movieId + "]");
            return null;
        }

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(TMDB_PATH_PARAM_VIDEOS)
                .appendQueryParameter(TMDB_PARAM_API_KEY, TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Trailers URI " + url);

        return url;
    }

    public static URL buildReviewsUrl(Context context, int movieId) {

        // check for invalid movieId
        if(movieId <= 0) {
            Log.d(TAG, "buildReviewsUrl() called with: context = [" + context + "], movieId = [" + movieId + "]");
            return null;
        }

        Uri builtUri = Uri.parse(TMDB_MOVIE_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(TMDB_PATH_PARAM_REVIEWS)
                .appendQueryParameter(TMDB_PARAM_API_KEY, TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Trailers URI " + url);

        return url;
    }
}