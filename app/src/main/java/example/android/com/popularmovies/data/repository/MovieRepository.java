package example.android.com.popularmovies.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import example.android.com.popularmovies.AppExecutors;
import example.android.com.popularmovies.R;
import example.android.com.popularmovies.api.TMDbService;
import example.android.com.popularmovies.api.TMDbServiceClient;
import example.android.com.popularmovies.api.response_models.ApiResponse;
import example.android.com.popularmovies.api.response_models.MovieListingResponse;
import example.android.com.popularmovies.api.response_models.MovieReviewListResponse;
import example.android.com.popularmovies.api.response_models.MovieVideoListResponse;
import example.android.com.popularmovies.api.response_models.MovieVideoResponseEntry;
import example.android.com.popularmovies.data.MovieQuery;
import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.data.ReviewQuery;
import example.android.com.popularmovies.data.TrailerQuery;
import example.android.com.popularmovies.db.AppDatabase;
import example.android.com.popularmovies.db.dao.MovieDao;
import example.android.com.popularmovies.db.dao.MovieReviewDao;
import example.android.com.popularmovies.db.dao.MovieTrailerDao;
import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.db.model.MovieReviewEntry;
import example.android.com.popularmovies.db.model.MovieTrailerEntry;
import example.android.com.popularmovies.utilities.PosterHelper;
import example.android.com.popularmovies.utilities.Resource;

