package example.android.com.popularmovies.api;

import android.arch.lifecycle.LiveData;

import example.android.com.popularmovies.api.response_models.ApiResponse;
import example.android.com.popularmovies.api.response_models.MovieListingResponse;
import example.android.com.popularmovies.api.response_models.MovieReviewListResponse;
import example.android.com.popularmovies.api.response_models.MovieVideoListResponse;
import example.android.com.popularmovies.db.model.MovieEntry;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static example.android.com.popularmovies.data.Constant.TMDB_PARAM_API_KEY;
import static example.android.com.popularmovies.data.Constant.TMDB_PATH_PARAM_REVIEWS;
import static example.android.com.popularmovies.data.Constant.TMDB_PATH_PARAM_SORT_ORDER_KEY;
import static example.android.com.popularmovies.data.Constant.TMDB_PATH_PARAM_VIDEOS;

/**
 * TMDb API end points
 */
public interface TMDbService {

    @GET("{" + TMDB_PATH_PARAM_SORT_ORDER_KEY + "}")
    LiveData<ApiResponse<MovieListingResponse>> getMovies(@Path(TMDB_PATH_PARAM_SORT_ORDER_KEY) String sortOrder,
                                                          @Query(TMDB_PARAM_API_KEY) String apiKey);

    @GET("{movieId}/"+TMDB_PATH_PARAM_VIDEOS)
    LiveData<ApiResponse<MovieVideoListResponse>> getMovieTrailers(@Path("movieId") int movieId,
                                                                   @Query(TMDB_PARAM_API_KEY) String apiKey);

    @GET("{movieId}/"+TMDB_PATH_PARAM_REVIEWS)
    LiveData<ApiResponse<MovieReviewListResponse>> getMovieReviews(@Path("movieId") int movieId,
                                                                   @Query(TMDB_PARAM_API_KEY) String apiKey);

    @GET("{movieId}")
    LiveData<ApiResponse<MovieEntry>> getMovieDetails(@Path("movieId") int movieId,
                                                      @Query(TMDB_PARAM_API_KEY) String apiKey);
}
