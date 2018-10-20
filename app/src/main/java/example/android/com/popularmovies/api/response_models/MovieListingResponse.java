package example.android.com.popularmovies.api.response_models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import example.android.com.popularmovies.db.model.MovieEntry;

public class MovieListingResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("results")
    private List<MovieEntry> results;

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

    public List<MovieEntry> getResults() {
        return results;
    }

    public void setResults(List<MovieEntry> results) {
        this.results = results;
    }
}
