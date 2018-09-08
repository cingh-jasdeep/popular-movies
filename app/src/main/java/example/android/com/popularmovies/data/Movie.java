package example.android.com.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Implement movie object as Parcelable for faster performance
 * see <a href="Parcelable">https://github.com/udacity/android-custom-arrayadapter/tree/parcelable</a>
 */

public class Movie implements Parcelable {
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

    public String toString() {
        return movieTitle + "--" + getSimpleDateString(releaseDate)
            + "--" + moviePosterUrl + "--" + voteAverage + "--" + plotSynopsis;
    }

    public Movie(Parcel in) {
        movieTitle = in.readString();
        releaseDate = new Date(in.readLong());
        moviePosterUrl = in.readString();
        voteAverage = in.readString();
        plotSynopsis = in.readString();
    }

    // online reference: https://stackoverflow.com/questions/21017404/reading-and-writing-java-util-date-from-parcelable-class
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeLong(releaseDate.getTime());
        dest.writeString(moviePosterUrl);
        dest.writeString(voteAverage);
        dest.writeString(plotSynopsis);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
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

    //reference: http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
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
