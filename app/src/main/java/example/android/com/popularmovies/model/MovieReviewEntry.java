package example.android.com.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movie_review",
        foreignKeys = @ForeignKey(entity = MovieEntry.class,
        parentColumns = "id",
        childColumns = "movie_id",
        onDelete = ForeignKey.CASCADE),
        indices = {@Index("movie_id")})

public class MovieReviewEntry {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "movie_id")
    private int movieId;

    private String author;

    private String content;

    private String url;

    public MovieReviewEntry(int id, int movieId, String author, String content, String url) {
        this.id = id;
        this.movieId = movieId;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
