package example.android.com.popularmovies.api;

import android.arch.lifecycle.LiveData;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static example.android.com.popularmovies.data.Constant.TMDB_PARAM_API_KEY;
import static example.android.com.popularmovies.data.Constant.TMDB_PATH_PARAM_SORT_ORDER_KEY;

/**
 * TMDb API end points
 */
public interface TMDbService {

    @GET("{" + TMDB_PATH_PARAM_SORT_ORDER_KEY + "}")
    LiveData<ApiResponse<MovieListingResponse>> getMovies(@Path(TMDB_PATH_PARAM_SORT_ORDER_KEY) String sortOrder,
                                                          @Query(TMDB_PARAM_API_KEY) String apiKey);

}
