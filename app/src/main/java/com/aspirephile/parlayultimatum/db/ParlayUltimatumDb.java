package com.aspirephile.parlayultimatum.db;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.aspirephile.parlayultimatum.R;
import com.aspirephile.parlayultimatum.point.PointContent.PointItem;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ParlayUltimatumDb {
    private static final String username = "reuben";
    private static final String password = "pass";
    public static ParlayUltimatumConnection remoteConnection;
    private static Logger l = new Logger(ParlayUltimatumDb.class);
    private static NullPointerAsserter asserter = new NullPointerAsserter(l);
    private static String backendUrl;
    private static Context context;

    public static void initialize(Context context) {
        ParlayUltimatumDb.context = context;
    }

    public static void getDefaultRemoteConnectionIfExists(OnRemoteConnectionEstablishedListener listener) {

        if (!asserter.assertPointerQuietly(backendUrl)) {
            backendUrl = PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getString(context.getString(R.string.pref_key_data_sync_backend_url),
                            context.getString(R.string.pref_default_data_sync_backend_url));
        }
        getRemoteConnection(backendUrl, listener);
    }

    public static void getRemoteConnection(final String backendUrl, final OnRemoteConnectionEstablishedListener listener) {
        ParlayUltimatumDb.backendUrl = backendUrl;
        if (asserter.assertPointerQuietly(remoteConnection))
            //Send cached remote connection if available
            listener.onRemoteConnectionEstablishedListener(remoteConnection, null);
        else {
            new GetRemoteConnectionTask().execute(listener);
        }
    }

    public static void getResultSet(final OnGetPreparedStatement onGetPreparedStatement, final OnGetResultSetListener onGetResultSetListener) {
        ParlayUltimatumDb.getDefaultRemoteConnectionIfExists(new OnRemoteConnectionEstablishedListener() {
            @Override
            public void onRemoteConnectionEstablishedListener(final ParlayUltimatumConnection remoteConnection, SQLException e) {
                if (e != null) {
                    onGetResultSetListener.onGetResultSet(null, e);
                } else {
                    ExecuteQueryTask executeQueryTask = new ExecuteQueryTask();
                    executeQueryTask.setParlayUltimatumConnection(remoteConnection);
                    executeQueryTask.setPreparedStatement(onGetPreparedStatement.onGetPreparedStatement(remoteConnection));
                    executeQueryTask.execute(onGetResultSetListener);
                }
            }
        });
    }

    public static void getPointItems(final OnQueryComplete<PointItem> onQueryComplete) {
        getResultSet(new OnGetPreparedStatement() {
            public PreparedStatement onGetPreparedStatement(ParlayUltimatumConnection remoteConnection) {
                String sql = "SELECT PID, " +
                        "username as poster, " +
                        "title, " +
                        "(select count(*) from PointView where PID = Point.PID) as views, " +
                        "(100*(select count(*) from VotesPoint where upDown='U' and PID = Point.PID)/(select count(*) from VotesPoint where PID = Point.PID)) as upVotesPercentage, " +
                        "tag1, tag2, tag3, tag4 " +
                        "FROM Point";
                try {
                    return remoteConnection.connection.prepareStatement(sql);
                } catch (SQLException e) {
                    onQueryComplete.onQueryComplete(null, e);
                    return null;
                }
            }
        }, new OnGetResultSetListener() {
            @Override
            public void onGetResultSet(ResultSet rs, SQLException e) {
                if (e != null) {
                    onQueryComplete.onQueryComplete(null, e);
                } else {
                    try {
                        ArrayList<PointItem> pointList = new ArrayList<>();
                        while (rs.next()) {
                            pointList.add(new PointItem(rs));
                        }
                        rs.close();
                        onQueryComplete.onQueryComplete(pointList, null);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        onQueryComplete.onQueryComplete(null, e1);
                    }
                }
            }
        });
    }

    private static class GetRemoteConnectionTask extends AsyncTask<OnRemoteConnectionEstablishedListener, Void, RemoteConnectionEstablishedResult> {
        private OnRemoteConnectionEstablishedListener listener;

        @Override
        protected RemoteConnectionEstablishedResult doInBackground(OnRemoteConnectionEstablishedListener... listeners) {
            listener = listeners[0];
            try {
                Connection connection = ParlayUltimatumConnection
                        .remoteConnectionBuilder(backendUrl, username, password);
                ParlayUltimatumConnection newRemoteConnection = new ParlayUltimatumConnection(
                        connection);
                if (asserter.assertPointer(newRemoteConnection)) {
                    //Close the old connection if it exists
                    if (remoteConnection != null)
                        remoteConnection.close();
                }
                //Update remote connection even if it is null as this may be intentional.
                remoteConnection = newRemoteConnection;
                return new RemoteConnectionEstablishedResult(remoteConnection, null);
            } catch (SQLException e) {
                return new RemoteConnectionEstablishedResult(null, e);
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(RemoteConnectionEstablishedResult result) {
            listener.onRemoteConnectionEstablishedListener(result.remoteConnection, result.sqlException);
        }
    }
}
