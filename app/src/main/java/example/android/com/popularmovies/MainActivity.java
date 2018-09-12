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
package example.android.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import example.android.com.popularmovies.data.Movie;
import example.android.com.popularmovies.data.MovieQuery;
import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.utilities.NetworkUtils;
import example.android.com.popularmovies.utilities.TheMovieDbJsonUtils;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Movie[]>,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FETCH_MOVIES_LOADER_ID = 22;
    private static final String EXTRA_MOVIE_QUERY = "movie_extra";

    // starting page to fetch movies from the api
    private static final int FETCH_MOVIES_LOADER_START_PAGE = 1;



    private GridView mGridView;
    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    // flag to indicate a change in user preferences
    private static boolean mPrefUpdate = false;

    // variable to store current page fetched from api
    // i will use later to make infinite scroll
    private static int mCurrPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = findViewById(R.id.gv_movies);

        mMoviesAdapter = new MoviesAdapter(this, new ArrayList<Movie>());

        mGridView.setAdapter(mMoviesAdapter);

        mGridView.setOnItemClickListener(this);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the movie data. */


        /* starting page is 1 */
        loadMovieData(FETCH_MOVIES_LOADER_START_PAGE);


        // register on shared preference change listener to check for preference change
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister on shared preference change listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mPrefUpdate = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //reload movie data if preferences changed
        if(mPrefUpdate) {
            loadMovieData(FETCH_MOVIES_LOADER_START_PAGE);
            mPrefUpdate = false;
        }
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadMovieData(int queryPage) {
        showMovieDataView();

        //get
        String queryType = MoviesPreferences.getPreferredSortOrderKey(this);
        MovieQuery movieQuery = new MovieQuery(queryType, queryPage);
        Bundle movieQueryBundle = new Bundle();
        movieQueryBundle.putParcelable(EXTRA_MOVIE_QUERY, movieQuery);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Movie[]> fetchMoviesLoader = loaderManager.getLoader(FETCH_MOVIES_LOADER_ID);
        if(fetchMoviesLoader == null) {
            loaderManager.initLoader(FETCH_MOVIES_LOADER_ID, movieQueryBundle, this);
        } else {
            loaderManager.restartLoader(FETCH_MOVIES_LOADER_ID, movieQueryBundle, this);
        }

    }

    @NonNull
    @Override
    public Loader<Movie[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new FetchMoviesTaskLoader(this, bundle);
    }

    private static class FetchMoviesTaskLoader extends AsyncTaskLoader<Movie[]> {

        Movie[] mMoviesData = null;
        Bundle mQueryBundle;

        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        FetchMoviesTaskLoader(MainActivity context, Bundle bundle) {
            super(context);
            activityReference = new WeakReference<>(context);
            mQueryBundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            if(mQueryBundle == null) {
                return;
            }

            // get a reference to the activity if it is still there
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            /* show loading before fetching movie details from network */
            activity.mLoadingIndicator.setVisibility(View.VISIBLE);

            /* if there is cached data return that instead */
            if(mMoviesData != null) {
                deliverResult(mMoviesData);
            }
            forceLoad();

        }

        @Nullable
        @Override
        public Movie[] loadInBackground() {
            MovieQuery query = mQueryBundle.getParcelable(EXTRA_MOVIE_QUERY);

            /* If there's no query, there's nothing to look up. */
            if (query == null) {
                return null;
            }

            String queryType = query.sort_order;
            int queryPage = query.page;

            URL moviesRequestUrl = NetworkUtils.buildUrl(getContext(), queryType, queryPage);

            try {
                if ( moviesRequestUrl == null ) {
                    return null;
                }

                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                return TheMovieDbJsonUtils
                        .getMovieDataFromJson(getContext(),
                                jsonMovieResponse);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(@Nullable Movie[] data) {
            mMoviesData = data;
            super.deliverResult(data);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie[]> loader, Movie[] moviesData) {
        /* hide loading after network request */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (moviesData != null) {
            showMovieDataView();
            mMoviesAdapter.setMoviesData(moviesData);
            setActionBarSubtitle();
        } else {
            showErrorMessage();
        }
    }

    private void setActionBarSubtitle() {
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            String subtitleText = MoviesPreferences.getPreferredSortOrderLabel(this);
            if(subtitleText!=null) { actionBar.setSubtitle(subtitleText); }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie[]> loader) {

    }


    /**
     * This method will make the View for the movie grid visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mGridView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie grid.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mGridView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle GridView item
     * clicks.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = (Movie) parent.getItemAtPosition(position);
        Context context = this;

        Class destinationClass = MovieDetailsActivity.class;
        Intent intent = new Intent(context, destinationClass);

        /* Put movie object into intent extra
         * see https://www.techjini.com/blog/passing-objects-via-intent-in-android/
         */
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }


//        if (id == R.id.action_refresh) {
//            mMoviesAdapter.setMoviesData(null);
//            loadMovieData();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }



}