package example.android.com.popularmovies.data;


import example.android.com.popularmovies.BuildConfig;

public class Constant {

    /*
     * themoviedb.org API endpoints
     */
    public static final String TMDB_POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular";

    public static final String TMDB_TOP_RATED_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/top_rated";

    /*
     * themoviedb.org API key
     *
     * PLEASE USE YOUR OWN API KEY HERE TO MAKE THE APP WORK
     */
    public static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;

    /*
     * themoviedb.org API parameter keys
     */
    public final static String TMDB_PARAM_API_KEY = "api_key";
    public final static String TMDB_PARAM_PAGE = "page";

    /*
     * themoviedb.org API other network related constants
     */
    public static final int TMDB_DEFAULT_PAGE = 1;



    /*
     * themoviedb.org API poster related constants
     *
     */
    public static final String TMDB_POSTER_BASE_URL =
            "http://image.tmdb.org/t/p/";

    /* All Available poster sizes */
    public static final String TMDB_POSTER_SIZE_W92 = "w92";
    public static final String TMDB_POSTER_SIZE_W154 = "w154";
    public static final String TMDB_POSTER_SIZE_W185 = "w185";
    public static final String TMDB_POSTER_SIZE_W342 = "w342";
    public static final String TMDB_POSTER_SIZE_W500 = "w500";
    public static final String TMDB_POSTER_SIZE_W780 = "w780";
    public static final String TMDB_POSTER_SIZE_ORIGINAL = "original";


    /* themoviedb.org API json data keys */

    public static final String TMDB_STATUS_MESSAGE = "status_message";

    /* Movie information.
     *  Note: Each movie's info is an element of the "results" array */
    public static final String TMDB_JSON_RESULTS = "results";


    /* Properties to read from one movie's JSON object */

    /* Movie id */
    public static final String TMDB_JSON_ID = "id";

    /* Movie title */
    public static final String TMDB_JSON_TITLE = "title";

    /* Movie release date
     *  Note: return format is yyyy-mm-dd */
    public static final String TMDB_JSON_RELEASE_DATE = "release_date";

    /* Movie poster url
     *  Note: will return partial url, for eg. "/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg" */
    public static final String TMDB_JSON_POSTER_PATH = "poster_path";

    /* Movie vote average
     *  Note: will return vote average out of 10, for eg. "9.3" */
    public static final String TMDB_JSON_VOTE_AVG = "vote_average";

    /* Movie plot synopsis */
    public static final String TMDB_JSON_PLOT_SYNOPSIS = "overview";

}
