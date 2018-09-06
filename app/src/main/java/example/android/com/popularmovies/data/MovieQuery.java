package example.android.com.popularmovies.data;

/**
 * query object to query movies from asynctask
 * for type see {@link MoviesPreferences}
 */

public class MovieQuery {
    public int type;
    public int page; //page number

    public MovieQuery(int type, int page) {
        this.type = type;
        this.page = page;
    }
}
