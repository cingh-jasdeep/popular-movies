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

package example.android.com.popularmovies.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.data.Movie;

/**
 *  Exposes a list of movies to a RecyclerView
 *  Note: RecyclerView may be abbreviated as "rv" in comments below
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{

    private List<Movie> mMoviesData;
    private Context mContext;
    private MoviesAdapterOnClickHandler mClickHandler;

    /**
     * click handler to get movie object at a particular position
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /**
     * Viewholder class to hold views for rv
     * we only use one viewType and one xml movie_item.xml
     *
     * **/
    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public final ImageView mPosterImageView;
        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mMoviesData!=null) {
                Movie movie = mMoviesData.get(getAdapterPosition());
                mClickHandler.onClick(movie);
            }
        }
    }

    /**
     * Inflates movie_item layout into one item of recycler view
     * @param parent parent viewgroup to attach rv item to
     * @param viewType viewType of rv item to be attached (we only use one view type)
     * @return new MoviesAdapterViewHolder with inflated layout
     */
    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    /**
     * Binds viewholder to data
     * @param holder viewholder with inflated layout
     * @param position position of view in rv
     */
    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {
        Movie movie = mMoviesData.get(position);
        if(movie!=null) {
            Picasso.with(mContext)
                    .load(movie.getMoviePosterUrl())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(holder.mPosterImageView);
            String posterA11y = String.format(mContext.getString(R.string.a11y_poster), movie.getMovieTitle());
            holder.mPosterImageView.setContentDescription(posterA11y);
        }
    }

    /**
     *
     * @return item count of current movies data mMoviesData
     */
    @Override
    public int getItemCount() {
        if(mMoviesData == null) { return 0; }
        return mMoviesData.size();
    }


    /**
     * @param movies array of movie objects to be added to rv
     * **/
    public void setMoviesData(Movie[] movies) {
        if(mMoviesData == null) { mMoviesData = new ArrayList<>();}
        mMoviesData.clear();
        //check argument
        if (movies != null && movies.length != 0) {
            mMoviesData.addAll(Arrays.asList(movies));
        }
        notifyDataSetChanged();
    }
}