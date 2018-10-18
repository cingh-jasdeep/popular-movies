package example.android.com.popularmovies.sync;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.db.AppDatabase;
import example.android.com.popularmovies.model.MovieEntry;
import example.android.com.popularmovies.api.NetworkUtils;
import example.android.com.popularmovies.api.TheMovieDbJsonUtils;
import example.android.com.popularmovies.model.MovieTrailerEntry;

import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;
import static example.android.com.popularmovies.data.Constant.TMDB_DEFAULT_PAGE;

/**
 * This class contains all background tasks for syncing movies in db to the server
 */

public class MoviesSyncTasks {

    public static final String ACTION_UPDATE_ALL_MOVIES = "action-update-all-movies";
    public static final String ACTION_UPDATE_MOVIE_TRAILERS = "action-update-movie-trailers";
    public static final String ACTION_UPDATE_MOVIE_REVIEWS = "action-update-movie-reviews";


    public static void executeTask(Context context, String action) {
        if(action.equals(ACTION_UPDATE_ALL_MOVIES)) {
            updateMoviesFromNetwork(context);
        } else if (action.equals(ACTION_UPDATE_MOVIE_TRAILERS)) {
            updateMovieTrailersFromNetwork(context);
        } else if (action.equals(ACTION_UPDATE_MOVIE_REVIEWS)) {
            updateMovieReviewsFromNetwork(context);
        }
    }


    /**
     * Fetches latest movies from api and update db accordingly
     * @param context need to access various data
     */
    private static void updateMoviesFromNetwork(Context context) {
        String queryType = MoviesPreferences.getPreferredSortOrder(context);

        URL moviesRequestUrl = NetworkUtils.buildUrl(context, queryType, TMDB_DEFAULT_PAGE);

        try {

            //check for favorite movies and keep them in separate list before updating data from website
            AppDatabase dB = AppDatabase.getInstance(context);
            MovieRepository repo = MovieRepository.getInstance(dB);

            LiveData<List<MovieEntry>> favoriteMoviesLiveData = repo.getFavoriteMovies();

            List<MovieEntry> favoriteMovies = favoriteMoviesLiveData.getValue();

            if ( moviesRequestUrl == null ) {
                if(favoriteMovies == null || favoriteMovies.isEmpty()) {
                    repo.replaceAll(new ArrayList<MovieEntry>());
                    return;
                } else {
                    repo.replaceAll(favoriteMovies);
                    return;
                }
            }

            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(moviesRequestUrl);
            MovieEntry[] updatedMoviesFromInternetArray = TheMovieDbJsonUtils
                    .getMovieDataFromJson(context, jsonMovieResponse,
                            queryType);

            //check if movie list from internet is null
            if(updatedMoviesFromInternetArray == null
                    || updatedMoviesFromInternetArray.length == 0){ return; }

            List<MovieEntry> updatedMoviesFromInternetList = new ArrayList<>
                    (Arrays.asList(updatedMoviesFromInternetArray));

            if( favoriteMovies != null ) {
                if (favoriteMovies.size() != 0 ) {
                    for (MovieEntry favoriteMovie :
                            favoriteMovies) {

                        MovieEntry matchingMovie = findMovieByIdInList(favoriteMovie.getId(),
                                updatedMoviesFromInternetList);

                        if (matchingMovie != null) {
                            int matchingIndex = updatedMoviesFromInternetList.indexOf(matchingMovie);
                            if (matchingIndex != -1) {
                                updatedMoviesFromInternetList.get(matchingIndex).setIsFavorite(MOVIE_ATTR_FLAG_TRUE);
                            }
                        } //couldn't find the favorite movie in list so retain this movie by adding to list
                        else {
                            updatedMoviesFromInternetList.add(favoriteMovie);
                        }
                    }
                }
            }


            //done processing favorite movies
            //update movies in database
            repo.replaceAll(updatedMoviesFromInternetList);
            MoviesPreferences.saveLastNetworkUpdateTime(context, System.currentTimeMillis());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static MovieEntry findMovieByIdInList(int id, List<MovieEntry> movieEntryList) {
        for(MovieEntry movieEntry : movieEntryList) {
            if(movieEntry.getId() == id) {
                return movieEntry;
            }
        }
        return null;
    }

    private static void updateMovieTrailersFromNetwork(Context context) {
        int movieId = MoviesPreferences.getCurrentMovieId(context);

        URL trailersRequestUrl = NetworkUtils.buildTrailersUrl(context, movieId);


        try {

            //check for favorite movies and keep them in separate list before updating data from website
            AppDatabase dB = AppDatabase.getInstance(context);
            MovieRepository repo = MovieRepository.getInstance(dB);

            String jsonResponse = NetworkUtils
                    .getResponseFromHttpUrl(trailersRequestUrl);
            List<MovieTrailerEntry> movieTrailerDataFromInternetList = TheMovieDbJsonUtils
                    .getMovieTrailersDataFromJson(context, jsonResponse);

            //check if movie list from internet is null
            if(movieTrailerDataFromInternetList == null
                    || movieTrailerDataFromInternetList.size() == 0){ return; }

            //update movie trailer data in database
            repo.insertTrailersData(movieTrailerDataFromInternetList, movieId);

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private static void updateMovieReviewsFromNetwork(Context context) {

    }
}
