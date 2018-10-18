package example.android.com.popularmovies.ui.movie_details;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import example.android.com.popularmovies.data.repository.MovieRepository;

public class MovieDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRepository mRepo;
    private final int mMovieId;

    public MovieDetailsViewModelFactory(MovieRepository repository, int movieId) {
        this.mRepo = repository;
        this.mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailsViewModel(mRepo, mMovieId);
    }
}
