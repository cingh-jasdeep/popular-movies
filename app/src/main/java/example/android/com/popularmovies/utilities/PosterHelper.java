package example.android.com.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class PosterHelper {

    private static final String TAG = PosterHelper.class.getSimpleName();

    private static final String POSTER_BASE_URL =
            "http://image.tmdb.org/t/p/";

    /* Available poster sizes */
    private static final String POSTER_SIZE_W92 = "w92";
    private static final String POSTER_SIZE_W154 = "w154";
    private static final String POSTER_SIZE_W185 = "w185";
    private static final String POSTER_SIZE_W342 = "w342";
    private static final String POSTER_SIZE_W500 = "w500";
    private static final String POSTER_SIZE_W780 = "w780";
    private static final String POSTER_SIZE_ORIGINAL = "original";


    /* Recommended size for phones is w185
     * see https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true
     */
    private static final String POSTER_SIZE_DEFAULT = POSTER_SIZE_W185;

    public static String buildPosterUrl(String posterPath) {
        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
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
