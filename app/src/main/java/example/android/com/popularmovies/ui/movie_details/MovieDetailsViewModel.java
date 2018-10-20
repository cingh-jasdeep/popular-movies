package example.android.com.popularmovies.ui.movie_details;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import example.android.com.popularmovies.data.MovieQuery;
import example.android.com.popularmovies.data.ReviewQuery;
import example.android.com.popularmovies.data.TrailerQuery;
import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.db.model.MovieReviewEntry;
import example.android.com.popularmovies.db.model.MovieTrailerEntry;
import example.android.com.popularmovies.utilities.Resource;

public class MovieDetailsViewModel extends ViewModel {

    private static final String TAG = MovieDetailsViewModel.class.getSimpleName();

    private LiveData<Resource<MovieEntry>> movie;

    private final MutableLiveData<TrailerQuery> trailerQuery = new MutableLiveData<>();
    private final MutableLiveData<ReviewQuery> reviewQuery = new MutableLiveData<>();


    private LiveData<Resource<List<MovieTrailerEntry>>> trailers =
            Transformations.switchMap(trailerQuery, new Function<TrailerQuery, LiveData<Resource<List<MovieTrailerEntry>>>>() {
                @Override
                public LiveData<Resource<List<MovieTrailerEntry>>> apply(TrailerQuery input) {
                    return mRepo.loadTrailersByMovieId(input);
                }
            });

    private LiveData<Resource<List<MovieReviewEntry>>> reviews =
            Transformations.switchMap(reviewQuery, new Function<ReviewQuery, LiveData<Resource<List<MovieReviewEntry>>>>() {
                @Override
                public LiveData<Resource<List<MovieReviewEntry>>> apply(ReviewQuery input) {
                    return mRepo.loadReviewsByMovieId(input);
                }
            });

    private MovieRepository mRepo;
    private int mMovieId;

    LiveData<Resource<MovieEntry>> loadMovieObject() { return movie;}
    LiveData<Resource<List<MovieTrailerEntry>>> getMovieTrailersResource() { return trailers;}
    LiveData<Resource<List<MovieReviewEntry>>> getMovieReviewsResource() { return reviews;}



    MovieDetailsViewModel(MovieRepository repository, int movieId) {
        mRepo = repository;
        mMovieId = movieId;

        movie = mRepo.loadMovieById(mMovieId);
        updateTrailerQuery(mMovieId, false);
        updateReviewQuery(mMovieId, false);

    }

    void updateTrailerQuery(int movieId, boolean forceRefresh) {
        trailerQuery.setValue(new TrailerQuery(movieId,forceRefresh));
    }

    void updateReviewQuery(int movieId, boolean forceRefresh) {
        reviewQuery.setValue(new ReviewQuery(movieId,forceRefresh));
    }

    void setIsFavorite(int isFavorite, MovieRepository.FavoriteMarkedListener listener) {
        mRepo.setIsFavorite(isFavorite, mMovieId, listener);
    }

}
