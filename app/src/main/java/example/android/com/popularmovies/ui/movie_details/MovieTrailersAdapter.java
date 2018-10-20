package example.android.com.popularmovies.ui.movie_details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.db.model.MovieTrailerEntry;
import example.android.com.popularmovies.ui.movie_grid.MoviesAdapter;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailerAdapterViewHolder>{

    private List<MovieTrailerEntry> mTrailersData;
    private Context mContext;
    private MovieTrailerAdapterOnClickHandler mClickHandler;
    private int mShortAnimationDuration;

    /**
     * click handler to get movie object at a particular position
     */
    public interface MovieTrailerAdapterOnClickHandler {
        void onTrailerClick(MovieTrailerEntry trailerEntry);
    }

    MovieTrailersAdapter(Context context, MovieTrailerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mShortAnimationDuration = mContext.getResources().getInteger(
                android.R.integer.config_longAnimTime);
    }

    /**
     * Viewholder class to hold views for rv
     * we only use one viewType and one xml trailer_item.xml
     *
     * **/
    public class MovieTrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final TextView mTrailerTitleTextView;
        public final ImageView mTrailerPlayIcon;
        public final View mItemView;

        public MovieTrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerTitleTextView = itemView.findViewById(R.id.tv_trailer_title);
            mTrailerPlayIcon = itemView.findViewById(R.id.iv_trailer_play_icon);
            itemView.setOnClickListener(this);
            mItemView = itemView;
        }

        @Override
        public void onClick(View v) {
            if(mTrailersData!=null) {
                MovieTrailerEntry trailerEntry = mTrailersData.get(getAdapterPosition());
                mClickHandler.onTrailerClick(trailerEntry);
            }
        }
    }

    /**
     * Inflates trailer_item layout into one item of recycler view
     * @param parent parent viewgroup to attach rv item to
     * @param viewType viewType of rv item to be attached (we only use one view type)
     * @return new MovieTrailerAdapterViewHolder with inflated layout
     */
    @NonNull
    @Override
    public MovieTrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        view.setVisibility(View.GONE);
        return new MovieTrailerAdapterViewHolder(view);
    }

    /**
     * Binds viewholder to data
     * @param holder viewholder with inflated layout
     * @param position position of view in rv
     */
    @Override
    public void onBindViewHolder(@NonNull MovieTrailerAdapterViewHolder holder, int position) {
        MovieTrailerEntry trailer = mTrailersData.get(position);
        if(trailer != null) {
            String trailerTitleFormatted = String.format(
                    mContext.getString(R.string.trailer_title_format), Integer.toString(position+1));

            holder.mTrailerTitleTextView.setText(trailerTitleFormatted);

            String trailerTitleA11y = String.format(mContext.getString(R.string.a11y_play_movie_trailer_formatted),
                    Integer.toString(position));
            holder.mTrailerPlayIcon.setContentDescription(trailerTitleA11y);
            fadeInItem(holder);
        }
    }

    private void fadeInItem(@NonNull MovieTrailerAdapterViewHolder holder) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        holder.mItemView.setAlpha(0f);
        holder.mItemView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        holder.mItemView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);
    }

    /**
     *
     * @return item count of current trailer data
     */
    @Override
    public int getItemCount() {
        if(mTrailersData == null) { return 0; }
        return mTrailersData.size();
    }

    /**
     * Returns all trailer data
     * **/
    public List<MovieTrailerEntry> getMovieTrailerData() {
        return mTrailersData;
    }

    /**
     * @param trailers list of trailers to be added to rv
     * **/
    public void setMovieTrailerData(List<MovieTrailerEntry> trailers) {
        if(mTrailersData == null) { mTrailersData = new ArrayList<>();}
        mTrailersData.clear();
        //check argument
        if (trailers != null && trailers.size() != 0) {
            mTrailersData.addAll(trailers);
        }
        notifyDataSetChanged();
    }

}
