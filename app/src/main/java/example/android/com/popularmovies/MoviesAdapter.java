/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * File copied from Sunshine project "S04.03-Solution-AddMapAndSharing"
 * and modified according to needs of this project
 */

package example.android.com.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.util.Arrays;
import java.util.List;

import example.android.com.popularmovies.data.Movie;

/**
 * {@link MoviesAdapter} exposes a list of movies to a
 * GridView
 */

public class MoviesAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    private List<Movie> mMoviesData;


    public MoviesAdapter(Activity context, List<Movie> movieList) {
        super(context, 0 , movieList);
        mMoviesData = movieList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Movie movie = getItem(position);

        if (movie != null) {

            /* Used to handle recycle views */
            /* no need to inflate another view if a recycled view is obtained */
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.movie_item, parent, false);
            }

            ImageView moviePoster = convertView.findViewById(R.id.iv_movie_poster);
            String posterUrl = movie.getMoviePosterUrl();

            if (posterUrl != null) {
                Picasso.with(getContext())
                        .load(posterUrl)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(moviePoster);
            } else {
                Picasso.with(getContext())
                        .load(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .into(moviePoster);
            }
        }
        return convertView;
    }

    public void setMoviesData(Movie[] movies) {
        mMoviesData.clear();
        if(movies!=null && movies.length!=0)
        {
            mMoviesData.addAll(Arrays.asList(movies));
        }
        notifyDataSetChanged();
    }

}
//public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
//
//    private Movie[] mMoviesData;
//
//    /*
//     * An on-click handler that we've defined to make it easy for an Activity to interface with
//     * our RecyclerView
//     */
//    private final MoviesAdapterOnClickHandler mClickHandler;
//
//    private final Context mContext;
//
//    /**
//     * The interface that receives onClick messages.
//     */
//    public interface MoviesAdapterOnClickHandler {
//        void onClick(Movie movie);
//    }
//
//    /**
//     * Creates a MoviesAdapter.
//     *
//     * @param clickHandler The on-click handler for this adapter. This single handler is called
//     *                     when an item is clicked.
//     */
//    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
//        mClickHandler = clickHandler;
//        mContext = context;
//    }
//
//    /**
//     * Cache of the children views for a Movie grid item.
//     */
//    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
//        public final ImageView mMoviePosterImageView;
//
//        public MoviesAdapterViewHolder(View view) {
//            super(view);
//            mMoviePosterImageView = view.findViewById(R.id.iv_movie_poster);
//            view.setOnClickListener(this);
//        }
//
//        /**
//         * This gets called by the child views during a click.
//         *
//         * @param v The View that was clicked
//         */
//        @Override
//        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
//            Movie movie = mMoviesData[adapterPosition];
//            mClickHandler.onClick(movie);
//        }
//    }
//
//    /**
//     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
//     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
//     *
//     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
//     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
//     *                  can use this viewType integer to provide a different layout. See
//     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
//     *                  for more details.
//     * @return A new MoviesAdapterViewHolder that holds the View for each list item
//     */
//    @NonNull
//    @Override
//    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        Context context = viewGroup.getContext();
//        int layoutIdForListItem = R.layout.movie_item;
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
//        return new MoviesAdapterViewHolder(view);
//    }
//
//    /**
//     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
//     * position. In this method, we update the contents of the ViewHolder to display the weather
//     * details for this particular position, using the "position" argument that is conveniently
//     * passed into us.
//     *
//     * @param moviesAdapterViewHolder The ViewHolder which should be updated to represent the
//     *                                  contents of the item at the given position in the data set.
//     * @param position                  The position of the item within the adapter's data set.
//     */
//    @Override
//    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
//        Movie movie = mMoviesData[position];
//        Picasso.with(mContext)
//                .load(movie.getMoviePosterUrl())
//                .placeholder(R.mipmap.ic_launcher_round)
//                .error(R.mipmap.ic_launcher_round)
//                .into(moviesAdapterViewHolder.mMoviePosterImageView);
//
////        moviesAdapterViewHolder.mMoviePosterImageView.setText(weatherForThisDay);
//    }
//
//    /**
//     * This method simply returns the number of items to display. It is used behind the scenes
//     * to help layout our Views and for animations.
//     *
//     * @return The number of items available in our forecast
//     */
//    @Override
//    public int getItemCount() {
//        if (null == mMoviesData) return 0;
//        return mMoviesData.length;
//    }
//
//    /**
//     * This method is used to set the movies data on a MoviesAdapter if we've already
//     * created one. This is handy when we get new data from the web but don't want to create a
//     * new MoviesAdapter to display it.
//     *
//     * @param moviesData The new movie data to be displayed.
//     */
//    public void setMoviesData(Movie[] moviesData) {
//        mMoviesData = moviesData;
//        notifyDataSetChanged();
//    }
//}