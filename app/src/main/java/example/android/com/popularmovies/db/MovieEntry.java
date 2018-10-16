package example.android.com.popularmovies.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_FALSE;

/**
 * Implement movie object as Parcelable for faster performance
 * see <a href="Parcelable">https://github.com/udacity/android-custom-arrayadapter/tree/parcelable</a>
 */
@Entity(tableName = "movie")
public class MovieEntry implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private int movieId;

    @ColumnInfo(name = "movie_title")
    private String movieTitle;

    @ColumnInfo(name = "release_date")
    private Date releaseDate; //parse in date object

    @ColumnInfo(name = "movie_poster_url")
    private String moviePosterUrl; //full url of movie poster

    @ColumnInfo(name = "vote_average")
    private double voteAverage;

    @ColumnInfo(name = "plot_synopsis")
    private String plotSynopsis;

    @ColumnInfo(name = "popularity_rating")
    private double popularityRating;

    private int isFavorite;

    private int isPopular;

    private int isTopRated;

    public MovieEntry(int movieId, String movieTitle, Date releaseDate, String moviePosterUrl,
                      double voteAverage, String plotSynopsis, double popularityRating,
                      int isFavorite, int isPopular, int isTopRated) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.moviePosterUrl = moviePosterUrl;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        this.popularityRating = popularityRating;

        this.isFavorite = isFavorite;
        this.isPopular = isPopular;
        this.isTopRated = isTopRated;
    }

    @Ignore
    public MovieEntry(int movieId, String movieTitle, Date releaseDate, String moviePosterUrl,
                      double voteAverage, String plotSynopsis, double popularityRating) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.moviePosterUrl = moviePosterUrl;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        this.popularityRating = popularityRating;

        //default values for sort
        this.isFavorite = MOVIE_ATTR_FLAG_FALSE;
        this.isPopular = MOVIE_ATTR_FLAG_FALSE;
        this.isTopRated = MOVIE_ATTR_FLAG_FALSE;
    }

    public String toString() {
        return movieId + "--" + movieTitle + "--" + getSimpleDateString(releaseDate)
            + "--" + moviePosterUrl + "--" + voteAverage + "--" + plotSynopsis + "--" + popularityRating;
    }

    public MovieEntry(Parcel in) {
        movieId = in.readInt();
        movieTitle = in.readString();
        releaseDate = new Date(in.readLong());
        moviePosterUrl = in.readString();
        voteAverage = in.readDouble();
        plotSynopsis = in.readString();
        popularityRating = in.readDouble();

        isFavorite = in.readInt();
        isPopular = in.readInt();
        isTopRated = in.readInt();
    }

    // online reference: https://stackoverflow.com/questions/21017404/reading-and-writing-java-util-date-from-parcelable-class
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieTitle);
        dest.writeLong(releaseDate.getTime());
        dest.writeString(moviePosterUrl);
        dest.writeDouble(voteAverage);
        dest.writeString(plotSynopsis);
        dest.writeDouble(popularityRating);

        dest.writeInt(isFavorite);
        dest.writeInt(isPopular);
        dest.writeInt(isTopRated);
    }

    public static final Creator<MovieEntry> CREATOR = new Creator<MovieEntry>() {
        @Override
        public MovieEntry createFromParcel(Parcel in) {
            return new MovieEntry(in);
        }

        @Override
        public MovieEntry[] newArray(int size) {
            return new MovieEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    //getters and setters
    public int getMovieId() { return movieId; }

    public void setMovieId(int movieId) { this.movieId = movieId; }

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

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }


    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }


    public int getIsPopular() {
        return isPopular;
    }

    public void setIsPopular(int isPopular) {
        this.isPopular = isPopular;
    }

    public int getIsTopRated() {
        return isTopRated;
    }

    public void setIsTopRated(int isTopRated) {
        this.isTopRated = isTopRated;
    }


    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public double getPopularityRating() {
        return popularityRating;
    }

    public void setPopularityRating(double popularityRating) {
        this.popularityRating = popularityRating;
    }


    //reference: http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
    //used to give a string representation in toString function
    private String getSimpleDateString (Date date) {
        if(date!=null) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            return simpleDateFormat.format(date);
        } else {
            return null;
        }
    }
}
