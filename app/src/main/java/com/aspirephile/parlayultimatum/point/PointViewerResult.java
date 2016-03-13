package com.aspirephile.parlayultimatum.point;

import android.content.Context;

import com.aspirephile.parlayultimatum.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created and maintained by Reuben John on 3/13/2016.
 */
public class PointViewerResult {

    private final String PID;
    private final String poster;
    private final String title;
    private final String description;
    private final Timestamp timestamp;
    private final String views;
    private final short upVotesPercentage;
    private final String tag1, tag2, tag3, tag4;

    public PointViewerResult(ResultSet rs, Context context) throws SQLException {
        PID = String.valueOf(rs.getInt("PID"));
        poster = rs.getString("poster");
        title = rs.getString("title");
        description = rs.getString("description");
        timestamp = rs.getTimestamp("timestamp");
        views = rs.getString("views") + " " + context.getString(R.string.views);
        upVotesPercentage = rs.getShort("upVotesPercentage");
        tag1 = rs.getString("tag1");
        tag2 = rs.getString("tag2");
        tag3 = rs.getString("tag3");
        tag4 = rs.getString("tag4");
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
