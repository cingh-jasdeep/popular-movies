package example.android.com.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import example.android.com.popularmovies.R;

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

    /**
     * https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     * @param context for the current activity
     * @return number of columns for the poster
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float dpPosterWidth = context.getResources().getDimensionPixelSize(R.dimen.poster_width);
        return Math.max(1, (int) (dpWidth / dpPosterWidth));
    }

}
