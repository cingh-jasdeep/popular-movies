package example.android.com.popularmovies.ui.movie_grid;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.util.List;

import example.android.com.popularmovies.data.MovieQuery;
import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.db.AppDatabase;
import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.data.repository.MovieRepository;
import example.android.com.popularmovies.utilities.Resource;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = AndroidViewModel.class.getSimpleName();
    private final MovieRepository mRepo;
//    private final MutableLiveData<String> sortOrder = new MutableLiveData<>();
    private final MutableLiveData<MovieQuery> query = new MutableLiveData<>();


    public final LiveData<Resource<List<MovieEntry>>> displayMovies =
        Transformations.switchMap(query, new Function<MovieQuery, LiveData<Resource<List<MovieEntry>>>>() {
            @Override
            public LiveData<Resource<List<MovieEntry>>> apply(MovieQuery input) {
                if(query.getValue() !=null ) {
                    return mRepo.loadPreferredMovies(getApplication().getApplicationContext(), query.getValue());
                } else { return null;}
            }
        });

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        mRepo = MovieRepository.getInstance(db);

        String sortOrder = MoviesPreferences.getPreferredSortOrder(application.getApplicationContext());
        updateQuery(sortOrder, false);
    }


    public void updateQuery(String sortOrder, boolean forceUpdate) {
        query.setValue(new MovieQuery(sortOrder, forceUpdate));
    }

}
