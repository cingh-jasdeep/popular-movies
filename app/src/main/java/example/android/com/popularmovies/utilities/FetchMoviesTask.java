package example.android.com.popularmovies.utilities;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import example.android.com.popularmovies.data.Movie;
import example.android.com.popularmovies.data.MovieQuery;

/**
 * Using a OnUpdateListener interface to perform onpostexecute steps in the calling activity thread
 *
 * see http://smartandroidians.blogspot.com/2015/03/how-to-use-callback-or-interface-pass.html
 */

public class FetchMoviesTask extends AsyncTask<MovieQuery, Void, Movie[]> {

    public interface OnUpdateListener {
        void onUpdate(Movie[] movieData);
    }

    private OnUpdateListener listener;

    public void setUpdateListener(OnUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    protected Movie[] doInBackground(MovieQuery... movieQueries) {
        /* If there's query, there's nothing to look up. */
        if (movieQueries.length == 0) {
            return null;
        }

        int queryType = movieQueries[0].type;
        int queryPage = movieQueries[0].page;

        URL moviesRequestUrl = NetworkUtils.buildUrl(queryType, queryPage);

        try {
            if ( moviesRequestUrl == null ) {
                return null;
            }

            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(moviesRequestUrl);

            return TheMovieDbJsonUtils
                    .getMovieDataFromJson(movieQueries[0].context,
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
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        if (listener != null) {
            listener.onUpdate(movies);
        }
    }
}
