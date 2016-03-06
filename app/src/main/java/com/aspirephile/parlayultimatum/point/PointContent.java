package com.aspirephile.parlayultimatum.point;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class PointContent {

    /**
     * A point item representing a piece of content.
     */
    public static class PointItem {
        public final String PID;
        public final String poster;
        public final String title;
        public final short upVotesPercentage;
        public final String tag1, tag2, tag3, tag4;
        public String views;

        public PointItem(ResultSet rs) throws SQLException {
            PID = String.valueOf(rs.getInt("PID"));
            poster = rs.getString("poster");
            title = rs.getString("title");
            views = rs.getString("views");
            upVotesPercentage = rs.getShort("upVotesPercentage");
            tag1 = rs.getString("tag1");
            tag2 = rs.getString("tag2");
            tag3 = rs.getString("tag3");
            tag4 = rs.getString("tag4");
        }
    }
}
