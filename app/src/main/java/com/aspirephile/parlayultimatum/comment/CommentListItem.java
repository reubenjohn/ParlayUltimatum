package com.aspirephile.parlayultimatum.comment;

import android.content.Context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class CommentListItem {

    /**
     * A point item representing a piece of content.
     */
    public static class CommentItem {
        public final String PID;
        public final String CID;
        public final String poster;
        public final Timestamp timestamp;
        public final String description;

        public CommentItem(ResultSet rs, Context context) throws SQLException {
            PID = rs.getString("PID");
            CID = rs.getString("CID");
            poster = rs.getString("poster");
            timestamp = rs.getTimestamp("timestamp");
            description = rs.getString("description");
        }
    }
}
