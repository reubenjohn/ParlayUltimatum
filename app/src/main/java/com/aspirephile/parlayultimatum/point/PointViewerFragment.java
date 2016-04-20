package com.aspirephile.parlayultimatum.point;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aspirephile.parlayultimatum.Constants;
import com.aspirephile.parlayultimatum.R;
import com.aspirephile.parlayultimatum.comment.CommentListActivity;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

import org.kawanfw.sql.api.client.android.AceQLDBManager;
import org.kawanfw.sql.api.client.android.BackendConnection;
import org.kawanfw.sql.api.client.android.execute.OnGetPrepareStatement;
import org.kawanfw.sql.api.client.android.execute.query.OnGetResultSetListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PointViewerFragment extends Fragment implements View.OnClickListener {

    OnFragmentInteractionListener fragmentInteractionListener;
    private Logger l = new Logger(PointViewerFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton editFab;
    private TextView descriptionView;
    private String PID;
    private PointViewerResult pointViewerResult;
    private PointListFragment forListF, againstListF;
    private Button commentB;

    public PointViewerFragment() {
        l.onConstructor();
    }

    @Override
    public void onAttach(Context context) {
        l.onAttach();
        super.onAttach(context);
        try {
            fragmentInteractionListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        l.onCreate();
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        OnGetPrepareStatement onGetPreparedStatementListener = new OnGetPrepareStatement() {
            @Override
            public PreparedStatement onGetPreparedStatement(BackendConnection remoteConnection) {

                String sql = "SELECT PID, " +
                        "username as poster, " +
                        "title, " +
                        "description, " +
                        "timestamp, " +
                        "(select count(*) from PointView where PID = Point.PID) as views, " +
                        "round(100*(select count(*) from VotesPoint where upDown='U' and PID = Point.PID)/((select count(*) from VotesPoint where PID = Point.PID)+0.1),0) as upVotesPercentage, " +
                        "tag1, tag2, tag3, tag4 " +
                        "FROM Point where PID = ?";
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = remoteConnection.prepareStatement(sql);
                    preparedStatement.setString(1, PID);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return preparedStatement;
            }
        };
        OnGetResultSetListener onGetResultSetListener = new OnGetResultSetListener() {
            @Override
            public void onGetResultSet(ResultSet rs, SQLException e) {
                if (e != null) {
                    fragmentInteractionListener.onFetchFailed(e);
                } else {
                    l.i("Point query completed successfully");
                    try {
                        if (rs != null) {
                            rs.next();
                            pointViewerResult = new PointViewerResult(rs, getContext());
                            updateViews(pointViewerResult);
                        } else {
                            fragmentInteractionListener.onPointNotFound();
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        fragmentInteractionListener.onFetchFailed(e1);
                    }
                }
            }
        };
        AceQLDBManager.executeQuery(onGetPreparedStatementListener, onGetResultSetListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        l.onCreateView();
        View v = inflater.inflate(R.layout.fragment_point_viewer, container, false);
        if (asserter.assertPointer(v))
            bridgeXML(v);
        initializeFields();
        if (pointViewerResult != null)
            updateViews(pointViewerResult);
        return v;
    }

    @Override
    public void onStart() {
        l.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        l.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        l.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        l.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        l.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        l.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        l.onDetach();
        super.onDetach();
        fragmentInteractionListener = null;
    }

    private void bridgeXML(View v) {
        l.bridgeXML();

        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.cl_point_viewer);
        collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.ctl_point_viewer);

        descriptionView = (TextView) v.findViewById(R.id.tv_point_viewer_description);

        editFab = (FloatingActionButton) v.findViewById(R.id.fab_point_viewer_edit);

        commentB = (Button) v.findViewById(R.id.b_point_viewer_comments);

        l.bridgeXML(asserter.assertPointer(coordinatorLayout, collapsingToolbarLayout, descriptionView, editFab));
    }

    private void initializeFields() {
        l.initializeFields();

        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.feature_not_available, Snackbar.LENGTH_LONG).show();
            }
        });

        commentB.setOnClickListener(this);

        openForListFragment();
        openAgainstListFragment();
    }

    private void openForListFragment() {
        // find the retained fragment on activity restarts
        FragmentManager fm = getActivity().getSupportFragmentManager();
        forListF = (PointListFragment) fm.findFragmentByTag(Constants.tags.pointListForFragment);

        if (!asserter.assertPointerQuietly(forListF)) {
            l.i("Creating new " + PointListFragment.class.getSimpleName() + " fragment");
            forListF = PointListFragment.newInstance(1);
            fm.beginTransaction()
                    .replace(R.id.container_point_viewer_for, forListF, Constants.tags.pointListForFragment)
                    .commit();
        }
    }

    private void openAgainstListFragment() {
        // find the retained fragment on activity restarts
        FragmentManager fm = getActivity().getSupportFragmentManager();
        againstListF = (PointListFragment) fm.findFragmentByTag(Constants.tags.pointListAgainstFragment);

        if (!asserter.assertPointerQuietly(againstListF)) {
            l.i("Creating new " + PointListFragment.class.getSimpleName() + " fragment");
            againstListF = PointListFragment.newInstance(1);
            fm.beginTransaction()
                    .replace(R.id.container_point_viewer_against, againstListF, Constants.tags.pointListAgainstFragment)
                    .commit();
        }
    }

    private void updateViews(PointViewerResult point) {
        collapsingToolbarLayout.setTitle(point.getTitle());
        descriptionView.setText(point.getDescription());
        //TODO Fill other fields here
    }

    private void editPoint() {
        //TODO Start activity to edit point
        Snackbar.make(coordinatorLayout, R.string.feature_not_available, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fab_point_creator) {
            editPoint();
        } else if (id == R.id.b_point_viewer_comments) {
            l.d("Opening comments");
            Intent i = new Intent(getActivity(), CommentListActivity.class);
            i.putExtra(Constants.extras.PID, PID);
            startActivity(i);
        } else {
            l.w("Unhandled view clicked with ID: " + v.getId());
        }
    }

    public void setPID(String PID) {
        this.PID = PID;
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
    public interface OnFragmentInteractionListener {
        void onFetchFailed(SQLException e);

        void onPointNotFound();
    }
}
