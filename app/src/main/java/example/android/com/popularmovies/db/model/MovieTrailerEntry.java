package example.android.com.popularmovies.db.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "movie_trailer",
        foreignKeys = @ForeignKey(entity = MovieEntry.class,
        parentColumns = "id",
        childColumns = "movie_id",
        onDelete = ForeignKey.CASCADE),
        indices = {@Index("movie_id")})

public class MovieTrailerEntry {

    @ColumnInfo(name = "movie_id")
    private int movieId;

    @NonNull
    @PrimaryKey
    private String id;

    @ColumnInfo(name = "url_key")
    private String ytUrlKey;

    private long updatedAt;

    public MovieTrailerEntry(int movieId, @NonNull String id, String ytUrlKey, long updatedAt) {
        this.movieId = movieId;
        this.id = id;
        this.ytUrlKey = ytUrlKey;
        this.updatedAt = updatedAt;
    }

    //getters and setters

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getYtUrlKey() {
        return ytUrlKey;
    }

    public void setYtUrlKey(String ytUrlKey) {
        this.ytUrlKey = ytUrlKey;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }


}
