package example.android.com.popularmovies.ui.movie_details;


import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.db.AppDatabase;

//public class MovieDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
//
//    private final MovieRepository mRepo;
//    private final int mMovieId;
//
//    public MovieDetailsViewModelFactory(MovieRepository repository, int movieId) {
//        this.mRepo = repository;
//        this.mMovieId = movieId;
//    }
//
//    @Override
//    public <T extends ViewModel> T create(Class<T> modelClass) {
//        //noinspection unchecked
//        return (T) new MovieDetailsViewModel(mRepo, mMovieId);
//    }
//}

public class MovieDetailsViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final MovieRepository mRepo;
    private final int mMovieId;

    public MovieDetailsViewModelFactory(Application application, int movieId) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        mRepo = MovieRepository.getInstance(db);
        this.mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailsViewModel(mRepo, mMovieId);
    }
}