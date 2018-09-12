package example.android.com.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * query object to query movies from async task loader
 */

public class MovieQuery implements Parcelable {
    public String sort_order; // sort order
    public int page; //page number

    protected MovieQuery(Parcel in) {
        sort_order = in.readString();
        page = in.readInt();
    }

    public MovieQuery(String sort_order, int page) {
        this.sort_order = sort_order;
        this.page = page;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sort_order);
        dest.writeInt(page);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
