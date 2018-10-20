package example.android.com.popularmovies.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import example.android.com.popularmovies.db.model.MovieTrailerEntry;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class MovieTrailerDao {


    @Query("SELECT * FROM movie_trailer WHERE movie_id = :movieId")
    public abstract LiveData<List<MovieTrailerEntry>> loadTrailersByMovieId(int movieId);

    @Insert(onConflict = REPLACE)
    public abstract void insertAll(List<MovieTrailerEntry> trailerEntries);

    @Query("DELETE FROM movie_trailer WHERE movie_id = :movieId")
    public abstract void deleteAllByMovieId(int movieId);

    @Transaction
    public void replaceTrailersForMovieId(List<MovieTrailerEntry> trailerEntries, int movieId) {
        deleteAllByMovieId(movieId);
        insertAll(trailerEntries);
    }

}
