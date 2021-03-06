package example.android.com.popularmovies.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import example.android.com.popularmovies.db.model.MovieEntry;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_TRUE;

@Dao
public abstract class MovieDao {


    @Query("SELECT * FROM movie WHERE id = :movieId")
    public abstract LiveData<MovieEntry> loadMovieById(int movieId);

    @Query("SELECT * FROM movie WHERE isTopRated = " + MOVIE_ATTR_FLAG_TRUE
            + " ORDER BY vote_average DESC")
    public abstract LiveData<List<MovieEntry>> getTopRatedMovies();


    @Query("SELECT * FROM movie WHERE isPopular = " + MOVIE_ATTR_FLAG_TRUE
            + " ORDER BY popularity_rating DESC")
    public abstract LiveData<List<MovieEntry>> getPopularMovies();

    @Query("SELECT * FROM movie WHERE isFavorite = " + MOVIE_ATTR_FLAG_TRUE)
    public abstract LiveData<List<MovieEntry>> getFavoriteMovies();

    @Query("SELECT * FROM movie WHERE isFavorite = " + MOVIE_ATTR_FLAG_TRUE)
    public abstract List<MovieEntry> getFavoriteMoviesOneShot();

    @Query("SELECT * FROM movie WHERE isFavorite = " + MOVIE_ATTR_FLAG_TRUE
            +" AND isTopRated = "+ MOVIE_ATTR_FLAG_TRUE)
    public abstract List<MovieEntry> getFavoriteTopRatedMoviesOneShot();

    @Query("SELECT * FROM movie WHERE isFavorite = " + MOVIE_ATTR_FLAG_TRUE
            +" AND isPopular = "+ MOVIE_ATTR_FLAG_TRUE)
    public abstract List<MovieEntry> getFavoritePopularMoviesOneShot();


    @Query("DELETE FROM movie ")
    public abstract void deleteAll();

    @Query("DELETE FROM movie WHERE isTopRated = " + MOVIE_ATTR_FLAG_TRUE)
    public abstract void deleteAllTopRatedMovies();

    @Query("DELETE FROM movie WHERE isTopRated = " + MOVIE_ATTR_FLAG_TRUE
            +" AND isFavorite != "+ MOVIE_ATTR_FLAG_TRUE)
    public abstract void deleteAllTopRatedMoviesExceptFavorites();

    @Query("DELETE FROM movie WHERE isPopular = " + MOVIE_ATTR_FLAG_TRUE)
    public abstract void deleteAllPopularMovies();

    @Query("DELETE FROM movie WHERE isPopular = " + MOVIE_ATTR_FLAG_TRUE
                +" AND isFavorite != "+ MOVIE_ATTR_FLAG_TRUE)
    public abstract void deleteAllPopularMoviesExceptFavorites();

    @Query("DELETE FROM movie WHERE isFavorite = " + MOVIE_ATTR_FLAG_TRUE)
    public abstract void deleteAllFavoriteMovies();

    @Query("UPDATE movie SET isFavorite = :favoriteValue WHERE id = :movieId")
    public abstract void setIsFavorite(int favoriteValue, int movieId);


    @Insert(onConflict = REPLACE)
    public abstract void insertAll(List<MovieEntry> movieEntries);

    @Transaction
    public void replaceAll(List<MovieEntry> movieEntries) {
        deleteAll();
        insertAll(movieEntries);
    }

    @Transaction
    public void replaceAllTopRatedMovies(List<MovieEntry> movieEntries) {
        deleteAllTopRatedMovies();
        insertAll(movieEntries);
    }

    @Transaction
    public void replaceAllPopularMovies(List<MovieEntry> movieEntries) {
        deleteAllPopularMovies();
        insertAll(movieEntries);
    }

    @Transaction
    public void replaceAllFavoriteMovies(List<MovieEntry> movieEntries) {
        deleteAllFavoriteMovies();
        insertAll(movieEntries);
    }
}
