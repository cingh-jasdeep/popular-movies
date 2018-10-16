package example.android.com.popularmovies.data;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.db.MovieDao;
import example.android.com.popularmovies.db.MovieEntry;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private final MovieDao mMovieDao;
    private boolean mInitialized = false;


    private MovieRepository(MovieDao movieDao) {
        mMovieDao = movieDao;
    }

    public synchronized static MovieRepository getInstance(
            MovieDao movieDao) {
        Log.d(TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieRepository(movieDao);
                Log.d(TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Database related operations
     **/


    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        Log.d(TAG, "Actively retrieving favorite movies from the DataBase in Repo");
        return mMovieDao.getFavoriteMovies();
    }

    public LiveData<List<MovieEntry>> getPopularMovies() {
        Log.d(TAG, "Actively retrieving popular movies from the DataBase in Repo");
        return mMovieDao.getPopularMovies();
    }

    public LiveData<List<MovieEntry>> getTopRatedMovies() {
        Log.d(TAG, "Actively retrieving top rated movies from the DataBase in Repo");
        return mMovieDao.getTopRatedMovies();
    }

    public LiveData<List<MovieEntry>> getPreferredMovies(@NonNull Context context) {

        String queryType = MoviesPreferences.getPreferredSortOrderKey(context);

        String topRatedQuery = context.getString(R.string.pref_sort_top_rated);
        String favoriteQuery = context.getString(R.string.pref_sort_favorite);

        if(queryType.equals(favoriteQuery)) {
            return getFavoriteMovies();

        } else if (queryType.equals(topRatedQuery)) {
            return getTopRatedMovies();

        } //default is popular sort key
            else {
            return getPopularMovies();
        }
    }

    public void insertAll(List<MovieEntry> movieEntryList) {
        Log.d(TAG, "Actively inserting all movies into the DataBase in Repo");
        mMovieDao.insertAll(movieEntryList);
    }

    public void deleteAll() {
        Log.d(TAG, "Actively deleting all movies from the DataBase in Repo");
        mMovieDao.deleteAll();
    }

    public void replaceAll(List<MovieEntry> movieEntryList) {
        Log.d(TAG, "Actively replacing all movies from the DataBase in Repo");
        mMovieDao.replaceAll(movieEntryList);
    }
}
