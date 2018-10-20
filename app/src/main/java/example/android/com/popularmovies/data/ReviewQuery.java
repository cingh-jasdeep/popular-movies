package example.android.com.popularmovies.data;

/**
 * query object to query movies from async task loader
 */

public class ReviewQuery {

    private int movieId;
    private boolean forceUpdate;

    public ReviewQuery(int movieId, boolean forceUpdate) {
        this.movieId = movieId;
        this.forceUpdate = forceUpdate;
    }


    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
