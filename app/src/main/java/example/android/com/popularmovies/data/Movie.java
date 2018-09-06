package example.android.com.popularmovies.data;

import java.util.Date;

public class Movie {
    private String movieTitle;
    private Date releaseDate; //parse in date object
    private String moviePosterUrl; //full url of movie poster
    private String voteAverage;
    private String plotSynopsis;

    public Movie(String movieTitle, Date releaseDate, String moviePosterUrl,
                 String voteAverage, String plotSynopsis) {
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.moviePosterUrl = moviePosterUrl;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }
}
