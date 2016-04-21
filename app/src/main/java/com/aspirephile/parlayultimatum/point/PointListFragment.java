package com.aspirephile.parlayultimatum.point;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.aspirephile.parlayultimatum.point.PointListItem.PointItem;
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
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PointListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_PID = "PID";
    private static final String ARG_STANDING = "STANDING";
    private Logger l = new Logger(PointListFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<PointItem> pointItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String PID;
    private char standing = '\0';

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PointListFragment() {
    }

    public static PointListFragment newInstance(int columnCount) {
        PointListFragment fragment = new PointListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static PointListFragment newInstance(int columnCount, @NonNull String PID, char standing) {
        PointListFragment fragment = new PointListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_PID, PID);
        args.putChar(ARG_STANDING, standing);
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
            standing = getArguments().getChar(ARG_STANDING);
        }

        onRefresh();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_point_list);
        swipeRefreshLayout.setOnRefreshListener(this);
        // Set the adapter
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_point_list);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new PointRecyclerViewAdapter(pointItems, mListener));

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
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);

        OnGetPrepareStatement getPreparedStatementListener = new OnGetPrepareStatement() {
            @Override
            public PreparedStatement onGetPreparedStatement(BackendConnection remoteConnection) {
                try {
                    String sql;
                    PreparedStatement preparedStatement;
                    if (standing != '\0') {
                        sql = "SELECT * from Topic " +
                                "where PID in " +
                                "(" +
                                "select child from PointRole " +
                                "where " +
                                "parent=? " +
                                "and " +
                                "standing=? " +
                                ");";
                        preparedStatement = remoteConnection.prepareStatement(sql);
                        preparedStatement.setString(1, PID);
                        preparedStatement.setString(2, String.valueOf(standing));
                    } else {
                        sql = "SELECT * FROM Topic";
                        preparedStatement = remoteConnection.prepareStatement(sql);
                    }
                    return preparedStatement;
                } catch (SQLException e) {
                    mListener.onPointListLoadFailed(e);
                    return null;
                }
            }
        };

        OnGetResultSetListener onGetResultSetListener = new OnGetResultSetListener() {
            @Override
            public void onGetResultSet(ResultSet rs, SQLException e) {
                if (e != null) {
                    mListener.onPointListLoadFailed(e);
                } else {
                    ArrayList<PointItem> list = new ArrayList<>();
                    Context context = getContext();
                    try {
                        while (rs.next()) {
                            list.add(new PointItem(rs, context));
                        }

                        l.i("Point query completed with " + list.size() + " results");
                        if (asserter.assertPointer(recyclerView))
                            recyclerView.setAdapter(new PointRecyclerViewAdapter(list, mListener));
                        swipeRefreshLayout.setRefreshing(false);

                    } catch (SQLException e1) {
                        mListener.onPointListLoadFailed(e1);
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
        void onPointListItemSelected(PointItem item);

        void onPointListLoadFailed(SQLException e);
    }
}
