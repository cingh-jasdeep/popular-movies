package example.android.com.popularmovies.ui.movie_details;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
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

import com.squareup.picasso.Picasso;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import java.util.Date;
import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.db.model.MovieReviewEntry;
import example.android.com.popularmovies.db.model.MovieTrailerEntry;
import example.android.com.popularmovies.ui.settings.SettingsActivity;
import example.android.com.popularmovies.utilities.MovieUtils;
import example.android.com.popularmovies.utilities.ConnectionBuddyUtilsForActivities;
import timber.log.Timber;

import static example.android.com.popularmovies.data.Constant.DEFAULT_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.EXTRA_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.INSTANCE_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_FALSE;
import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;

public class MovieDetailsActivity extends AppCompatActivity
    implements ConnectivityChangeListener,
    MovieTrailersAdapter.MovieTrailerAdapterOnClickHandler, View.OnClickListener
    , MovieRepository.FavoriteMarkedListener, SwipeRefreshLayout.OnRefreshListener {

//    public static final String EXTRA_MOVIE = "extra_movie";


    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    /* MovieEntry object to store movie details */
    private int mMovieId = DEFAULT_MOVIE_ID;


    /* MovieEntry Details Ui variables */
    private SwipeRefreshLayout mMainSwipeRefreshLayout;
    private NestedScrollView mMainScrollView;
    private TextView mMovieTitleTextView;
    private ImageView mMovieDetailPosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieVoteAverageTextView;
    private TextView mMoviePlotSynopsisTextView;

    private Button mMovieFavoriteButton;
    // Constants for favorite button

    public static final int FAVORITE_FALSE = MOVIE_ATTR_FLAG_FALSE;
    public static final int FAVORITE_TRUE = MOVIE_ATTR_FLAG_TRUE;

    private int mFavoriteButtonState = FAVORITE_FALSE;


    private RecyclerView mTrailerRecyclerView;
    private RecyclerView mReviewRecyclerView;

    private MovieTrailersAdapter mMovieTrailersAdapter;
    private MovieReviewsAdapter mMovieReviewsAdapter;


    private View mTrailerView;
    private View mReviewView;

    private Snackbar mSnackBar;

    private MovieDetailsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ConnectionBuddyUtilsForActivities.clearConnectionBuddyState(savedInstanceState, this);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            mMovieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE_ID)) {
            //there is no saved instance state
            if (mMovieId == DEFAULT_MOVIE_ID) {
                //populate UI
                mMovieId = intent.getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
//                MoviesPreferences.saveCurrentMovieId(getApplicationContext(), mMovieId);
            }

            mMainSwipeRefreshLayout.setRefreshing(true);
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

    private void loadMovieData(boolean forceRefresh) {



        MovieDetailsViewModelFactory viewModelFactory = new MovieDetailsViewModelFactory(getApplication(), mMovieId);

        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel.class);

        if(forceRefresh) {
            mViewModel.updateTrailerQuery(mMovieId, true);
            mViewModel.updateReviewQuery(mMovieId, true);
        }

        mViewModel.loadMovieObject().observe(this, movieEntryResource -> {
            Timber.d(TAG, "Receiving movie database update from LiveData in ViewModel");

            if(movieEntryResource != null) {
                switch (movieEntryResource.status) {
                    case ERROR: {
                        Log.i(TAG, "onChanged: [error] " + movieEntryResource.message);
                        populateUI(movieEntryResource.data, false);
                        break;
                    }
                    case LOADING: {
                        mMainSwipeRefreshLayout.setRefreshing(true);
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
            Timber.d(TAG, "Receiving movie trailer database update from LiveData in ViewModel");

            if(listResource != null) {
                switch (listResource.status) {
                    case ERROR: {
                        Log.i(TAG, "onChanged: [error] " + listResource.message);
                        showTrailerData(listResource.data, false);
                        break;
                    }
                    case LOADING: {
                        mMainSwipeRefreshLayout.setRefreshing(true);
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
                        mMainSwipeRefreshLayout.setRefreshing(true);
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

    private void showTrailerData(List<MovieTrailerEntry> trailerEntries, boolean keepRefreshingUi) {
        if(!keepRefreshingUi && mMainSwipeRefreshLayout.isRefreshing()) {
            mMainSwipeRefreshLayout.setRefreshing(false);
        }
        if (trailerEntries != null && trailerEntries.size() !=0 ) {
            mTrailerView.setVisibility(View.VISIBLE);
            mMovieTrailersAdapter.setMovieTrailerData(trailerEntries);
        } else {
            mTrailerView.setVisibility(View.GONE);
        }

    }

    private void showReviewData(List<MovieReviewEntry> reviewEntries, boolean keepRefreshingUi) {
        if(!keepRefreshingUi && mMainSwipeRefreshLayout.isRefreshing()) {
            mMainSwipeRefreshLayout.setRefreshing(false);
        }
        if (reviewEntries != null && reviewEntries.size() !=0 ) {
            mReviewView.setVisibility(View.VISIBLE);
            mMovieReviewsAdapter.setMovieReviewData(reviewEntries);
        } else {
            mReviewView.setVisibility(View.GONE);
        }

    }

    private void populateUI(MovieEntry movieEntry, boolean keepRefreshingUi) {
        if(!keepRefreshingUi && mMainSwipeRefreshLayout.isRefreshing()) {
            mMainSwipeRefreshLayout.setRefreshing(false);
        }

        if(movieEntry != null) {
            String movieTitle = movieEntry.getMovieTitle();
            if (movieTitle != null && !(movieTitle.equals(""))) {
                mMovieTitleTextView.setText(
                        movieTitle);
            }

            String moviePosterUrl = movieEntry.getMoviePosterUrl();
            if (moviePosterUrl != null && !(moviePosterUrl.equals(""))) {
                Picasso.with(this)
                        .load(movieEntry.getMoviePosterUrl())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(mMovieDetailPosterImageView);
            }

            Date releaseDate = movieEntry.getReleaseDate();
            if (releaseDate != null) {
                mMovieReleaseDateTextView.setText(
                        MovieUtils.getFormattedDateString(this,
                                releaseDate));
            }

            String movieVoteAverage = Double.toString(movieEntry.getVoteAverage());
            if (movieVoteAverage != null && !(movieVoteAverage.equals(""))) {
                mMovieVoteAverageTextView.setText(
                        MovieUtils.getFormattedVoteString(this,
                                movieVoteAverage));
            }

            String moviePlotSynopsis = movieEntry.getPlotSynopsis();
            if (moviePlotSynopsis != null && !(moviePlotSynopsis.equals(""))) {
                mMoviePlotSynopsisTextView.setText(
                        moviePlotSynopsis);
            }

            int isFavorite = movieEntry.getIsFavorite();
            mFavoriteButtonState = isFavorite;
            setFavoriteButtonUi();
        }

    }

    @Override
    public void onRefresh() {
        loadMovieData(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_MOVIE_ID, mMovieId);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMainSwipeRefreshLayout = findViewById(R.id.srl_movie_detail_main_view);
        mMainSwipeRefreshLayout.setOnRefreshListener(this);
        mMainScrollView = findViewById(R.id.sc_movie_detail_main_view);
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMovieDetailPosterImageView = findViewById(R.id.iv_movie_detail_poster);
        mMovieReleaseDateTextView = findViewById(R.id.tv_release_date);
        mMovieVoteAverageTextView = findViewById(R.id.tv_vote_average);
        mMoviePlotSynopsisTextView = findViewById(R.id.tv_plot_synopsis);
        mTrailerRecyclerView = findViewById(R.id.rv_trailers_movie_detail);
        mReviewRecyclerView = findViewById(R.id.rv_reviews_movie_detail);

        mMovieFavoriteButton = findViewById(R.id.button_favorite);

        mMovieFavoriteButton.setOnClickListener(this);

        mMovieTrailersAdapter = new MovieTrailersAdapter(this, this);
        mMovieReviewsAdapter = new MovieReviewsAdapter(this);

        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setAdapter(mMovieTrailersAdapter);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setAdapter(mMovieReviewsAdapter);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTrailerView = findViewById(R.id.layout_trailers_list);
        mReviewView = findViewById(R.id.layout_reviews_list);

        mTrailerView.setVisibility(View.GONE);
        mReviewView.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail,menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }



    private void setFavoriteButtonUi() {
        boolean isFavorite = mFavoriteButtonState == FAVORITE_TRUE;
        if(isFavorite) {
            mMovieFavoriteButton.setText(R.string.button_text_favorite);
        } else {
            mMovieFavoriteButton.setText(R.string.button_text_mark_as_favorite);
        }
        mMovieFavoriteButton.setSelected(isFavorite);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_favorite) {
            toggleFavorite();
        }
    }

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
        //hide trailer and reviews
        showConnectionSnackBar(isConnected);
    }

    private void showConnectionSnackBar(boolean isConnected) {
        if (isConnected) {
            //hide snackbar
            if (mSnackBar != null) {
                mSnackBar.dismiss();
            }
        } else {
            //show snackbar
            mSnackBar = Snackbar.make(mMainScrollView, R.string.message_no_internet_detail, Snackbar.LENGTH_INDEFINITE);
            mSnackBar.show();
        }
    }

    @Override
    public void onTrailerClick(MovieTrailerEntry trailerEntry) {

    }

    @Override
    public void onFavoriteMarkedSaved(boolean favoriteMarked) {
        Snackbar.make(mMainScrollView, R.string.snackbar_message_favorite_marked, Snackbar.LENGTH_SHORT).show();
    }


}