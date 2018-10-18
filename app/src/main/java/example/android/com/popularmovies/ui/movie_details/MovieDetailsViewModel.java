package example.android.com.popularmovies.ui.movie_details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.model.MovieEntry;
import example.android.com.popularmovies.model.MovieTrailerEntry;

public class MovieDetailsViewModel extends ViewModel {

    private static final String TAG = MovieDetailsViewModel.class.getSimpleName();

    private LiveData<MovieEntry> movie;
    private LiveData<List<MovieTrailerEntry>> trailers;

    public LiveData<MovieEntry> getMovie() { return movie;}
    public LiveData<List<MovieTrailerEntry>> getMovieTrailers() { return trailers;}


    public MovieDetailsViewModel(MovieRepository repository, int movieId) {
        movie = repository.getMovieById(movieId);
        trailers = repository.loadTrailersByMovieId(movieId);
    }
}
