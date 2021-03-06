package example.android.com.popularmovies.ui.movie_details;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import java.util.Date;
import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.databinding.ActivityMovieDetailsBinding;
import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.db.model.MovieReviewEntry;
import example.android.com.popularmovies.db.model.MovieTrailerEntry;
import example.android.com.popularmovies.ui.settings.SettingsActivity;
import example.android.com.popularmovies.utilities.MovieUtils;
import example.android.com.popularmovies.utilities.ConnectionBuddyUtilsForActivities;

import static example.android.com.popularmovies.data.Constant.DEFAULT_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.EXTRA_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.INSTANCE_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_FALSE;
import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;
import static example.android.com.popularmovies.data.Constant.YOUTUBE_VIDEO_BASE_URL;

public class MovieDetailsActivity extends AppCompatActivity
    implements ConnectivityChangeListener,
    MovieTrailersAdapter.MovieTrailerAdapterOnClickHandler, View.OnClickListener
    , MovieRepository.FavoriteMarkedListener, SwipeRefreshLayout.OnRefreshListener {

    /* Movie Details binding variable */
    private ActivityMovieDetailsBinding mDetailsBinding;


    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    /* MovieEntry object to store movie details */
    private int mCurrMovieId = DEFAULT_MOVIE_ID;
    private MovieEntry mCurrMovieEntry;


    private MenuItem mRefreshMenuItem;

    // Constants for favorite button

    public static final int FAVORITE_FALSE = MOVIE_ATTR_FLAG_FALSE;
    public static final int FAVORITE_TRUE = MOVIE_ATTR_FLAG_TRUE;

    private int mFavoriteButtonState = FAVORITE_FALSE;


    private MovieTrailersAdapter mMovieTrailersAdapter;
    private MovieReviewsAdapter mMovieReviewsAdapter;


    private Snackbar mSnackBar;

    private MovieDetailsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        ConnectionBuddyUtilsForActivities.clearConnectionBuddyState(savedInstanceState, this);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            mCurrMovieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE_ID)) {
            //there is no saved instance state
            if (mCurrMovieId == DEFAULT_MOVIE_ID) {
                //populate UI
                mCurrMovieId = intent.getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
            }

            loadMovieData(false);
        } else {
            closeOnError();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionBuddyUtilsForActivities.registerConnectionBuddyEvents(this, this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        ConnectionBuddyUtilsForActivities.unregisterConnectionBuddyEvents(this);
    }

    /**
     * loads movie data from viewmodel and sets observers for new data
     * @param forceRefresh indicates whether the data needs to be refreshed online
     */
    private void loadMovieData(boolean forceRefresh) {

        mDetailsBinding.srlMovieDetailMainView.setRefreshing(true);

        MovieDetailsViewModelFactory viewModelFactory = new MovieDetailsViewModelFactory(getApplication(), mCurrMovieId);

        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel.class);

        if(forceRefresh) {
            mViewModel.updateTrailerQuery(mCurrMovieId, true);
            mViewModel.updateReviewQuery(mCurrMovieId, true);
        }

        mViewModel.loadMovieObject().observe(this, movieEntryResource -> {
            Log.d(TAG, "Receiving movie database update from LiveData in ViewModel");
            if(movieEntryResource != null) {
                switch (movieEntryResource.status) {
                    case ERROR: {
                        Log.i(TAG, "onChanged: [error] " + movieEntryResource.message);
                        populateUI(movieEntryResource.data, false);
                        break;
                    }
                    case LOADING: {
                        mDetailsBinding.srlMovieDetailMainView.setRefreshing(true);
                        populateUI(movieEntryResource.data, true);
                        break;
                    }
                    case SUCCESS: {
                        populateUI(movieEntryResource.data, false);
                        break;
                    }
                }
            }
        });

        mViewModel.getMovieTrailersResource().observe(this, listResource -> {
            Log.d(TAG, "Receiving movie trailer database update from LiveData in ViewModel");
            if(listResource != null) {
                switch (listResource.status) {
                    case ERROR: {
                        Log.i(TAG, "onChanged: [error] " + listResource.message);
                        showTrailerData(listResource.data, false);
                        break;
                    }
                    case LOADING: {
                        mDetailsBinding.srlMovieDetailMainView.setRefreshing(true);
                        showTrailerData(listResource.data, true);
                        break;
                    }
                    case SUCCESS: {
                        showTrailerData(listResource.data, false);
                        break;
                    }
                }
            }
        });

        mViewModel.getMovieReviewsResource().observe(this, listResource -> {
            if(listResource != null) {
                switch (listResource.status) {
                    case ERROR: {
                        Log.i(TAG, "onChanged: [error] " + listResource.message);
                        showReviewData(listResource.data, false);
                        break;
                    }
                    case LOADING: {
                        mDetailsBinding.srlMovieDetailMainView.setRefreshing(true);
                        showReviewData(listResource.data, true);
                        break;
                    }
                    case SUCCESS: {
                        showReviewData(listResource.data, false);
                        break;
                    }
                }
            }
        });

    }

    /**
     * updates trailer data into the recycler view
     * @param trailerEntries data for recycler view
     * @param keepRefreshingUi indicates that data is still loading
     */
    private void showTrailerData(List<MovieTrailerEntry> trailerEntries, boolean keepRefreshingUi) {
        if(!keepRefreshingUi && mDetailsBinding.srlMovieDetailMainView.isRefreshing()) {
            mDetailsBinding.srlMovieDetailMainView.setRefreshing(false);
        }
        if (trailerEntries != null && trailerEntries.size() !=0 ) {
            mDetailsBinding.layoutTrailersList.getRoot().setVisibility(View.VISIBLE);
            mMovieTrailersAdapter.setMovieTrailerData(trailerEntries);
        } else {
            mDetailsBinding.layoutTrailersList.getRoot().setVisibility(View.GONE);
        }

    }

    /**
     * updates review data into the recycler view
     * @param reviewEntries data for recycler view
     * @param keepRefreshingUi indicates that data is still loading
     */
    private void showReviewData(List<MovieReviewEntry> reviewEntries, boolean keepRefreshingUi) {
        if(!keepRefreshingUi && mDetailsBinding.srlMovieDetailMainView.isRefreshing()) {
            mDetailsBinding.srlMovieDetailMainView.setRefreshing(false);
        }
        if (reviewEntries != null && reviewEntries.size() !=0 ) {
            mDetailsBinding.layoutReviewsList.getRoot().setVisibility(View.VISIBLE);
            mMovieReviewsAdapter.setMovieReviewData(reviewEntries);
        } else {
            mDetailsBinding.layoutReviewsList.getRoot().setVisibility(View.GONE);
        }

    }

    /**
     * populates movie details into ui
     * @param movieEntry object to update ui
     * @param keepRefreshingUi indicates that data is still loading
     */
    private void populateUI(MovieEntry movieEntry, boolean keepRefreshingUi) {
        if(!keepRefreshingUi && mDetailsBinding.srlMovieDetailMainView.isRefreshing()) {
            mDetailsBinding.srlMovieDetailMainView.setRefreshing(false);
        }

        if(movieEntry != null) {
            mCurrMovieEntry = movieEntry;
            String movieTitle = movieEntry.getMovieTitle();
            if (movieTitle != null && !(movieTitle.equals(""))) {
                mDetailsBinding.tvMovieTitle.setText(
                        movieTitle);
            }

            String moviePosterUrl = movieEntry.getMoviePosterUrl();
            if (moviePosterUrl != null && !(moviePosterUrl.equals(""))) {
//                https://stackoverflow.com/questions/23391523/load-images-from-disk-cache-with-picasso-if-offline
                Picasso.with(this)
                        .load(movieEntry.getMoviePosterUrl())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mDetailsBinding.ivMovieDetailPoster, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext())
                                        .load(movieEntry.getMoviePosterUrl())
                                        .into(mDetailsBinding.ivMovieDetailPoster);
                            }
                        });
            }

            Date releaseDate = movieEntry.getReleaseDate();
            if (releaseDate != null) {
                mDetailsBinding.tvReleaseDate.setText(
                        MovieUtils.getFormattedDateString(this,
                                releaseDate));
            }

            String movieVoteAverage = Double.toString(movieEntry.getVoteAverage());
            if (movieVoteAverage != null && !(movieVoteAverage.equals(""))) {
                mDetailsBinding.tvVoteAverage.setText(
                        MovieUtils.getFormattedVoteString(this,
                                movieVoteAverage));
            }

            String moviePlotSynopsis = movieEntry.getPlotSynopsis();
            if (moviePlotSynopsis != null && !(moviePlotSynopsis.equals(""))) {
                mDetailsBinding.tvPlotSynopsis.setText(
                        moviePlotSynopsis);
            }

            mFavoriteButtonState = movieEntry.getIsFavorite();
            setFavoriteButtonUi();
        }

    }

    @Override
    public void onRefresh() {
        loadMovieData(true);
    }

    /**
     * used to toggle the refresh ui components for instances like no internet connection
     * @param bool indicates where to show or hide refresh ui components
     */
    private void showRefreshUi(boolean bool) {
        mDetailsBinding.srlMovieDetailMainView.setEnabled(bool);
        if(mRefreshMenuItem!=null) mRefreshMenuItem.setEnabled(bool);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_MOVIE_ID, mCurrMovieId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initialize ui components properties like clickListener, recyclerView adapters and visibility
     */
    private void initViews() {
        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDetailsBinding.srlMovieDetailMainView.setOnRefreshListener(this);

        mDetailsBinding.buttonFavorite.setOnClickListener(this);

        mMovieTrailersAdapter = new MovieTrailersAdapter(this, this);
        mMovieReviewsAdapter = new MovieReviewsAdapter(this);

        mDetailsBinding.layoutTrailersList.rvTrailersMovieDetail.setHasFixedSize(true);
        mDetailsBinding.layoutTrailersList.rvTrailersMovieDetail.setAdapter(mMovieTrailersAdapter);
        mDetailsBinding.layoutTrailersList.rvTrailersMovieDetail.setLayoutManager(new LinearLayoutManager(this));

        mDetailsBinding.layoutReviewsList.rvReviewsMovieDetail.setHasFixedSize(true);
        mDetailsBinding.layoutReviewsList.rvReviewsMovieDetail.setAdapter(mMovieReviewsAdapter);
        mDetailsBinding.layoutReviewsList.rvReviewsMovieDetail.setLayoutManager(new LinearLayoutManager(this));


        mDetailsBinding.layoutTrailersList.getRoot().setVisibility(View.GONE);
        mDetailsBinding.layoutReviewsList.getRoot().setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail,menu);
        mRefreshMenuItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        } else if (id == R.id.action_refresh) {
            loadMovieData(true);
        } else if (id == R.id.action_share) {
            shareFirstTrailerLink();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * share first trailer link (if available) from trailer list
     */
    private void shareFirstTrailerLink() {
        MovieTrailerEntry firstTrailer = mMovieTrailersAdapter.getMovieTrailerData().get(0);
        if(firstTrailer!=null && mCurrMovieEntry !=null) {
            String textToShare = "Check out trailer for " +
                    mCurrMovieEntry.getMovieTitle() +
                    " " + YOUTUBE_VIDEO_BASE_URL + firstTrailer.getYtUrlKey()
                    + " #" + getString(R.string.app_name);
            shareText(textToShare);
        } else {
            Snackbar.make(mDetailsBinding.svMovieDetailMainView, R.string.message_share_no_trailer, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * used when no movie data is available
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.message_no_movie_data_error, Toast.LENGTH_SHORT).show();
    }


    /**
     * used to set favorite button ui when movie is set favorite/unFavorite
     */
    private void setFavoriteButtonUi() {
        boolean isFavorite = mFavoriteButtonState == FAVORITE_TRUE;
        if(isFavorite) {
            mDetailsBinding.buttonFavorite.setText(R.string.button_text_favorite);
        } else {
            mDetailsBinding.buttonFavorite.setText(R.string.button_text_mark_as_favorite);
        }
        mDetailsBinding.buttonFavorite.setSelected(isFavorite);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_favorite) {
            toggleFavorite();
        }
    }

    /**
     * toggle favorite state of the movie
     */
    private void toggleFavorite() {
        //toggle state

        if(mFavoriteButtonState == FAVORITE_TRUE) {
            mFavoriteButtonState = FAVORITE_FALSE;
        } else {
            mFavoriteButtonState = FAVORITE_TRUE;
        }


        mViewModel.setIsFavorite(mFavoriteButtonState, this);
        setFavoriteButtonUi();
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        boolean isConnected = event.getState() == ConnectivityState.CONNECTED;
        showRefreshUi(isConnected);
        showConnectionSnackBar(isConnected);
    }

    /**
     * show snackbar to indicate offline connectivity
     * @param isConnected indicates whether application has active internet connection
     */
    private void showConnectionSnackBar(boolean isConnected) {
        if (isConnected) {
            //hide snackbar
            if (mSnackBar != null) {
                mSnackBar.dismiss();
            }
        } else {
            //show snackbar
            mSnackBar = Snackbar.make(mDetailsBinding.svMovieDetailMainView, R.string.message_no_internet_refresh_disabled, Snackbar.LENGTH_INDEFINITE);
            mSnackBar.show();
        }
    }

    @Override
    public void onTrailerClick(MovieTrailerEntry trailerEntry) {
        String urlAsString = YOUTUBE_VIDEO_BASE_URL + trailerEntry.getYtUrlKey();
        openWebPage(urlAsString);
    }

    /**
     * This method fires off an implicit Intent to open a webpage.
     *
     * @param url Url of webpage to open. Should start with http:// or https:// as that is the
     *            scheme of the URI expected with this Intent according to the Common Intents page
     */
    /*
     * copied from lessor04b - implicit intents
     * @param url
     */
    private void openWebPage(String url) {
        /*
         * We wanted to demonstrate the Uri.parse method because its usage occurs frequently. You
         * could have just as easily passed in a Uri as the parameter of this method.
         */
        Uri webpage = Uri.parse(url);

        /*
         * Here, we create the Intent with the action of ACTION_VIEW. This action allows the user
         * to view particular content. In this case, our webpage URL.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * this method is used to share text using ShareCompat
     * @param textToShare text to share to other apps
     */
    void shareText(String textToShare) {

        String mimeType = "text/plain";

        String title = "Share trailer link";

        ShareCompat.IntentBuilder.from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();
    }

    @Override
    public void onFavoriteMarkedSaved(boolean favoriteMarked) {
        Snackbar.make(mDetailsBinding.svMovieDetailMainView, R.string.snackbar_message_favorite_marked, Snackbar.LENGTH_SHORT).show();
    }


}
