package example.android.com.popularmovies.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import example.android.com.popularmovies.utilities.LiveDataCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static example.android.com.popularmovies.data.Constant.TMDB_DATE_FORMAT;
import static example.android.com.popularmovies.data.Constant.TMDB_MOVIE_BASE_URL;

public class TMDbServiceClient {
    //Constant used for Logs
    private static final String LOG_TAG = TMDbServiceClient.class.getSimpleName();

    /**
     * Creates the Retrofit Service for Github API
     *
     * @return The {@link TMDbService} instance
     */
    public static TMDbService create() {

        //Building the HTTPClient with the stetho
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat(TMDB_DATE_FORMAT)
                .create();

        //Returning the Retrofit service for the BASE_URL
        return new Retrofit.Builder()
                .baseUrl(TMDB_MOVIE_BASE_URL)
                //Using the HTTPClient setup
                .client(httpClient)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                //GSON converter to convert the JSON elements to a POJO
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                //Creating the service for the defined API Interface
                .create(TMDbService.class);
    }
}
