package example.android.com.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import example.android.com.popularmovies.R;

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

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Poster URI " + url);

        return url.toString();
    }

    /**
     * Used to calculate columns for the poster grid
     * Reference link :
     * https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     *
     * @param context Context object used to get screen size and poster_width dimensions
     * @return Calculated number of columns for the poster grid
     */

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        float posterWidth = context.getResources().getDimension(R.dimen.poster_width);
        int noOfColumns = (int) (dpWidth / posterWidth);
        return noOfColumns;
    }

}
