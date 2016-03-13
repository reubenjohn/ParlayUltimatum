package com.aspirephile.parlayultimatum.point;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspirephile.parlayultimatum.R;
import com.aspirephile.parlayultimatum.point.PointListFragment.OnListFragmentInteractionListener;
import com.aspirephile.parlayultimatum.point.PointListItem.PointItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PointItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PointRecyclerViewAdapter extends RecyclerView.Adapter<PointRecyclerViewAdapter.ViewHolder> {

    private final List<PointItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public PointRecyclerViewAdapter(List<PointItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.titleView.setText(mValues.get(position).title);
        holder.viewCountView.setText(mValues.get(position).views);
        holder.votesView.setProgress(mValues.get(position).upVotesPercentage);
        holder.tag1View.setText(mValues.get(position).tag1);
        holder.tag2View.setText(mValues.get(position).tag2);
        holder.tag3View.setText(mValues.get(position).tag3);
        holder.tag4View.setText(mValues.get(position).tag4);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPointListItemSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView titleView;
        public final TextView viewCountView;
        public final ProgressBar votesView;
        public final TextView tag1View, tag2View, tag3View, tag4View;
        public PointItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            titleView = (TextView) view.findViewById(R.id.tv_item_point_title);
            viewCountView = (TextView) view.findViewById(R.id.tv_item_point_views);
            votesView = (ProgressBar) view.findViewById(R.id.pm_item_point_votes);
            tag1View = (TextView) view.findViewById(R.id.tv_item_point_tag_1);
            tag2View = (TextView) view.findViewById(R.id.tv_item_point_tag_2);
            tag3View = (TextView) view.findViewById(R.id.tv_item_point_tag_3);
            tag4View = (TextView) view.findViewById(R.id.tv_item_point_tag_4);
        }

    }
}
