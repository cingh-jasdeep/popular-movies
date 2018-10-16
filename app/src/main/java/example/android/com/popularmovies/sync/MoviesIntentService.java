package example.android.com.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class MoviesIntentService extends IntentService {
    public MoviesIntentService() {
        super(MoviesIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(action != null && !(action.isEmpty())) {
                MoviesSyncTasks.executeTask(this, action);
            }
        }
    }
}