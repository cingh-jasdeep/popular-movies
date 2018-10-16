package example.android.com.popularmovies.ui;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import example.android.com.popularmovies.data.MoviesPreferences;
import example.android.com.popularmovies.db.AppDatabase;
import example.android.com.popularmovies.db.MovieEntry;
import example.android.com.popularmovies.data.MovieRepository;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = AndroidViewModel.class.getSimpleName();
    private final MovieRepository mRepo;
    private final MutableLiveData<String> sortOrder = new MutableLiveData<>();

    public final LiveData<List<MovieEntry>> displayMovies =
            Transformations.switchMap(sortOrder, new Function<String, LiveData<List<MovieEntry>>>() {
                @Override
                public LiveData<List<MovieEntry>> apply(String input) {
                    return mRepo.getPreferredMovies(getApplication());
                }
            });


    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        mRepo = MovieRepository.getInstance(db.movieDao());
        updateSortedResults(application);
    }

    public void updateSortedResults(@NonNull Context context) {
        String queryType = MoviesPreferences.getPreferredSortOrderKey(context);
        sortOrder.setValue(queryType);
    }

}
