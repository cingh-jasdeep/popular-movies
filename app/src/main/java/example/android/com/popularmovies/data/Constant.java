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

    public static final String TMDB_MOVIE_BASE_URL =
            "https://api.themoviedb.org/3/movie/";


    public static final String TMDB_POPULAR_MOVIES_URL_PATH =
            "popular";

    public static final String TMDB_TOP_RATED_MOVIES_URL_PATH =
            "top_rated";
    /*
     * Youtube related urls
     */

    public static final String YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";

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

    public final static String TMDB_PATH_PARAM_VIDEOS = "videos";
    public final static String TMDB_PATH_PARAM_REVIEWS = "reviews";

    public final static String TMDB_PATH_PARAM_SORT_ORDER_KEY = "sort_order";


    /*
     * themoviedb.org API other network related constants
     */
    public static final int TMDB_DEFAULT_PAGE = 1;
    public static final String TMDB_DATE_FORMAT = "yyyy-MM-dd";


    /*
     * flags to store movie tag
     */
    public final static int MOVIE_ATTR_FLAG_TRUE = 1;
    public final static int MOVIE_ATTR_FLAG_FALSE = 0;
    public final static int MOVIE_RUNTIME_DEFAULT = -1;

    /*
     * constants used by database
     */
    public final static String APP_DATABASE_NAME = "pop_movies";

    /*
     * constants used by network sync
     */
    public final static long NETWORK_UPDATE_THRESHOLD_IN_HOURS = 6;

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

    /* MovieEntry information.
     *  Note: Each movie's info is an element of the "results" array */
    public static final String TMDB_JSON_RESULTS = "results";


    /* Properties to read from one movie's JSON object */

    /* MovieEntry id */
    public static final String TMDB_JSON_ID = "id";

    /* MovieEntry title */
    public static final String TMDB_JSON_TITLE = "title";

    /* MovieEntry release date
     *  Note: return format is yyyy-mm-dd */
    public static final String TMDB_JSON_RELEASE_DATE = "release_date";

    /* MovieEntry poster url
     *  Note: will return partial url, for eg. "/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg" */
    public static final String TMDB_JSON_POSTER_PATH = "poster_path";

    /* MovieEntry vote average
     *  Note: will return vote average out of 10, for eg. "9.3" */
    public static final String TMDB_JSON_VOTE_AVG = "vote_average";

    /* MovieEntry plot synopsis */
    public static final String TMDB_JSON_PLOT_SYNOPSIS = "overview";

    /* MovieEntry popularity */
    public static final String TMDB_JSON_POPULARITY_RATING = "popularity";

    public static final String TMDB_TRAILER_JSON_SITE = "site";

    public static final String TMDB_TRAILER_JSON_SITE_VALUE_YOUTUBE = "YouTube";

    public static final String TMDB_TRAILER_JSON_TYPE = "type";

    public static final String TMDB_TRAILER_JSON_TYPE_VALUE_TRAILER = "Trailer";

    public static final String TMDB_TRAILER_JSON_KEY = "key";

    public static final String TMDB_REVIEW_JSON_AUTHOR = "author";

    public static final String TMDB_REVIEW_JSON_CONTENT = "content";

    public static final String TMDB_REVIEW_JSON_URL = "url";



    /* Constants for moviedetail activity */

    // Extra for the movie ID to be received in the intent
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    // Extra for the movie ID to be received after rotation
    public static final String INSTANCE_MOVIE_ID = "instanceTaskId";

    public static final int DEFAULT_MOVIE_ID = -1;

}
