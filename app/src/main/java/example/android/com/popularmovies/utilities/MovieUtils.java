package example.android.com.popularmovies.utilities;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import example.android.com.popularmovies.R;

public class MovieUtils {

    /**
     * Used to parse jsonDate to Date object
     *
     * @param jsonDate String with date in yyyy-mm-dd format
     *
     * @return Date object
     */
    public static Date getDate(Context context, String jsonDate) {

        /* source: https://www.javatpoint.com/java-string-to-date */
        SimpleDateFormat formatter = new SimpleDateFormat(
                context.getString(R.string.TMDB_DATE_FORMAT), Locale.getDefault());

        try {
            return formatter.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    //reference: http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
    public static String getFormattedDateString (Context context, Date date) {
        if(date!=null) {
            String pattern = context.getString(R.string.display_format_release_date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            return simpleDateFormat.format(date).trim();
        } else {
            return null;
        }
    }

    /**
     * Format voting average from Json for display
     *
     * @param ratingFromJson String with rating in single float eg. 7.8
     *
     * @return String in display format eg. 7.8/10
     */
    public static String getFormattedVoteString(Context context, String ratingFromJson) {
        if(ratingFromJson!=null && !ratingFromJson.equals("")) {
            return String.format(context.getString(
                    R.string.display_format_voting_average),
                    ratingFromJson);
        } else {
            return null;
        }
    }
}
