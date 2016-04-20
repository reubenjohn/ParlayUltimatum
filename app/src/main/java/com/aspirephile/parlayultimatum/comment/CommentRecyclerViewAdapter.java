package com.aspirephile.parlayultimatum.comment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspirephile.parlayultimatum.R;
import com.aspirephile.parlayultimatum.comment.CommentListFragment.OnListFragmentInteractionListener;
import com.aspirephile.parlayultimatum.comment.CommentListItem.CommentItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CommentItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {

    private final List<CommentItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public CommentRecyclerViewAdapter(List<CommentItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_comment, parent, false);        //CHANGED! other than other parts
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.descView.setText(mValues.get(position).description);
        holder.timestampView.setText(mValues.get(position).timestamp.toString());
        holder.usernameView.setText(mValues.get(position).poster);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCommentListItemSelected(holder.mItem);
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
        public final TextView descView;
        public final TextView timestampView;
        public final TextView usernameView;
        public CommentItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            descView = (TextView) view.findViewById(R.id.tv_item_comment_description);
            timestampView = (TextView) view.findViewById(R.id.tv_item_comment_timestamp);
            usernameView = (TextView) view.findViewById(R.id.tv_item_comment_username);             //What to do?
        }

    }
}
