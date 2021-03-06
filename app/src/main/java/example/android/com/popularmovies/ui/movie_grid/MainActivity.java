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
package example.android.com.popularmovies.ui.movie_grid;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.ui.settings.SettingsActivity;
import example.android.com.popularmovies.ui.movie_details.MovieDetailsActivity;
import example.android.com.popularmovies.utilities.ConnectionBuddyUtilsForActivities;

import static example.android.com.popularmovies.data.Constant.EXTRA_MOVIE_ID;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener,
        ConnectivityChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // starting page to fetch movies from the api
    private static final int FETCH_MOVIES_LOADER_START_PAGE = 1;


    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessageDisplay;

//    private ProgressBar mLoadingIndicator;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private MainViewModel mViewModel;

    // flag to indicate a change in user preferences
    private static boolean mSortPrefUpdate = false;

    // variable to store current page fetched from api
    // i will use later to make infinite scroll
    private static int mCurrPage = 1;

    private MenuItem mRefreshMenuItem;

    private Snackbar mSnackBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectionBuddyUtilsForActivities.clearConnectionBuddyState(savedInstanceState, this);

        //setup ui components including recyclerview
        setupUiComponents();

        /* Once all of our views are setup, we can load the movie data. */

        // register on shared preference change listener to check for preference change
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        mSwipeRefreshLayout.setRefreshing(true);
        loadMovieData(false, false);

    }

    private void setupUiComponents() {
        mRecyclerView = findViewById(R.id.rv_movies);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMoviesAdapter);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        /*
         * The Refresh Layout that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mSwipeRefreshLayout = findViewById(R.id.srl_rv_movies);
        mSwipeRefreshLayout.setOnRefreshListener(this);
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
        if(key.equals(getString(R.string.pref_sort_key))) {
            mSortPrefUpdate = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionBuddyUtilsForActivities.registerConnectionBuddyEvents(this, this);

        if(mSortPrefUpdate) {
            mSortPrefUpdate = false;
            loadMovieData(false, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        ConnectionBuddyUtilsForActivities.unregisterConnectionBuddyEvents(this);
    }



    private void showRefreshUi(boolean bool) {
        mSwipeRefreshLayout.setEnabled(bool);
        if(mRefreshMenuItem!=null) mRefreshMenuItem.setEnabled(bool);
    }

    /**
     * This method will get load movie data into the recyclerview adapter
     *
     */
    private void loadMovieData(boolean forceUpdate, boolean prefChanged) {

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // reinitialize data on change in preferences and force update
        if(prefChanged || forceUpdate) {
            mSwipeRefreshLayout.setRefreshing(true);
            String sortOrder = MoviesPreferences.getPreferredSortOrder(this);
            mViewModel.updateQuery(sortOrder, forceUpdate);
        }
        mViewModel.displayMovies.observe(this, listResource -> {
            Log.d(TAG, "Updating movies display from LiveData in ViewModel");
            if(listResource != null) {
                switch (listResource.status) {
                    case ERROR: {
                        Log.i(TAG, "onChanged: [error] " + listResource.message);
                        showMoviesData(listResource.data, false);
                        break;
                    }
                    case LOADING: {
                        showMoviesData(listResource.data, true);
                        break;
                    }
                    case SUCCESS: {
                        showMoviesData(listResource.data, false);
                        break;
                    }
                }
            }
        });
    }


    public void showMoviesData(List<MovieEntry> moviesData, boolean keepRefreshingUi) {
        if(!keepRefreshingUi && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        setActionBarSubtitle();
        if (moviesData != null && moviesData.size() !=0 ) {
            showMovieDataView();
            mMoviesAdapter.setMoviesData(moviesData);
        } else {
            showMessage(getString(R.string.message_no_show));
        }
    }

    private void setActionBarSubtitle() {
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            String subtitleText = MoviesPreferences.getPreferredSortOrderLabel(this);
            if(subtitleText!=null) { actionBar.setSubtitle(subtitleText); }
        }
    }


    /**
     * This method will make the View for the movie grid visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie grid.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie grid.
     */
    private void showMessage(String message) {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        mRefreshMenuItem = menu.findItem(R.id.action_refresh);
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

        if (id == R.id.action_refresh) {
            loadMovieData(true, false);
            //don't do anything if favorite sort order

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        loadMovieData(true, false);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     */
    @Override
    public void onClick(MovieEntry movie) {
        Context context = this;

        Class destinationClass = MovieDetailsActivity.class;
        Intent intent = new Intent(context, destinationClass);

        /* Put movie object into intent extra
         * see https://www.techjini.com/blog/passing-objects-via-intent-in-android/
         */
//        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        intent.putExtra(EXTRA_MOVIE_ID, movie.getId());

        startActivity(intent);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        boolean isConnected = event.getState() == ConnectivityState.CONNECTED;
        showRefreshUi(isConnected);
        loadMovieData(false,false);
        showConnectionSnackBar(isConnected);
    }

    private void showConnectionSnackBar(boolean isConnected) {
        if(mSwipeRefreshLayout!=null) {
            if(isConnected) {
                //hide snackbar
                if(mSnackBar!=null) {
                    mSnackBar.dismiss();
                }

            } else {
                //show snackbar
                mSnackBar = Snackbar.make(mSwipeRefreshLayout, R.string.message_no_internet_refresh_disabled, Snackbar.LENGTH_INDEFINITE);
                mSnackBar.show();
            }
        }
    }


}