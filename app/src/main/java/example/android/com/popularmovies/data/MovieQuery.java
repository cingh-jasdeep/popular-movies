package example.android.com.popularmovies.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * query object to query movies from asynctask
 * for type see {@link MoviesPreferences}
 */

public class MovieQuery implements Parcelable {
    public int type;
    public int page; //page number

    public MovieQuery(int type, int page) {
        this.type = type;
        this.page = page;
    }

    protected MovieQuery(Parcel in) {
        type = in.readInt();
        page = in.readInt();
    }

    public static final Creator<MovieQuery> CREATOR = new Creator<MovieQuery>() {
        @Override
        public MovieQuery createFromParcel(Parcel in) {
            return new MovieQuery(in);
        }

        @Override
        public MovieQuery[] newArray(int size) {
            return new MovieQuery[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(page);
    }
}
