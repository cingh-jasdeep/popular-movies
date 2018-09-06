package example.android.com.popularmovies.utilities;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import example.android.com.popularmovies.R;

public class MovieDateUtils {

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
                context.getString(R.string.TMDB_DATE_FORMAT));

        try {
            return formatter.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
