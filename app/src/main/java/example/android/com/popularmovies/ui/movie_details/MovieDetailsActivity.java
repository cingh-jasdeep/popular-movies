package example.android.com.popularmovies.ui.movie_details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.db.AppDatabase;
import example.android.com.popularmovies.model.MovieEntry;
import example.android.com.popularmovies.model.MovieTrailerEntry;
import example.android.com.popularmovies.sync.MovieSyncUtilities;
import example.android.com.popularmovies.sync.MoviesSyncTasks;
import example.android.com.popularmovies.ui.settings.SettingsActivity;
import example.android.com.popularmovies.utilities.MovieUtils;

import static example.android.com.popularmovies.data.Constant.DEFAULT_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.EXTRA_MOVIE_ID;
import static example.android.com.popularmovies.data.Constant.INSTANCE_MOVIE_ID;

public class MovieDetailsActivity extends AppCompatActivity
    implements ConnectivityChangeListener,
    MovieTrailerAdapter.MovieTrailerAdapterOnClickHandler {

//    public static final String EXTRA_MOVIE = "extra_movie";



    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    /* MovieEntry object to store movie details */
    private int mMovieId = DEFAULT_MOVIE_ID;


    /* MovieEntry Details Ui variables */
    private ScrollView mMainScrollView;
    private TextView mMovieTitleTextView;
    private ImageView mMovieDetailPosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieVoteAverageTextView;
    private TextView mMoviePlotSynopsisTextView;


    private RecyclerView mTrailerRecyclerView;
    private MovieTrailerAdapter mMoviesTrailerAdapter;

    private View mTrailerView;

    private Snackbar mSnackBar;

    // Member variable for the Database
    private AppDatabase mDb;
    private MovieRepository mRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        initViews();
        mDb = AppDatabase.getInstance(getApplicationContext());
        mRepo = MovieRepository.getInstance(mDb);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MOVIE_ID)) {
            mMovieId = savedInstanceState.getInt(INSTANCE_MOVIE_ID, DEFAULT_MOVIE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE_ID)) {
            //this is first load
            if (mMovieId == DEFAULT_MOVIE_ID) {
                //populate UI
                mMovieId = intent.getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);

                MoviesPreferences.saveCurrentMovieId(getApplicationContext(), mMovieId);
                loadMovieData();
            }
        } else {
            closeOnError();
        }
    }

    private void loadMovieData() {
        MovieSyncUtilities.startImmediateSync(this, MoviesSyncTasks.ACTION_UPDATE_MOVIE_TRAILERS);
        setupViewModel();
    }

    private void setupViewModel() {
        MovieDetailsViewModelFactory viewModelFactory = new MovieDetailsViewModelFactory(mRepo, mMovieId);

        final MovieDetailsViewModel viewModel =
                ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel.class);

        viewModel.getMovie().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                Log.d(TAG, "Receiving database update from LiveData in ViewModel");
                populateUI(movieEntry);
            }
        });
        viewModel.getMovieTrailers().observe(this, new Observer<List<MovieTrailerEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieTrailerEntry> trailerEntries) {
                showTrailerData(trailerEntries);
            }
        });
    }

    private void showTrailerData(List<MovieTrailerEntry> trailerEntries) {
        if (trailerEntries != null && trailerEntries.size() !=0 ) {
            mTrailerView.setVisibility(View.VISIBLE);
            mMoviesTrailerAdapter.setMovieTrailerData(trailerEntries);
        }

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

        mMainScrollView = findViewById(R.id.sc_movie_detail_main_view);
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMovieDetailPosterImageView = findViewById(R.id.iv_movie_detail_poster);
        mMovieReleaseDateTextView = findViewById(R.id.tv_release_date);
        mMovieVoteAverageTextView = findViewById(R.id.tv_vote_average);
        mMoviePlotSynopsisTextView = findViewById(R.id.tv_plot_synopsis);
        mTrailerRecyclerView = findViewById(R.id.rv_trailers_movie_detail);

        mMoviesTrailerAdapter = new MovieTrailerAdapter(this, this);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setAdapter(mMoviesTrailerAdapter);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTrailerView = findViewById(R.id.layout_trailer_list);

        mTrailerView.setVisibility(View.INVISIBLE);

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

    private void populateUI(MovieEntry movieEntry) {
        String movieTitle = movieEntry.getMovieTitle();
        if(movieTitle!=null && !(movieTitle.equals(""))) {
            mMovieTitleTextView.setText(
                    movieTitle);
        }

        String moviePosterUrl = movieEntry.getMoviePosterUrl();
        if(moviePosterUrl!=null && !(moviePosterUrl.equals(""))) {
            Picasso.with(this)
                    .load(movieEntry.getMoviePosterUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(mMovieDetailPosterImageView);
        }

        Date releaseDate = movieEntry.getReleaseDate();
        if(releaseDate!=null) {
            mMovieReleaseDateTextView.setText(
                    MovieUtils.getFormattedDateString( this,
                            releaseDate));
        }

        String movieVoteAverage = Double.toString(movieEntry.getVoteAverage());
        if(movieVoteAverage!=null && !(movieVoteAverage.equals(""))) {
            mMovieVoteAverageTextView.setText(
                    MovieUtils.getFormattedVoteString(this,
                            movieVoteAverage));
        }

        String moviePlotSynopsis = movieEntry.getPlotSynopsis();
        if(moviePlotSynopsis!=null && !(moviePlotSynopsis.equals(""))) {
            mMoviePlotSynopsisTextView.setText(
                    moviePlotSynopsis);
        }

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
            } else {
                //show snackbar
                mSnackBar = Snackbar.make(mMainScrollView, R.string.message_no_internet_detail, Snackbar.LENGTH_INDEFINITE);
                mSnackBar.show();
            }
        }
    }

    @Override
    public void onClick(MovieTrailerEntry trailerEntry) {

    }
}
