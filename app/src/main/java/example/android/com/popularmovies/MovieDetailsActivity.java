package example.android.com.popularmovies;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import example.android.com.popularmovies.data.Movie;
import example.android.com.popularmovies.utilities.MovieUtils;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    /* Movie object to store movie details */
    private Movie mMovie;

    /* Movie Details Ui variables */
    private TextView mMovieTitleTextView;
    private ImageView mMovieDetailPosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieVoteAverageTextView;
    private TextView mMoviePlotSynopsisTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMovieDetailPosterImageView = findViewById(R.id.iv_movie_detail_poster);
        mMovieReleaseDateTextView = findViewById(R.id.tv_release_date);
        mMovieVoteAverageTextView = findViewById(R.id.tv_vote_average);
        mMoviePlotSynopsisTextView = findViewById(R.id.tv_plot_synopsis);

        Intent intent = getIntent();

        if(intent == null ) { closeOnError(); }
        else if (!(intent.hasExtra(EXTRA_MOVIE))) { closeOnError(); }
        else {
            mMovie = intent.getParcelableExtra(EXTRA_MOVIE);
            if(mMovie == null) { closeOnError(); }
            populateUI();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI() {
        mMovieTitleTextView.setText(
                mMovie.getMovieTitle());

        Picasso.with(this)
                .load(mMovie.getMoviePosterUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(mMovieDetailPosterImageView);

        mMovieReleaseDateTextView.setText(
                MovieUtils.getFormattedDateString( this,
                        mMovie.getReleaseDate()));

        mMovieVoteAverageTextView.setText(
                MovieUtils.getFormattedVoteString(this,
                        mMovie.getVoteAverage()));

        mMoviePlotSynopsisTextView.setText(
                mMovie.getPlotSynopsis());
    }
}
