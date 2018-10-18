package example.android.com.popularmovies.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import example.android.com.popularmovies.model.MovieReviewEntry;
import example.android.com.popularmovies.model.MovieTrailerEntry;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class MovieReviewDao {


    @Query("SELECT * FROM movie_review WHERE id = :movieId")
    public abstract LiveData<MovieReviewEntry> loadTrailersByMovieId(int movieId);

    @Insert(onConflict = REPLACE)
    public abstract void insertAll(List<MovieReviewEntry> reviewEntries);

    @Query("DELETE FROM movie_review WHERE id = :movieId")
    public abstract void deleteAll(int movieId);

    @Transaction
    public void replaceAll(List<MovieReviewEntry> reviewEntries, int movieId) {
        deleteAll(movieId);
        insertAll(reviewEntries);
    }

}
