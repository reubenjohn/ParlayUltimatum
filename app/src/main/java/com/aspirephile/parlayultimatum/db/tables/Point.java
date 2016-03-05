package com.aspirephile.parlayultimatum.db.tables;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Point {

    private int PID;
    private String username;
    private String title;
    private String description;
    private String timestamp;

    public Point(ResultSet rs) throws SQLException {
        PID = rs.getInt("PID");
        username = rs.getString("username");
        title = rs.getString("title");
        description = rs.getString("description");
        timestamp = rs.getString("timestamp");
    }

    public String toString() {
        return "{pid: " + PID + ", username: " + username + ", title: " + title
                + ", description: " + description + ", timestamp: " + timestamp
                + "}";
    }

}
