package example.android.com.popularmovies.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import example.android.com.popularmovies.db.dao.MovieDao;
import example.android.com.popularmovies.db.dao.MovieReviewDao;
import example.android.com.popularmovies.db.dao.MovieTrailerDao;
import example.android.com.popularmovies.db.model.MovieEntry;
import example.android.com.popularmovies.db.model.MovieReviewEntry;
import example.android.com.popularmovies.db.model.MovieTrailerEntry;

import static example.android.com.popularmovies.data.Constant.APP_DATABASE_NAME;

@Database(entities = {MovieEntry.class,
                        MovieTrailerEntry.class,
                        MovieReviewEntry.class},
                        version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = APP_DATABASE_NAME;
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract MovieDao movieDao();
    public abstract MovieTrailerDao movieTrailerDao();
    public abstract MovieReviewDao movieReviewDao();

}
