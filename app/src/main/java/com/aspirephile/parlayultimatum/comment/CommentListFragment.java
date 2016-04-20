package com.aspirephile.parlayultimatum.comment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspirephile.parlayultimatum.R;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

import org.kawanfw.sql.api.client.android.AceQLDBManager;
import org.kawanfw.sql.api.client.android.BackendConnection;
import org.kawanfw.sql.api.client.android.execute.OnGetPrepareStatement;
import org.kawanfw.sql.api.client.android.execute.query.OnGetResultSetListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CommentListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_PID = "PID";
    private Logger l = new Logger(CommentListFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String PID;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommentListFragment() {
    }

    @SuppressWarnings("unused")
    public static CommentListFragment newInstance(int columnCount, String PID) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(CommentListFragment.ARG_PID, PID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        l.onCreate();
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            PID = getArguments().getString(ARG_PID);
        }

        onRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_comment_list);
        swipeRefreshLayout.setOnRefreshListener(this);
        // Set the adapter
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_comment_list);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        l.d("onRefresh");
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        final String sql = "select username as poster, PID, CID, timestamp, description from ParlayComment where PID=? order by CID desc";

        OnGetPrepareStatement getPreparedStatementListener = new OnGetPrepareStatement() {
            @Override
            public PreparedStatement onGetPreparedStatement(BackendConnection remoteConnection) {
                try {
                    PreparedStatement preparedStatement = remoteConnection.prepareStatement(sql);
                    preparedStatement.setString(1, PID);
                    return preparedStatement;
                } catch (SQLException e) {
                    mListener.onCommentListLoadFailed(e);
                    return null;
                }
            }
        };

        OnGetResultSetListener onGetResultSetListener = new OnGetResultSetListener() {
            @Override
            public void onGetResultSet(ResultSet rs, SQLException e) {
                if (e != null) {
                    mListener.onCommentListLoadFailed(e);
                } else {
                    ArrayList<CommentListItem.CommentItem> list = new ArrayList<>();
                    Context context = getContext();
                    try {
                        while (rs.next()) {
                            list.add(new CommentListItem.CommentItem(rs, context));
                        }

                        l.i("Parlay Comment query completed with " + list.size() + " results");
                        if (asserter.assertPointer(recyclerView))
                            recyclerView.setAdapter(new CommentRecyclerViewAdapter(list, mListener));
                        swipeRefreshLayout.setRefreshing(false);

                    } catch (SQLException e1) {
                        mListener.onCommentListLoadFailed(e1);
                    }
                }
            }
        };
        AceQLDBManager.executeQuery(getPreparedStatementListener, onGetResultSetListener);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onCommentListItemSelected(CommentListItem.CommentItem item);

        void onCommentListLoadFailed(SQLException e);
    }
}
