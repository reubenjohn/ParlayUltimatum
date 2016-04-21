package com.aspirephile.parlayultimatum.point;

import android.content.Context;

import com.aspirephile.parlayultimatum.R;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        public final String description_comment;
        public final String timestamp_comment;                  //WIll it work!?
        public final String username_comment;

        public CommentItem(ResultSet rs, Context context) throws SQLException {
            PID = String.valueOf(rs.getInt("PID"));
            CID = String.valueOf(rs.getInt("CID"));
            description_comment = rs.getString("description_comment");
            timestamp_comment = rs.getString("timestamp_comment");
            username_comment = rs.getString("username_comment");
        }
    }
}
