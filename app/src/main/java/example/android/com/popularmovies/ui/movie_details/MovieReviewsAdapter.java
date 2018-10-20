package example.android.com.popularmovies.ui.movie_details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.android.com.popularmovies.R;
import example.android.com.popularmovies.db.model.MovieReviewEntry;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewAdapterViewHolder>{

    private List<MovieReviewEntry> mReviewsData;
    private Context mContext;
    private int mShortAnimationDuration;


    MovieReviewsAdapter(Context context) {
        mContext = context;
        mShortAnimationDuration = mContext.getResources().getInteger(
                android.R.integer.config_longAnimTime);
    }

    /**
     * Viewholder class to hold views for rv
     * we only use one viewType and one xml trailer_item.xml
     *
     * **/
    class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView mReviewAuthorTextView;
        final TextView mReviewUrlTextView;
        final TextView mReviewContentTextView;
        final View mItemView;

        MovieReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mReviewAuthorTextView = itemView.findViewById(R.id.tv_review_author);
            mReviewUrlTextView = itemView.findViewById(R.id.tv_review_url);
            mReviewContentTextView = itemView.findViewById(R.id.tv_review_content);

            mItemView = itemView;
        }

    }

    /**
     * Inflates trailer_item layout into one item of recycler view
     * @param parent parent viewgroup to attach rv item to
     * @param viewType viewType of rv item to be attached (we only use one view type)
     * @return new MovieReviewAdapterViewHolder with inflated layout
     */
    @NonNull
    @Override
    public MovieReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        view.setVisibility(View.GONE);
        return new MovieReviewAdapterViewHolder(view);
    }

    /**
     * Binds viewholder to data
     * @param holder viewholder with inflated layout
     * @param position position of view in rv
     */
    @Override
    public void onBindViewHolder(@NonNull MovieReviewAdapterViewHolder holder, int position) {
        MovieReviewEntry review = mReviewsData.get(position);
        if(review != null) {
            String formattedAuthorText = String.format(mContext.getString(R.string.review_author_formatted)
                    ,review.getAuthor());
            holder.mReviewAuthorTextView.setText(formattedAuthorText);
            holder.mReviewUrlTextView.setText(review.getUrl());
            holder.mReviewContentTextView.setText(review.getContent());

            String reviewUrlA11y = mContext.getString(R.string.a11y_click_movie_url);
            holder.mReviewUrlTextView.setContentDescription(reviewUrlA11y);
            fadeInItem(holder);
        }
    }

    private void fadeInItem(@NonNull MovieReviewAdapterViewHolder holder) {
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
        if(mReviewsData == null) { return 0; }
        return mReviewsData.size();
    }

    /**
     * @param reviewEntries list of trailers to be added to rv
     * **/
    void setMovieReviewData(List<MovieReviewEntry> reviewEntries) {
        if(mReviewsData == null) { mReviewsData = new ArrayList<>();}

        mReviewsData.clear();
        //check argument
        if (reviewEntries != null && reviewEntries.size() != 0) {
            mReviewsData.addAll(reviewEntries);
        }
        notifyDataSetChanged();
    }

}
