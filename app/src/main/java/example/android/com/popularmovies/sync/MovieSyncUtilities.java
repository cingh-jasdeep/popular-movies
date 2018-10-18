package example.android.com.popularmovies.sync;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import example.android.com.popularmovies.AppExecutors;
import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.db.AppDatabase;
import example.android.com.popularmovies.model.MovieEntry;

import static example.android.com.popularmovies.data.Constant.NETWORK_UPDATE_THRESHOLD_IN_HOURS;

public class MovieSyncUtilities {

    private static boolean isDataPresent = false;
    private static boolean sixHoursPassedSinceLastNetworkUpdate = false;

    public static void doNetworkUpdate(final Context context, @NonNull final String action) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                AppDatabase dB = AppDatabase.getInstance(context);
                MovieRepository repo = MovieRepository.getInstance(dB);

                if(action.equals(MoviesSyncTasks.ACTION_UPDATE_ALL_MOVIES)) {
                    LiveData<List<MovieEntry>> dbMoviesLiveData = repo.getPreferredMovies(context);
                    List<MovieEntry> dbMoviesData = dbMoviesLiveData.getValue();
                    // Check if we have data in preferred sort order and if yes,
                    isDataPresent = false;

                    if(dbMoviesData != null && !(dbMoviesData.isEmpty())) {
                        isDataPresent = true;
                    }

                    // Check if 6 hours has passed since the last network update

                    /*
                     * If the last network update was shown was more than 6 hours, we need to refresh movies list.
                     * Remember, it's important to not update movie list very frequently
                     * unless user explicitly refreshes using pull down.
                     */

                    long timeSinceLastNetworkUpdate = MoviesPreferences
                            .getEllapsedTimeSinceLastNetworkUpdate(context);
                    sixHoursPassedSinceLastNetworkUpdate = (timeSinceLastNetworkUpdate >=
                            TimeUnit.HOURS.toMillis(NETWORK_UPDATE_THRESHOLD_IN_HOURS) );

                } else if (action.equals(MoviesSyncTasks.ACTION_UPDATE_MOVIE_TRAILERS)) {
                    startImmediateSync(context, action);

                } else if (action.equals(MoviesSyncTasks.ACTION_UPDATE_MOVIE_REVIEWS)) {
                    startImmediateSync(context, action);
                }

                // If more than 6 hours have passed and data is present, do a network update
                // or do a network update if data is not present
                if((!isDataPresent) ||
                        (sixHoursPassedSinceLastNetworkUpdate)) {
                    startImmediateSync(context, action);
                }
            }
        });
    }

    public static void startImmediateSync(@NonNull final Context context, @NonNull String action) {
        Intent fetchMovieDataIntent = new Intent(context, MoviesIntentService.class);
        fetchMovieDataIntent.setAction(action);
        context.startService(fetchMovieDataIntent);
    }

}
