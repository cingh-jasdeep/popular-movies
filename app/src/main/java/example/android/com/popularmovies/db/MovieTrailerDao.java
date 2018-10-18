package example.android.com.popularmovies.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

import example.android.com.popularmovies.model.MovieEntry;
import example.android.com.popularmovies.model.MovieTrailerEntry;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;

@Dao
public abstract class MovieTrailerDao {


    @Query("SELECT * FROM movie_trailer WHERE id = :movieId")
    public abstract LiveData<List<MovieTrailerEntry>> loadTrailersByMovieId(int movieId);

    @Insert(onConflict = REPLACE)
    public abstract void insertAll(List<MovieTrailerEntry> trailerEntries);

    @Query("DELETE FROM movie_trailer WHERE id = :movieId")
    public abstract void deleteAll(int movieId);

    @Transaction
    public void replaceTrailersForMovieId(List<MovieTrailerEntry> trailerEntries, int movieId) {
        deleteAll(movieId);
        insertAll(trailerEntries);
    }

}
