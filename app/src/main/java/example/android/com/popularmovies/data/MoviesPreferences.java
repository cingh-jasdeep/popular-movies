package example.android.com.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import example.android.com.popularmovies.R;

import static example.android.com.popularmovies.data.Constant.DEFAULT_MOVIE_ID;

public class MoviesPreferences {

    /**
     * Returns the sort order currently set in Preferences. The default sort this method
     * will return is "popular", which sort movies by Most Popular
     *
     * @param context Context used to get the SharedPreferences
     * @return Sort Order The current user has set in SharedPreferences.
     */
    public static String getPreferredSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSort = context.getString(R.string.pref_sort_key);
        String defaultSort = context.getString(R.string.pref_sort_default);
        return sharedPreferences.getString(keyForSort, defaultSort);
    }

    public static String getPreferredSortOrderLabel(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSort = context.getString(R.string.pref_sort_key);
        String defaultSort = context.getString(R.string.pref_sort_default);
        String preferredSort = sharedPreferences.getString(keyForSort, defaultSort);

        List<String> sortLabels = Arrays.asList(context.getResources().getStringArray(R.array.pref_sort_options));
        List<String> sortValues = Arrays.asList(context.getResources().getStringArray(R.array.pref_sort_values));

        int prefIndex = sortValues.indexOf(preferredSort);
        if (prefIndex >= 0) {
            return sortLabels.get(prefIndex);
        } else {
            return null;
        }
    }

    public static boolean isFavoriteSortOrderLabel(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSort = context.getString(R.string.pref_sort_key);
        String defaultSort = context.getString(R.string.pref_sort_default);
        String favoriteSort = context.getString(R.string.pref_sort_favorite);

        return ( sharedPreferences.getString(keyForSort, defaultSort).equals(favoriteSort) );
    }

    /**
     * Saves the time that a network update is done. This will be used to get the ellapsed time
     * since a netowprk update was done.
     *
     * @param context Used to access SharedPreferences
     * @param timeOfUpdate Time of last notification to save (in UNIX time)
     */
    public static void saveLastNetworkUpdateTime(Context context, long timeOfUpdate) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNetworkUpdateKey = context.getString(R.string.pref_last_network_update_key);
        editor.putLong(lastNetworkUpdateKey, timeOfUpdate);
        editor.apply();
    }

    /**
     * Returns the last time that a network update was done (in UNIX time)
     *
     * @param context Used to access SharedPreferences
     * @return UNIX time of when the last network update was done
     */
    public static long getLastNetworkUpdateTimeInMillis(Context context) {
        /* Key for accessing the time at which Pop Movies updated movies over network */
        String lastNetworkUpdateKey = context.getString(R.string.pref_last_network_update_key);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        /*
         * Here, we retrieve the time in milliseconds when the last network update was done. If
         * SharedPreferences doesn't have a value for lastNotificationKey, we return 0. The reason
         * we return 0 is because we compare the value returned from this method to the current
         * system time. If the difference between the last update time and the current time
         * is greater than 6 hours, we will do a network update again. When we compare the two
         * values, we subtract the last notification time from the current system time. If the
         * time of the last notification was 0, the difference will always be greater than the
         * number of milliseconds in 6 hours and we will done another network update.
         */
        long lastNetworkUpdateTime = sp.getLong(lastNetworkUpdateKey, 0);

        return lastNetworkUpdateTime;
    }

    /**
     * Returns the elapsed time in milliseconds since the last network update was done. This is used
     * as part of our check to see if we should done another network update when app configuration changes.
     *
     * @param context Used to access SharedPreferences as well as use other utility methods
     * @return Elapsed time in milliseconds since the last notification was shown
     */
    public static long getEllapsedTimeSinceLastNetworkUpdate(Context context) {
        long lastNotificationTimeMillis =
                MoviesPreferences.getLastNetworkUpdateTimeInMillis(context);
        long timeSinceLastNetworkUpdate = System.currentTimeMillis() - lastNotificationTimeMillis;
        return timeSinceLastNetworkUpdate;
    }



    public static void saveCurrentMovieId(Context context, int movieId) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String currentMovieIdKey = context.getString(R.string.pref_curr_movie_id_key);
        editor.putInt(currentMovieIdKey, movieId);
        editor.apply();

    }

    public static int getCurrentMovieId(Context context) {

        /* Key for accessing the time at which Sunshine last displayed a notification */
        String currentMovieIdKey = context.getString(R.string.pref_curr_movie_id_key);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(currentMovieIdKey, DEFAULT_MOVIE_ID);
    }
}
