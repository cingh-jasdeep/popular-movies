package example.android.com.popularmovies.data;

import android.content.Context;

/**
 * query object to query movies from asynctask
 * for type see {@link MoviesPreferences}
 */

public class MovieQuery {
    public Context context;
    public int type;
    public int page; //page number

    public MovieQuery(Context context, int type, int page) {
        this.context = context;
        this.type = type;
        this.page = page;
    }
}
