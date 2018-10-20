package example.android.com.popularmovies.api.response_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideoListResponse {

    @SerializedName("id")
    private int movieId;
    @SerializedName("results")
    private List<MovieVideoResponseEntry> results;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public List<MovieVideoResponseEntry> getResults() {
        return results;
    }

    public void setResults(List<MovieVideoResponseEntry> results) {
        this.results = results;
    }
}
