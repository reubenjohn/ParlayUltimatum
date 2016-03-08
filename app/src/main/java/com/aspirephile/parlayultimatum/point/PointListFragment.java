package com.aspirephile.parlayultimatum.point;

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

import org.kawanfw.sql.api.client.android.ItemBuilder;
import org.kawanfw.sql.api.client.android.OnQueryComplete;
import org.kawanfw.sql.api.client.android.AceQLDBManager;

import com.aspirephile.parlayultimatum.point.PointContent.PointItem;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

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
    private Logger l = new Logger(PointListFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<PointItem> pointItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PointListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PointListFragment newInstance(int columnCount) {
        PointListFragment fragment = new PointListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        l.onCreate();
        super.onCreate(savedInstanceState);

        onRefresh();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
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
        String sql = "SELECT PID, " +
                "username as poster, " +
                "title, " +
                "(select count(*) from PointView where PID = Point.PID) as views, " +
                "round(100*(select count(*) from VotesPoint where upDown='U' and PID = Point.PID)/((select count(*) from VotesPoint where PID = Point.PID)+0.1),0) as upVotesPercentage, " +
                "tag1, tag2, tag3, tag4 " +
                "FROM Point";
        OnQueryComplete<PointItem> onQueryCompletedListener = new OnQueryComplete<PointItem>() {
            @Override
            public void onQueryComplete(List<PointItem> list, SQLException e1) {
                if (e1 != null) {
                    e1.printStackTrace();
                } else {
                    l.i("Point query completed successfully");
                    pointItems = list;
                    if (asserter.assertPointer(recyclerView))
                        recyclerView.setAdapter(new PointRecyclerViewAdapter(pointItems, mListener));
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        ItemBuilder<PointItem> itemBuilder = new ItemBuilder<PointItem>() {
            @Override
            public PointItem buildItem(ResultSet rs) {
                try {
                    return new PointItem(rs, getContext());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        AceQLDBManager.getRowList(sql, itemBuilder, onQueryCompletedListener);
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(PointItem item);
    }
}
