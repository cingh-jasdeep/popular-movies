package example.android.com.popularmovies.db.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static example.android.com.popularmovies.data.Constant.MOVIE_ATTR_FLAG_FALSE;
import static example.android.com.popularmovies.data.Constant.MOVIE_RUNTIME_DEFAULT;
import static example.android.com.popularmovies.data.Constant.TMDB_DATE_FORMAT;

/**
 * Implement movie object as Parcelable for faster performance
 * see <a href="Parcelable">https://github.com/udacity/android-custom-arrayadapter/tree/parcelable</a>
 */
@Entity(tableName = "movie")

public class MovieEntry implements Parcelable {

    @SerializedName("id")
    @PrimaryKey
    private int id;

    @SerializedName("title")
    @ColumnInfo(name = "movie_title")
    private String movieTitle;

    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    private Date releaseDate; //parse in date object

    @SerializedName("poster_path")
    @ColumnInfo(name = "movie_poster_url")
    private String moviePosterUrl; //full url of movie poster

    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    private double voteAverage;


    @SerializedName("overview")
    @ColumnInfo(name = "plot_synopsis")
    private String plotSynopsis;

    @SerializedName("popularity")
    @ColumnInfo(name = "popularity_rating")
    private double popularityRating;

    private int isFavorite;

    private int isPopular;

    private int isTopRated;

    private long updatedAt;

    private int runtime;

    public MovieEntry(int id, String movieTitle, Date releaseDate, String moviePosterUrl,
                      double voteAverage, String plotSynopsis, double popularityRating,
                      int isFavorite, int isPopular, int isTopRated, long updatedAt, int runtime) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.moviePosterUrl = moviePosterUrl;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
        this.popularityRating = popularityRating;

        this.isFavorite = isFavorite;
        this.isPopular = isPopular;
        this.isTopRated = isTopRated;
        this.updatedAt = updatedAt;
        this.runtime = runtime;
    }

    @Ignore
    public MovieEntry(int id, String movieTitle, Date releaseDate, String moviePosterUrl,
                      double voteAverage, String plotSynopsis, double popularityRating) {
        this.id = id;
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
        this.updatedAt = System.currentTimeMillis();
        this.runtime = MOVIE_RUNTIME_DEFAULT;
    }

    public String toString() {
        return id + "--" + movieTitle + "--" + getSimpleDateString(releaseDate)
            + "--" + moviePosterUrl + "--" + voteAverage + "--" + plotSynopsis + "--" + popularityRating;
    }

    public MovieEntry(Parcel in) {
        id = in.readInt();
        movieTitle = in.readString();
        releaseDate = new Date(in.readLong());
        moviePosterUrl = in.readString();
        voteAverage = in.readDouble();
        plotSynopsis = in.readString();
        popularityRating = in.readDouble();

        isFavorite = in.readInt();
        isPopular = in.readInt();
        isTopRated = in.readInt();
        updatedAt = in.readLong();
        runtime = in.readInt();
    }

    // online reference: https://stackoverflow.com/questions/21017404/reading-and-writing-java-util-date-from-parcelable-class
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(movieTitle);
        dest.writeLong(releaseDate.getTime());
        dest.writeString(moviePosterUrl);
        dest.writeDouble(voteAverage);
        dest.writeString(plotSynopsis);
        dest.writeDouble(popularityRating);

        dest.writeInt(isFavorite);
        dest.writeInt(isPopular);
        dest.writeInt(isTopRated);
        dest.writeLong(updatedAt);
        dest.writeInt(runtime);
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
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

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

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }


    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }


    //reference: http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
    //used to give a string representation in toString function
    private String getSimpleDateString (Date date) {
        if(date!=null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TMDB_DATE_FORMAT, Locale.getDefault());
            return simpleDateFormat.format(date);
        } else {
            return null;
        }
    }
}
