package example.android.com.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import static example.android.com.popularmovies.data.Constant.TMDB_POSTER_BASE_URL;
import static example.android.com.popularmovies.data.Constant.TMDB_POSTER_SIZE_W185;

public class PosterHelper {

    private static final String TAG = PosterHelper.class.getSimpleName();


    /* Recommended size for phones is w185
     * see https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true
     */
    private static final String POSTER_SIZE_DEFAULT = TMDB_POSTER_SIZE_W185;

    public static String buildPosterUrl(String posterPath) {
        Uri builtUri = Uri.parse(TMDB_POSTER_BASE_URL).buildUpon()
                .appendPath(POSTER_SIZE_DEFAULT)
                .appendEncodedPath(posterPath)
                .build();

        try {
            URL url;
            url = new URL(builtUri.toString());
            Log.v(TAG, "Built Poster URI " + url);
            return url.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