import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_FALSE;
import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;
import static example.android.com.popularmovies.data.Constant.NETWORK_UPDATE_THRESHOLD_IN_HOURS;
import static example.android.com.popularmovies.data.Constant.TMDB_API_KEY;
import static example.android.com.popularmovies.data.Constant.TMDB_TRAILER_JSON_SITE_VALUE_YOUTUBE;
import static example.android.com.popularmovies.data.Constant.TMDB_TRAILER_JSON_TYPE_VALUE_TRAILER;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private final AppDatabase mDb;
    private final MovieDao mMovieDao;
    private final MovieTrailerDao mTrailerDao;
    private final MovieReviewDao mReviewDao;
    private final TMDbService mTmDbService;

    private boolean mInitialized = false;


    private MovieRepository(AppDatabase db) {
        mDb = db;
        mMovieDao = db.movieDao();
        mTrailerDao = db.movieTrailerDao();
        mReviewDao = db.movieReviewDao();
        mTmDbService = TMDbServiceClient.create();
    }

    public synchronized static MovieRepository getInstance(
            AppDatabase db) {
        Log.d(TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieRepository(db);
                Log.d(TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Database related operations
     **/

    public LiveData<MovieEntry> getMovieById(int movieId) {
        Log.d(TAG, "Actively retrieving a specific movie from the DataBase in Repository");
        return mMovieDao.loadMovieById(movieId);
    }

    public LiveData<List<MovieTrailerEntry>> getTrailersByMovieId(int movieId) {
        Log.d(TAG, "Actively retrieving trailers for a specific movie from the DataBase in Repository");
        return mTrailerDao.loadTrailersByMovieId(movieId);
    }

    public LiveData<List<MovieReviewEntry>> getReviewsByMovieId(int movieId) {
        Log.d(TAG, "Actively retrieving reviews for a specific movie from the DataBase in Repository");
        return mReviewDao.loadReviewsByMovieId(movieId);
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        Log.d(TAG, "Actively retrieving favorite movies from the DataBase in Repo");
        return mMovieDao.getFavoriteMovies();
    }

    public List<MovieEntry> getFavoriteMoviesOneShot() {
        Log.d(TAG, "Actively retrieving favorite movies in one shot from the DataBase in Repo");
        return mMovieDao.getFavoriteMoviesOneShot();
    }

    public List<MovieEntry> getFavoriteTopRatedMoviesOneShot() {
        Log.d(TAG, "Actively retrieving favorite top rated movies in one shot from the DataBase in Repo");
        return mMovieDao.getFavoriteTopRatedMoviesOneShot();
    }

    public List<MovieEntry> getFavoritePopularMoviesOneShot() {
        Log.d(TAG, "Actively retrieving favorite popular movies in one shot from the DataBase in Repo");
        return mMovieDao.getFavoritePopularMoviesOneShot();
    }

    public LiveData<List<MovieEntry>> getPopularMovies() {
        Log.d(TAG, "Actively retrieving popular movies from the DataBase in Repo");
        return mMovieDao.getPopularMovies();
    }

    public LiveData<List<MovieEntry>> getTopRatedMovies() {
        Log.d(TAG, "Actively retrieving top rated movies from the DataBase in Repo");
        return mMovieDao.getTopRatedMovies();
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

    public void replaceAllTopRatedMovies(List<MovieEntry> movieEntryList) {
        Log.d(TAG, "Actively replacing all movies from the DataBase in Repo");
        mMovieDao.replaceAllTopRatedMovies(movieEntryList);
    }

    public void replaceAllPopularMovies(List<MovieEntry> movieEntryList) {
        Log.d(TAG, "Actively replacing all movies from the DataBase in Repo");
        mMovieDao.replaceAllPopularMovies(movieEntryList);
    }

    public void replaceAllFavoriteMovies(List<MovieEntry> movieEntryList) {
        Log.d(TAG, "Actively replacing all movies from the DataBase in Repo");
        mMovieDao.replaceAllFavoriteMovies(movieEntryList);
    }

    public void insertTrailersData(List<MovieTrailerEntry> movieTrailerEntryList, int movieId) {
        Log.d(TAG, "Actively replacing all movies from the DataBase in Repo");
        mTrailerDao.replaceTrailersForMovieId(movieTrailerEntryList, movieId);
    }


    public LiveData<List<MovieEntry>> getPreferredMovies(@NonNull Context context) {

        String queryType = MoviesPreferences.getPreferredSortOrder(context);

        String topRatedQuery = context.getString(R.string.pref_sort_top_rated);
        String favoriteQuery = context.getString(R.string.pref_sort_favorite);

        if (queryType.equals(favoriteQuery)) {
            return getFavoriteMovies();

        } else if (queryType.equals(topRatedQuery)) {
            return getTopRatedMovies();

        } //default is popular sort key
        else {
            return getPopularMovies();
        }
    }

    public LiveData<Resource<List<MovieEntry>>> loadPreferredMovies(@NonNull Context context,
                                                                    @NonNull MovieQuery value) {
        String sortOrder = value.getSortOrder();
        boolean forceUpdate = value.isForceUpdate();


        String topRatedQuery = context.getString(R.string.pref_sort_top_rated);
        String favoriteQuery = context.getString(R.string.pref_sort_favorite);
        AppExecutors appExecutors = AppExecutors.getInstance();

        //for favorite movies just get offline results
        if (sortOrder.equals(favoriteQuery)) {

            return new NetworkBoundResource<List<MovieEntry>, List<MovieEntry>>(appExecutors) {

                //get results only offline
                //so shouldFetch is false and createCall, saveCallResult are not used

                @Override
                protected void saveCallResult(@NonNull List<MovieEntry> data) {
                }

                @Override
                protected boolean shouldFetch(@Nullable List<MovieEntry> data) {
                    return false;
                }

                @NonNull
                @Override
                protected LiveData<List<MovieEntry>> loadFromDb() {
                    return getFavoriteMovies();
                }

                @NonNull
                @Override
                protected LiveData<ApiResponse<List<MovieEntry>>> createCall() {
                    return new MediatorLiveData<>();
                }

            }.asLiveData();

        } else {
            //for popular and top rated movies get updates from network if needed
            boolean isTopRated = sortOrder.equals(topRatedQuery);

            return new NetworkBoundResource<List<MovieEntry>, MovieListingResponse>(appExecutors) {

                @Override
                protected void saveCallResult(@NonNull MovieListingResponse responseData) {

                    //extract movie list from response object
                    List<MovieEntry> moviesFetchedFromNetwork = responseData.getResults();

                    //if no movies fetched from network even after successful network transaction
                    if (moviesFetchedFromNetwork != null &&
                            moviesFetchedFromNetwork.size() == 0) {
                        if (isTopRated) {
                            mMovieDao.deleteAllTopRatedMoviesExceptFavorites();
                        } else {
                            mMovieDao.deleteAllPopularMoviesExceptFavorites();
                        }
                        return;
                    }

                    //complete partial poster urls

                    fixPosterUrls(moviesFetchedFromNetwork);

                    //set appropriate flag for fetched movies
                    for (MovieEntry entry : moviesFetchedFromNetwork) {
                        resetSortFlags(entry);
                        if (isTopRated) {
                            entry.setIsTopRated(MOVIE_ATTR_FLAG_TRUE);
                        } else {
                            entry.setIsPopular(MOVIE_ATTR_FLAG_TRUE);
                        }
                    }

                    // process favorite movies
                    // add favorite movies to the list or just update favorite flag if already in network list
                    adjustFavoriteMovies(moviesFetchedFromNetwork, isTopRated);

                    //set updatedAt time to current time in millis
                    setUpdatedAtToCurrent(moviesFetchedFromNetwork);

                    //update movies in database
                    mDb.beginTransaction();
                    try {
                        //clear old movies
//                        mMovieDao.deleteAllFavoriteMovies();
                        if (isTopRated) {
                            mMovieDao.deleteAllTopRatedMovies();
                        } else {
                            mMovieDao.deleteAllPopularMovies();
                        }

                        mMovieDao.insertAll(moviesFetchedFromNetwork);
                        mDb.setTransactionSuccessful();
                    } finally {
                        mDb.endTransaction();
                    }

                }

                @Override
                protected boolean shouldFetch(@Nullable List<MovieEntry> data) {
                    if (data == null || data.size() == 0 || forceUpdate) {
                        return true;
                    } else {
                        return !isFresh(data.get(0).getUpdatedAt(), NETWORK_UPDATE_THRESHOLD_IN_HOURS);
                    }
                }

                @NonNull
                @Override
                protected LiveData<List<MovieEntry>> loadFromDb() {
                    if (isTopRated) {
                        return getTopRatedMovies();
                    } else {
                        return getPopularMovies();
                    }
                }

                @NonNull
                @Override
                protected LiveData<ApiResponse<MovieListingResponse>> createCall() {
                    return mTmDbService.getMovies(sortOrder, TMDB_API_KEY);
                }

            }.asLiveData();
        }

    }

    private void fixPosterUrls(List<MovieEntry> moviesFetchedFromNetwork) {
        for (MovieEntry movie :
                moviesFetchedFromNetwork) {
            String completeUrl = PosterHelper.buildPosterUrl(movie.getMoviePosterUrl());
            movie.setMoviePosterUrl(completeUrl);
        }
    }

    private void resetSortFlags(MovieEntry entry) {
        entry.setIsPopular(MOVIE_ATTR_FLAG_FALSE);
        entry.setIsTopRated(MOVIE_ATTR_FLAG_FALSE);
        entry.setIsFavorite(MOVIE_ATTR_FLAG_FALSE);
    }

    private boolean isFresh(@NonNull long dataUpdatedTimeMillis, long networkUpdateThresholdInHours) {
        long timeSinceLastNetworkUpdate =
                System.currentTimeMillis() - dataUpdatedTimeMillis;
        return TimeUnit.HOURS.toMillis(networkUpdateThresholdInHours) >
                timeSinceLastNetworkUpdate;
    }

    private void setUpdatedAtToCurrent(List<MovieEntry> moviesFetchedFromNetwork) {
        for (MovieEntry movieEntry :
                moviesFetchedFromNetwork) {
            movieEntry.setUpdatedAt(System.currentTimeMillis());
        }
    }

    private void adjustFavoriteMovies(List<MovieEntry> moviesFetchedFromNetwork, boolean isTopRated) {
        List<MovieEntry> favoriteMovies;
        if (isTopRated) {
            favoriteMovies = getFavoriteTopRatedMoviesOneShot();
        } else {
            favoriteMovies = getFavoritePopularMoviesOneShot();
        }

        if (favoriteMovies != null && favoriteMovies.size() != 0) {
            for (MovieEntry favoriteMovie :
                    favoriteMovies) {

                int matchingMovieIndex = findMovieIndexByIdInList(favoriteMovie.getId(),
                        moviesFetchedFromNetwork);

                if (matchingMovieIndex != -1) {
                    moviesFetchedFromNetwork.get(matchingMovieIndex).setIsFavorite(MOVIE_ATTR_FLAG_TRUE);
                } //couldn't find the favorite movie in list so retain this movie by adding to list
                else {
                    //remove top rated or popular flags from favorite movie as this is not in the list
                    resetSortFlags(favoriteMovie);
                    favoriteMovie.setIsFavorite(MOVIE_ATTR_FLAG_TRUE);
                    moviesFetchedFromNetwork.add(favoriteMovie);
                }
            }
        }
    }

    private static int findMovieIndexByIdInList(int id, List<MovieEntry> movieEntryList) {
        for (MovieEntry movieEntry : movieEntryList) {
            if (movieEntry.getId() == id) {
                return movieEntryList.indexOf(movieEntry);
            }
        }
        return -1;
    }

    public void setIsFavorite(int isFavorite, int movieId, FavoriteMarkedListener listener) {
        if (isFavorite == MOVIE_ATTR_FLAG_TRUE || isFavorite == MOVIE_ATTR_FLAG_FALSE) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                mMovieDao.setIsFavorite(isFavorite, movieId);
                if (listener != null && isFavorite == MOVIE_ATTR_FLAG_TRUE) {
                    listener.onFavoriteMarkedSaved(true);
                }
            });
        }
    }


    public interface FavoriteMarkedListener {
        void onFavoriteMarkedSaved(boolean favoriteMarked);
    }


    //db access functions for movie details

    public LiveData<Resource<MovieEntry>> loadMovieById(int movieId) {
        AppExecutors appExecutors = AppExecutors.getInstance();

        return new NetworkBoundResource<MovieEntry, MovieEntry>(appExecutors) {

            //get results only offline
            //so shouldFetch is false and createCall, saveCallResult are not used

            @Override
            protected void saveCallResult(@NonNull MovieEntry data) {
            }

            @Override
            protected boolean shouldFetch(@Nullable MovieEntry data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<MovieEntry> loadFromDb() {
                return getMovieById(movieId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieEntry>> createCall() {
                return new MediatorLiveData<>();
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<MovieTrailerEntry>>> loadTrailersByMovieId(TrailerQuery query) {

        int movieId = query.getMovieId();
        boolean forceUpdate = query.isForceUpdate();

        AppExecutors appExecutors = AppExecutors.getInstance();


        return new NetworkBoundResource<List<MovieTrailerEntry>, MovieVideoListResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull MovieVideoListResponse responseData) {

                //extract movie list from response object
                List<MovieVideoResponseEntry> videosFetchedFromNetwork = responseData.getResults();

                //if no videos fetched from network even after successful network transaction
                if (videosFetchedFromNetwork != null
                        && videosFetchedFromNetwork.size() == 0) {
                    mTrailerDao.deleteAllByMovieId(movieId);
                    return;
                }

                //filter for non trailers and non youtube items
                //and store results in a MovieTrailerEntry list
                List<MovieTrailerEntry> trailersFromNetwork =
                        filterOutVideos(movieId, videosFetchedFromNetwork);

                //update movie trailers in database
                mTrailerDao.replaceTrailersForMovieId(trailersFromNetwork, movieId);

            }

            @Override
            protected boolean shouldFetch(@Nullable List<MovieTrailerEntry> data) {
                if (data == null || data.size() == 0 || forceUpdate) {
                    return true;
                } else {
                    return !isFresh(data.get(0).getUpdatedAt(), NETWORK_UPDATE_THRESHOLD_IN_HOURS);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<MovieTrailerEntry>> loadFromDb() {
                return getTrailersByMovieId(movieId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieVideoListResponse>> createCall() {
                return mTmDbService.getMovieTrailers(movieId, TMDB_API_KEY);
            }

        }.asLiveData();

    }

    private List<MovieTrailerEntry> filterOutVideos(int movieId,
                                                    @NonNull List<MovieVideoResponseEntry> videosFetchedFromNetwork) {

        List<MovieTrailerEntry> filteredVideos = new ArrayList<>();

        for (MovieVideoResponseEntry videoEntry :
                videosFetchedFromNetwork) {
            String site = videoEntry.getSite();
            String type = videoEntry.getType();

            if (TMDB_TRAILER_JSON_SITE_VALUE_YOUTUBE.equals(site)
                    && TMDB_TRAILER_JSON_TYPE_VALUE_TRAILER.equals(type)) {
                //set updatedAt time to current time in millis
                filteredVideos.add(new MovieTrailerEntry(movieId, videoEntry.getId(),
                        videoEntry.getKey(), System.currentTimeMillis()));

            }
        }

        return filteredVideos;
    }

    public LiveData<Resource<List<MovieReviewEntry>>> loadReviewsByMovieId(ReviewQuery query) {

        int movieId = query.getMovieId();
        boolean forceUpdate = query.isForceUpdate();

        AppExecutors appExecutors = AppExecutors.getInstance();


        return new NetworkBoundResource<List<MovieReviewEntry>, MovieReviewListResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull MovieReviewListResponse responseData) {

                //extract movie list from response object
                List<MovieReviewEntry> reviewsFetchedFromNetwork = responseData.getResults();

                //if no reviews fetched from network even after successful network transaction
                if (reviewsFetchedFromNetwork != null
                        && reviewsFetchedFromNetwork.size() == 0) {
                    mReviewDao.deleteAllByMovieId(movieId);
                    return;
                }

                //update movieId and updatedAt flag for all reviews
                adjustReviewAttributes(movieId, reviewsFetchedFromNetwork);

                //update movie trailers in database
                mReviewDao.replaceReviewsForMovieId(reviewsFetchedFromNetwork, movieId);

            }

            @Override
            protected boolean shouldFetch(@Nullable List<MovieReviewEntry> data) {
                if (data == null || data.size() == 0 || forceUpdate) {
                    return true;
                } else {
                    return !isFresh(data.get(0).getUpdatedAt(), NETWORK_UPDATE_THRESHOLD_IN_HOURS);
                }
            }

            @NonNull
            @Override
            protected LiveData<List<MovieReviewEntry>> loadFromDb() {
                return getReviewsByMovieId(movieId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieReviewListResponse>> createCall() {
                return mTmDbService.getMovieReviews(movieId, TMDB_API_KEY);
            }

        }.asLiveData();

    }

    private void adjustReviewAttributes(int movieId,
                                        @NonNull List<MovieReviewEntry> reviewsFetchedFromNetwork) {
        for (MovieReviewEntry reviewEntry :
                reviewsFetchedFromNetwork) {

            //save movie id
            reviewEntry.setMovieId(movieId);

            //save updated at flag
            reviewEntry.setUpdatedAt(System.currentTimeMillis());
        }
    }


}
