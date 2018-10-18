package example.android.com.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * query object to query movies from async task loader
 */

public class MovieQuery {
    private String sortOrder; // sort order
    private boolean forceUpdate; //page number

    public MovieQuery(String sortOrder, boolean forceUpdate) {
        this.sortOrder = sortOrder;
        this.forceUpdate = forceUpdate;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
