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
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import example.android.com.popularmovies.R;

/**
 * These utilities will be used to communicate with the movie servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular";

    private static final String TOP_RATED_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/top_rated";

    /*
     * API KEY
     * Please insert your themoviedb.org API key here
     */
    private static final String API_KEY =
            "75a991b5d58647d7767c5139219e2e57";

    /*
     * API parameter keys
     * themoviedb.org API parameter keys
     */
    final static private String API_KEY_PARAM = "api_key";
    final static private String PAGE_PARAM = "page";

    /*
     * some constants for themoviedb.org API
     */
    private static final int DEFAULT_PAGE = 1;


    /**
     * Builds the URL used to fetch movie lists from to the movie database
     *
     * @param type The sort_order of movie list to be fetched. See {@link example.android.com.popularmovies.data.MoviesPreferences}
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(Context context, String type, int pageNumber) {

        String baseUrl;

        if(type.equals(context.getString(R.string.pref_sort_popular))) {
            baseUrl = POPULAR_MOVIES_URL;
        } else if(type.equals(context.getString(R.string.pref_sort_top_rated))) {
            baseUrl = TOP_RATED_MOVIES_URL;
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
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(pageNumber))
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
}