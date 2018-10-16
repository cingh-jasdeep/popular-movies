package example.android.com.popularmovies.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;

@Dao
public abstract class MovieDao {

    @Query("SELECT * FROM movie WHERE isTopRated = " + MOVIE_ATTR_FLAG_TRUE
            + " ORDER BY vote_average DESC")
    public abstract LiveData<List<MovieEntry>> getTopRatedMovies();

    @Query("SELECT * FROM movie WHERE isPopular = " + MOVIE_ATTR_FLAG_TRUE
            + " ORDER BY popularity_rating DESC")
    public abstract LiveData<List<MovieEntry>> getPopularMovies();

    @Query("SELECT * FROM movie WHERE isFavorite = " + MOVIE_ATTR_FLAG_TRUE)
    public abstract LiveData<List<MovieEntry>> getFavoriteMovies();

    @Query("DELETE FROM movie")
    public abstract void deleteAll();

    @Insert
    public abstract void insertAll(List<MovieEntry> movieEntries);

    @Transaction
    public void replaceAll(List<MovieEntry> movieEntries) {
        deleteAll();
        insertAll(movieEntries);
    }

}
