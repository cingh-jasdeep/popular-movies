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

}
