package example.android.com.popularmovies.api.response_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.db.model.MovieReviewEntry;

public class MovieReviewListResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("id")
    private int movieId;

    @SerializedName("results")
    private List<MovieReviewEntry> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public List<MovieReviewEntry> getResults() {
        return results;
    }

    public void setResults(List<MovieReviewEntry> results) {
        this.results = results;
    }
}
